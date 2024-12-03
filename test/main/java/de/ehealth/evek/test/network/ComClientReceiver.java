package de.ehealth.evek.test.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.List;

import de.ehealth.evek.entity.Address;
import de.ehealth.evek.entity.Insurance;
import de.ehealth.evek.entity.InsuranceData;
import de.ehealth.evek.entity.Patient;
import de.ehealth.evek.entity.ServiceProvider;
import de.ehealth.evek.entity.TransportDetails;
import de.ehealth.evek.entity.TransportDocument;
import de.ehealth.evek.entity.User;
import de.ehealth.evek.exception.WrongObjectTypeException;
import de.ehealth.evek.network.interfaces.IComClientReceiver;

public class ComClientReceiver implements IComClientReceiver{

	private final ObjectInputStream objReader;
	
	public ComClientReceiver(Socket server) throws IOException {
		objReader = new ObjectInputStream(server.getInputStream());
	}
	
	@Override
	public Address receiveAddress() throws Exception {
		Object object = objReader.readObject();
		if(object instanceof Address) 
			return (Address) object;
		throw wrongObjectType(Address.class, object);
	}
	
	@Override
	public Insurance receiveInsurance() throws Exception {
		Object object = objReader.readObject();
		if(object instanceof Insurance) 
			return (Insurance) object;
		throw wrongObjectType(Insurance.class, object);
	}
	
	@Override
	public InsuranceData receiveInsuranceData() throws Exception {
		Object object = objReader.readObject();
		if(object instanceof InsuranceData) 
			return (InsuranceData) object;
		throw wrongObjectType(InsuranceData.class, object);
	}
	
//	public Invoice receiveInvoice() throws Exception {
//		Object object = objReader.readObject();
//		if(object instanceof Invoice) 
//			return (Invoice) object;
//		else throw new WrongObjectTypeException(Invoice.class, object);
//	}
	
	@Override
	public Patient receivePatient() throws Exception {
		Object object = objReader.readObject();
		if(object instanceof Patient) 
			return (Patient) object;
		throw wrongObjectType(Patient.class, object);
	}
	
//	public Protocol receiveProtocol() throws Exception {
//		Object object = objReader.readObject();
//		if(object instanceof Protocol) 
//			return (Protocol) object;
//		else throw new WrongObjectTypeException(Protocol.class, object);
//	}
	
	@Override
	public ServiceProvider receiveServiceProvider() throws Exception {
		Object object = objReader.readObject();
		if(object instanceof ServiceProvider) 
			return (ServiceProvider) object;
		throw wrongObjectType(ServiceProvider.class, object);
	}
	
	@Override
	public TransportDetails receiveTransportDetails() throws Exception {
		Object object = objReader.readObject();
		if(object instanceof TransportDetails) 
			return (TransportDetails) object;
		throw wrongObjectType(TransportDetails.class, object);
	}
	
	@Override
	public TransportDocument receiveTransportDocument() throws Exception {
		Object object = objReader.readObject();
		if(object instanceof TransportDocument) 
			return (TransportDocument) object;
		throw wrongObjectType(TransportDocument.class, object);
	}
	
	@Override
	public User receiveUser() throws Exception {
		Object object = objReader.readObject();
		if(object instanceof User) 
			return (User) object;
		throw wrongObjectType(User.class, object);
	}

	@Override
	public Throwable receiveException() throws Exception {
		Object object = objReader.readObject();
		if(object instanceof Throwable) 
			return (Throwable) object;
		throw wrongObjectType(Throwable.class, object);
	}

	@Override
	public List<?> receiveList() throws Exception {
		Object object = objReader.readObject();
		if(object instanceof List<?>) 
			return (List<?>) object;
		throw wrongObjectType(List.class, object);
	}
	
	private static Exception wrongObjectType(Type expected, Object received) {
		if(received instanceof Exception)
			return (Exception) received;
		return new WrongObjectTypeException(expected, received);
	}
}
