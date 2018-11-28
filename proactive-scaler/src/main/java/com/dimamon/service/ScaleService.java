package com.dimamon.service;


import com.dimamon.entities.WorkloadPoint;
import com.dimamon.repo.MeasurementsRepo;
import com.dimamon.service.kubernetes.KubernetesService;
import com.dimamon.service.predict.PredictorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Periodically check database with metrics to make workload prediction and make scaling decision
 */
@Service
public class ScaleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScaleService.class);

    private static final int INITIAL_DELAY = 10 * 1000;
    private static final int CHECK_EVERY = 30 * 1000;

    /**
     * 1 unit is 10 seconds, so 6 * 5 = 1 min
     */
    private static final int FORECAST_FOR = 6 * 5; // 5 minutes

    private static final int SCALE_THRESHOLD = 80;

    @Autowired
    private MeasurementsRepo measurementsRepo;

    @Qualifier("desPredictor")
    @Autowired
    private PredictorService predictorService;

    @Autowired
    private KubernetesService kubernetesService;

    @Scheduled(initialDelay = INITIAL_DELAY, fixedDelay = CHECK_EVERY)
    public void checkMetrics() {
        LOGGER.info("### Checking metrics task");
        List<WorkloadPoint> allMeasurements = measurementsRepo.getLoadMetrics();
        LOGGER.info(allMeasurements.toString());

        // in progress: predict workload
        List<Double> cpuMeasurements = allMeasurements.stream()
                .map(WorkloadPoint::getCpu)
                .collect(Collectors.toList());

        double avgPredictedWorkload = predictorService.averagePrediction(FORECAST_FOR, cpuMeasurements);
        LOGGER.info("### AVERAGE PREDICTION = {}", avgPredictedWorkload);
        measurementsRepo.writePrediction("all", avgPredictedWorkload);

        if (shouldScale(avgPredictedWorkload)) {
            LOGGER.info("### SCALING UP !!! ###");
        }
    }

    private boolean shouldScale(double predictedWorkload) {
        return predictedWorkload > SCALE_THRESHOLD;
    }

}
