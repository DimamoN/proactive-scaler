package com.dimamon.controllers;

import com.dimamon.repo.MeasurementsRepo;
import com.dimamon.service.WorkloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public String cpuPower(@PathVariable("power") Integer power) {
        measurementsRepo.measureConnection(power, "cpu");
        workloadService.loadCpu(power);
        return HttpStatus.OK.toString();
    }

    @RequestMapping(value = "/ram/{power}", method = RequestMethod.GET)
    public String ramPower(@PathVariable("power") Integer power) {
        measurementsRepo.measureConnection(power, "ram");
        workloadService.loadRam(power);
        return HttpStatus.OK.toString();
    }

    @RequestMapping(value = "/ram/clear", method = RequestMethod.GET)
    public String ramPower() {
        measurementsRepo.measureConnection(1, "ram_clear");
        workloadService.clearRam();
        return HttpStatus.OK.toString();
    }

}
