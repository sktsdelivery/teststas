package com.smartKrow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartKrow.dto.DocumentResponse;
import com.smartKrow.service.DownloadService;

@RestController
@RequestMapping("/api/v1")
public class DownloadController {

	/** The download service. */
	@Autowired
	private DownloadService downloadService;

	/**
	 * Gets the download history.
	 *
	 * @return the download history
	 */
	@GetMapping("/downloads")
	public ResponseEntity<DocumentResponse> getDownloadHistory() {
		return new ResponseEntity<>(downloadService.getDownloadHistory(), HttpStatus.OK);
	}

	/**
	 * Gets the call back from lambda.
	 *
	 * @return the call back from lambda
	 */
	@GetMapping("/downloads/process")
	public ResponseEntity<DocumentResponse> getCallBackFromLambda() {
		return new ResponseEntity<>(downloadService.getCallBackFromLambda(), HttpStatus.OK);
	}

}
