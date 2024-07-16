package com.archiveapi.jobs;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.archiveapi.service.FNCABulkImportCSVFile;

@Component
public class CsvFileImportJob {

	private static final Logger log = LogManager.getLogger(CsvFileImportJob.class);

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
