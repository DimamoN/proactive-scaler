package com.dimamon.Entity.measurements;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.time.Instant;

@Measurement(name = "connection")
public class ConnectionPoint {

    @Column(name = "time")
    private Instant time;

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
                ", id=" + id +
                ", method='" + method + '\'' +
                '}';
    }
}
