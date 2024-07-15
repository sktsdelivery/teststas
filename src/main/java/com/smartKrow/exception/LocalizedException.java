package com.smartKrow.exception;

import java.util.Locale;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalizedException extends Exception {

	private static final long serialVersionUID = 1L;

	private String messageCode = null;
	private Locale locale = null;
	private String msg = null;

	public LocalizedException(String messageCode) {
		this(messageCode, Locale.getDefault());
	}

	public LocalizedException(String messageCode, Locale locale) {
		this.messageCode = messageCode;
		this.locale = locale;
	}

	/**
	 * @return a localized message based on the messageCode provided at
	 *         instantiation.
	 */
	public String getMessage() {

		/*
		 * This is a deliberate role reversal of the default implementation of
		 * getLocalizedMessage. some logging frameworks like Log4J 1 & 2 and Logback
		 * will use getMessage instead of getLocalizedMessage when logging Throwables.
		 * If we want to use these frameworks in client applications to log localized
		 * messages, then we'll need to override getMessage in a similar fashion to
		 * return the appropriate content. Or, you can call getLocalizedMessage on your
		 * own to create the log content.
		 */
		return getLocalizedMessage();
	}

	/**
	 * @return a localized message based on the messageKey provided at
	 *         instantiation.
	 */
	public String getLocalizedMessage() {

		/*
		 * java.util.logging uses getLocalizedMessage when logging Throwables.
		 */
		if (locale == null) {
			locale = Locale.getDefault();
		}
		return getLocalizedMessage(locale);
	}

	public String getLocalizedMessage(Locale locale) {

		if (msg != null) {
			return msg;
		}

		msg = messageCode;
		try {

		} catch (Exception e) {
			log.error(e.toString());
		}
		return msg;
	}

	public LocalizedException(Throwable cause) {
		super(cause);
	}

	public LocalizedException(String message, Throwable cause) {
		super(message, cause);
	}

	public LocalizedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
