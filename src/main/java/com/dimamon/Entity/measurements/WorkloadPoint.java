package com.dimamon.Entity.measurements;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.time.Instant;

@Measurement(name = "workload")
public class WorkloadPoint {

    @Column(name = "time")
    private Instant time;

    @Column(name = "cpu")
    private Double cpu;

    @Column(name = "free_ram")
    private Integer freeRam;

    @Column(name = "instanceName")
    private String instanceName;

    public WorkloadPoint() {
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public Double getCpu() {
        return cpu;
    }

    public void setCpu(Double cpu) {
        this.cpu = cpu;
    }

    public Integer getFreeRam() {
        return freeRam;
    }

    public void setFreeRam(Integer freeRam) {
        this.freeRam = freeRam;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    @Override
    public String toString() {
        return "WorkloadPoint{" +
                "time=" + time +
                ", cpu=" + cpu +
                ", freeRam=" + freeRam +
                ", instanceName='" + instanceName + '\'' +
                '}';
    }
}
