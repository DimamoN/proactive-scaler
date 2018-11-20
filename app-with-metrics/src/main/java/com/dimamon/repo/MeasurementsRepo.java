package com.dimamon.repo;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;


/**
 * Sending metrics to InfluxDB
 * Created by dimamon on 11/16/16.
 */
@Repository
@Qualifier("Influx")
public class MeasurementsRepo {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeasurementsRepo.class);

    private InfluxDB influxDB;

    @Value("${db.username}")
    private String username;

    @Value("${db.password}")
    private String password;

    @Value("${db.port}")
    private int port;

    @Value("${db.name}")
    private String dbName;

    @Value("${db.url}")
    private String databaseUrl;

    @PostConstruct
    private void init() {
        final String url = "http://" + databaseUrl + ":" + port;
        LOGGER.info("Attempting to connect to: {}", url);
        this.influxDB = InfluxDBFactory.connect(url, username, password);
    }

    public void measureConnection(int id, final String method) {
        BatchPoints batchPoints = getBatchPoints();
        Point point = Point.measurement("connection")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("id", id)
                .addField("method", method)
                .build();
        batchPoints.point(point);
        this.write(batchPoints);
    }


    public void measureLoad(final String instanceName, double cpuLoad, long freeMemory) {
        BatchPoints batchPoints = getBatchPoints();
        Point point = Point.measurement("workload")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("instanceName", instanceName)
                .addField("cpu", cpuLoad)
                .addField("free_ram", freeMemory)
                .build();
        batchPoints.point(point);
        this.write(batchPoints);
    }

    private BatchPoints getBatchPoints() {
        return BatchPoints
                .database(dbName)
                .retentionPolicy("autogen")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();
    }

    private void write(final BatchPoints batchPoints) {
        influxDB.write(batchPoints);
    }
}
