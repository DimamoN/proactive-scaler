package com.dimamon.service.kubernetes;

import org.springframework.stereotype.Service;

@Service
public class KubernetesServiceImpl implements KubernetesService {
    @Override
    public void scaleService(String serviceName, int instanceCount) {
        //todo
    }
}
