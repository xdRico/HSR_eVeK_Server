package de.ehealth.evek.test;

import java.net.InetAddress;
import java.net.Socket;

import de.ehealth.evek.entity.Address;
import de.ehealth.evek.entity.Address.Create;
import de.ehealth.evek.entity.ServiceProvider;
import de.ehealth.evek.entity.User;
import de.ehealth.evek.network.ComServerSender;

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
			ComClientSender sender = new ComClientSender(server);
			ComClientReceiver receiver = new ComClientReceiver(server);

			
			Create addressUser = new Address.Create(
					COptional.empty(), 
					"Street", "10", "de", "12345", "city");
			
			Create addressSP = new Address.Create(
					COptional.empty(), "Way", "13/1", "de", "15432", "cyti");
			
			ServiceProvider.CreateFull sp = new ServiceProvider.CreateFull(
					"spid-124", "SP", "Transportation", false, true, addressSP, COptional.empty());
			
			User.CreateFull user = new User.CreateFull(
					"username", "LastTest", "PreTest", addressUser, sp, UserRole.superUser);
			
			sender.send(user);
			Log.sendMessage("User: " + user.toString());
			User receivedUser = receiver.receiveUser();
			Log.sendMessage("receivedUser: " + receivedUser.toString());

			sender.loginUser(user.userName(), "PW");
			User loginUser = receiver.receiveUser();
			Log.sendMessage("loginUser: " + loginUser.toString());

			Thread.sleep(10000);
			
			Log.sendMessage("Finished ClientMain!");
//			ComReceiver receiver = new ComReceiver(server, null);
			
		}catch(Exception e) {
			Log.sendException(e);
		}
	}
}
