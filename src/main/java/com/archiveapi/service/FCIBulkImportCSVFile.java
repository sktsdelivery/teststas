package com.archiveapi.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.archiveapi.dto.FCIDto;
import com.archiveapi.model.MasterSearchDataTable;
import com.opencsv.CSVWriter;

import jakarta.validation.ValidationException;

@Service
public class FCIBulkImportCSVFile {


	private static final Logger log = LogManager.getLogger(FCIBulkImportCSVFile.class);

	@Value("${S3_BUCKET_NAME}")
	private String bucketName;

	@Value("${OFFLINE_CSV_INPUT_FCI}")
	private String fci;

	@Value("${OFFLINE_CSV_OUTPUT_FCI}")
	private String fciZipped;
	@Autowired
	private DynamoDBMapper dynamoDBMapper;

	@Autowired
	private AmazonS3 s3Client;

	@Autowired
	private S3Service s3Service;

	@Autowired
	private DynamoDBService client;

	public void getObjectOfBucket() throws Exception {

		ListObjectsV2Result result = s3Client.listObjectsV2(bucketName, fci);
		List<S3ObjectSummary> objects = result.getObjectSummaries();
		objects.remove(0);
		for (S3ObjectSummary os : objects) {
			importFiles(os.getKey());
			s3Client.deleteObject(bucketName, os.getKey());
		}
	}

	public void importFiles(String key) {
		log.info("Started the CSV Import at: " + Date.from(Instant.now()));
		try {
			String filePath = s3Service.downloadFile(key);
			CSVFormat custom = CSVFormat.DEFAULT.builder().setIgnoreEmptyLines(true).build();
			Reader is = new FileReader(filePath);
			CSVParser parser = new CSVParser(is, custom);
			String[] fileName = key.replace(".csv", "").split("/");
			importZipProcessFile(parser, fileName[fileName.length - 1]);
		} catch (FileNotFoundException e) {
			log.error(e.toString(), e);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		log.info("Stopping the Zip CSV Import at: " + Date.from(Instant.now()));

	}

	private void importZipProcessFile(CSVParser parser, String fileName) throws IOException {
		int rowCount = 0;
		List<String> row = null;
		List<FCIDto> cscRecords = new ArrayList<>();
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		List<MasterSearchDataTable> data = dynamoDBMapper.scan(MasterSearchDataTable.class, scanExpression);
		HashMap<String, String> masterData = new HashMap<>();
		for (MasterSearchDataTable master : data) {
			masterData.put(master.getDisplayName(), master.getEntityName());
		}
		for (CSVRecord record : parser) {
			try {
				row = record.toList();
				if (rowCount <= 0) {
					rowCount++;
					continue;
				}
				log.info("------------------>  Import Processing Row: " + rowCount);
				try {

					FCIDto csvRecord = prepareRecordForBulkProcessZipFile(row);
					cscRecords.add(csvRecord);
					rowCount++;
					log.info("------------------>  Import Processing End Row: " + rowCount);
				} catch (ValidationException e) {
					log.error(e.getLocalizedMessage() + " Row:" + rowCount);
				} catch (Exception e) {
					log.error(e.toString() + " Row:" + rowCount);
				}
			} catch (Exception e1) {
				log.info("Problem in parsing the file." + e1);
			}

		}
		startZippingProcess(cscRecords, masterData, fileName + ".zip", fileName + ".csv");
	}

	private FCIDto prepareRecordForBulkProcessZipFile(List<String> row) throws Exception {
		FCIDto request = new FCIDto();
		request.setTABLE_Name(isEmptyOrBlank(row.get(0)));
		request.setStartDate(isEmptyOrBlank(row.get(1)));
		request.setEndDate(isEmptyOrBlank(row.get(2)));
		request.setScanBatchId(isEmptyOrBlank(row.get(3)));
		return request;
	}

	private String isEmptyOrBlank(String str) {
		if (str == null) {
			return str;
		} else if (str.isBlank()) {
			return null;
		}
		return str;
	}

	private Map<String, Condition> scanFilter(FCIDto request) {
		Map<String, Condition> scanFilter = new HashMap<>();
		if (request.getScanBatchId() != null) {
			Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(request.getScanBatchId()));
			scanFilter.put("Scan Batch ID", condition);
		}

		if (request.getStartDate() != null || request.getEndDate() != null) {
			if (request.getStartDate() != null && request.getEndDate() != null) {
				Condition condition = new Condition().withComparisonOperator(ComparisonOperator.BETWEEN)
						.withAttributeValueList(new AttributeValue().withS(changeDate(request.getStartDate())))
						.withAttributeValueList(new AttributeValue().withS(changeDate(request.getEndDate())));
				scanFilter.put("f_entrydate", condition);
			}
			if (request.getStartDate() != null) {
				Condition condition = new Condition().withComparisonOperator(ComparisonOperator.GE)
						.withAttributeValueList(new AttributeValue().withS(changeDate(request.getStartDate())));
				scanFilter.put("f_entrydate", condition);
			}
			if (request.getEndDate() != null) {

				Condition condition = new Condition().withComparisonOperator(ComparisonOperator.LE)
						.withAttributeValueList(new AttributeValue().withS(changeDate(request.getEndDate())));
				scanFilter.put("f_entrydate", condition);
			}

		}

		return scanFilter;

	}

