package com.dimamon.Entity;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.time.Instant;

@Measurement(name = "connection")
public class ConnectionPoint {

    @Column(name = "time")
    private Instant time;

    // todo: move to separate measurement
    @Column(name = "cpu")
    private Double cpu;
    @Column(name = "free_ram")
    private Integer freeRam;

    @Column(name = "id")
    private Integer id;
    @Column(name = "method")
    private String method;

    public ConnectionPoint() {}

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "ConnectionPoint{" +
                "time=" + time +
                ", cpu=" + cpu +
                ", freeRam=" + freeRam +
                ", id=" + id +
                ", method='" + method + '\'' +
                '}';
    }
}
