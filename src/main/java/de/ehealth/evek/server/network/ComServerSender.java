package de.ehealth.evek.server.network;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import de.ehealth.evek.api.entity.Address;
import de.ehealth.evek.api.entity.Insurance;
import de.ehealth.evek.api.entity.InsuranceData;
import de.ehealth.evek.api.entity.Patient;
import de.ehealth.evek.api.entity.ServiceProvider;
import de.ehealth.evek.api.entity.TransportDetails;
import de.ehealth.evek.api.entity.TransportDocument;
import de.ehealth.evek.api.entity.User;
import de.ehealth.evek.api.network.ComEncryptionKey;
import de.ehealth.evek.api.network.interfaces.ComSender;
import de.ehealth.evek.api.network.interfaces.IComServerSender;

public class ComServerSender extends ComSender implements IComServerSender {
	
	public ComServerSender(Socket client) throws IOException {
		super(client);
	}
	
	public void sendPCUser(User.LoginUser loginUser) throws IOException {
		sendAsObject(loginUser);
	}
	
	public void send(Address cmd) throws IOException {
		sendAsObject(cmd); 
	}
	
	public void send(Insurance cmd) throws IOException {
		sendAsObject(cmd);
	}
	
	public void send(InsuranceData cmd) throws IOException {
		sendAsObject(cmd);
	}
	
//	public void send(Invoice cmd) throws IOException {
//		sendAsObject(cmd);
//	}
	
	public void send(Patient cmd) throws IOException {
		sendAsObject(cmd);
	}
	
//	public void send(Protocol cmd) throws IOException {
//		sendAsObject(cmd);
//	}
	
	public void send(ServiceProvider cmd) throws IOException {
		sendAsObject(cmd);
	}
	
	public void send(TransportDetails cmd) throws IOException {
		sendAsObject(cmd);
	}
	
	public void send(TransportDocument cmd) throws IOException {
		sendAsObject(cmd);
	}
	
	public void send(User cmd) throws IOException {
		sendAsObject(cmd);
	}

	public void send(Throwable e) throws IOException {
		sendAsObject(e);
	}
	
	public void send(ArrayList<?> list) throws IOException {
		sendAsObject(list);
	}
	
	void setPublicKey(ComEncryptionKey publicKey) {
		this.publicKey = publicKey;
	}
}
