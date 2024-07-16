package com.archiveapi.dto;

import java.util.List;

import lombok.Data;

@Data
public class BulkDownloadResponse {

	private Boolean isSuccess;

	private List<HistoryResponse> data;
	
	private ErrorDetails errorDetails ;

}
