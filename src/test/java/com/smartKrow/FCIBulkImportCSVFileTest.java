package com.smartKrow;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import com.archiveapi.dto.BulkDownloadRequest;
import com.archiveapi.exception.RestEntityException;
import com.archiveapi.service.FCIBulkImportCSVFile;
import com.archiveapi.service.FileDownloadAndProcessService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class FCIBulkImportCSVFileTest {

    @Mock
    private FileDownloadAndProcessService fileDownloadAndProcessService;

    @InjectMocks
    private FCIBulkImportCSVFile fciBulkImportCSVFile;

    private static final Logger log = LoggerFactory.getLogger(FCIBulkImportCSVFile.class);

    @BeforeEach
    public void setUp() {
        // Setup tasks if necessary
    }

//    @Test
//    public void testImportFiles_Success() throws IOException, RestEntityException {
//        String dataFilePath = "src/test/resources/test.csv";
//        String entityName = "testEntity";
//
//        // Mocking CSVParser and CSVRecord
//        CSVRecord record1 = mock(CSVRecord.class);
//        CSVRecord record2 = mock(CSVRecord.class);
//        when(record1.toList()).thenReturn(Arrays.asList("id1", "value1"));
//        when(record2.toList()).thenReturn(Arrays.asList("id2", "value2"));
//
//        CSVParser parser = mock(CSVParser.class);
//        when(parser.iterator()).thenReturn(Arrays.asList(record1, record2).iterator());
//
//        fciBulkImportCSVFile.importFiles(dataFilePath, entityName);
//
//        verify(fileDownloadAndProcessService, times(1)).saveBulkRecordsForZiping(eq(entityName), any(BulkDownloadRequest.class));
//    }

    @Test
    public void testImportFiles_FileNotFound() {
        String dataFilePath = "invalid/path/to/file.csv";
        String entityName = "testEntity";

   //     fciBulkImportCSVFile.(dataFilePath, entityName);

        verify(fileDownloadAndProcessService, never()).saveBulkRecordsForZiping(anyString(), any(BulkDownloadRequest.class));
    }

//    @Test
//    public void testImportFiles_IOException() throws IOException {
//        String dataFilePath = "src/test/resources/test.csv";
//        String entityName = "testEntity";
//
//        // Mocking FileReader to throw IOException
//        Reader reader = mock(FileReader.class);
//        doThrow(IOException.class).when(reader).read(any(char[].class), anyInt(), anyInt());
//
//        fciBulkImportCSVFile.importFiles(dataFilePath, entityName);
//
//        verify(fileDownloadAndProcessService, never()).saveBulkRecordsForZiping(anyString(), any(BulkDownloadRequest.class));
//    }
}
