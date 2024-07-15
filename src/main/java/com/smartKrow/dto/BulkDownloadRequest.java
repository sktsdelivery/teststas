package com.smartKrow.dto;

import java.util.List;

import lombok.Data;

@Data
public class BulkDownloadRequest {

	private List<String> documentIds;

}
