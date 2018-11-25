package com.dimamon.entities;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.time.Instant;

@Measurement(name = "workload_jvm")
public class WorkloadJVMPoint {

    @Column(name = "time")
    private Instant time;

    @Column(name = "instanceName")
    private String instanceName;

    @Column(name = "free_ram")
    private Integer freeRam;

    @Column(name = "total_ram")
    private Integer totalRam;

    @Column(name = "max_ram")
    private Integer maxRam;

    public WorkloadJVMPoint() {
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public Integer getFreeRam() {
        return freeRam;
    }

    public void setFreeRam(Integer freeRam) {
        this.freeRam = freeRam;
    }

    public Integer getTotalRam() {
        return totalRam;
    }

    public void setTotalRam(Integer totalRam) {
        this.totalRam = totalRam;
    }

    public Integer getMaxRam() {
        return maxRam;
    }

    public void setMaxRam(Integer maxRam) {
        this.maxRam = maxRam;
    }

    @Override
    public String toString() {
        return "WorkloadJVMPoint{" +
                "time=" + time +
                ", instanceName='" + instanceName + '\'' +
                ", freeRam=" + freeRam +
                ", totalRam=" + totalRam +
                ", maxRam=" + maxRam +
                '}';
    }
}
