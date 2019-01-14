package com.dimamon.entities;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.time.Instant;

@Measurement(name = "workload_prediction")
public class WorkloadPredictionPoint {

    @Column(name = "time")
    private Instant time;

    @Column(name = "instanceName")
    private String instanceName;

    @Column(name = "cpu")
    private Integer cpu;

    public WorkloadPredictionPoint() {
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

    public Integer getCpu() {
        return cpu;
    }

    public void setCpu(Integer cpu) {
        this.cpu = cpu;
    }

    @Override
    public String toString() {
        return "WorkloadPredictionPoint{" +
                "time=" + time +
                ", instanceName='" + instanceName + '\'' +
                ", cpu=" + cpu +
                '}';
    }
}
