package com.smartKrow;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.archiveapi.controller.DocumentController;
import com.archiveapi.dto.DocumentResponse;
import com.archiveapi.service.DocumentService;

public class DocumentControllerTest {

	@Mock
	private DocumentService documentService;

	@InjectMocks
	private DocumentController documentController;

	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(documentController).build();
	}

	@Test
	public void testGetAllEntities() throws Exception {
		DocumentResponse response = new DocumentResponse();
		when(documentService.getAllEntities()).thenReturn(response);

		mockMvc.perform(get("/api/v1/entity/all")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

		verify(documentService, times(1)).getAllEntities();
	}

//    @Test
//    public void testGetDataFromSearch() throws Exception {
//        String entityName = "entity";
//        DocumentSearchRequest payload = new DocumentSearchRequest();
//        Page<Map<String, Object>> page = new PageImpl<>(Collections.singletonList(Collections.singletonMap("key", "value")));
//        when(documentService.getDataFromSearch(eq(entityName), any(DocumentSearchRequest.class), eq(0), eq(50), eq("creationDate"), eq("asc"))).thenReturn(page);
//
//        mockMvc.perform(post("/api/v1/entity/{entityName}/search", entityName)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(new ObjectMapper().writeValueAsString(payload))
//                .param("page", "0")
//                .param("size", "50")
//                .param("sortBy", "creationDate")
//                .param("sortOrder", "asc"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
//
//        verify(documentService, times(1)).getDataFromSearch(eq(entityName), any(DocumentSearchRequest.class), eq(0), eq(50), eq("creationDate"), eq("asc"));
//    }

//    @Test
//    public void testGetDataFromSearch_InvalidParameters() throws Exception {
//        String entityName = "entity";
//        DocumentSearchRequest payload = new DocumentSearchRequest();
//        when(documentService.getDataFromSearch(eq(entityName), any(DocumentSearchRequest.class), eq(0), eq(50), eq("creationDate"), eq("asc"))).thenThrow(new ValidationException("Validation error"));
//
//        mockMvc.perform(post("/api/v1/entity/{entityName}/search", entityName)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(new ObjectMapper().writeValueAsString(payload))
//                .param("page", "0")
//                .param("size", "50")
//                .param("sortBy", "creationDate")
//                .param("sortOrder", "asc"))
//                .andExpect(status().isBadRequest());
//    }
}
