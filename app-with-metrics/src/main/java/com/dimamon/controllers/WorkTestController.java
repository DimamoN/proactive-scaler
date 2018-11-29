package com.dimamon.controllers;

import com.dimamon.repo.MeasurementsRepo;
import com.dimamon.service.WorkloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workload")
public class WorkTestController {

    @Autowired
    private MeasurementsRepo measurementsRepo;

    @Autowired
    private WorkloadService workloadService;

    @RequestMapping(value = "/cpu/{power}", method = RequestMethod.GET)
    public void cpuPower(@PathVariable("power") Integer power) {
        measurementsRepo.measureConnection(power, "cpu");
        workloadService.loadCpu(power);
    }

    @RequestMapping(value = "/ram/{power}", method = RequestMethod.GET)
    public void ramPower(@PathVariable("power") Integer power) {
        measurementsRepo.measureConnection(power, "ram");
        workloadService.loadRam(power);
    }

    @RequestMapping(value = "/ram/clear", method = RequestMethod.GET)
    public void ramPower() {
        measurementsRepo.measureConnection(1, "ram_clear");
        workloadService.clearRam();
    }

}
