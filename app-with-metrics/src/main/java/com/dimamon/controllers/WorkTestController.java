package com.dimamon.controllers;

import com.dimamon.repo.MeasurementsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/workload")
public class WorkTestController {

    @Autowired
    private MeasurementsRepo measurementsRepo;

    private List<String> tmpStrings = new LinkedList<>();

    @RequestMapping(value = "/cpu", method = RequestMethod.GET)
    public void cpu(){
        measurementsRepo.measureConnection(1, "cpu");
        int tmp = 0;
        for (int i = 0; i < 1_000_000; i++) {
            tmp++;
        }
    }

    @RequestMapping(value = "/ram", method = RequestMethod.GET)
    public void ram(){
        measurementsRepo.measureConnection(1, "ram");
        for (int i = 0; i < 1000 ; i++) {
            tmpStrings.add(new Date().toString());
        }
    }

    @RequestMapping(value = "/cpu/{power}", method = RequestMethod.GET)
    public void cpuPower(@PathVariable("power") Integer power){
        for (int i = 0; i < power ; i++) {
            try {
                load("cpu");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "/ram/{power}", method = RequestMethod.GET)
    public void ramPower(@PathVariable("power") Integer power){
        for (int i = 0; i < power ; i++) {
            try {
                load("ram");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    // move to workload generator service
    private void load(String methodName) throws MalformedURLException {
        final String url = "http://localhost:8081/workload/" + methodName;
        URLConnection connection = null;
        try {
            connection = new URL(url).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            InputStream inputStream = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
