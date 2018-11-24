package com.dimamon.repo;

import com.dimamon.utils.StringUtils;
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

    @Value("${db.store_metrics}")
    private String storeMetrics;

    @Value("${db.metricsEnabled}")
    private Boolean metricsEnabled;

    private static final String RETENTION_POLICY = "defaultPolicy";

    @PostConstruct
    private void init() {

        if (!metricsEnabled) {
            return;
        }

        final String url = "http://" + databaseUrl + ":" + port;
        LOGGER.info("Attempting connect to influxDB at {}", url);

        this.influxDB = InfluxDBFactory.connect(url, username, password);

        // create db if not exists
        if (!influxDB.databaseExists(dbName)) {
            influxDB.createDatabase(dbName);
            influxDB.createRetentionPolicy(RETENTION_POLICY, dbName, storeMetrics, 1, true);
            influxDB.setRetentionPolicy(RETENTION_POLICY);
            influxDB.setDatabase(dbName);
        }
    }

    public void measureConnection(int id, final String method) {
        if (!metricsEnabled) {
            return;
        }
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
        if (!metricsEnabled) {
            return;
        }
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

    public void measureJVMLoad(final String instanceName, long freeMemory, long totalMemory, long maxMemory) {
        if (!metricsEnabled) {
            return;
        }
        BatchPoints batchPoints = getBatchPoints();
        Point point = Point.measurement("workload_jvm")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("instanceName", instanceName)
                .addField("free_ram", freeMemory)
                .addField("total_ram", totalMemory)
                .addField("max_ram", maxMemory)
                .build();
        batchPoints.point(point);
        this.write(batchPoints);
    }

    private BatchPoints getBatchPoints() {
        return BatchPoints
                .database(dbName)
                .retentionPolicy(RETENTION_POLICY)
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();
    }

    private void write(final BatchPoints batchPoints) {
        influxDB.write(batchPoints);
    }
}
