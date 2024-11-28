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

	void send(Address.Command cmd) throws IOException;
	
	void send(Insurance.Command cmd) throws IOException;
	
	void send(InsuranceData.Command cmd) throws IOException;
	
//	void send(Invoice.Command cmd) throws IOException;
	
	void send(Patient.Command cmd) throws IOException;
	
//	void send(Protocol.Command cmd) throws IOException;
	
	void send(ServiceProvider.Command cmd) throws IOException;
	
	void send(TransportDetails.Command cmd) throws IOException;
	
	void send(TransportDocument.Command cmd) throws IOException;
	
	void send(User.Command cmd) throws IOException;

	default void loginUser(String username, String password) throws IOException {
		send(new User.LoginUser(username, password));
	}
}
