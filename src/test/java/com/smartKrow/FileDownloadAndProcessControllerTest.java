package com.smartKrow;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartKrow.controller.FileDownloadAndProcessController;
import com.smartKrow.dto.BulkDownloadRequest;
import com.smartKrow.dto.BulkDownloadResponse;
import com.smartKrow.dto.PresignedUrlResponse;
import com.smartKrow.service.BulkDownloadCSVFiles;
import com.smartKrow.service.FileDownloadAndProcessService;

import jakarta.validation.ValidationException;

public class FileDownloadAndProcessControllerTest {

    @Mock
    private FileDownloadAndProcessService fileDownloadAndProcessService;

    @Mock
    private BulkDownloadCSVFiles bulkDownloadCSVFiles;

    @InjectMocks
    private FileDownloadAndProcessController fileDownloadAndProcessController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(fileDownloadAndProcessController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetPresignedUrl() throws Exception {
        PresignedUrlResponse response = new PresignedUrlResponse();
        when(fileDownloadAndProcessService.getPresignedUrl(anyString())).thenReturn(response);

        mockMvc.perform(post("/api/fileDownloadAndProcess/presignedUrl")
                .param("downloadLink", "someLink"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(fileDownloadAndProcessService, times(1)).getPresignedUrl(anyString());
    }

    @Test
    public void testProcessBuldDownlload() throws Exception {
        PresignedUrlResponse response = new PresignedUrlResponse();
        BulkDownloadRequest request = new BulkDownloadRequest();
        when(fileDownloadAndProcessService.saveBulkRecordsForZiping(anyString(), any(BulkDownloadRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/fileDownloadAndProcess/processBuldDownlload")
                .param("entityName", "someEntity")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(fileDownloadAndProcessService, times(1)).saveBulkRecordsForZiping(anyString(), any(BulkDownloadRequest.class));
    }

    @Test
    public void testBulkDownloadProcessStatus() throws Exception {
        BulkDownloadResponse response = new BulkDownloadResponse();
        when(fileDownloadAndProcessService.bulkDownloadProcessStatus(null)).thenReturn(response);

        mockMvc.perform(get("/api/fileDownloadAndProcess/bulkDownloadProcessStatus"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(fileDownloadAndProcessService, times(1)).bulkDownloadProcessStatus(null);
    }

    @Test
    public void testTest123() throws Exception {
        doNothing().when(fileDownloadAndProcessService).startZippingProcess();

        mockMvc.perform(get("/api/fileDownloadAndProcess/test123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Success"));

        verify(fileDownloadAndProcessService, times(1)).startZippingProcess();
    }

//    @Test
//    public void testGetPresignedUrl_ValidationException() throws Exception {
//        when(fileDownloadAndProcessService.getPresignedUrl(anyString())).thenThrow(new ValidationException("Invalid URL"));
//
//        mockMvc.perform(post("/api/fileDownloadAndProcess/presignedUrl")
//                .param("downloadLink", "someLink"))
//                .andExpect(status().isBadRequest());
//
//        verify(fileDownloadAndProcessService, times(1)).getPresignedUrl(anyString());
//    }

//    @Test
//    public void testProcessBuldDownlload_ValidationException() throws Exception {
//        BulkDownloadRequest request = new BulkDownloadRequest();
//        when(fileDownloadAndProcessService.saveBulkRecordsForZiping(anyString(), any(BulkDownloadRequest.class))).thenThrow(new ValidationException("Invalid Request"));
//
//        mockMvc.perform(post("/api/fileDownloadAndProcess/processBuldDownlload")
//                .param("entityName", "someEntity")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest());
//
//        verify(fileDownloadAndProcessService, times(1)).saveBulkRecordsForZiping(anyString(), any(BulkDownloadRequest.class));
//    }

//    @Test
//    public void testBulkDownloadProcessStatus_Exception() throws Exception {
//        when(fileDownloadAndProcessService.bulkDownloadProcessStatus(null)).thenThrow(new IOException("IO Exception"));
//
//        mockMvc.perform(get("/api/fileDownloadAndProcess/bulkDownloadProcessStatus"))
//                .andExpect(status().isInternalServerError());
//
//        verify(fileDownloadAndProcessService, times(1)).bulkDownloadProcessStatus(null);
//    }

//    @Test
//    public void testTest123_Exception() throws Exception {
//        doThrow(new IOException("IO Exception")).when(fileDownloadAndProcessService).startZippingProcess();
//
//        mockMvc.perform(get("/api/fileDownloadAndProcess/test123"))
//                .andExpect(status().isInternalServerError());
//
//        verify(fileDownloadAndProcessService, times(1)).startZippingProcess();
//    }
}
