package de.ehealth.evek.network.interfaces;

import java.util.List;

import de.ehealth.evek.entity.Address;
import de.ehealth.evek.entity.Insurance;
import de.ehealth.evek.entity.InsuranceData;
import de.ehealth.evek.entity.Patient;
import de.ehealth.evek.entity.ServiceProvider;
import de.ehealth.evek.entity.TransportDetails;
import de.ehealth.evek.entity.TransportDocument;
import de.ehealth.evek.entity.User;

public interface IComClientReceiver {
	
	public Address receiveAddress() throws Exception;
	
	public Insurance receiveInsurance() throws Exception;
	
	public InsuranceData receiveInsuranceData() throws Exception;
	
//	public Invoice receiveInvoice() throws Exception;
	
	public Patient receivePatient() throws Exception;
	
//	public Protocol receiveProtocol() throws Exception;
	
	public ServiceProvider receiveServiceProvider() throws Exception;
	
	public TransportDetails receiveTransportDetails() throws Exception;
	
	public TransportDocument receiveTransportDocument() throws Exception;
	
	public User receiveUser() throws Exception;
	
	public Throwable receiveException() throws Exception;
	
	public List<?> receiveList() throws Exception;
	
}
