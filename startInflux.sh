#!/usr/bin/env bash
docker run -p 8086:8086 \
           -v influxdb:/var/lib/influxdb \
              influxdb