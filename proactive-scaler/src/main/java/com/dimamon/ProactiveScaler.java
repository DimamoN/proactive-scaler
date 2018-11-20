package com.dimamon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProactiveScaler {
    public static void main(String[] args){
        SpringApplication.run(ProactiveScaler.class, args);
    }
}
