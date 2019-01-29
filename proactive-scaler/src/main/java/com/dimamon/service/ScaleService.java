package com.dimamon.service;


import com.dimamon.config.ScalerConfig;
import com.dimamon.entities.WorkloadPoint;
import com.dimamon.entities.WorkloadPredictionPoint;
import com.dimamon.repo.MeasurementsRepo;
import com.dimamon.service.kubernetes.KubernetesService;
import com.dimamon.service.predict.PredictorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.dimamon.utils.StringUtils.showValue;
import static com.dimamon.utils.StringUtils.showValues;


/**
 * Periodically check database with metrics to make workload prediction and make scaling decision
 */
@Service
public class ScaleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScaleService.class);

    private static final String APP_NAME = "scaler-app";
    private static final int INITIAL_DELAY = 10 * 1000;
    private static final int CHECK_EVERY = 30 * 1000;

    private static ScalerConfig config = ScalerConfig.PROACTIVE_V1;

    private static int ignoreScaling = 0;

    @Autowired
    private MeasurementsRepo measurementsRepo;

    @Qualifier("esPredictor")
    @Autowired
    private PredictorService predictorService;

    @Autowired
    private KubernetesService kubService;


    @Scheduled(initialDelay = INITIAL_DELAY, fixedDelay = CHECK_EVERY)
    public void checkMetrics() {
        LOGGER.info("### Checking metrics task = {} | {}", new Date(), config.proactiveString());
        kubService.checkPods();
        Set<String> podNames = kubService.getPodNames();

        measurementsRepo.writePodsInfo(APP_NAME, kubService.getMetricsPodCount(),
                kubService.getMetricsPodReadyCount(), podNames);

        class PodMetrics {
            private String podName;
            private List<Double> metrics;

            private PodMetrics(String podName, List<Double> metrics) {
                this.podName = podName;
                this.metrics = metrics;
            }

            @Override
            public String toString() {
                return "PodMetrics{" +
                        "podName='" + podName + '\'' +
                        ", metrics=" + showValues(metrics) +
                        '}';
            }
        }

        List<PodMetrics> podMetricsList = new ArrayList<>();
        podNames.forEach(podName -> {
            List<Double> lastPodCpu = measurementsRepo.getLastLoadMetrics(podName, config.getForecastBasedOn())
                    .stream().map(WorkloadPoint::getPodCpu)
                    .collect(Collectors.toList());
            if (!lastPodCpu.isEmpty()) {
                podMetricsList.add(new PodMetrics(podName, lastPodCpu));
            }
        });

        LOGGER.info(" = = = POD METRICS = = = ");
        podMetricsList.forEach(pm -> LOGGER.info(pm.toString()));
        LOGGER.info(" = = = = = = = = = = = = ");

        OptionalDouble avgWorkloadAllPodsOpt = podMetricsList.stream()
                .flatMap(pm -> pm.metrics.stream())
                .mapToDouble(a -> a).average();

        if (avgWorkloadAllPodsOpt.isEmpty()) {
            LOGGER.error("Can't write predictionForNow : average workload calculation error");
            return;
        }

        double avgWorkloadAllPods = avgWorkloadAllPodsOpt.getAsDouble();
        LOGGER.info("AVERAGE WORKLOAD ALL PODS = {}", showValue(avgWorkloadAllPods));

        if (config.isProactive()) {
            writePredictionStats(avgWorkloadAllPods);

            Set<Double> avgPredictions = podMetricsList.stream()
                    .map(pm -> predictorService.averagePrediction(config.getForecastFor(), pm.metrics))
                    .collect(Collectors.toSet());
            double avgPrediction = avgPredictions.stream().mapToDouble(a -> a).average().getAsDouble();
            LOGGER.info("AVERAGE PREDICTION FOR {} PODS = {}", podNames.size(), showValue(avgPrediction));
            measurementsRepo.writePrediction(APP_NAME, avgPrediction);
            scaleTask(avgPrediction);

        } else {
            scaleTask(avgWorkloadAllPods);
        }
    }

    private void writePredictionStats(double averageWorkload) {
        List<WorkloadPredictionPoint> lastPredictions = measurementsRepo
                .getLastWorkloadPredictions(config.getPredictionForNow());
        if (lastPredictions.size() == config.getPredictionForNow()) {
            double predictionForNow = lastPredictions.get(0).getCpu();
            LOGGER.info("Prediction stats. ESTIMATED={}, REAL={}", predictionForNow, showValue(averageWorkload));
            measurementsRepo.writeCurrentPrediction(APP_NAME, averageWorkload, predictionForNow);
        } else {
            LOGGER.error("Can't write predictionForNow : there no prediction for current moment");
        }
    }

    private static void setIgnoreScaling() {
        ignoreScaling = config.getTreshHoldAfterScaling();
    }

    private static boolean ignoreScaling() {
        return ignoreScaling-- > 0;
    }

    private void scaleTask(double averageResult) {

        if (ignoreScaling()) {
            LOGGER.info("Ignoring scaling");
            return;
        }

        boolean scaled = false;
        if (shouldScaleUp(averageResult)) {
            LOGGER.info("Avg result {}% > {}%", showValue(averageResult), config.getScaleUpTreshold());
            scaled = kubService.scaleUp();
        } else if (shouldScaleDown(averageResult)) {
            LOGGER.info("Avg result {}% < {}%", showValue(averageResult), config.getScaleDownTreshold());
            scaled = kubService.scaleDown();
        } else {
            LOGGER.info("Avg result {}%, no need to scale", showValue(averageResult));
        }

        if (scaled) {
            setIgnoreScaling();
        }
    }

    private boolean shouldScaleUp(double predictedWorkload) {
        return predictedWorkload > config.getScaleUpTreshold();
    }

    private boolean shouldScaleDown(double predictedWorkload) {
        return predictedWorkload < config.getScaleDownTreshold();
    }

}
