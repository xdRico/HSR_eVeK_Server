package de.ehealth.evek.server.network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import de.ehealth.evek.api.entity.Address;
import de.ehealth.evek.api.entity.Insurance;
import de.ehealth.evek.api.entity.InsuranceData;
import de.ehealth.evek.api.entity.Patient;
import de.ehealth.evek.api.entity.ServiceProvider;
import de.ehealth.evek.api.entity.TransportDetails;
import de.ehealth.evek.api.entity.TransportDocument;
import de.ehealth.evek.api.entity.User;
import de.ehealth.evek.api.network.interfaces.IComServerSender;
import de.ehealth.evek.api.type.Reference;
import de.ehealth.evek.api.util.Log;

public class ComServerSender implements IComServerSender {
	
	Reference<User> user;
	ObjectOutputStream objSender;
	
	public ComServerSender(Socket client) throws IOException {
		try {
			//this.outputStream =  new PrintWriter(client.getOutputStream(), true);
			this.objSender = new ObjectOutputStream(client.getOutputStream());
		} catch (IOException e) {
			Log.sendException(e);
			throw e;
		}
	}
	
	public void sendPCUser(User.LoginUser loginUser) throws IOException {
		objSender.writeObject(loginUser);
	}
	
	public void send(Address cmd) throws IOException {
		objSender.writeObject(cmd); 
	}
	
	public void send(Insurance cmd) throws IOException {
		objSender.writeObject(cmd);
	}
	
	public void send(InsuranceData cmd) throws IOException {
		objSender.writeObject(cmd);
	}
	
//	public void send(Invoice cmd) throws IOException {
//	objSender.writeObject(cmd);
//	}
	
	public void send(Patient cmd) throws IOException {
		objSender.writeObject(cmd);
	}
	
//	public void send(Protocol cmd) throws IOException {
//	objSender.writeObject(cmd);
//	}
	
	public void send(ServiceProvider cmd) throws IOException {
		objSender.writeObject(cmd);
	}
	
	public void send(TransportDetails cmd) throws IOException {
		objSender.writeObject(cmd);
	}
	
	public void send(TransportDocument cmd) throws IOException {
		objSender.writeObject(cmd);
	}
	
	public void send(User cmd) throws IOException {
		objSender.writeObject(cmd);
	}

	public void send(Throwable e) throws IOException {
		objSender.writeObject(e);
	}
	
	public void send(List<?> list) throws IOException {
		objSender.writeObject(list);
	}
}
