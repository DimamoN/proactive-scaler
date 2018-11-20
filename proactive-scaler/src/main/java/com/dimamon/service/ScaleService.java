package com.dimamon.service;


import com.dimamon.entities.WorkloadPoint;
import com.dimamon.repo.MeasurementsRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Periodically check database with metrics to make workload prediction and make scaling decision
 */
@Service
public class ScaleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScaleService.class);

    private static final int INITIAL_DELAY = 10 * 1000;
    private static final int CHECK_EVERY = 30 * 1000;

    @Autowired
    private MeasurementsRepo measurementsRepo;

    @Scheduled(initialDelay = INITIAL_DELAY, fixedDelay = CHECK_EVERY)
    public void checkMetrics() {
        LOGGER.info("### Checking metrics task");
        List<WorkloadPoint> allMeasurements = measurementsRepo.getLoadMetrics();
        LOGGER.info(allMeasurements.toString());

        // todo: predict workload
        // todo: Kubernetes service
    }

}
