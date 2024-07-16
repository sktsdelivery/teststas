package com.archiveapi.jobs;

import java.io.IOException;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.archiveapi.service.FileDownloadAndProcessService;

@Component
public class BulkFileProcessJob {

	@Autowired
	private FileDownloadAndProcessService fileDownloadAndProcessService;

	private static final Logger log = LogManager.getLogger(BulkFileProcessJob.class);

	@Scheduled(cron = "0 * * * * *")
	public void scheduleTaskUsingCronExpression() {
		log.info("Starting Bulk File Process Job");
		try {
			fileDownloadAndProcessService.startZippingProcess();
		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
		}
		log.info("Stopping Bulk File Process Job");
		log.info("Deletion of  Bulk File Process Job started");
		try {
			fileDownloadAndProcessService.deleteBulkDownloadStatus();
		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
		}
		log.info("Deletion of  Bulk File Process Job ended");
	}
}
