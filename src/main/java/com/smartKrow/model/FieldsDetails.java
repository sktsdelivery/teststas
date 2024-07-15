package com.smartKrow.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBDocument
public class FieldsDetails {
	
	@DynamoDBAttribute(attributeName = "fieldName")
	private String fieldName;
	
	@DynamoDBAttribute(attributeName = "displayName")
    private String displayName;
	
	@DynamoDBAttribute(attributeName = "dataType")
    private String dataType;
	
	@DynamoDBAttribute(attributeName = "isForSearchGrid")
    private Boolean isForSearchGrid;
	
	@DynamoDBAttribute(attributeName = "isForSort")
    private Boolean isForSort;
	
	@DynamoDBAttribute(attributeName = "isMandatory")
    private Boolean isMandatory;
	
	@DynamoDBAttribute(attributeName = "isForSearch")
    private Boolean isForSearch;
	
	@DynamoDBAttribute(attributeName = "status")
    private String status;

}
