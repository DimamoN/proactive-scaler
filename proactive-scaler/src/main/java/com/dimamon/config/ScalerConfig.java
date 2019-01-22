package com.dimamon.config;

public class ScalerConfig {

    public static ScalerConfig PROACTIVE_V1 = new ScalerConfig();
    static {
        PROACTIVE_V1.setProactive(true);
        PROACTIVE_V1.setScaleTresholds(80, 25);
        PROACTIVE_V1.setForecastingParams(6, 12);
        PROACTIVE_V1.setPredictionForNow(1); // 30 sec
    }

    public static ScalerConfig REACTIVE_V1 = new ScalerConfig();
    static {
        REACTIVE_V1.setProactive(false);
        REACTIVE_V1.setScaleTresholds(80, 25);
        REACTIVE_V1.setForecastingParams(6, 12);
        REACTIVE_V1.setPredictionForNow(1); // 30 sec
    }

    /**
     * true - proactive, false - reactive
     */
    private boolean proactive;

    /**
     * 1 unit is 10 seconds, so 6 * 5 = 1 min
     */
    private int forecastFor; // in scale tasks (only for proactive)
    private int forecastBasedOn; // in scale tasks

    private int treshHoldAfterScaling = 1; // how many scale tasks will be ignored

    private int predictionForNow; // in scale tasks  (forecastFor / checkEvery) / 2

    private int scaleUpTreshold; // in percents
    private int scaleDownTreshold; // in percents

    public ScalerConfig() {
    }

    public String proactiveString() {
        return this.proactive ? "proactive" : "reactive";
    }

    public void setProactive(boolean proactive) {
        this.proactive = proactive;
    }

    public void setScaleTresholds(int scaleUpTreshold, int scaleDownTreshold) {
        this.scaleUpTreshold = scaleUpTreshold;
        this.scaleDownTreshold = scaleDownTreshold;
    }

    public void setForecastingParams(int forecastFor, int forecastBasedOn) {
        this.forecastFor = forecastFor;
        this.forecastBasedOn = forecastBasedOn;
    }

    public void setPredictionForNow(int predictionForNow) {
        this.predictionForNow = predictionForNow;
    }

    public boolean isProactive() {
        return proactive;
    }

    public int getForecastFor() {
        return forecastFor;
    }

    public int getForecastBasedOn() {
        return forecastBasedOn;
    }

    public int getTreshHoldAfterScaling() {
        return treshHoldAfterScaling;
    }

    public int getPredictionForNow() {
        return predictionForNow;
    }

    public int getScaleUpTreshold() {
        return scaleUpTreshold;
    }

    public int getScaleDownTreshold() {
        return scaleDownTreshold;
    }
}