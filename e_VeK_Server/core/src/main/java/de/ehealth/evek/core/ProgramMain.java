package de.ehealth.evek.core;

public class ProgramMain {
	
	public static void main(String[] args) {
		ProgramMain main = new ProgramMain();
	}


	private ConnectionHandler connectionHandler;
	
	public ProgramMain() {
		connectionHandler = new ConnectionHandler();
		
		
		loadConfig();
		
		connectionHandler.init();
	}
	
	private void loadConfig() {
		
	}
	
}
