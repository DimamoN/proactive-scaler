package com.dimamon.repo;

import com.dimamon.entities.WorkloadPoint;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;


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

    @PostConstruct
    private void init() {
        final String url = "http://" + databaseUrl + ":" + port;
        LOGGER.info("Attempting to connect to: {}", url);
        this.influxDB = InfluxDBFactory.connect(url, username, password);
    }

    public List<WorkloadPoint> getLoadMetrics() {
        QueryResult queryResult = influxDB.query(new Query("select * from workload", dbName));
        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
        return resultMapper.toPOJO(queryResult, WorkloadPoint.class);
    }

}
