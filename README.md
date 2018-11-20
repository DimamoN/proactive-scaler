# SpringBoot_influxDB
SpringBoot + influxDB + grafana

There are two spring-boot apps here:
* app-with-metrics
    sends CPU and RAM metrics to local influx db
* proactive-scaler
    reads metrics from influx db 

### How to run (method 1):

* start influx db locally
```bash
docker run -p 8086:8086 \
           -v influxdb:/var/lib/influxdb \
              influxdb
```

* create database *for_grafana*
```bash
$ influx
> create database for_grafana
```

### How to run (method 2):
* create docker volume for data persistent
```
docker volume create --name=influxdb
```

* start docker-compose
```bash
$ docker-compose up
```

### Queries for grafana:
```
SELECT count("id") FROM "connection" WHERE $timeFilter GROUP BY time(1s) fill(null)
SELECT mean("cpu") FROM "connection" WHERE $timeFilter GROUP BY time(1s) fill(null)
```

<h3>Data in InfluxDB</h3>
<img src="https://pp.vk.me/c638331/v638331767/bfde/QnsfkyVDEGg.jpg" alt="influx" />

<h3>Dashboard in Grafana</h3>
<img src="https://pp.vk.me/c638331/v638331767/bfd6/CCIxqKysD8U.jpg" alt="grafana" />

