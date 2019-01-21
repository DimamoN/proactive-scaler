package com.dimamon.service;


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

    static class ScalerConfig {

        /**
         * true - proactive, false - reactive
         */
        boolean proactive;

        /**
         * 1 unit is 10 seconds, so 6 * 5 = 1 min
         */
        int forecastFor; // in scale tasks (only for proactive)
        int forecastBasedOn; // in scale tasks

        int treshHoldAfterScaling = 1; // how many scale tasks will be ignored

        int predictionForNow; // in scale tasks  (forecastFor / checkEvery) / 2

        int scaleUpTreshold; // in percents
        int scaleDownTreshold; // in percents

        ScalerConfig() {
        }

        String proactiveString() {
            return this.proactive ? "proactive" : "reactive";
        }

        void setProactive(boolean proactive) {
            this.proactive = proactive;
        }

        void setScaleTresholds(int scaleUpTreshold, int scaleDownTreshold) {
            this.scaleUpTreshold = scaleUpTreshold;
            this.scaleDownTreshold = scaleDownTreshold;
        }

        void setForecastingParams(int forecastFor, int forecastBasedOn) {
            this.forecastFor = forecastFor;
            this.forecastBasedOn = forecastBasedOn;
        }

        void setPredictionForNow(int predictionForNow) {
            this.predictionForNow = predictionForNow;
        }
    }

    private static ScalerConfig proactiveClassic = new ScalerConfig();
    {
        proactiveClassic.setProactive(true);
        proactiveClassic.setScaleTresholds(80, 25);
        proactiveClassic.setForecastingParams(6, 12);
        proactiveClassic.setPredictionForNow(1); // 30 sec
    }

    private static ScalerConfig reactiveClassic = new ScalerConfig();
    {
        reactiveClassic.setProactive(false);
        reactiveClassic.setScaleTresholds(80, 25);
        reactiveClassic.setForecastingParams(6, 12);
        reactiveClassic.setPredictionForNow(1); // 30 sec
    }

    private static ScalerConfig config = proactiveClassic;

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

        List<Double> cpuMeasurements = measurementsRepo.getLastLoadMetrics(config.forecastBasedOn)
                .stream().map(WorkloadPoint::getPodCpu)
                .collect(Collectors.toList());
        OptionalDouble averageWorkload = cpuMeasurements.stream().mapToDouble(a -> a).average();

        if (config.proactive) {
            if (averageWorkload.isPresent()) {
                writePredictionStats(averageWorkload.getAsDouble());
            } else {
                LOGGER.error("Can't write predictionForNow : average workload calculation error");
            }
            double avgPrediction = predictorService.averagePrediction(config.forecastFor, cpuMeasurements);
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
                .getLastWorkloadPredictions(config.predictionForNow);
        if (lastPredictions.size() == config.predictionForNow) {
            double predictionForNow = lastPredictions.get(0).getCpu();
            LOGGER.info("Prediction stats. ESTIMATED={}, REAL={}", predictionForNow, averageWorkload);
            measurementsRepo.writeCurrentPrediction(APP_NAME, averageWorkload, predictionForNow);
        } else {
            LOGGER.error("Can't write predictionForNow : there no prediction for current moment");
        }
    }

    private static void setIgnoreScaling() {
        ignoreScaling = config.treshHoldAfterScaling;
    }

    private static boolean ignoreScaling() {
        return ignoreScaling-- > 0;
    }

    private void scaleTask(double averageResult) {

        if (ignoreScaling()) {
            LOGGER.info("Ignoring scaling");
            return;
        }

        if (shouldScaleUp(averageResult)) {
            LOGGER.info("Avg result {}% > {}%", showValue(averageResult), config.scaleUpTreshold);
            boolean scaled = kubernetesService.scaleUpService();
            if (scaled) {
                setIgnoreScaling();
            }
        } else if (shouldScaleDown(averageResult)) {
            LOGGER.info("Avg result {}% < {}%", showValue(averageResult), config.scaleDownTreshold);
            boolean scaled = kubernetesService.scaleDownService();
            if (scaled) {
                setIgnoreScaling();
            }
        } else {
            LOGGER.info("Avg result {}%, no need to scale", showValue(averageResult));
        }
    }

    private boolean shouldScaleUp(double predictedWorkload) {
        return predictedWorkload > config.scaleUpTreshold;
    }

    private boolean shouldScaleDown(double predictedWorkload) {
        return predictedWorkload < config.scaleDownTreshold;
    }

}
