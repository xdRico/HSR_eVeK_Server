package de.ehealth.evek.network.interfaces;

import de.ehealth.evek.entity.Address;
import de.ehealth.evek.entity.Insurance;
import de.ehealth.evek.entity.InsuranceData;
import de.ehealth.evek.entity.Patient;
import de.ehealth.evek.entity.ServiceProvider;
import de.ehealth.evek.entity.TransportDetails;
import de.ehealth.evek.entity.TransportDocument;
import de.ehealth.evek.entity.User;
import de.ehealth.evek.exception.UserNotProvidedException;
import de.ehealth.evek.util.Log;

public interface IComServerReceiver {

	default boolean receiveObject(Object inputObject) throws Throwable {
		if(customHandleInput(inputObject))
			return true;
		
		if(inputObject instanceof User.LoginUser) 
			return setProcessingUser((User.LoginUser) inputObject);
		if(inputObject instanceof User.CreateFull) {
			process((User.CreateFull) inputObject);
			return true;
		}
		
		if(!hasProcessingUser())
			throw new UserNotProvidedException();
		
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
	
	boolean setProcessingUser(User.LoginUser user);
	
	boolean hasProcessingUser();
	
	default boolean customHandleInput(Object inputObject) {
		Log.sendMessage(String.format("	Object of Type %s has been recieved!", inputObject.getClass()));
		return false;
	}
	
	void process(Address.Command cmd) throws Throwable;
	void process(Insurance.Command cmd) throws Throwable;
	void process(InsuranceData.Command cmd) throws Throwable;
//	void process(Invoice.Command cmd) throws Throwable;
	void process(Patient.Command cmd) throws Throwable;
//	void process(Protocol.Command cmd) throws Throwable;
	void process(ServiceProvider.Command cmd) throws Throwable;
	void process(TransportDetails.Command cmd) throws Throwable;
	void process(TransportDocument.Command cmd) throws Throwable;
	void process(User.Command cmd) throws Throwable;

	
}
