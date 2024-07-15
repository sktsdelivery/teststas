package com.smartKrow.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AWSConfig {
	@Value("${amazon.aws.credentials.access-key}")
	private String accessKeyId;

	@Value("${amazon.aws.credentials.secret-key}")
	private String accessKeySecret;

	@Value("${amazon.aws.region}")
	private String regionName;

	@Value("${amazon.aws.end-point}")
	private String endPoint;

//	@Bean
//	public AWSStaticCredentialsProvider amazonSNS() {
//		final BasicAWSCredentials basicAwsCredentials = new BasicAWSCredentials(accessKeyId, accessKeySecret);
//		return new AWSStaticCredentialsProvider(basicAwsCredentials);
//	}

	@Bean
	public AmazonS3 s3Client() {
		//final BasicAWSCredentials basicAwsCredentials = new BasicAWSCredentials(accessKeyId, accessKeySecret);
		return AmazonS3ClientBuilder.standard()//.withCredentials(new AWSStaticCredentialsProvider(basicAwsCredentials))
				.withRegion(regionName).build();
	}

}
