package com.dimamon.Service;

import com.sun.management.OperatingSystemMXBean;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;

@Service
public class ResourcesService {

    private OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

    public double getCpuLoad() {
        return osBean.getSystemCpuLoad();
    }

}
