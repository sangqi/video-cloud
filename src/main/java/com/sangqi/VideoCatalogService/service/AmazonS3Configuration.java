package com.sangqi.VideoCatalogService.service;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonS3Configuration {

    @Value("${amazon.aws.profile}")
    private String amazonAwsProfile;

    @Bean
    public AmazonS3 provideAmazonS3() {
        return AmazonS3ClientBuilder.standard()
            .withRegion(Regions.US_WEST_1)
            .withCredentials(new ProfileCredentialsProvider(amazonAwsProfile))
            .build();
    }
}
