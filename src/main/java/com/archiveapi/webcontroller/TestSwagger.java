package com.archiveapi.webcontroller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestSwagger {

	@GetMapping("/")
	public ResponseEntity<String> getTestSwgger() {
		return new ResponseEntity<>("SuccessfullyTested", HttpStatus.OK);
	}

}
