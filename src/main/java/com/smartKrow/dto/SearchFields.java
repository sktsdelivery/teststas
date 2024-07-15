package com.smartKrow.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class SearchFields {

	private String documentId;
	private String fieldName;
	private String displayName;
	private Long minValue;
	private Long maxValue;
	private String status;
	private Object value;
	private String dataType;
	private Boolean isMandatory;
    private Boolean isForSearchGrid;
    private Boolean isForSort;
    private Boolean isForSearch;
	
}