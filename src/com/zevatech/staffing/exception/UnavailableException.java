package com.zevatech.staffing.exception;

public class UnavailableException extends RuntimeException {

	public UnavailableException() {
	}

	public UnavailableException(String arg0) {
		super(arg0);
	}

	public UnavailableException(Throwable arg0) {
		super(arg0);
	}

	public UnavailableException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
