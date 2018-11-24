package com.dimamon.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/workload")
public class WorkTestController {

    private List<String> tmpStrings = new LinkedList<>();

    @RequestMapping(value = "/cpu", method = RequestMethod.GET)
    public void cpu(){
        int tmp = 0;
        for (int i = 0; i < 1_000_000; i++) {
            tmp++;
        }
    }

    @RequestMapping(value = "/ram", method = RequestMethod.GET)
    public void ram(){
        for (int i = 0; i < 1000 ; i++) {
            tmpStrings.add(new Date().toString());
        }
    }

}