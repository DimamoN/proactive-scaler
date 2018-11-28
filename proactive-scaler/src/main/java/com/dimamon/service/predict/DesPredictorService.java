package com.dimamon.service.predict;

import com.dimamon.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.dimamon.utils.StringUtils.showValues;

@Service("desPredictor")
public class DesPredictorService implements PredictorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DesPredictorService.class);

    private static final Double ALPHA = 0.75;
    private static final Double BETA = 0.5; // Trend line

    @Override
    public List<Double> predictWorkload(int valuesToPredict, List<Double> lastWorkload) {

        if (lastWorkload.isEmpty()) {
            LOGGER.debug("last workload is empty, can't predict");
        }

        final Double lastActualValue = lastWorkload.get(lastWorkload.size() - 1);

        PrePredictedResult result = this.predictListPrev(lastWorkload);
        final Double lastTrend = result.lastTrend;
        final Double lastPredicted = result.lastPredicted;

        List<Double> predictedValues = predictNext(valuesToPredict, lastPredicted, lastTrend);
        LOGGER.info("Predicted {} values is : {}", valuesToPredict, showValues(predictedValues));
        return predictedValues;
    }


    private double predictOne(final Double level, final Double trend) {
        return level + trend;
    }

    private double calcLevel(final Double actual, final Double prevLevel, final Double prevTrend) {
        return ALPHA * (actual) + (1 - ALPHA) * (prevLevel + prevTrend);
    }

    private double calcTrend(final Double level, final Double prevLevel, final Double prevTrend) {
        return BETA * (level - prevLevel) + (1 - BETA) * prevTrend;
    }

    private List<Double> predictNext(int toPredict, final Double lastPredicted, final Double lastTrend) {
        List<Double> predictedList = new ArrayList<>();
        for (int i = 1; i <= toPredict; i++) {
            // mistake here! i need to calc level, too!
            if (i == 1) {
                predictedList.add(predictOne(lastPredicted, lastTrend));
            } else {
                predictedList.add(predictOne(lastPredicted, i * lastTrend));
            }
        }
        return predictedList;
    }

    private PrePredictedResult predictListPrev(List<Double> realValues) {
        LOGGER.info("Predict from : {}", showValues(realValues));

        PrePredictedResult result = new PrePredictedResult();

        List<Double> predictedValues = new ArrayList<>();
        List<Double> levels = new ArrayList<>();
        List<Double> trends = new ArrayList<>();

        for (int i = 0; i < realValues.size(); ++i) {
            if (i == 0) {
                predictedValues.add(0.0);
                levels.add(0.0); //maybe use another approach
                trends.add(0.0);
            } else if (i == 1) {
                predictedValues.add(realValues.get(i - 1));
                levels.add(this.calcLevel(realValues.get(i - 1), levels.get(i - 1), trends.get(i - 1)));
                trends.add(this.calcTrend(levels.get(i), levels.get(i - 1), trends.get(i - 1)));
            } else {
                levels.add(this.calcLevel(realValues.get(i - 1), levels.get(i - 1), trends.get(i - 1)));
                trends.add(this.calcLevel(levels.get(i), levels.get(i - 1), trends.get(i - 1)));
                predictedValues.add(this.predictOne(levels.get(i - 1), trends.get(i - 1)));
            }
        }

        result.lastPredicted = predictedValues.get(predictedValues.size() - 1);
        result.lastTrend = trends.get(trends.size() - 1);

        LOGGER.info("Pre-Predicted list : {}", showValues(predictedValues));
        return result;
    }

    class PrePredictedResult {
        Double lastPredicted;
        Double lastTrend;
    }

}
