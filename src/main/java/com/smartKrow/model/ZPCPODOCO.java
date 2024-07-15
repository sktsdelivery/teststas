package com.smartKrow.model;

import java.util.Date;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedTimestamp;

import lombok.Data;

@Data
@DynamoDBTable(tableName = "ZPCPODOCO")
public class ZPCPODOCO extends CommonFields{

	@DynamoDBHashKey
	@DynamoDBAutoGeneratedKey
	@DynamoDBAttribute(attributeName = "document_id")
	private String documentId;

	@DynamoDBAttribute(attributeName = "application_type")
	private String applicationType;

	@DynamoDBAttribute(attributeName = "PO_NUMBER")
	private String poNumber;

	//@DynamoDBTypeConvertedTimestamp
	@DynamoDBAttribute(attributeName = "CREATION_DATE")
	private String creationDate;

	@DynamoDBAttribute(attributeName = "CREATION_TIME")
	private Long creationTime;

	@DynamoDBAttribute(attributeName = "ARCHIVE_DOC_ID")
	private String archiveDocId;

	@DynamoDBAttribute(attributeName = "FNCA_PO_TYPE")
	private String fncaPoType;

	@DynamoDBAttribute(attributeName = "COCD_DESCRIPTION")
	private String cocdDescription;

	@DynamoDBAttribute(attributeName = "VENDOR_ID")
	private String vendorId;

	@DynamoDBAttribute(attributeName = "VENDOR_NAME")
	private String vendorName;

	@DynamoDBAttribute(attributeName = "PO_TYPE_DESCRIPTION")
	private String poTypeDescription;

	@DynamoDBAttribute(attributeName = "COMPANY_CODE")
	private String companyCode;

	@DynamoDBAttribute(attributeName = "DOCUMENT_DATE")
	private String documentDate;

	@DynamoDBAttribute(attributeName = "NET_AMT")
	private String netAmt;

	@DynamoDBAttribute(attributeName = "CURRENCY")
	private String currency;

	@DynamoDBAttribute(attributeName = "PURCHASE_GRP")
	private String purchseGrp;

	@DynamoDBAttribute(attributeName = "PO_CREATOR")
	private String poCreator;

	@DynamoDBAttribute(attributeName = "PURCHASING_ORG")
	private String purchasingOrg;

	@DynamoDBAttribute(attributeName = "POCREATION")
	private String poCreation;

	@DynamoDBAttribute(attributeName = "ENTERED_BY")
	private String enteredBy;

	//@DynamoDBTypeConvertedTimestamp
	@DynamoDBAttribute(attributeName = "ENTRY_DATE")
	private String entryDate;

	@DynamoDBAttribute(attributeName = "DOCUMENT_LINK")
	private String documentLink;

}
