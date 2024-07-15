package com.smartKrow.model;

import java.util.Date;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedTimestamp;

import lombok.Data;

@Data
@DynamoDBTable(tableName = "ZVMVENDORT")
public class ZVMVENDORT extends CommonFields {


	@DynamoDBHashKey
	@DynamoDBAutoGeneratedKey
	@DynamoDBAttribute(attributeName = "document_id")
	private String documentId;

	@DynamoDBAttribute(attributeName = "application_type")
	private String applicationType;

	@DynamoDBAttribute(attributeName = "VENDOR_ID")
	private String vendorId;

	//@DynamoDBTypeConvertedTimestamp
	@DynamoDBAttribute(attributeName = "CREATION_DATE")
	private String creationDate;

	@DynamoDBAttribute(attributeName = "CREATION_TIME")
	private Long creationTime;

	@DynamoDBAttribute(attributeName = "ARCHIVE_DOC_ID")
	private String archiveDocId;

	@DynamoDBAttribute(attributeName = "FNCA_DOC_TYPE")
	private String fncaDocType;

	@DynamoDBAttribute(attributeName = "COCD_DESCRIPTION")
	private String cocdDescription;

	@DynamoDBAttribute(attributeName = "VENDOR_NAME1")
	private String vendorName1;

	@DynamoDBAttribute(attributeName = "NAME2")
	private String name2;
	@DynamoDBAttribute(attributeName = "NAME3")
	private String name3;
	@DynamoDBAttribute(attributeName = "NAME4")
	private String name4;
	@DynamoDBAttribute(attributeName = "STREET/HOUSE NO")
	private String streetHouseNo;
	@DynamoDBAttribute(attributeName = "POSTAL CODE")
	private String postalCodr;
	@DynamoDBAttribute(attributeName = "CITY")
	private String city;
	@DynamoDBAttribute(attributeName = "COUNTRY")
	private String country;
	@DynamoDBAttribute(attributeName = "REGION")
	private String region;
	@DynamoDBAttribute(attributeName = "DOCUMENT_LINK")
	private String documentLink;

}
