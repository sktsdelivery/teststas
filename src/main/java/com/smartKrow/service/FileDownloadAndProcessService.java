package com.smartKrow.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.s3.model.S3Object;
import com.smartKrow.constants.ApplicationConstants;
import com.smartKrow.dto.BulkDownloadRequest;
import com.smartKrow.dto.BulkDownloadResponse;
import com.smartKrow.dto.ErrorDetails;
import com.smartKrow.dto.ErrorList;
import com.smartKrow.dto.HistoryResponse;
import com.smartKrow.dto.PresignedUrlResponse;
import com.smartKrow.exception.RestEntityException;
import com.smartKrow.model.BulkProgressDownloadStatus;

import jakarta.validation.ValidationException;

@Service
public class FileDownloadAndProcessService {

	private final static Logger log = LoggerFactory.getLogger(FileDownloadAndProcessService.class);

	private String folderName = "test.zip";

	@Autowired
	private S3Service s3Service;

	@Autowired
	private DynamoDBMapper dynamoDBMapper;

	@Autowired
	private DynamoDBService client;

	public PresignedUrlResponse getPresignedUrl(String downloadLink) throws ValidationException {
		log.info("Staring creating presigned url for download link: " + downloadLink);
		if (downloadLink == null || downloadLink.isBlank()) {
			throw new ValidationException("Please send proper document link");
		}
		log.info("Staring creating presigned url for entity id: ");

		PresignedUrlResponse response = new PresignedUrlResponse();
		try {
			String url = s3Service.getPreSignedDownloadUrl(downloadLink);
			HashMap<String, String> link = new HashMap<>();
			link.put("downloadLink", url);
			response.setData(link);
			response.setIsSuccess("true");

		} catch (Exception e) {
			List<ErrorList> errors = new ArrayList<>();
			ErrorList err = new ErrorList();
			List<String> error = new ArrayList<>();
			error.add(e.getLocalizedMessage());
			err.setErrorCode(HttpStatus.BAD_REQUEST.value());
			err.setErrorMessage(error);
			errors.add(err);
			response.setErrorList(errors);
			response.setIsSuccess("false");
		}
		return response;
	}

	public PresignedUrlResponse saveBulkRecordsForZiping(String entityName, BulkDownloadRequest request)
			throws ValidationException {
		log.info("Staring creating presigned url for entity id: ");

		if (entityName == null || entityName.isBlank()) {
			throw new ValidationException("Entity Name is required");
		}
		if (request.getDocumentIds() == null || request.getDocumentIds().size() < 2) {
			throw new ValidationException("Document Ids should be atleast two.");
		}
		BulkProgressDownloadStatus bulkStatus = new BulkProgressDownloadStatus();
		bulkStatus.setEntityName(entityName);
		bulkStatus.setCreationDate(Date.from(Instant.now()));
		bulkStatus.setLastUpdateDate(Date.from(Instant.now()));
		bulkStatus.setUserId("System");
		HashMap<String, Boolean> documentIds = new HashMap<>();
		for (String ids : request.getDocumentIds()) {
			documentIds.put(ids, false);
		}
		OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
		String zipFileName = entityName + "_" + now.toString().replace(":", "_").replace("-", "_").replace(".", "_")
				+ ".zip";
		;
		bulkStatus.setDocumentIds(documentIds);
		bulkStatus.setStatus(ApplicationConstants.INITIATED);
		bulkStatus.setDownloadLink(zipFileName);
		bulkStatus.setTotalDocument(request.getDocumentIds().size());
		bulkStatus.setSuccessfullyProcessed(0);
		dynamoDBMapper.save(bulkStatus);
		PresignedUrlResponse response = new PresignedUrlResponse();
		HashMap<String, String> fileName = new HashMap<>();
		fileName.put("zipFileName", zipFileName);
		response.setData(fileName);
		response.setIsSuccess("SUCCESS");
		return response;

	}

