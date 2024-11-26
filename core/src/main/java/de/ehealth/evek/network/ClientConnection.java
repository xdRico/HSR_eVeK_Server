package de.ehealth.evek.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import de.ehealth.evek.data.ITransportManagementService;
import de.ehealth.evek.entity.User;
import de.ehealth.evek.type.Reference;
import de.ehealth.evek.util.Log;

class ClientConnection extends Thread {

	private Reference<User> user;
	private Socket client;
	private PrintWriter out;
	private BufferedReader in;
	private ITransportManagementService transportManagementService;
	
	
	
	public ClientConnection(Socket client, ITransportManagementService transportManagementService) {
		this.client = client;
		this.transportManagementService = transportManagementService;
		Log.sendMessage("	Starting up ConnectionThread...");
		;
		this.setName(String.format("%s-%s:%d", 
				getClass().getSimpleName().toString(),
				client.getInetAddress().toString().replace("/", ""),
				client.getPort()));
		try {
			out = new PrintWriter(client.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Network Communication, creating db commands
		
	}
	
	@Override
	public String toString() {
		return String.format("IP-Adress: %s; Port: %d; Thread: %s", 
				client.getInetAddress().toString().replace("/", ""), 
				client.getPort(),
				this.getName());
	}
	
	/*
	public String sendMessage(String msg) {
		out.println(msg);
		String resp = in.readLine();
		return resp;
	}
	
	public void stopConnection() {
		in.close();
		out.close();
	}
	*/
}
