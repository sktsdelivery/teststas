package com.smartKrow.jobs;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.smartKrow.service.FileDownloadAndProcessService;

@Component
public class BulkFileProcessJob {

	@Autowired
	private FileDownloadAndProcessService fileDownloadAndProcessService;

	private static final Logger log = LoggerFactory.getLogger(BulkFileProcessJob.class);

	@Scheduled(cron = "0 * * * * *")
	public void scheduleTaskUsingCronExpression() {
		log.info("Starting Bulk File Process Job");
		try {
			fileDownloadAndProcessService.startZippingProcess();
		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
		}
		log.info("Stopping Bulk File Process Job");
	}
}
