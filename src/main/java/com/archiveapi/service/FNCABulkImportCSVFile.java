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
import com.archiveapi.dto.FncaCsvProcessDto;
import com.archiveapi.model.MasterSearchDataTable;
import com.opencsv.CSVWriter;

import jakarta.validation.ValidationException;

@Service
public class FNCABulkImportCSVFile {

	private static final Logger log = LogManager.getLogger(FNCABulkImportCSVFile.class);

	/** The bucket name. */
	@Value("${S3_BUCKET_NAME}")
	private String bucketName;

	/** The fnca. */
	@Value("${OFFLINE_CSV_INPUT_FNCA}")
	private String fnca;

	/** The fnca zipped. */
	@Value("${OFFLINE_CSV_OUTPUT_FNCA}")
	private String fncaZipped;

	@Autowired
	private DynamoDBMapper dynamoDBMapper;

	@Autowired
	private AmazonS3 s3Client;

	@Autowired
	private S3Service s3Service;

	@Autowired
	private DynamoDBService client;

	public void getObjectOfBucket() throws Exception {

		ListObjectsV2Result result = s3Client.listObjectsV2(bucketName, fnca);
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
		List<FncaCsvProcessDto> cscRecords = new ArrayList<>();
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

					FncaCsvProcessDto csvRecord = prepareRecordForBulkProcessZipFile(row);
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

	private FncaCsvProcessDto prepareRecordForBulkProcessZipFile(List<String> row) throws Exception {
		FncaCsvProcessDto request = new FncaCsvProcessDto();
		request.setTABLE_Name(isEmptyOrBlank(row.get(0)));
		request.setCOCD(isEmptyOrBlank(row.get(1)));
		request.setPHC_NUMBER(isEmptyOrBlank(row.get(2)));
		request.setDOC_NUMBER(isEmptyOrBlank(row.get(3)));
		request.setYear_From(isEmptyOrBlank(row.get(4)));
		request.setYear_To(isEmptyOrBlank(row.get(5)));
		request.setMAT_DOC(isEmptyOrBlank(row.get(6)));
		request.setPO_NUMBER(isEmptyOrBlank(row.get(7)));
		request.setVENDOR_ID(isEmptyOrBlank(row.get(8)));
		request.setPOCREATION(isEmptyOrBlank(row.get(9)));
		request.setName2(isEmptyOrBlank(row.get(10)));
		request.setTRANSAC_ID(isEmptyOrBlank(row.get(11)));
		request.setSTART_DATE(isEmptyOrBlank(row.get(12)));
		request.setASSET_NUM(isEmptyOrBlank(row.get(13)));
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

	private Map<String, Condition> scanFilter(FncaCsvProcessDto request) {
		Map<String, Condition> scanFilter = new HashMap<>();
		if (request.getCOCD() != null) {
			Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(request.getCOCD()));
			scanFilter.put("COCD", condition);
		}
		if (request.getDOC_NUMBER() != null) {
			Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(request.getDOC_NUMBER()));
			scanFilter.put("DOC_NUMBER", condition);
		}
		if (request.getPO_NUMBER() != null) {
			Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(request.getPO_NUMBER()));
			scanFilter.put("PO_NUMBER", condition);
		}
		if (request.getASSET_NUM() != null) {
			Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(request.getASSET_NUM()));
			scanFilter.put("ASSET NUM", condition);
		}
		if (request.getTRANSAC_ID() != null) {
			Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(request.getTRANSAC_ID()));
			scanFilter.put("TRANSAC ID", condition);
		}
		if (request.getName2() != null) {
			Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(request.getName2()));
			scanFilter.put("NAME2", condition);
		}
		if (request.getMAT_DOC() != null) {
			Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(request.getMAT_DOC()));
			scanFilter.put("MAT_DOC", condition);
		}
		if (request.getPHC_NUMBER() != null) {
			Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(request.getPHC_NUMBER()));
			scanFilter.put("PHC_NUMBER", condition);
		}
		if (request.getVENDOR_ID() != null) {
			Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(request.getVENDOR_ID()));
			scanFilter.put("VENDOR_ID", condition);
		}
		if (request.getYear_From() != null || request.getYear_To() != null) {
			if (request.getYear_From() != null && request.getYear_To() != null) {
				Condition condition = new Condition().withComparisonOperator(ComparisonOperator.BETWEEN)
						.withAttributeValueList(new AttributeValue().withS(changeDateYear(request.getYear_From())))
						.withAttributeValueList(new AttributeValue().withS(changeDateYear(request.getYear_To())));
				scanFilter.put("YEAR", condition);
			}
			if (request.getYear_From() != null) {
				Condition condition = new Condition().withComparisonOperator(ComparisonOperator.GE)
						.withAttributeValueList(new AttributeValue().withS(changeDateYear(request.getYear_From())));
				scanFilter.put("YEAR", condition);
			}
			if (request.getYear_To() != null) {
				Condition condition = new Condition().withComparisonOperator(ComparisonOperator.LE)
						.withAttributeValueList(new AttributeValue().withS(changeDateYear(request.getYear_To())));
				scanFilter.put("YEAR", condition);
			}

		}

		if (request.getSTART_DATE() != null) {
			Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(changeDate(request.getSTART_DATE())));
			scanFilter.put("START DATE", condition);
		}
		if (request.getPOCREATION() != null) {
			Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withS(request.getPOCREATION()));
			scanFilter.put("POCREATION", condition);
		}
		return scanFilter;

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

	private String changeDateYear(String obj) {
		String[] str = obj.split("/");
		try {
			return str[2];
		} catch (Exception e) {
			log.error("Unparsable Date");
			return "";
		}
	}

	public void startZippingProcess(List<FncaCsvProcessDto> requests, HashMap<String, String> tableName,
			String zipFolderName, String csvFile) throws IOException {
		FileOutputStream fos = new FileOutputStream(zipFolderName);
		ZipOutputStream zipOut = new ZipOutputStream(fos);
		// int i = 0;
		List<FncaCsvProcessDto> csv = new ArrayList<>();
		for (FncaCsvProcessDto entity : requests) {
			FncaCsvProcessDto enti = entity;
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
		String zipFileName = fncaZipped + zipFolderName;
		s3Service.putObject3(zipFile, zipFileName);
		System.out.println("ZIP file created and uploaded successfully!");
		File file = new File(csvFile);
		file.delete();
		zipFile.delete();

	}

	public void csvFile(List<FncaCsvProcessDto> request, String csvFilePath) {
		String[] header = { "Table Name", "COCD", "PHC_NUMBER", "DOC NUMBER", "Year From (DD/MM/YYY)",
				"Year To (DD/MM/YYYY)", "MAT DOC	PO_NUMBER", "VENDOR_ID", "POCREATION", "Name2", "TRANSAC ID",
				"START DATE", "(DD/MM/YYYY)", "ASSET_NUM", "Is Success", "Note", "No of records" };

		try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
			writer.writeNext(header);
			for (FncaCsvProcessDto record : request) {
				String[] data2 = { record.getTABLE_Name(), record.getCOCD(), record.getPHC_NUMBER(),
						record.getDOC_NUMBER(), record.getYear_From(), record.getYear_To(), record.getMAT_DOC(),
						record.getPO_NUMBER(), record.getVENDOR_ID(), record.getPOCREATION(), record.getName2(),
						record.getTRANSAC_ID(), record.getSTART_DATE(), record.getASSET_NUM(),
						record.getIsSuccess().toString(), record.getNote(), record.getNoOfRecorde().toString() };
				writer.writeNext(data2);
			}
			System.out.println("CSV file created successfully with OpenCSV.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
