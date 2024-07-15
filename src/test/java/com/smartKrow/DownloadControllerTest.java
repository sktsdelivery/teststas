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

import com.smartKrow.controller.DownloadController;
import com.smartKrow.dto.DocumentResponse;
import com.smartKrow.service.DownloadService;

public class DownloadControllerTest {

    @Mock
    private DownloadService downloadService;

    @InjectMocks
    private DownloadController downloadController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(downloadController).build();
    }

    @Test
    public void testGetDownloadHistory() throws Exception {
        DocumentResponse response = new DocumentResponse();
        when(downloadService.getDownloadHistory()).thenReturn(response);

        mockMvc.perform(get("/api/v1/downloads"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(downloadService, times(1)).getDownloadHistory();
    }

    @Test
    public void testGetCallBackFromLambda() throws Exception {
        DocumentResponse response = new DocumentResponse();
        when(downloadService.getCallBackFromLambda()).thenReturn(response);

        mockMvc.perform(get("/api/v1/downloads/process"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(downloadService, times(1)).getCallBackFromLambda();
    }

//    @Test
//    public void testGetDownloadHistory_Error() throws Exception {
//        when(downloadService.getDownloadHistory()).thenThrow(new RuntimeException("Error"));
//
//        mockMvc.perform(get("/api/v1/downloads"))
//                .andExpect(status().isInternalServerError());
//
//        verify(downloadService, times(1)).getDownloadHistory();
//    }

//    @Test
//    public void testGetCallBackFromLambda_Error() throws Exception {
//        when(downloadService.getCallBackFromLambda()).thenThrow(new RuntimeException("Error"));
//
//        mockMvc.perform(get("/api/v1/downloads/process"))
//                .andExpect(status().isInternalServerError());
//
//        verify(downloadService, times(1)).getCallBackFromLambda();
//    }
}
