package com.archiveapi.dto;

import lombok.Data;

@Data
public class FncaCsvProcessDto {

	private String TABLE_Name;
	private String COCD;
	private String PHC_NUMBER;
	private String DOC_NUMBER;
	private String Year_From;
	private String Year_To;
	private String MAT_DOC;
	private String PO_NUMBER;
	private String VENDOR_ID;
	private String POCREATION;
	private String Name2;
	private String TRANSAC_ID;
	private String START_DATE;
	private String ASSET_NUM;
	private Boolean isSuccess;
	private String note;
	private Integer noOfRecorde ;
}
