package com.archiveapi.model;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@DynamoDBTable(tableName = "Master_Search_Data_Table")
@EqualsAndHashCode(callSuper = false)
public class MasterSearchDataTable {
	
	@DynamoDBHashKey
	@DynamoDBAutoGeneratedKey
	@DynamoDBAttribute(attributeName = "entity_id")
	private String entityId;
	
	@DynamoDBAttribute(attributeName = "displayName")
	private String displayName;
	
	@DynamoDBAttribute(attributeName = "entityName")
	private String entityName;
	
    
	@DynamoDBAttribute(attributeName = "searchFields")
	private List<FieldsDetails> searchFields;
	
	@DynamoDBAttribute(attributeName = "type")
	private String type;

}
