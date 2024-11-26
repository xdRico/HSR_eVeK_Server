package de.ehealth.evek.core;

import java.net.Socket;

import de.ehealth.evek.data.ITransportManagementService;
import de.ehealth.evek.util.Log;

class ClientConnection implements Runnable {

	private Socket client;
	private ITransportManagementService transportManagementService;
	private Thread thread;
	
	public ClientConnection(Socket client, ITransportManagementService transportManagementService) {
		this.client = client;
		this.transportManagementService = transportManagementService;
		Log.sendMessage("	Starting up ConnectionThread...");
		this.thread = new Thread(this);
		this.thread.setName(String.format("%s-%s", 
				getClass().getSimpleName().toString(),
				client.getInetAddress().toString()));
		}

	@Override
	public void run() {
		// TODO Network Communication, creating db commands
		
	}
	
	@Override
	public String toString() {
		return String.format("IP-Adress: %s; Thread: %s", client.getInetAddress(), thread.getName());
	}
	
}
