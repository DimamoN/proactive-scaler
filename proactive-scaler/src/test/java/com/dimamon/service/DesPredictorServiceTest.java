package com.dimamon.service;

import com.dimamon.TestValues;
import com.dimamon.service.predict.DesPredictorService;
import com.dimamon.service.predict.EsPredictorService;
import com.dimamon.utils.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

class DesPredictorServiceTest {

    private DesPredictorService service = new DesPredictorService();

    @Test
    void predictWorkload() {
        service.predictWorkload(10, TestValues.DATASET_1);
    }
}