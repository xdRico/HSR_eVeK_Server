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

public interface IComServerSender extends IComSender {
	
	
	void send(Address cmd) throws IOException;
	
	void send(Insurance cmd) throws IOException;
	
	void send(InsuranceData cmd) throws IOException;
	
//	void send(Invoice cmd) throws IOException;
	
	void send(Patient cmd) throws IOException;
	
//	void send(Protocol cmd) throws IOException;
	
	void send(ServiceProvider cmd) throws IOException;
	
	void send(TransportDetails cmd) throws IOException;

	void send(TransportDocument cmd) throws IOException;

	void send(User cmd) throws IOException;

}
