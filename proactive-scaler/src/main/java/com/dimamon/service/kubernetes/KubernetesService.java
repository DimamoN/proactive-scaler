package com.dimamon.service.kubernetes;

public interface KubernetesService {

    void scaleUpService();

    void scaleDownService();

    void checkPods();

    int getMetricsPodCount();
}
