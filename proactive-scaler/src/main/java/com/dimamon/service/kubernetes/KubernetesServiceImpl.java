package com.dimamon.service.kubernetes;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class KubernetesServiceImpl implements KubernetesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KubernetesServiceImpl.class);

    /**
     * Application name in kubernetes (pod names starts from this)
     */
    private String prefix = "metrics-app";

    /**
     * Max pods count
     */
    private int maxPodCount = 3;

    // get pod count!
    private int metricsPodCount;

    public void checkPods() {

        AtomicInteger podCount = new AtomicInteger();

        try (final KubernetesClient client = new DefaultKubernetesClient()) {
            client.pods().list().getItems().forEach(pod -> {
                if (pod.getMetadata().getNamespace().equals("default")) {
                    if (pod.getStatus().getPhase().equals("Running")) {
                        LOGGER.info("RUNNING POD FOUND: name: {}", pod.getMetadata().getName());
                    }
                    if (pod.getMetadata().getName().startsWith(prefix))
                    {
                        podCount.getAndIncrement();
                    }
                }
            });
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        metricsPodCount = podCount.get();
        LOGGER.info("There are {} {} pods", metricsPodCount, prefix);
    }

    @Override
    public void scaleUpService() {
        int scaleTo = metricsPodCount + 1;
        if (scaleTo > maxPodCount) {
            LOGGER.warn("Can't scale more, limit is {}", scaleTo);
            return;
        }
        LOGGER.info("Attempting to scale {} service to {} instances", prefix, scaleTo);
        //todo
    }

    @Override
    public void scaleDownService() {
        int scaleTo = metricsPodCount - 1;
        if (scaleTo <= 0) {
            return;
        }
        LOGGER.info("Attempting to scale {} service to {} instances", prefix, scaleTo);
        //todo
    }
}
