package com.dimamon;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

class LoadTest {

    @Test
    void loadTest() throws IOException, InterruptedException {
        for (int i = 0; i < 10000 ; i++) {
            Thread.sleep(1000);
            final String url = "http://localhost:8081/students/2";
            URLConnection connection = new URL(url).openConnection();
            InputStream inputStream = connection.getInputStream();
        }
    }

    @Test
    void loadCPU() throws IOException, InterruptedException {
        for (int i = 0; i < 50000 ; i++) {
//            Thread.sleep(2);
            final String url = "http://localhost:8081/workload/cpu";
            URLConnection connection = new URL(url).openConnection();
            InputStream inputStream = connection.getInputStream();
        }
    }

    @Test
    void loadRAM() throws IOException, InterruptedException {
        for (int i = 0; i < 10000 ; i++) {
//            Thread.sleep(100);
            final String url = "http://localhost:8081/workload/ram";
            URLConnection connection = new URL(url).openConnection();
            InputStream inputStream = connection.getInputStream();
        }
    }
}
