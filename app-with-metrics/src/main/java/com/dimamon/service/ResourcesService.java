package com.dimamon.service;

import com.sun.management.OperatingSystemMXBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;

@Service
public class ResourcesService {

    /**
     * @link https://docs.oracle.com/javase/7/docs/jre/api/management/extension/com/sun/management/OperatingSystemMXBean.html
     */
    private OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

    /// SYSTEM ///

    /**
     * @return CPU load in %
     */
    public double getCpuLoad() {
        return osBean.getSystemCpuLoad() * 100;
    }

    public double getProcessCpuLoad() {
        return osBean.getProcessCpuLoad() * 100;
    }

    public long getFreeMemory() {
        return osBean.getFreePhysicalMemorySize() / (1024 * 1024);
    }

    public long getTotalMemory() {
        return osBean.getTotalPhysicalMemorySize() / (1024 * 1024);
    }

    /// JVM ///

    /**
     * @return free JVM RAM in MB
     */
    public long getFreeJVMMemory() {
        return Runtime.getRuntime().freeMemory() / (1024 * 1024);
    }

    public long getMaxJVMMemory() {
        return Runtime.getRuntime().maxMemory() / (1024 * 1024);
    }

    public long getTotalJVMMemory() {
        return Runtime.getRuntime().totalMemory() / (1024 * 1024);
    }

    public int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

}
