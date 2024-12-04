package de.ehealth.evek.server.core;

import java.io.IOException;

import de.ehealth.evek.server.network.ConnectionHandler;
import de.ehealth.evek.server.data.IRepository;
import de.ehealth.evek.server.data.ITransportManagementService;
import de.ehealth.evek.server.data.TransportManagementService;
import de.ehealth.evek.api.util.Debug;
import de.ehealth.evek.api.util.Log;

public class ServerMain {
		
	private static ServerMain main;
	
	public static void main(String[] args) {
		if(main != null)
			return;
		main = new ServerMain();
		Log.sendMessage("ServerMain has been finished!");

	}
	


	private ConnectionHandler connectionHandler;
	private ITransportManagementService transportManagementService;
	
	private ServerMain() {
		loadConfig();	
		Log.sendMessage("Setting up ServerMain...");

		setTransportManagementService(
				new TransportManagementService(
						IRepository.loadInstance()));
		ConnectionHandler handler = new ConnectionHandler();
		setConnectionHandler(handler);

		try {
			//TODO ServerMain - implement alternative start procedure?
			handler.startServer();
		} catch(IOException e) {
			
		}
		
	}
	
	private void loadConfig() {
		
		//TODO ServerMain.loadConfig - Configs
		System.setProperty("evek.repo.jdbc.url", "jdbc:postgresql:evek");
		System.setProperty("evek.repo.jdbc.user", "postgres");
		System.setProperty("evek.repo.jdbc.password", "toor");
		//TODO ServerMain.loadConfig - StartArgs
		Log.setPath("C:\\Coding\\test\\log\\eclipse\\");
		Log.setWriteToFile(true);
		Debug.setDebugMode(true);
		Log.sendMessage("Settings have been successfully configured!");

	}
	
	private void setConnectionHandler(ConnectionHandler connectionHandler) {
		this.connectionHandler = connectionHandler;
		Log.sendMessage("	ConnectionHandler has been set!");
		if(transportManagementService != null) 
			setupConnectionHandler();
	}
	
	private void setTransportManagementService(ITransportManagementService service) {
		this.transportManagementService = service;
		Log.sendMessage("	ITransportManagementService has been set!");
		if(connectionHandler != null)
			setupConnectionHandler();
	}
	
	private void setupConnectionHandler() {
		if(this.connectionHandler.setService(transportManagementService)) {
			Log.sendMessage("	ConnectionHandler has been initialized!");
			return;
		}
		Log.sendMessage("	ConnectionHandler could not be initialized!");
	}
	
}
