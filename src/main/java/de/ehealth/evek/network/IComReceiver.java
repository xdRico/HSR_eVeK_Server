package de.ehealth.evek.network;

import de.ehealth.evek.entity.Address;
import de.ehealth.evek.entity.Insurance;
import de.ehealth.evek.entity.InsuranceData;
import de.ehealth.evek.entity.Patient;
import de.ehealth.evek.entity.ServiceProvider;
import de.ehealth.evek.entity.TransportDetails;
import de.ehealth.evek.entity.TransportDocument;
import de.ehealth.evek.entity.User;
import de.ehealth.evek.util.Log;

public interface IComReceiver {

	default boolean receiveObject(Object inputObject) throws Exception {
		if(customHandleInput(inputObject))
			return true;
		
		if(inputObject instanceof User) 
			return setProcessingUser((User) inputObject);
		
		if(inputObject instanceof Address.Command)
			process((Address.Command) inputObject);
		else if(inputObject instanceof Insurance.Command)
			process((Insurance.Command) inputObject);
		else if(inputObject instanceof InsuranceData.Command)
			process((InsuranceData.Command) inputObject);
//		else if(inputObject instanceof Invoice.Command)
//			process((Invoice.Command) inputObject);
		else if(inputObject instanceof Patient.Command)
			process((Patient.Command) inputObject);
//		else if(inputObject instanceof Protocol.Command)
//			process((Protocol.Command) inputObject);
		else if(inputObject instanceof ServiceProvider.Command)
			process((ServiceProvider.Command) inputObject);
		else if(inputObject instanceof TransportDetails.Command)
			process((TransportDetails.Command) inputObject);
		else if(inputObject instanceof TransportDocument.Command)
			process((TransportDocument.Command) inputObject);
		else if(inputObject instanceof User.Command)
			process((User.Command) inputObject);	
		else return false;	
		
		return true;	
	}
	
	boolean setProcessingUser(User user);
	
	default boolean customHandleInput(Object inputObject) {
		Log.sendMessage(String.format("	Object of Type %s has been recieved!", inputObject.getClass()));
		return false;
	}
	
	void process(Address.Command cmd) throws Exception;
	void process(Insurance.Command cmd) throws Exception;
	void process(InsuranceData.Command cmd) throws Exception;
//	void process(Invoice.Command cmd) throws Exception;
	void process(Patient.Command cmd) throws Exception;
//	void process(Protocol.Command cmd) throws Exception;
	void process(ServiceProvider.Command cmd) throws Exception;
	void process(TransportDetails.Command cmd) throws Exception;
	void process(TransportDocument.Command cmd) throws Exception;
	void process(User.Command cmd) throws Exception;

	
}
