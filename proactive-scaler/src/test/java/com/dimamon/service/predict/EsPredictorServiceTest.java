package com.dimamon.service.predict;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
class EsPredictorServiceTest {

    @Autowired
    private EsPredictorService esPredictorService;

    @Test
    void predictWorkload() {
        List<Double> lastWorkload = Arrays.asList(50., 52., 54., 56., 58., 60.);
        List<Double> predicted = esPredictorService.predictWorkload(6, lastWorkload);

        System.out.println("LAST:" + lastWorkload.toString());
        System.out.println("PREDICT:" + predicted.toString());
    }
}