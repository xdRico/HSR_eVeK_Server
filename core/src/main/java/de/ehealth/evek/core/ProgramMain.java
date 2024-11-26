package de.ehealth.evek.core;

import de.ehealth.evek.data.IRepository;
import de.ehealth.evek.data.ITransportManagementService;
import de.ehealth.evek.data.TransportManagementService;
import de.ehealth.evek.util.Debug;
import de.ehealth.evek.util.Log;

public class ProgramMain {
		
	private static ProgramMain main;
	
	public static void main(String[] args) {
		if(main != null)
			return;
		main = new ProgramMain();
		main.loadConfig();	

		System.setProperty("evek.repo.jdbc.url", "jdbc:postgresql:evek");
		System.setProperty("evek.repo.jdbc.user", "postgres");
		System.setProperty("evek.repo.jdbc.password", "toor");
		//TODO StartArgs
		Log.setPath("C:\\Coding\\test\\log\\eclipse\\");
		Log.setWriteToFile(true);
		Debug.setDebugMode(true);
		try {
		main.setTransportManagementService(
				new TransportManagementService(
						IRepository.loadInstance()));
		ConnectionHandler handler = new ConnectionHandler();
		main.setConnectionHandler(handler);
		//TODO implement alternative start procedure?
		handler.startServer();
		} catch(Exception e) {
			
		}
	
	}
	


	private ConnectionHandler connectionHandler;
	private ITransportManagementService transportManagementService;
	
	private void loadConfig() {
		//TODO Configs
	}
	
	private void setConnectionHandler(ConnectionHandler connectionHandler) throws Exception {
		this.connectionHandler = connectionHandler;
		if(transportManagementService != null)
			this.connectionHandler.setService(transportManagementService);
	}
	
	private void setTransportManagementService(ITransportManagementService service) throws Exception {
		this.transportManagementService = service;
		if(connectionHandler != null)
			connectionHandler.setService(transportManagementService);
	}
	
}
