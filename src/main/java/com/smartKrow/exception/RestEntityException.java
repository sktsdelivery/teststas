package com.smartKrow.exception;


import java.util.Locale;

public class RestEntityException extends LocalizedException {

	/**
	 *
	 */
	private static final long serialVersionUID = 6313732668465543866L;

	public RestEntityException(String messageCode) {
		super(messageCode);
		// TODO Auto-generated constructor stub
	}

	public RestEntityException(String messageCode, Locale locale) {
		super(messageCode, locale);
		// TODO Auto-generated constructor stub
	}

	public RestEntityException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public RestEntityException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public RestEntityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
