package de.ehealth.evek.network.interfaces;

import java.io.IOException;

import de.ehealth.evek.entity.Address;
import de.ehealth.evek.entity.Insurance;
import de.ehealth.evek.entity.InsuranceData;
import de.ehealth.evek.entity.Patient;
import de.ehealth.evek.entity.ServiceProvider;
import de.ehealth.evek.entity.TransportDetails;
import de.ehealth.evek.entity.TransportDocument;
import de.ehealth.evek.entity.User;

public interface IComClientSender extends IComSender {

	void sendAddress(Address.Command cmd) throws IOException;
	
	void sendInsurance(Insurance.Command cmd) throws IOException;
	
	void sendInsuranceData(InsuranceData.Command cmd) throws IOException;
	
//	void sendInvoice(Invoice.Command cmd) throws IOException;
	
	void sendPatient(Patient.Command cmd) throws IOException;
	
//	void sendProtocol(Protocol.Command cmd) throws IOException;
	
	void sendServiceProvider(ServiceProvider.Command cmd) throws IOException;
	
	void sendTransportDetails(TransportDetails.Command cmd) throws IOException;
	
	void sendTransportDocument(TransportDocument.Command cmd) throws IOException;
	
	void sendUser(User.Command cmd) throws IOException;

	default void loginUser(String username, String password) throws IOException {
		sendUser(new User.LoginUser(username, password));
	}
}
