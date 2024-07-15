package com.smartKrow.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableCollection;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;

@Service
public class DynamoDBService {

//	static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
//
//	static DynamoDB dynamoDB = new DynamoDB(client);

	@Value("${amazon.aws.credentials.access-key}")
	private String accessKeyId;

	@Value("${amazon.aws.credentials.secret-key}")
	private String accessKeySecret;

	@Value("${amazon.aws.region}")
	private String regionName;

	public AmazonDynamoDB buildAmazonDynamoDB() {
//		final BasicAWSCredentials basicAwsCredentials = new BasicAWSCredentials(accessKeyId, accessKeySecret);
//		return AmazonDynamoDBClientBuilder.standard()
//				.withCredentials(new AWSStaticCredentialsProvider(basicAwsCredentials)).withRegion(regionName).build();
		return AmazonDynamoDBClientBuilder.standard()
                .withRegion(regionName) // specify your region
                .build();
	}

	private DynamoDB getDynamoDB() {

		return new DynamoDB(buildAmazonDynamoDB());
	}

	public List<String> getAllTables() {
		List<String> ta = new ArrayList<>();
		TableCollection<ListTablesResult> tables = getDynamoDB().listTables();
		Iterator<Table> iterator = tables.iterator();

		System.out.println("Listing table names");

		while (iterator.hasNext()) {
			Table table = iterator.next();
			Iterator<Item> items = table.scan(new ScanSpec()).iterator();

			while (items.hasNext()) {
					System.out.println(items.next().toJSON());

				
				System.out.println();
			}

			ta.add(table.getTableName());

		}

		return ta;
	}

}
