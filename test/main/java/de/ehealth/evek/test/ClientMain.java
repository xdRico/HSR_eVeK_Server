package de.ehealth.evek.test;

import java.net.InetAddress;
import java.net.Socket;

import de.ehealth.evek.entity.Address;
import de.ehealth.evek.entity.Address.Create;
import de.ehealth.evek.entity.ServiceProvider;
import de.ehealth.evek.entity.User;
import de.ehealth.evek.network.ComSender;

import de.ehealth.evek.type.UserRole;
import de.ehealth.evek.util.COptional;
import de.ehealth.evek.util.Debug;
import de.ehealth.evek.util.Log;

public class ClientMain {

	public static void main(String[] args) {
		new ClientMain();
	}
	
	private ClientMain() {
		try {
			InetAddress serverAddress = InetAddress.getLocalHost();
			int port = 12013;
			Debug.setDebugMode(true);
			Log.sendMessage(String.format("Trying to connect to Server at %s:%s", serverAddress, port));
			Socket server = new Socket(serverAddress, port);
			ComSender sender = new ComSender(server);
			
			
			Create addressUser = new Address.Create(
					COptional.empty(), 
					"Street", "10", "de", "12345", "city");
			
			Create addressSP = new Address.Create(
					COptional.empty(), "Way", "13/1", "de", "15432", "cyti");
			
			ServiceProvider.CreateFull sp = new ServiceProvider.CreateFull(
					"spid-124", "SP", "Transportation", false, true, addressSP, COptional.empty());
			
			User.CreateFull user = new User.CreateFull(
					"LastTest", "PreTest", addressUser, "username", sp, UserRole.superUser);
			
			sender.sendUser(user);
			
			Thread.sleep(10000);
			
			Log.sendMessage("Finished ClientMain!");
//			ComReceiver receiver = new ComReceiver(server, null);
			
		}catch(Exception e) {
			Log.sendException(e);
		}
	}
}
