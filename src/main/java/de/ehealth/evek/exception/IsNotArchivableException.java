package de.ehealth.evek.exception;

import de.ehealth.evek.entity.TransportDocument;
import de.ehealth.evek.type.Id;

public class IsNotArchivableException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7970434035849987198L;

	private final String message;
	private final Id<TransportDocument> transportDoc;
	
	public IsNotArchivableException(String message, Id<TransportDocument> transportDoc) {
		this.message = message;
		this.transportDoc = transportDoc;
	}
	
	public IsNotArchivableException(Id<TransportDocument> transportDoc) {
		this.message = transportDoc.toString();
		this.transportDoc = transportDoc;
	}
	
	public Id<TransportDocument> getTransportDocument(){
		return transportDoc;
	}
	
	public String getMessage() {
		return message;
	}
	
}
