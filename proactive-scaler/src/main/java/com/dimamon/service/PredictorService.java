package com.dimamon.service;

import com.dimamon.entities.WorkloadPoint;

import java.util.List;

public interface PredictorService {

    void predictWorkload(int valuesToPredict, List<WorkloadPoint> lastWorkload);

}