	public void startZippingProcess() throws IOException {
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		Map<String, Condition> attributeConditions = new HashMap<>();
		List<BulkProgressDownloadStatus> list = new ArrayList<>();
		Condition condition = new Condition().withComparisonOperator(ComparisonOperator.IN).withAttributeValueList(
				new AttributeValue().withS(ApplicationConstants.INITIATED),
				new AttributeValue().withS(ApplicationConstants.IN_PROGRESS));
		attributeConditions.put("status", condition);
		scanExpression.setScanFilter(attributeConditions);
		AmazonDynamoDB dynamo = client.buildAmazonDynamoDB();
		list = dynamoDBMapper.scan(BulkProgressDownloadStatus.class, scanExpression);
		for (BulkProgressDownloadStatus entity : list) {
			String zipFileName = entity.getDownloadLink();
			HashMap<String, Boolean> documents = entity.getDocumentIds();
			FileOutputStream fos = new FileOutputStream(folderName);
			ZipOutputStream zipOut = new ZipOutputStream(fos);
			for (Map.Entry<String, Boolean> entry : documents.entrySet()) {
				if (!entry.getValue()) {
					Map<String, Condition> scanFilter = new HashMap<>();
					Condition condition1 = new Condition().withComparisonOperator(ComparisonOperator.EQ)
							.withAttributeValueList(new AttributeValue().withS(entry.getKey()));

					scanFilter.put("document_id", condition1);
					ScanRequest scanRequest = new ScanRequest().withTableName(entity.getEntityName())
							.withScanFilter(scanFilter);
					try {
						ScanResult result = dynamo.scan(scanRequest);
						AttributeValue value = result.getItems().get(0).get("Document_Link");
						if (value == null) {
							value = result.getItems().get(0).get("DOCUMENT_LINK");
						}
						String key = value.getS();
//						S3Object s3Object = s3Service.getObjectFromS3(key);
//						ZipEntry zipEntry = new ZipEntry(key);
//						try {
//							zos.putNextEntry(zipEntry);
//							s3Object.getObjectContent().transferTo(zos);
//							zos.closeEntry();
//						} catch (IOException e) {
//							e.printStackTrace();
//							s3Object.getObjectContent().transferTo(zos);
//							zos.closeEntry();
//						}
//						byte[] zipBytes = baos.toByteArray();

						S3Object s3Object = s3Service.getObjectFromS3(key);
						InputStream is = s3Object.getObjectContent();
						String key2[] = key.split("/");
						zipOut.putNextEntry(new ZipEntry(key2[key2.length - 1]));

						byte[] bytes = new byte[1024];
						int length;
						while ((length = is.read(bytes)) >= 0) {
							zipOut.write(bytes, 0, length);
						}
						is.close();
						zipOut.closeEntry();

					} catch (Exception e) {
						log.error(e.getLocalizedMessage());
					}

				}
			}
			zipOut.close();
			fos.close();
			File zipFile = new File(folderName);
			s3Service.putObject3(zipFile, zipFileName);
			System.out.println("ZIP file created and uploaded successfully!");
			zipFile.delete();
			entity.setDocumentIds(documents);
			entity.setSuccessfullyProcessed(entity.getSuccessfullyProcessed() + 1);
			entity.setStatus(ApplicationConstants.COMPLETED);
			dynamoDBMapper.save(entity);
		}
	}

	public BulkDownloadResponse bulkDownloadProcessStatus(String userId) throws IOException {
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		Map<String, Condition> attributeConditions = new HashMap<>();
		if (userId != null) {
			Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ)
					.withAttributeValueList(new AttributeValue().withN(userId));
			attributeConditions.put("user_id", condition);
			scanExpression.setScanFilter(attributeConditions);
		}
		List<BulkProgressDownloadStatus> list = new ArrayList<>();

		list = dynamoDBMapper.scan(BulkProgressDownloadStatus.class, scanExpression);

		List<HistoryResponse> res = new ArrayList<>();
//		for (BulkProgressDownloadStatus entity : list) {
//			HistoryResponse history = new HistoryResponse();
//			history.setId(entity.getDocumentId());
//			history.setDownlloadLink(entity.getDownloadLink());
//			history.setStatus(entity.getStatus());
//			history.setCreatedOn(entity.getCreationDate().toString());
//			history.setLastUpdateOn(entity.getLastUpdateDate().toString());
//			res.add(history);
//		}

		for (int i = list.size() - 1; i >= 0; i--) {
			BulkProgressDownloadStatus entity = list.get(i);
			HistoryResponse history = new HistoryResponse();
			history.setId(entity.getDocumentId());
			history.setDownlloadLink(entity.getDownloadLink());
			history.setStatus(entity.getStatus());
			history.setCreatedOn(entity.getCreationDate().toString());
			history.setLastUpdateOn(entity.getLastUpdateDate().toString());
			res.add(history);
		}
		BulkDownloadResponse response = new BulkDownloadResponse();
		response.setIsSuccess(true);
		response.setData(res);
		ErrorDetails error = new ErrorDetails();
		error.setErrorCode("201");
		error.setErrorMessage("Success");
		response.setErrorDetails(error);
		return response;

	}

}
