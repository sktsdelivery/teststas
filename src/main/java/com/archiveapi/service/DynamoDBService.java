package com.archiveapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DynamoDBService {

	@Value("${AWS_REGION}")
	private String regionName;

	public AmazonDynamoDB buildAmazonDynamoDB() {
		log.info("reginName:-" + regionName);

		return AmazonDynamoDBClientBuilder.standard().withRegion(regionName).build();
	}

}
