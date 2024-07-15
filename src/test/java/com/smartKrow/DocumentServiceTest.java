package com.smartKrow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.smartKrow.dto.Document;
import com.smartKrow.dto.DocumentResponse;
import com.smartKrow.dto.DocumentSearchRequest;
import com.smartKrow.model.MasterSearchDataTable;
import com.smartKrow.model.ZAPGFFUP;
import com.smartKrow.model.ZAPPHC;
import com.smartKrow.service.DocumentService;

import jakarta.validation.ValidationException;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

	@Mock
	private DynamoDBMapper mapper;

	@InjectMocks
	private DocumentService documentService;

	private ZAPPHC zapphc;
	private DocumentSearchRequest searchRequest;
	private Map<String, Object> searchCriteria;

	@BeforeEach
	void setUp() {
		zapphc = new ZAPPHC();
		zapphc.setDocumentId("12345");
		zapphc.setApplicationType("TypeA");

		searchCriteria = new HashMap<>();
		searchCriteria.put("documentId", "12345");

		searchRequest = new DocumentSearchRequest();
		searchRequest.setSearchCriteria(searchCriteria);
	}

	@Test
	void testGetDataFromSearchNoModelClassFound() {
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
				() -> documentService.getDataFromSearch("INVALID", searchRequest, 0, 10, "documentId", "asc"));
		assertEquals("No model class found for table: INVALID", thrown.getMessage());
	}

	@Test
	void testGetAllEntitiesNoDataFound() {
		PaginatedScanList<Object> scanList = mock(PaginatedScanList.class);

		when(mapper.scan(any(Class.class), any(DynamoDBScanExpression.class))).thenReturn(scanList);

		DocumentResponse response = documentService.getAllEntities();

		assertNotNull(response);
		assertEquals("Error", response.getIsSuccess());
		assertEquals(404, response.getErrorList().getErrorCode());
		assertTrue(response.getErrorList().getErrorMessage().contains("No Data found"));
	}

	@Test
	void testGetResponseSuccess() {
		List<Document> documents = Collections.singletonList(new Document());
		DocumentResponse response = documentService.getResponse(documents, "Success", null, null);

		assertNotNull(response);
		assertEquals("Success", response.getIsSuccess());
		assertEquals(documents, response.getResultData());
	}

	@Test
	void testGetResponseError() {
		List<String> errorMessages = Collections.singletonList("Error occurred");
		DocumentResponse response = documentService.getResponse(null, "Error", errorMessages, 500);

		assertNotNull(response);
		assertEquals("Error", response.getIsSuccess());
		assertEquals(500, response.getErrorList().getErrorCode());
		assertEquals(errorMessages, response.getErrorList().getErrorMessage());
	}
	
//	 @Test
//	    public void testGetAllEntities() {
//	        MasterSearchDataTable mockTable = new MasterSearchDataTable();
//	        mockTable.setDisplayName("Test Display");
//	        mockTable.setEntityName("Test Entity");
//
//	        PaginatedScanList<MasterSearchDataTable> mockData = mock(PaginatedScanList.class);
//	        when(mockData.iterator()).thenReturn(Arrays.asList(mockTable).iterator());
//	        when(mapper.scan(eq(MasterSearchDataTable.class), any(DynamoDBScanExpression.class))).thenReturn(mockData);
//
//	        DocumentResponse response = documentService.getAllEntities();
//
//	        assertNotNull(response);
//	        assertEquals("Success", response.getIsSuccess());
//	        assertNotNull(response.getResultData());
//	    }

//	    @Test
//	    public void testGetDataFromSearch() throws IllegalArgumentException, IllegalAccessException {
//	        String entityName = "ZAPGFFUP";
//	        DocumentSearchRequest payload = new DocumentSearchRequest();
//	        payload.setSearchCriteria(new HashMap<>());
//	        payload.getSearchCriteria().put("documentId", "123");
//
//	        ZAPGFFUP mockField = new ZAPGFFUP();
//	        PaginatedScanList<ZAPGFFUP> mockData = mock(PaginatedScanList.class);
//	        when(mockData.iterator()).thenReturn(Arrays.asList(mockField).iterator());
//	        when(mapper.scan(eq(ZAPGFFUP.class), any(DynamoDBScanExpression.class))).thenReturn(mockData);
//
//	        Page<Map<String, Object>> page = documentService.getDataFromSearch(entityName, payload, 0, 10, null, null);
//
//	        assertNotNull(page);
//	        assertEquals(1, page.getTotalElements());
//	    }

	    @Test
	    public void testGetDataFromSearch_InvalidEntity() {
	        String entityName = "InvalidEntity";
	        DocumentSearchRequest payload = new DocumentSearchRequest();

	        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
	            documentService.getDataFromSearch(entityName, payload, 0, 10, null, null);
	        });

	        assertEquals("No model class found for table: InvalidEntity", exception.getMessage());
	    }

//	    @Test
//	    public void testGetDataFromSearch_NoSearchCriteria() {
//	        String entityName = "ZAPGFFUP";
//	        DocumentSearchRequest payload = new DocumentSearchRequest();
//
//	        ValidationException exception = assertThrows(ValidationException.class, () -> {
//	            documentService.getDataFromSearch(entityName, payload, 0, 10, null, null);
//	        });
//
//	        assertEquals("SearchCriteria can not be empty", exception.getMessage());
//	    }
}