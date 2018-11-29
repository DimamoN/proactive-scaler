package com.dimamon.service;

import com.dimamon.TestValues;
import com.dimamon.service.predict.DesPredictorService;
import org.junit.jupiter.api.Test;

class DesPredictorServiceTest {

    private DesPredictorService service = new DesPredictorService();

    @Test
    void predictWorkload() {
        service.predictWorkload(10, TestValues.DATASET_1);
        service.predictWorkload(10, TestValues.DATASET_2);
    }
}