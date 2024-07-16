package com.archiveapi.controller;

import java.util.Map;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.archiveapi.dto.DocumentResponse;
import com.archiveapi.dto.DocumentSearchRequest;
import com.archiveapi.service.DocumentService;
import com.archiveapi.service.QuerySearch;

import jakarta.xml.bind.ValidationException;

@RestController
@RequestMapping("/api/v1/entity")
public class DocumentController {

	/** The document service. */
	@Autowired
	private DocumentService documentService;

	@Autowired
	private QuerySearch querySearch;

	/**
	 * Gets the all entities.
	 *
	 * @return the all entities
	 * @throws JSONException
	 */
	@GetMapping("/all")
	public ResponseEntity<DocumentResponse> getAllEntities() throws JSONException {
		// return new ResponseEntity<>(masterSearchCriteriaData.getMasterRecords(),
		// HttpStatus.OK);
		return new ResponseEntity<>(documentService.getAllEntities(), HttpStatus.OK);
	}

	/**
	 * Gets the data from search.
	 *
	 * @param entityId the entity id
	 * @param payload  the payload
	 * @param page     the page
	 * @param size     the size
	 * @return the data from search
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws ValidationException
	 */
	@PostMapping("/{entityName}/search")
	public ResponseEntity<Page<Map<String, Object>>> getDataFromSearch(
			@PathVariable(name = "entityName") String entityName, @RequestBody DocumentSearchRequest payload,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size,
			@RequestParam(name = "sortBy", required = false, defaultValue = "creationDate") String sortBy,
			@RequestParam(name = "sortOrder", required = false, defaultValue = "asc") String sortOrder)
			throws IllegalArgumentException, IllegalAccessException, ValidationException {
		return new ResponseEntity<>(querySearch.getSerahcResultTest(payload, entityName, page, size, sortBy, sortOrder),
				HttpStatus.OK);
	}

}