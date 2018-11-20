package com.dimamon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AppWithMetrics {
    public static void main(String[] args){
        SpringApplication.run(AppWithMetrics.class, args);
    }
}