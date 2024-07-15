package com.smartKrow.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartKrow.service.DynamoDBService;

@RestController
@RequestMapping("/api/test")
public class TestControllerDynamo {

	@Autowired
	private DynamoDBService dynamoDBService;

	@GetMapping("/get")

	public ResponseEntity<List<String>> create() {
		return new ResponseEntity<>(dynamoDBService.getAllTables(), HttpStatus.OK);
	}

}
