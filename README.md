# SpringBoot_influxDB
SpringBoot + influxDB + grafana

* start influx db locally
* create database *for_grafana*
```bash
$ influx
> create database for_grafana
```

* queries for grafana:
```
SELECT count("id") FROM "connection" WHERE $timeFilter GROUP BY time(1s) fill(null)
SELECT mean("cpu") FROM "connection" WHERE $timeFilter GROUP BY time(1s) fill(null)
```

<h3>Data in InfluxDB</h3>
<img src="https://pp.vk.me/c638331/v638331767/bfde/QnsfkyVDEGg.jpg" alt="influx" />

<h3>Dashboard in Grafana</h3>
<img src="https://pp.vk.me/c638331/v638331767/bfd6/CCIxqKysD8U.jpg" alt="grafana" />

