package com.dimamon.service.kubernetes;

public interface KubernetesService {

    void scaleService(final String serviceName, int instanceCount);

}
