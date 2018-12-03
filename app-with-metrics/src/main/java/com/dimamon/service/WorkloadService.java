package com.dimamon.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class WorkloadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkloadService.class);

    @Value("${workload.basic_cpu}")
    private long BASIC_CPU_WORK;

    @Value("${workload.basic_ram}")
    private long BASIC_RAM_WORK;

    private List<String> tmpStrings = new LinkedList<>();

    public void loadCpu(Integer power) {
        long iterations = BASIC_CPU_WORK * power;
        LOGGER.info("Get cpu load task: {} = {} iterations", power, iterations);
        int tmp = 0;
        double sin = 0, cos = 0;
        for (long i = 0; i < iterations; i++) {
            tmp++;
            sin = Math.sin(i);
            cos = Math.cos(1 - i);
        }
        tmp = 0;
        double res = sin + cos;
    }

    public void loadRam(Integer power) {
        long iterations = BASIC_RAM_WORK * power;
        LOGGER.info("Get ram load task: {} = {} iterations", power, iterations);
        for (int i = 0; i < iterations ; i++) {
            tmpStrings.add(new Date().toString());
        }
    }

    public void clearRam() {
        LOGGER.info("Clearing ram");
        tmpStrings.clear();
     }

}
