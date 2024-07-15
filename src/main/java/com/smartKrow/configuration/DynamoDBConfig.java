package com.smartKrow.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDBConfig {

	@Value("${amazon.aws.credentials.access-key}")
	private String accessKeyId = "";

	@Value("${amazon.aws.credentials.secret-key}")
	private String accessKeySecret = "";

	@Value("${amazon.aws.region}")
	private String regionName;

	@Value("${amazon.aws.end-point}")
	private String endPoint;

	@Bean
	public DynamoDbClient dynamoDbClient() {
		AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider
				.create(AwsBasicCredentials.create(accessKeyId, accessKeySecret));
		return DynamoDbClient.builder().credentialsProvider(credentialsProvider).region(Region.US_EAST_2).build();
	}

	@Bean
	public DynamoDBMapper dynamoDBMapper() {
		return new DynamoDBMapper(buildAmazonDynamoDB());
	}
	
	@Bean 
	public DynamoDB dynamoDB() {
		return new DynamoDB(buildAmazonDynamoDB());
	}

	public AmazonDynamoDB buildAmazonDynamoDB() {
		final BasicAWSCredentials basicAwsCredentials = new BasicAWSCredentials(accessKeyId, accessKeySecret);
		return AmazonDynamoDBClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(basicAwsCredentials))
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName)).build();
	}

}
