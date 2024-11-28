package de.ehealth.evek.network;

import java.io.IOException;
import java.net.Socket;

import de.ehealth.evek.data.ITransportManagementService;
import de.ehealth.evek.util.Log;

class ClientConnection {

	private final Socket client;	
	private final ComServerReceiver receiver;
	private final ComServerSender sender;
	private ITransportManagementService transportManagementService;
	
	
	
	public ClientConnection(Socket client, ITransportManagementService transportManagementService) throws IOException {
		this.client = client;
		this.transportManagementService = transportManagementService;
		try {
			Log.sendMessage(String.format("A Connection has been requested by %s:%s!", client.getInetAddress(), client.getPort()));
			this.sender = new ComServerSender(client);
			this.receiver = new ComServerReceiver(client, transportManagementService, sender);
			Log.sendMessage(String.format("	Client Connection[%s] has been successfully established!", toString()));
		} catch (IOException e) {
			Log.sendMessage(String.format("	Client Connection[%s] could not be established!", toString()));
			Log.sendException(e);
			throw e;
		}

	}
	//TODO
	@Override
	public String toString() {
		return String.format("IP-Adress: %s; Port: %d", 
				client.getInetAddress().toString().replace("/", ""), 
				client.getPort());
	}
}
