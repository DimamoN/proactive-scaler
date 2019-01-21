package com.dimamon.service.kubernetes;

public interface KubernetesService {

    /**
     * @return true - successful scale up
     */
    boolean scaleUpService();

    /**
     * @return true - successful scale down
     */
    boolean scaleDownService();

    void checkPods();

    int getMetricsPodCount();

    int getMetricsPodReadyCount();
}
