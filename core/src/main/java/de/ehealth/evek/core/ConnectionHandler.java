package de.ehealth.evek.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import de.ehealth.evek.data.ITransportManagementService;
import de.ehealth.evek.util.Debug;
import de.ehealth.evek.util.Log;

class ConnectionHandler implements Runnable{

	private boolean initialized = false;
	private boolean serverRunning = false;
	private int port = 12013;
	
	private ServerSocket serverSocket;
	private Thread waiterThread;
	private List<ClientConnection> clientConnections = new ArrayList<ClientConnection>();
	private ITransportManagementService transportManagementService;
	
	@Override
	public void run() {
		serverRunning = true;
		while(serverRunning) {
			waitForConnection();
		}
	}
	
	boolean startServer()  throws IOException{
		Log.sendMessage("Starting Server...");
		if(!initialized) {
			Log.sendMessage("Server has not been started!");
			Log.sendMessage(" - Server has not been initialized yet!");
			return false; 
		}
		Log.sendMessage("	Setting up Server Socket...");
		serverSocket = new ServerSocket(port);
		Log.sendMessage("	Starting up WaiterThread...");
		waiterThread = new Thread(this);
		waiterThread.setName(getClass().getSimpleName().toString());
		waiterThread.start();
		Log.sendMessage("Server has been successfully started!");
		return true;
	}
	
	void stopServer(){
		serverRunning = false;
		
	}
	
	private void waitForConnection() {
		try {
			Log.sendMessage("Waiting for Client Connection...");
			ClientConnection client = new ClientConnection(serverSocket.accept(), 
					transportManagementService);
			clientConnections.add(client);
			Log.sendMessage("A Connection has been established!");
			Log.sendMessage(String.format("	Connection: %s", client.toString()));
		} catch (IOException e) {
			Debug.sendMessage("Could not initialize connection to a Client!");
			Log.sendMessage("Could not initialize connection to a Client!");
			Log.sendException(e);
		}
	}
	
	boolean setService(ITransportManagementService transportManagementService) {
		if(transportManagementService == null)
			return false;
		this.transportManagementService = transportManagementService;
		this.initialized = true;
		return true;
	}
	
	boolean setPort(int port) throws IllegalArgumentException{
		if(port < 1024 || port > 65535) throw new IllegalArgumentException("Port out of range!");
		if(serverRunning || this.port == port) return false;
		this.port = port;
		return true;
	}
}
