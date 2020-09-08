package com.sangqi.VideoCatalogService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableMongoRepositories(basePackages = "com.sangqi.VideoCatalogService.repository")
public class VideoCatalogServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoCatalogServiceApplication.class, args);
    }

}
