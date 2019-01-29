package com.dimamon.service.kubernetes;

import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private int metricsPodReadyCount;
    private Set<String> podNames = new HashSet<>();

    public void checkPods() {
        AtomicInteger podCount = new AtomicInteger();
        AtomicInteger podReadyCount = new AtomicInteger();
        Set<String> foundPods = new HashSet<>();

        try (final KubernetesClient client = new DefaultKubernetesClient()) {
            client.pods().list().getItems().forEach(pod -> {
                if (pod.getMetadata().getNamespace().equals("default")) {
                    final String podName = pod.getMetadata().getName();
                    if (podName.startsWith(podPrefix)) {
                        List<ContainerStatus> containerStatuses = pod.getStatus().getContainerStatuses();
                        if (!containerStatuses.isEmpty() && containerStatuses.get(0).getReady()) {
                            podReadyCount.incrementAndGet();
                        }
                        podCount.getAndIncrement();
                        foundPods.add(podName);
                    }
                }
            });
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        this.metricsPodCount = podCount.get();
        this.metricsPodReadyCount = podReadyCount.get();
        this.podNames = foundPods;

        LOGGER.info("There are {}(ready)/{}(all) {} pods = {}",
                metricsPodReadyCount, metricsPodCount, podPrefix, foundPods.toString());
    }

    @Override
    public boolean scaleUp() {
        int scaleTo = metricsPodCount + 1;
        if (scaleTo > MAX_POD_COUNT) {
            LOGGER.warn("Can't scale more, limit is {}", MAX_POD_COUNT);
            return false;
        }
        LOGGER.info("Attempting to scale up {} service to {} instances", deploymentName, scaleTo);
        scaleDeployment(scaleTo);
        return true;
    }

    @Override
    public boolean scaleDown() {
        int scaleTo = metricsPodCount - 1;
        if (scaleTo <= 0) {
            return false;
        }
        LOGGER.info("Attempting to scale down {} service to {} instances", deploymentName, scaleTo);
        scaleDeployment(scaleTo);
        return true;
    }

    @Override
    public int getMetricsPodCount() {
        return this.metricsPodCount;
    }

    @Override
    public int getMetricsPodReadyCount() {
        return this.metricsPodReadyCount;
    }

    @Override
    public Set<String> getPodNames() {
        return this.podNames;
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
