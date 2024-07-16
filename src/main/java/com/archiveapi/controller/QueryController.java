package com.archiveapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.archiveapi.service.QuerySearch;

@RestController
@RequestMapping("/query")
public class QueryController {

//	@Autowired
//	QuerySearch querySearch;
//
//	@GetMapping("/")
//	public ResponseEntity<String> testQuewry() {
//		querySearch.getSerahcResultTest();
//
//		return new ResponseEntity<>("Success", HttpStatus.OK);
//	}
}
