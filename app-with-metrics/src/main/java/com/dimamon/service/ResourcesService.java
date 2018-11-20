package com.dimamon.service;

import com.sun.management.OperatingSystemMXBean;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;

@Service
public class ResourcesService {

    private OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

    /**
     * @return CPU load in %
     */
    public double getCpuLoad() {
        return osBean.getSystemCpuLoad() * 100;
    }

    /**
     * @return free RAM in MB
     */
    public long getFreeMemory() {
        return Runtime.getRuntime().freeMemory() / (1024 * 1024);
    }

}
