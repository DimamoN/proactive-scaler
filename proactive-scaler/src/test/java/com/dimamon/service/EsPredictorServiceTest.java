package com.dimamon.service;

import com.dimamon.TestValues;
import com.dimamon.service.predict.EsPredictorService;
import org.junit.jupiter.api.Test;

class EsPredictorServiceTest {

    private EsPredictorService service = new EsPredictorService();

    @Test
    void predictWorkload() {
        service.predictWorkload(10, TestValues.DATASET_1);
        service.predictWorkload(10, TestValues.DATASET_2);
    }
}