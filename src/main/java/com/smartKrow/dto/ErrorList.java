package com.smartKrow.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorList {
	private Integer errorCode;
	private List<String> errorMessage;

}
