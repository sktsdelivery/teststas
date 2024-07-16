package com.archiveapi.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ErrorDetails {
	private String errorCode;
	private String errorMessage;
}
