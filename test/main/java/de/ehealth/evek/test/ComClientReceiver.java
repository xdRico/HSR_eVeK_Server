package de.ehealth.evek.test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import de.ehealth.evek.entity.Address;
import de.ehealth.evek.entity.Insurance;
import de.ehealth.evek.entity.InsuranceData;
import de.ehealth.evek.entity.Patient;
import de.ehealth.evek.entity.ServiceProvider;
import de.ehealth.evek.entity.TransportDetails;
import de.ehealth.evek.entity.TransportDocument;
import de.ehealth.evek.entity.User;
import de.ehealth.evek.network.interfaces.IComClientReceiver;

public class ComClientReceiver implements IComClientReceiver{

	private final Socket server;
	private final ObjectInputStream objReader;
	
	public ComClientReceiver(Socket server) throws IOException {
		this.server = server;
		objReader = new ObjectInputStream(server.getInputStream());
	}
	
	public Address receiveAddress() throws Exception {
		Object object = objReader.readObject();
		if(object instanceof Address) 
			return (Address) object;
		else throw new WrongObjectTypeException(Address.class, object);
	}
	
	public Insurance receiveInsurance() throws Exception {
		Object object = objReader.readObject();
		if(object instanceof Insurance) 
			return (Insurance) object;
		else throw new WrongObjectTypeException(Insurance.class, object);
	}
	
	public InsuranceData receiveInsuranceData() throws Exception {
		Object object = objReader.readObject();
		if(object instanceof InsuranceData) 
			return (InsuranceData) object;
		else throw new WrongObjectTypeException(InsuranceData.class, object);
	}
	
//	public Invoice receiveInvoice() throws Exception {
//		Object object = objReader.readObject();
//		if(object instanceof Invoice) 
//			return (Invoice) object;
//		else throw new WrongObjectTypeException(Invoice.class, object);
//	}
	
	public Patient receivePatient() throws Exception {
		Object object = objReader.readObject();
		if(object instanceof Patient) 
			return (Patient) object;
		else throw new WrongObjectTypeException(Patient.class, object);
	}
	
//	public Protocol receiveProtocol() throws Exception {
//		Object object = objReader.readObject();
//		if(object instanceof Protocol) 
//			return (Protocol) object;
//		else throw new WrongObjectTypeException(Protocol.class, object);
//	}
	
	public ServiceProvider receiveServiceProvider() throws Exception {
		Object object = objReader.readObject();
		if(object instanceof ServiceProvider) 
			return (ServiceProvider) object;
		else throw new WrongObjectTypeException(ServiceProvider.class, object);
	}
	
	public TransportDetails receiveTransportDetails() throws Exception {
		Object object = objReader.readObject();
		if(object instanceof TransportDetails) 
			return (TransportDetails) object;
		else throw new WrongObjectTypeException(TransportDetails.class, object);
	}
	
	public TransportDocument receiveTransportDocument() throws Exception {
		Object object = objReader.readObject();
		if(object instanceof TransportDocument) 
			return (TransportDocument) object;
		else throw new WrongObjectTypeException(TransportDocument.class, object);
	}
	
	public User receiveUser() throws Exception {
		Object object = objReader.readObject();
		if(object instanceof User) 
			return (User) object;
		else throw new WrongObjectTypeException(User.class, object);
	}
}
