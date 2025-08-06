package com.github.taxbeans.exception;

public class TaxBeansException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TaxBeansException(Exception e) {
		super(e);
	}

	public TaxBeansException(String message, Exception e) {
		super(message, e);
	}

	public TaxBeansException(String message) {
		super(message);
	}

}
