package com.smartKrow.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.smartKrow.service.FNCABulkImportCSVFile;

@Component
public class CsvFileImportJob {

	private static final Logger log = LoggerFactory.getLogger(CsvFileImportJob.class);

	@Autowired
	private FNCABulkImportCSVFile fnCABulkImportCSVFile;

	@Scheduled(cron = "0 * * * * *")
	public void scheduleTaskUsingCronExpression() {
		log.info("Starting FNCA File Process Job");
		try {
			fnCABulkImportCSVFile.getObjectOfBucket();
		} catch (Exception e) {
			log.error("Something went wrong while Exporting the file" + e.getLocalizedMessage());
		}
		log.info("Stopping FNCA File Process Job");
	}

}
