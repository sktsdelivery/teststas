package com.archiveapi.service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.archiveapi.dto.BulkDownloadRequest;
import com.archiveapi.exception.RestEntityException;

import jakarta.validation.ValidationException;

@Service
public class BulkDownloadCSVFiles {

	private static final Logger log = LogManager.getLogger(DocumentService.class);

	@Autowired
	private FileDownloadAndProcessService fileDownloadAndProcessService;

	public void importFiles(String dataFilePath, String entityName) {
		log.info("Started the CSV Import at: " + Date.from(Instant.now()));
		try {
			CSVFormat custom = CSVFormat.DEFAULT.builder().setIgnoreEmptyLines(true).build();

			Reader is = new FileReader(dataFilePath);
			CSVParser parser = new CSVParser(is, custom);

			importZipProcessFile(parser, entityName);
		} catch (FileNotFoundException e) {
			log.error(e.toString(), e);
			e.printStackTrace();
		} catch (Exception e) {
			log.error(e.toString(), e);
			e.printStackTrace();
		}

		log.info("Stopping the Zip CSV Import at: " + Date.from(Instant.now()));

	}

	private void importZipProcessFile(CSVParser parser, String entityName) throws IOException, RestEntityException {
		int rowCount = 0;
		List<String> row = null;
		List<String> documentIds = new ArrayList<>();

		BulkDownloadRequest request = new BulkDownloadRequest();
		for (CSVRecord record : parser) {
			try {
				row = record.toList();
				if (rowCount <= 0) {
					rowCount++;
					continue;
				}
				log.info("------------------>  Import Processing Row: " + rowCount);
				try {

					String documentId = prepareRecordForBulkProcessZipFile(row);
					documentIds.add(documentId);
					rowCount++;

				} catch (ValidationException e) {
					log.error(e.getLocalizedMessage());
					e.printStackTrace();
				} catch (Exception e) {
					log.error(e.toString());
					e.printStackTrace();
				}
			} catch (Exception e1) {
				log.info("Problem in parsing the file." + e1);
			}

		}
		request.setDocumentIds(documentIds);
		log.info("Starting saveBulkRecordsForZiping");
		fileDownloadAndProcessService.saveBulkRecordsForZiping(entityName, request);
	}

	private String prepareRecordForBulkProcessZipFile(List<String> row) {
		return row.get(0);
	}

}
