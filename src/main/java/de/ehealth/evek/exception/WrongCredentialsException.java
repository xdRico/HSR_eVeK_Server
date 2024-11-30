package de.ehealth.evek.exception;

public class WrongCredentialsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -759410424357382732L;

	private Exception cause = null;
	
	public WrongCredentialsException() {
	}
	
	public WrongCredentialsException(Exception cause) {
		this.cause = cause;
	}
	
	public Exception getCause(){
		return cause;
	}
	
}
