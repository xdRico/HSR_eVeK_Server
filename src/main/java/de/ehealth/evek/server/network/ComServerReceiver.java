package de.ehealth.evek.server.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;

import de.ehealth.evek.api.entity.Address;
import de.ehealth.evek.api.entity.Insurance;
import de.ehealth.evek.api.entity.InsuranceData;
import de.ehealth.evek.api.entity.Patient;
import de.ehealth.evek.api.entity.ServiceProvider;
import de.ehealth.evek.api.entity.TransportDetails;
import de.ehealth.evek.api.entity.TransportDocument;
import de.ehealth.evek.api.entity.User;
import de.ehealth.evek.api.exception.EncryptionException;
import de.ehealth.evek.api.exception.GetListThrowable;
import de.ehealth.evek.api.exception.WrongCredentialsException;
import de.ehealth.evek.api.network.ComEncryptionKey;
import de.ehealth.evek.api.network.ComEncryption;
import de.ehealth.evek.api.network.IComServerReceiver;
import de.ehealth.evek.api.network.IComServerSender;
import de.ehealth.evek.api.type.Reference;
import de.ehealth.evek.api.util.Debug;
import de.ehealth.evek.api.util.Log;
import de.ehealth.evek.server.data.ITransportManagementService;

public final class ComServerReceiver extends Thread implements IComServerReceiver {

	private boolean isRunning = false;
	
	private final Socket socket;
	private final IComServerSender sender;
	private Reference<User> user;
	private ObjectInputStream objReader;
	private ComEncryption encryption;
	private final ITransportManagementService transportManagementService;
	
	public ComServerReceiver(Socket socket, ITransportManagementService transportManagementService, IComServerSender sender) throws IOException, SocketException {
		this.transportManagementService = transportManagementService;
		this.socket = socket;
		this.sender = sender;
		this.setName(String.format("%s-%s:%d", 
				getClass().getSimpleName().toString(),
				socket.getInetAddress().toString().replace("/", ""),
				socket.getPort()));
		try {
			this.objReader = new ObjectInputStream(socket.getInputStream());
			this.start();

		}catch(IOException e) {
			Log.sendMessage(String.format("	Receiver[%s] could not be initialized!", toString()));
			Debug.sendException(e);	
		} 
	}
	
	@Override
	public void start() {
		try {
			Log.sendMessage(String.format("	Starting up ReceiverThread[%s]...", this.getName()));
			super.start();
		} catch(IllegalThreadStateException e) {
			Log.sendMessage(String.format("	ReceiverThread[%s] could not be started!", this.getName()));
			Debug.sendException(e);
			throw e;
		}
	} 

	@Override
	public void run() {
		isRunning = true;
		Log.sendMessage(String.format("	ReceiverThread[%s] has been successfully started!", this.getName()));
		Object readObj;
		try {
			while(isRunning && objReader != null && (readObj = objReader.readObject()) != null) {
				Log.sendMessage(String.valueOf(readObj));
				if(!receiveObject(readObj))
					Log.sendMessage("Message could not be read!");
			}
		}catch (Throwable e) {
			if(e.getMessage() != "Connection reset" 
					&& !(e instanceof EOFException)) {
				Debug.sendException(e);
				Log.sendMessage(String.format("	ReceiverThread[%s] has been stopped unexpected!", this.getName()));
				isRunning = false;
				return;
			}
		}
		isRunning = false;
		Log.sendMessage(String.format("	ReceiverThread[%s] has been stopped due to lost Connection!", this.getName()));
		return;
	}
	
	@Override
	public String toString() {
		return String.format("IP-Address: %s; Port: %d; Thread: %s", 
				socket.getInetAddress().toString().replace("/", ""), 
				socket.getPort(),
				this.getName());
	}
	
	@Override
	public boolean setProcessingUser(User.LoginUser loginUser) {
		try {
			User user = transportManagementService.process(loginUser, null);
			this.user = Reference.to(user.id().value().toString());
			sender.send(user);
			
			return true;

		} catch(WrongCredentialsException e) {
			try {
				sender.send(e);
				Log.sendMessage("Credentials for user " + loginUser.userName() + " aren't correct!");
			}catch(Exception ex) {
				Log.sendException(ex);
			}
		} catch (Throwable e) {
			Log.sendException(e);
			try {
				sender.send((User) null);
			}catch(Exception ex) {
				Log.sendException(ex);

			}
		}
		return false;
	}
	
	public boolean hasProcessingUser() {
		return user != null;
	}
	
	@Override
	public void process(Address.Command cmd) throws Throwable {
		try{
			sender.send(transportManagementService.process(cmd, user));
		}catch(GetListThrowable t) {
			sender.send(t.getArrayList());
		}catch(Exception e) {
			sender.send(e);
			throw e;
		}
	}

	@Override
	public void process(Insurance.Command cmd) throws Throwable{
		try{
			sender.send(transportManagementService.process(cmd, user));
		}catch(GetListThrowable t) {
			sender.send(t.getArrayList());
		}catch(Exception e) {
			sender.send(e);
			throw e;
		}	
	}

	@Override
	public void process(InsuranceData.Command cmd) throws Throwable {
		try {
			sender.send(transportManagementService.process(cmd, user));
		}catch(GetListThrowable t) {
			sender.send(t.getArrayList());
		}catch(Exception e) {
			sender.send(e);
			throw e;
		}
	}

	@Override
	public void process(Patient.Command cmd) throws Throwable {
		try{
			sender.send(transportManagementService.process(cmd, user));
		}catch(GetListThrowable t) {
			sender.send(t.getArrayList());
		}catch(Exception e) {
			sender.send(e);
			throw e;
		}
	}

	@Override
	public void process(ServiceProvider.Command cmd) throws Throwable {
		try {
			sender.send(transportManagementService.process(cmd, user));
		}catch(GetListThrowable t) {
			sender.send(t.getArrayList());
		}catch(Exception e) {
			sender.send(e);
			throw e;
		}
	}

	@Override
	public void process(TransportDetails.Command cmd) throws Throwable {
		try{
			sender.send(transportManagementService.process(cmd, user));
		}catch(GetListThrowable t) {
			sender.send(t.getArrayList());
		}catch(Exception e) {
			sender.send(e);
			throw e;
		}
	}

	@Override
	public void process(TransportDocument.Command cmd) throws Throwable {
		try{
			sender.send(transportManagementService.process(cmd, user));	
		}catch(GetListThrowable t) {
			sender.send(t.getArrayList());
		}catch(Exception e) {
			sender.send(e);
			throw e;
		}
	}

	@Override
	public void process(User.Command cmd) throws Throwable {
		try{
			sender.send(transportManagementService.process(cmd, user));	
		}catch(GetListThrowable t) {
			sender.send(t.getArrayList());
		}catch(Exception e) {
			sender.send(e);
			throw e;
		}
	}

	@Override
	public void process(ComEncryptionKey key) throws Throwable {
		try{ 
			Log.sendMessage("Settung up Encryption for Client-Server-communication...");
			if(encryption == null)
				encryption = new ComEncryption(this, sender);
			encryption.useEncryption(key);
		
			Log.sendMessage("	Encryption for Client-Server-communication has been successfully set up!");

		}catch(EncryptionException e) {
			Log.sendException(e);
		}		
	}

	@Override
	public Serializable handleInputEncryption(Serializable inputObject) throws EncryptionException {
		if(encryption == null)
			encryption = new ComEncryption(this, sender);
		return encryption.getObject(inputObject); 
	}
}
