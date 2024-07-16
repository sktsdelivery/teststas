package com.archiveapi.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.archiveapi.dto.BulkDownloadRequest;
import com.archiveapi.dto.BulkDownloadResponse;
import com.archiveapi.dto.PresignedUrlResponse;
import com.archiveapi.service.BulkDownloadCSVFiles;
import com.archiveapi.service.FileDownloadAndProcessService;

import jakarta.validation.ValidationException;

@RestController
@RequestMapping("/api/fileDownloadAndProcess")
public class FileDownloadAndProcessController {

	// @Value("${app.dir.fileupload.temp}")
	private String folderName = "data.csv";

	@Autowired
	private FileDownloadAndProcessService fileDownloadAndProcessService;

	@Autowired
	private BulkDownloadCSVFiles bulkDownloadCSVFiles;

	@PostMapping("/presignedUrl")
	private ResponseEntity<PresignedUrlResponse> getPresignedUrl(
			@RequestParam(value = "downloadLink") String downloadLink) throws ValidationException {
		return new ResponseEntity<>(fileDownloadAndProcessService.getPresignedUrl(downloadLink), HttpStatus.OK);
	}

	@PostMapping("/processBuldDownlload")
	private ResponseEntity<PresignedUrlResponse> processBuldDownlload(
			@RequestParam(value = "entityName") String entityName, @RequestBody BulkDownloadRequest request)
			throws ValidationException {
		return new ResponseEntity<>(fileDownloadAndProcessService.saveBulkRecordsForZiping(entityName, request),
				HttpStatus.OK);
	}

	@GetMapping("/bulkDownloadProcessStatus")
	private ResponseEntity<BulkDownloadResponse> bulkDownloadProcessStatus() throws IOException {
		return new ResponseEntity<>(fileDownloadAndProcessService.bulkDownloadProcessStatus(null), HttpStatus.OK);
	}

//	@PostMapping("/importdata")
//	public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file,
//			@RequestParam(value = "entityName") String entityName) throws IOException {
//
//		if (!folderName.endsWith(File.separator)) {
//			folderName = folderName + File.separator;
//		}
//		Path uploadDir = Paths.get(folderName).toAbsolutePath().normalize();
//		// Path filePath = uploadDir.resolve(RandomString.make(4) + "_" +
//		// file.getOriginalFilename());
//		Files.copy(file.getInputStream(), uploadDir, StandardCopyOption.REPLACE_EXISTING);
//		bulkDownloadCSVFiles.importFiles(uploadDir.toString(), entityName);
//		return new ResponseEntity<>("Success", HttpStatus.ACCEPTED);
//	}

	@GetMapping("/test123")
	private ResponseEntity<String> test123() throws IOException {
		fileDownloadAndProcessService.startZippingProcess();
		return new ResponseEntity<>("Success", HttpStatus.OK);
	}

}
