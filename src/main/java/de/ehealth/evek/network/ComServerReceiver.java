package de.ehealth.evek.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import de.ehealth.evek.data.ITransportManagementService;
import de.ehealth.evek.entity.Address;
import de.ehealth.evek.entity.Insurance;
import de.ehealth.evek.entity.InsuranceData;
import de.ehealth.evek.entity.Patient;
import de.ehealth.evek.entity.ServiceProvider;
import de.ehealth.evek.entity.TransportDetails;
import de.ehealth.evek.entity.TransportDocument;
import de.ehealth.evek.entity.User;
import de.ehealth.evek.network.interfaces.IComServerReceiver;
import de.ehealth.evek.network.interfaces.IComServerSender;
import de.ehealth.evek.type.Reference;
import de.ehealth.evek.util.Debug;
import de.ehealth.evek.util.Log;

public final class ComServerReceiver extends Thread implements IComServerReceiver {

	private boolean isRunning = false;
	
	private final Socket socket;
	private final IComServerSender sender;
	private Reference<User> user;
//	private BufferedReader reader;
	private ObjectInputStream objReader;
	private final ITransportManagementService transportManagementService;
	
	public ComServerReceiver(Socket socket, ITransportManagementService transportManagementService, IComServerSender sender) throws IOException {
		this.transportManagementService = transportManagementService;
		this.socket = socket;
		this.sender = sender;
		this.setName(String.format("%s-%s:%d", 
				getClass().getSimpleName().toString(),
				socket.getInetAddress().toString().replace("/", ""),
				socket.getPort()));
		try {
//			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
		// TODO Network Communication, creating db commands
		isRunning = true;
		Log.sendMessage(String.format("	ReceiverThread[%s] has been successfully started!", this.getName()));
		Object readObj;
		try {
			while(isRunning && objReader != null && (readObj = objReader.readObject()) != null) {
				Log.sendMessage(String.valueOf(readObj));
				if(!receiveObject(readObj))
					Log.sendMessage("Message could not be read!");
			}
		}catch (Exception e) {
			if(e.getMessage() != "Connection reset") {
				Debug.sendException(e);
				Log.sendMessage(String.format("	ReceiverThread[%s] has been stopped unexpected!", this.getName()));
				return;
			}
		}
		
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
			User user = transportManagementService.process(loginUser);
			this.user = Reference.to(user.id().value().toString());
			sender.send(user);
			
			return true;

		} catch (Exception e) {
			Log.sendException(e);
			try {
				sender.send((User) null);
			}catch(Exception ex) {
				Log.sendException(ex);

			}
		}
		return false;

	}
	
	@Override
	public void process(Address.Command cmd) throws Exception {
		sender.send(transportManagementService.process(cmd));
		
	}

	@Override
	public void process(Insurance.Command cmd) throws Exception{
		sender.send(transportManagementService.process(cmd));				
	}

	@Override
	public void process(InsuranceData.Command cmd) throws Exception {
		sender.send(transportManagementService.process(cmd));				
		
	}

	@Override
	public void process(Patient.Command cmd) throws Exception {
		sender.send(transportManagementService.process(cmd));				
		
	}

	@Override
	public void process(ServiceProvider.Command cmd) throws Exception {
		sender.send(transportManagementService.process(cmd));				
	}

	@Override
	public void process(TransportDetails.Command cmd) throws Exception {
		sender.send(transportManagementService.process(cmd));				
	}

	@Override
	public void process(TransportDocument.Command cmd) throws Exception {
		sender.send(transportManagementService.process(cmd));				
	}

	@Override
	public void process(User.Command cmd) throws Exception {
		sender.send(transportManagementService.process(cmd));				
	}
	
}
