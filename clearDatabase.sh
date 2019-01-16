#!/usr/bin/env bash
influx -database 'for_grafana' -execute 'drop series from workload, workload_jvm, workload_prediction, connection, pods, prediction_stats'