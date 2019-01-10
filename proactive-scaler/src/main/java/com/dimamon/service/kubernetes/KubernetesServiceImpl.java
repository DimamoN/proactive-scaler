package com.dimamon.service.kubernetes;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class KubernetesServiceImpl implements KubernetesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesServiceImpl.class);

    /**
     * Application name in kubernetes (pod names starts from this)
     */
    private String podPrefix = "metrics-app";
    private String deploymentName = "metrics-app";

    private String namespace = "default";

    /**
     * Max pods count
     */
    private static final int MAX_POD_COUNT = 3;

    private int metricsPodCount;

    public void checkPods() {

        AtomicInteger podCount = new AtomicInteger();
        List<String> foundPods = new ArrayList<>();

        try (final KubernetesClient client = new DefaultKubernetesClient()) {
            client.pods().list().getItems().forEach(pod -> {
                if (pod.getMetadata().getNamespace().equals("default")) {
                    if (pod.getMetadata().getName().startsWith(podPrefix)) {
                        podCount.getAndIncrement();
                        foundPods.add(pod.getMetadata().getName());
                    }
                }
            });
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        metricsPodCount = podCount.get();
        LOGGER.info("There are {} {} pods = {}", metricsPodCount, podPrefix, foundPods.toString());
    }

    @Override
    public void scaleUpService() {
        int scaleTo = metricsPodCount + 1;
        if (scaleTo > MAX_POD_COUNT) {
            LOGGER.warn("Can't scale more, limit is {}", MAX_POD_COUNT);
            return;
        }
        LOGGER.info("Attempting to scale up {} service to {} instances", deploymentName, scaleTo);
        scaleDeployment(scaleTo);
    }

    @Override
    public void scaleDownService() {
        int scaleTo = metricsPodCount - 1;
        if (scaleTo <= 0) {
            return;
        }
        LOGGER.info("Attempting to scale down {} service to {} instances", deploymentName, scaleTo);
        scaleDeployment(scaleTo);
    }

    @Override
    public int getMetricsPodCount() {
        return this.metricsPodCount;
    }

    private void scaleDeployment(int replicas) {
        try (final KubernetesClient client = new DefaultKubernetesClient()) {
            client.extensions().deployments()
                    .inNamespace(namespace).withName(deploymentName)
                    .edit().editSpec().withReplicas(replicas).endSpec().done();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
