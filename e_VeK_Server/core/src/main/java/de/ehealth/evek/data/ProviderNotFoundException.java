package de.ehealth.evek.data;


public class ProviderNotFoundException extends ClassNotFoundException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5101669316701672426L;

	public ProviderNotFoundException(String provider) {
		super(String.format("Class of type %s could not be provided!", provider));
	}
	
}
