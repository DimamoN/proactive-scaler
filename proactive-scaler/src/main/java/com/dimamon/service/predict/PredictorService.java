package com.dimamon.service.predict;

import java.util.List;
import java.util.OptionalDouble;

public interface PredictorService {

    List<Double> predictWorkload(int valuesToPredict, List<Double> values);

    default double averagePrediction(int valuesToPredict, List<Double> values) {
        List<Double> predictedValues = this.predictWorkload(valuesToPredict, values);
        OptionalDouble average = predictedValues.stream()
                .mapToDouble(a -> a)
                .average();
        return average.isPresent() ? average.getAsDouble() : 0.0;
    }

}
