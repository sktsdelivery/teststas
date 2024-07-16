package com.archiveapi.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AWSConfig {

	@Value("${AWS_REGION}")
	private String regionName;

	@Bean
	public AmazonS3 s3Client() {
		return AmazonS3ClientBuilder.standard().withRegion(regionName).build();
	}

}
