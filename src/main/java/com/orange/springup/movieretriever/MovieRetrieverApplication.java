package com.orange.springup.movieretriever;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MovieRetrieverApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieRetrieverApplication.class, args);
    }

}
