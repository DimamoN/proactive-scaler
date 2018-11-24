package com.dimamon.service;

import com.sun.management.OperatingSystemMXBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;

@Service
public class ResourcesService {

    private OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);


    static final String TOP_COMMAND = "top -bn1 | grep \"Cpu(s)\" | \\\n" +
            "           sed \"s/.*, *\\([0-9.]*\\)%* id.*/\\1/\" | \\\n" +
            "           awk '{print 100 - $1\"%\"}'";
    public String getCPUloadNative() {
        StringBuilder result = new StringBuilder("");

        String s;
        Process p;
        try {
            p = Runtime.getRuntime().exec(TOP_COMMAND);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null) {
                System.out.println("line: " + s);

                result.append("line ").append(s).append("\n");
            }
            p.waitFor();

            System.out.println ("exit: " + p.exitValue());
            result.append("exit: " + p.exitValue());

            p.destroy();
        } catch (Exception e) {}

        return result.toString();
    }


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
