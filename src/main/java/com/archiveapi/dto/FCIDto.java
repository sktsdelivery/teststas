package com.archiveapi.dto;

import lombok.Data;

@Data
public class FCIDto {
	private String TABLE_Name;
	private String startDate;
	private String endDate;
	private String scanBatchId;
	private Boolean isSuccess;
	private String note;
	private Integer noOfRecorde;
}
