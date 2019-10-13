package org.jrichardsz.jenkins.plugins.easywebhook.exceptions;

public class RequiredParameterWasNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public RequiredParameterWasNotFoundException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public RequiredParameterWasNotFoundException(Throwable throwable) {
		super(throwable);
	}

	public RequiredParameterWasNotFoundException(String message) {
		super(message);
	}
}
