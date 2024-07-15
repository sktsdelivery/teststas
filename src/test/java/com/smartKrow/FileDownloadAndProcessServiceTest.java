package com.smartKrow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.smartKrow.constants.ApplicationConstants;
import com.smartKrow.dto.BulkDownloadRequest;
import com.smartKrow.dto.BulkDownloadResponse;
import com.smartKrow.dto.PresignedUrlResponse;
import com.smartKrow.exception.RestEntityException;
import com.smartKrow.model.BulkProgressDownloadStatus;
import com.smartKrow.service.DynamoDBService;
import com.smartKrow.service.FileDownloadAndProcessService;
import com.smartKrow.service.S3Service;

import jakarta.validation.ValidationException;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class FileDownloadAndProcessServiceTest {

	@Mock
	private S3Service s3Service;

	@Mock
	private DynamoDBMapper dynamoDBMapper;

	@Mock
	private DynamoDBService dynamoDBService;

	@InjectMocks
	private FileDownloadAndProcessService fileDownloadAndProcessService;

	private static final Logger log = LoggerFactory.getLogger(FileDownloadAndProcessService.class);

	@BeforeEach
	public void setUp() {
		// Setup tasks if necessary
	}

	@Test
	public void testGetPresignedUrl_Success() throws ValidationException {
		String downloadLink = "valid_link";
		when(s3Service.getPreSignedDownloadUrl(anyString())).thenReturn("http://example.com/presigned");

		PresignedUrlResponse response = fileDownloadAndProcessService.getPresignedUrl(downloadLink);

		assertTrue(response.getIsSuccess().equals("true"));
		assertNotNull(response.getData().get("downloadLink"));
	}

	@Test
	public void testGetPresignedUrl_InvalidLink() {
		String downloadLink = "";

		assertThrows(ValidationException.class, () -> fileDownloadAndProcessService.getPresignedUrl(downloadLink));
	}

	@Test
	public void testSaveBulkRecordsForZiping_Success() throws ValidationException {
		BulkDownloadRequest request = new BulkDownloadRequest();
		request.setDocumentIds(Arrays.asList("doc1", "doc2"));

		PresignedUrlResponse response = fileDownloadAndProcessService.saveBulkRecordsForZiping("entity", request);

		assertEquals("SUCCESS", response.getIsSuccess());
		assertNotNull(response.getData().get("zipFileName"));
		verify(dynamoDBMapper, times(1)).save(any(BulkProgressDownloadStatus.class));
	}

	@Test
	public void testSaveBulkRecordsForZiping_ValidationErrors() {
		BulkDownloadRequest request = new BulkDownloadRequest();

		assertThrows(ValidationException.class,
				() -> fileDownloadAndProcessService.saveBulkRecordsForZiping(null, request));
		assertThrows(ValidationException.class,
				() -> fileDownloadAndProcessService.saveBulkRecordsForZiping("", request));

		request.setDocumentIds(Arrays.asList("doc1"));
		assertThrows(ValidationException.class,
				() -> fileDownloadAndProcessService.saveBulkRecordsForZiping("entity", request));
	}

//	@Test
//	public void testStartZippingProcess_Success() throws IOException, RestEntityException {
//		BulkProgressDownloadStatus bulkStatus = new BulkProgressDownloadStatus();
//		bulkStatus.setEntityName("entity");
//		bulkStatus.setDownloadLink("test.zip");
//		bulkStatus.setDocumentIds(new HashMap<String, Boolean>() {
//			{
//				put("doc1", false);
//			}
//		});
//		bulkStatus.setStatus(ApplicationConstants.INITIATED);
//
//		when(dynamoDBService.buildAmazonDynamoDB()).thenReturn(mock(AmazonDynamoDB.class));
//
//		PaginatedScanList<BulkProgressDownloadStatus> scanList = mock(PaginatedScanList.class);
//
//		when(scanList.stream()).thenReturn(Arrays.asList(bulkStatus).stream())
//				.thenReturn(Arrays.asList(bulkStatus).stream());
//
//		when(dynamoDBMapper.scan(BulkProgressDownloadStatus.class, any(DynamoDBScanExpression.class)))
//				.thenReturn(scanList);
//
//		S3Object s3Object = mock(S3Object.class);
//		when(s3Service.getObjectFromS3(anyString())).thenReturn(s3Object);
//
//		S3ObjectInputStream s3ObjectInputStream = new S3ObjectInputStream(
//				new ByteArrayInputStream("test content".getBytes()), null);
//		when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);
//
//		fileDownloadAndProcessService.startZippingProcess();
//
//		verify(dynamoDBMapper, times(1)).save(any(BulkProgressDownloadStatus.class));
//		verify(s3Service, times(1)).putObject3(any(File.class), anyString());
//	}

//	@Test
//	public void testBulkDownloadProcessStatus_Success() throws IOException {
//		BulkProgressDownloadStatus bulkStatus = new BulkProgressDownloadStatus();
//		bulkStatus.setEntityName("entity");
//		bulkStatus.setDownloadLink("test.zip");
//		bulkStatus.setDocumentIds(new HashMap<>());
//		bulkStatus.setStatus(ApplicationConstants.COMPLETED);
//		bulkStatus.setCreationDate(Date.from(Instant.now()));
//		bulkStatus.setLastUpdateDate(Date.from(Instant.now()));
//
//		PaginatedScanList<BulkProgressDownloadStatus> scanList = mock(PaginatedScanList.class);
//
//		when(scanList.stream()).thenReturn(Arrays.asList(bulkStatus).stream())
//				.thenReturn(Arrays.asList(bulkStatus).stream());
//
//		when(dynamoDBMapper.scan(BulkProgressDownloadStatus.class, any(DynamoDBScanExpression.class)))
//				.thenReturn(scanList);
//
//		BulkDownloadResponse response = fileDownloadAndProcessService.bulkDownloadProcessStatus("1");
//
//		assertTrue(response.getIsSuccess());
//		assertEquals(1, response.getData().size());
//		verify(dynamoDBMapper, times(1)).scan(any(), any(DynamoDBScanExpression.class));
//	}

//	@Test
//	public void testBulkDownloadProcessStatus_NoUserId() throws IOException {
//		BulkProgressDownloadStatus bulkStatus = new BulkProgressDownloadStatus();
//		bulkStatus.setEntityName("entity");
//		bulkStatus.setDownloadLink("test.zip");
//		bulkStatus.setDocumentIds(new HashMap<>());
//		bulkStatus.setStatus(ApplicationConstants.COMPLETED);
//		bulkStatus.setCreationDate(Date.from(Instant.now()));
//		bulkStatus.setLastUpdateDate(Date.from(Instant.now()));
//
//		PaginatedScanList<BulkProgressDownloadStatus> scanList = mock(PaginatedScanList.class);
//
//		when(scanList.stream()).thenReturn(Arrays.asList(bulkStatus).stream())
//				.thenReturn(Arrays.asList(bulkStatus).stream());
//
//		when(dynamoDBMapper.scan(BulkProgressDownloadStatus.class, any(DynamoDBScanExpression.class)))
//				.thenReturn(scanList);
//
//		BulkDownloadResponse response = fileDownloadAndProcessService.bulkDownloadProcessStatus(null);
//
//		assertTrue(response.getIsSuccess());
//		assertEquals(1, response.getData().size());
//		verify(dynamoDBMapper, times(1)).scan(any(), any(DynamoDBScanExpression.class));
//	}
}
