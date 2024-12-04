package de.ehealth.evek.server.network;

import java.io.IOException;
import java.net.Socket;

import de.ehealth.evek.api.util.Log;
import de.ehealth.evek.server.data.ITransportManagementService;

class ClientConnection {

	private final Socket client;	
	private final ComServerSender sender;
	private ITransportManagementService transportManagementService;
	
	
	
	public ClientConnection(Socket client, ITransportManagementService transportManagementService) throws IOException {
		this.client = client;
		this.transportManagementService = transportManagementService;
		try {
			Log.sendMessage(String.format("A Connection has been requested by %s:%s!", client.getInetAddress(), client.getPort()));
			this.sender = new ComServerSender(client);
			new ComServerReceiver(client, this.transportManagementService, sender);
			Log.sendMessage(String.format("	Client Connection[%s] has been successfully established!", toString()));
		} catch (IOException e) {
			Log.sendMessage(String.format("	Client Connection[%s] could not be established!", toString()));
			Log.sendException(e);
			throw e;
		}

	}
	
	@Override
	public String toString() {
		return String.format("IP-Adress: %s; Port: %d", 
				client.getInetAddress().toString().replace("/", ""), 
				client.getPort());
	}
}
