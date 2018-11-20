package com.dimamon.service;

import com.dimamon.repo.MeasurementsRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class MetricsSenderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsSenderService.class);

    private static final int INITIAL_DELAY = 10 * 1000;
    private static final int SEND_LOAD_EVERY = 10 * 1000;

    @Autowired
    private ResourcesService resourcesService;

    @Autowired
    private MeasurementsRepo measurementsRepo;

    @Scheduled(initialDelay = INITIAL_DELAY, fixedDelay = SEND_LOAD_EVERY)
    public void sendMetrics() {
        LOGGER.info("Sending metrics");
        measurementsRepo.measureLoad("app1", resourcesService.getCpuLoad(), resourcesService.getFreeMemory());
    }

}
