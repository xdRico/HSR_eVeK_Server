package de.ehealth.evek.test;

import java.io.IOException;
import java.lang.reflect.Type;

public class WrongObjectTypeException extends IOException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2003340380324984292L;
	
	final Object receivedObject;
	
	public WrongObjectTypeException(String message, Type expectedType, Object receivedObject) {
		super(message);
		this.receivedObject = receivedObject;
	}
	public WrongObjectTypeException(Type expectedType, Object receivedObject) {
		this.receivedObject = receivedObject;
	}
}