	public void startZippingProcess(List<FCIDto> requests, HashMap<String, String> tableName, String zipFolderName,
			String csvFile) throws IOException {
		FileOutputStream fos = new FileOutputStream(zipFolderName);
		ZipOutputStream zipOut = new ZipOutputStream(fos);
		// int i = 0;
		List<FCIDto> csv = new ArrayList<>();
		for (FCIDto entity : requests) {
			FCIDto enti = entity;
			AmazonDynamoDB dynamo = client.buildAmazonDynamoDB();
			ScanRequest scanRequest = new ScanRequest().withTableName(tableName.get(entity.getTABLE_Name()))
					.withScanFilter(scanFilter(entity));
			ScanResult result = dynamo.scan(scanRequest);
			if (result == null || result.getItems().size() == 0) {
				enti.setIsSuccess(false);
				enti.setNote("No record found");
				enti.setNoOfRecorde(0);

			} else if (result.getItems().size() > 1) {
				enti.setIsSuccess(true);
				enti.setNote("More than 1 records found");
				enti.setNoOfRecorde(result.getItems().size());
			} else {
				enti.setIsSuccess(true);
				enti.setNote("Only1 records found");
				enti.setNoOfRecorde(1);
			}
			for (Map<String, AttributeValue> attributes : result.getItems()) {
				try {
					AttributeValue attrubuteValue = attributes.get("DOCUMENT_LINK");
					if (attrubuteValue == null) {
						attrubuteValue = attributes.get("Document_Link");
					}
					String filePath = attrubuteValue.getS();
					S3Object s3Object = s3Service.getObjectFromS3(filePath);
					InputStream is = s3Object.getObjectContent();
					String[] zipPath = filePath.split("/");
					zipOut.putNextEntry(new ZipEntry(zipPath[zipPath.length - 1]));
					byte[] bytes = new byte[1024];
					int length;
					while ((length = is.read(bytes)) >= 0) {
						zipOut.write(bytes, 0, length);
					}

					is.close();
					zipOut.closeEntry();

				} catch (Exception e) {
					zipOut.closeEntry();
				}

			}
			csv.add(enti);
		}

		csvFile(requests, csvFile);
		ZipEntry csvEntry = new ZipEntry(csvFile);
		zipOut.putNextEntry(csvEntry);
		zipOut.close();
		fos.close();
		File zipFile = new File(zipFolderName);
		String zipFileName = fciZipped + zipFolderName;
		s3Service.putObject3(zipFile, zipFileName);
		System.out.println("ZIP file created and uploaded successfully!");
		File file = new File(csvFile);
		file.delete();
		zipFile.delete();

	}

	private String changeDate(String obj) {
		String[] str = obj.split("/");
		try {
			return str[2] + str[1] + str[0];
		} catch (Exception e) {
			log.error("Unparsable Date");
			return "";
		}
	}

	public void csvFile(List<FCIDto> request, String csvFilePath) {
		String[] header = { "Table Name", "IS Create Date From (DD/MM/YYYY)", "IS Create Date To (DD/MM/YYYY)",
				"Scan Batch ID", "Year From (DD/MM/YYY)", "Is Success", "Note", "No of records" };

		try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
			writer.writeNext(header);
			for (FCIDto record : request) {
				String[] data2 = { record.getTABLE_Name(), record.getStartDate(), record.getEndDate(),
						record.getScanBatchId(), record.getIsSuccess().toString(), record.getNote(),
						record.getNoOfRecorde().toString() };
				writer.writeNext(data2);
			}
			System.out.println("CSV file created successfully with OpenCSV.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
