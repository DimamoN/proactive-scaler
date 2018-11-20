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
            final String url = "http://localhost:8080/students/2";
            URLConnection connection = new URL(url).openConnection();
            InputStream inputStream = connection.getInputStream();
        }
    }
}
