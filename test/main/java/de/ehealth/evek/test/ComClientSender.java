package de.ehealth.evek.test;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import de.ehealth.evek.entity.Address;
import de.ehealth.evek.entity.Insurance;
import de.ehealth.evek.entity.InsuranceData;
import de.ehealth.evek.entity.Patient;
import de.ehealth.evek.entity.ServiceProvider;
import de.ehealth.evek.entity.TransportDetails;
import de.ehealth.evek.entity.TransportDocument;
import de.ehealth.evek.entity.User;
import de.ehealth.evek.network.interfaces.IComClientSender;
import de.ehealth.evek.type.Reference;
import de.ehealth.evek.util.Log;

public class ComClientSender implements IComClientSender {
	
	Reference<User> user;
	ObjectOutputStream objSender;
	
	public ComClientSender(Socket client) throws IOException {
		try {
			//this.outputStream =  new PrintWriter(client.getOutputStream(), true);
			this.objSender = new ObjectOutputStream(client.getOutputStream());
		} catch (IOException e) {
			Log.sendException(e);
			throw e;
		}
	}
	
	public void send(User pcUser) throws IOException {
		objSender.writeObject(pcUser);
	}
	
	public void send(Address.Command cmd) throws IOException {
		objSender.writeObject(cmd); 
	}
	
	public void send(Insurance.Command cmd) throws IOException {
		objSender.writeObject(cmd);
	}
	
	public void send(InsuranceData.Command cmd) throws IOException {
		objSender.writeObject(cmd);
	}
	
//	public void send(Invoice.Command cmd) throws IOException {
//	objSender.writeObject(cmd);
//	}
	
	public void send(Patient.Command cmd) throws IOException {
		objSender.writeObject(cmd);
	}
	
//	public void send(Protocol.Command cmd) throws IOException {
//	objSender.writeObject(cmd);
//	}
	
	public void send(ServiceProvider.Command cmd) throws IOException {
		objSender.writeObject(cmd);
	}
	
	public void send(TransportDetails.Command cmd) throws IOException {
		objSender.writeObject(cmd);
	}
	
	public void send(TransportDocument.Command cmd) throws IOException {
		objSender.writeObject(cmd);
	}
	
	public void send(User.Command cmd) throws IOException {
		objSender.writeObject(cmd);
	}

	@Override
	public void sendPCUser(User pcUser) throws IOException {
		// TODO Auto-generated method stub
		
	}
}
