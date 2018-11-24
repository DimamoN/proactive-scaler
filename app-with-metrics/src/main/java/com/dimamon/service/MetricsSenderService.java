package com.dimamon.service;

import com.dimamon.repo.MeasurementsRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static com.dimamon.utils.StringUtils.stringify;

@Service
public class MetricsSenderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsSenderService.class);

    private static final int INITIAL_DELAY = 10 * 1000;
    private static final int SEND_LOAD_EVERY = 3 * 1000;

    @Autowired
    private ResourcesService resourcesService;

    @Autowired
    private MeasurementsRepo measurementsRepo;

    @Scheduled(initialDelay = INITIAL_DELAY, fixedDelay = SEND_LOAD_EVERY)
    public void sendMetrics() {
        double cpuLoad = resourcesService.getCpuLoad();
        double cpuLoadProcess = resourcesService.getProcessCpuLoad();
        long freeMemory = resourcesService.getFreeMemory();
        long totalMemory = resourcesService.getTotalMemory();

        LOGGER.info("# # # #");
        LOGGER.info("OS STATS  # cpuLoad={} processLoad={} free_ram={} total_ram={}",
                stringify(cpuLoad), stringify(cpuLoadProcess), freeMemory, totalMemory);

        long freeJVMMem = resourcesService.getFreeJVMMemory();
        long maxJVMMem = resourcesService.getMaxJVMMemory();
        long totalJVMMem = resourcesService.getTotalJVMMemory();
        int availableProcessors = resourcesService.getAvailableProcessors();

        LOGGER.info("JVM STATS # free/total/max jvm_ram=[{}/{}/{}]",
                freeJVMMem, totalJVMMem, maxJVMMem);

        measurementsRepo.measureLoad("app1", cpuLoad, freeMemory, totalMemory);
        measurementsRepo.measureJVMLoad("app1", freeJVMMem, totalJVMMem, maxJVMMem);
    }

}
