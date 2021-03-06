package com.dimamon.service.predict;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.dimamon.utils.StringUtils.showValues;

@Service("esPredictor")
public class EsPredictorService implements PredictorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EsPredictorService.class);

    private static final Double ALPHA = 0.75;

    @Override
    public List<Double> predictWorkload(int valuesToPredict, List<Double> lastWorkload) {

        if (lastWorkload.isEmpty()) {
            LOGGER.debug("last workload is empty, can't predict");
            return Collections.emptyList();
        }

        final Double lastActualValue = lastWorkload.get(lastWorkload.size() - 1);

        List<Double> preList = predictListPrev(lastWorkload);
        final Double lastPredictedValue = preList.get(preList.size() - 1);

        List<Double> predictedValues = predictNext(valuesToPredict, lastPredictedValue, lastActualValue);
        LOGGER.info("Predicted {} values is : {}", valuesToPredict, showValues(predictedValues));
        return predictedValues;
    }

    private Double predictOne(final Double prevActual, final Double prevPredicted) {
        return (ALPHA * prevActual) + ((1 - ALPHA) * prevPredicted);
    }

    private List<Double> predictNext(int toPredict, final Double lastPredicted, final Double lastActual) {
        List<Double> predictedList = new ArrayList<>();
        for (int i = 0; i < toPredict ; i++) {
            if (i == 0) {
                predictedList.add(predictOne(lastActual, lastPredicted));
            } else {
                predictedList.add(predictOne(lastActual, predictedList.get(predictedList.size() - 1)));
            }
        }
        return predictedList;
    }

    //1
    private List<Double> predictListPrev(List<Double> realValues) {

        LOGGER.info("Predict from : {}", showValues(realValues));

        List<Double> predictedValues = new ArrayList<>();
        for (int i = 0; i < realValues.size(); ++i) {
            if (i == 0) {
                predictedValues.add(0.0);
            }
            else if (i == 1) {
                predictedValues.add(realValues.get(i - 1));
            }
            else {
                predictedValues.add(this.predictOne(realValues.get(i - 1), predictedValues.get(i - 1)));
            }
        }

        LOGGER.info("Pre-Predicted list : {}", showValues(predictedValues));
        return predictedValues;
    }

}
