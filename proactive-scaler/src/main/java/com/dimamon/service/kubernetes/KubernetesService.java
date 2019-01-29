package com.dimamon.service.kubernetes;

import java.util.Set;

public interface KubernetesService {

    /**
     * @return true - successful scale up
     */
    boolean scaleUp();

    /**
     * @return true - successful scale down
     */
    boolean scaleDown();

    void checkPods();

    int getMetricsPodCount();

    int getMetricsPodReadyCount();

    Set<String> getPodNames();
}
