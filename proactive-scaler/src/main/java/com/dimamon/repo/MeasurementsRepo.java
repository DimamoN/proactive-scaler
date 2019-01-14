package com.dimamon.repo;

import com.dimamon.entities.WorkloadJVMPoint;
import com.dimamon.entities.WorkloadPoint;
import com.dimamon.entities.WorkloadPredictionPoint;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Reads metrics from InfluxDB
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

    private static final String RETENTION_POLICY = "defaultPolicy";

    @PostConstruct
    private void init() {
        final String url = "http://" + databaseUrl + ":" + port;
        LOGGER.info("Attempting to connect to: {}", url);
        this.influxDB = InfluxDBFactory.connect(url, username, password);
    }

    public void writePrediction(final String instanceName, double cpuLoad) {
        BatchPoints batchPoints = getBatchPoints();
        Point point = Point.measurement("workload_prediction")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("instanceName", instanceName)
                .addField("cpu", cpuLoad)
                .build();
        batchPoints.point(point);
        influxDB.write(batchPoints);
    }

    public void writePodCount(final String instanceName, int podCount) {
        BatchPoints batchPoints = getBatchPoints();
        Point point = Point.measurement("pods")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("savedFrom", instanceName)
                .addField("podCount", podCount)
                .build();
        batchPoints.point(point);
        influxDB.write(batchPoints);
    }

    /**
     * @param instanceName
     * @param currentMediumWorkload - medium workload
     * @param predictionForNow - from past for current time moment
     */
    public void writeCurrentPrediction(final String instanceName,
                                       double currentMediumWorkload,
                                       double predictionForNow) {
        BatchPoints batchPoints = getBatchPoints();
        Point point = Point.measurement("prediction_stats")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("savedFrom", instanceName)
                .addField("currentMediumWorkload", currentMediumWorkload)
                .addField("predictionForNow", predictionForNow)
                .build();
        batchPoints.point(point);
        influxDB.write(batchPoints);
    }

    public List<WorkloadPoint> getLoadMetrics() {
        QueryResult queryResult = influxDB.query(new Query("select * from workload", dbName));
        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
        return resultMapper.toPOJO(queryResult, WorkloadPoint.class);
    }

    /**
     * @param count - last metrics
     * @return list with last "count" metrics
     */
    public List<WorkloadPoint> getLastLoadMetrics(int count) {
        String query = "SELECT * FROM workload ORDER BY time DESC LIMIT " + count;
        QueryResult queryResult = influxDB.query(new Query(query, dbName));
        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
        List<WorkloadPoint> workloadPoints = resultMapper.toPOJO(queryResult, WorkloadPoint.class);
        Collections.reverse(workloadPoints);
        return workloadPoints;
    }

    public List<WorkloadJVMPoint> getLoadJVMMetrics() {
        QueryResult queryResult = influxDB.query(new Query("select * from workload_jvm", dbName));
        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
        return resultMapper.toPOJO(queryResult, WorkloadJVMPoint.class);
    }

    /**
     * @param count - last workload predictions
     * @return list with last "count" predictions
     */
    public List<WorkloadPredictionPoint> getLastWorkloadPredictions(int count) {
        String query = "SELECT * FROM workload_prediction ORDER BY time DESC LIMIT " + count;
        QueryResult queryResult = influxDB.query(new Query(query, dbName));
        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
        List<WorkloadPredictionPoint> workloadPoints = resultMapper.toPOJO(queryResult, WorkloadPredictionPoint.class);
        Collections.reverse(workloadPoints);
        return workloadPoints;
    }

    private BatchPoints getBatchPoints() {
        return BatchPoints
                .database(dbName)
                .retentionPolicy(RETENTION_POLICY)
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();
    }

}
