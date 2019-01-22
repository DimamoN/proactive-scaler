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

import java.util.Date;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import static com.dimamon.utils.StringUtils.showValue;


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
    private KubernetesService kubernetesService;


    @Scheduled(initialDelay = INITIAL_DELAY, fixedDelay = CHECK_EVERY)
    public void checkMetrics() {
        LOGGER.info("### Checking metrics task = {} | {}", new Date(), config.proactiveString());
        kubernetesService.checkPods();
        measurementsRepo.writePodCount(APP_NAME,
                kubernetesService.getMetricsPodCount(), kubernetesService.getMetricsPodReadyCount());

        List<Double> cpuMeasurements = measurementsRepo.getLastLoadMetrics(config.getForecastBasedOn())
                .stream().map(WorkloadPoint::getPodCpu)
                .collect(Collectors.toList());
        OptionalDouble averageWorkload = cpuMeasurements.stream().mapToDouble(a -> a).average();

        if (config.isProactive()) {
            if (averageWorkload.isPresent()) {
                writePredictionStats(averageWorkload.getAsDouble());
            } else {
                LOGGER.error("Can't write predictionForNow : average workload calculation error");
            }
            double avgPrediction = predictorService.averagePrediction(config.getForecastFor(), cpuMeasurements);
            measurementsRepo.writePrediction(APP_NAME, avgPrediction);
            scaleTask(avgPrediction);

        } else {
            if (averageWorkload.isPresent()) {
                scaleTask(averageWorkload.getAsDouble());
            } else {
                LOGGER.error("Can't calculate average and do scale task");
            }
        }
    }

    private void writePredictionStats(double averageWorkload) {
        List<WorkloadPredictionPoint> lastPredictions = measurementsRepo
                .getLastWorkloadPredictions(config.getPredictionForNow());
        if (lastPredictions.size() == config.getPredictionForNow()) {
            double predictionForNow = lastPredictions.get(0).getCpu();
            LOGGER.info("Prediction stats. ESTIMATED={}, REAL={}", predictionForNow, averageWorkload);
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
            scaled = kubernetesService.scaleUpService();
        } else if (shouldScaleDown(averageResult)) {
            LOGGER.info("Avg result {}% < {}%", showValue(averageResult), config.getScaleDownTreshold());
            scaled = kubernetesService.scaleDownService();
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
