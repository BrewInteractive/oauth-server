package com.brew.oauth20.server.config;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

    @Value("${s3.service.aws.access.key}")
    String awsS3ServiceAccessKey;
    @Value("${s3.service.aws.secret.key}")
    String awsS3ServiceSecretKey;
    @Value("${s3.service.aws.region}")
    String awsS3ServiceRegion;

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(
                        new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsS3ServiceAccessKey, awsS3ServiceSecretKey))
                )
                .withRegion(awsS3ServiceRegion)
                .build();
    }
}
