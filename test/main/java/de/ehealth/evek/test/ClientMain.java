package de.ehealth.evek.test;

import java.net.InetAddress;
import java.net.Socket;
import java.sql.Date;

import de.ehealth.evek.entity.Address;
import de.ehealth.evek.entity.Insurance;
import de.ehealth.evek.entity.InsuranceData;
import de.ehealth.evek.entity.Patient;
import de.ehealth.evek.entity.ServiceProvider;
import de.ehealth.evek.entity.TransportDetails;
import de.ehealth.evek.entity.TransportDocument;
import de.ehealth.evek.entity.User;
import de.ehealth.evek.test.network.ComClientReceiver;
import de.ehealth.evek.test.network.ComClientSender;
import de.ehealth.evek.type.Reference;
import de.ehealth.evek.type.TransportReason;
import de.ehealth.evek.type.TransportationType;
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

			
			Address.Create addressUser = new Address.Create(
					COptional.empty(), 
					"Street", "10", "de", "12345", "city");
			
			Address.Create addressSP = new Address.Create(
					COptional.empty(), "Way", "13/1", "de", "15432", "cyti");
			
			ServiceProvider.CreateFull sp = new ServiceProvider.CreateFull(
					"spid-124", "SP", "Transportation", false, true, addressSP, COptional.empty());
			
			User.CreateFull crUser = new User.CreateFull(
					"username", "Passwort", "LastTest", "PreTest", addressUser, sp, UserRole.SuperUser);
			
			sender.loginUser(crUser.userName(), "Passwort");
			User loginUser = receiver.receiveUser();
			Log.sendMessage("loginUser: " + loginUser.toString());
			Log.sendMessage("Successfully created user!");
			
			sender.sendUser(crUser);
			Log.sendMessage("sentUser: " + crUser.toString());
			User receivedUser = receiver.receiveUser();
			Log.sendMessage("receivedUser: " + receivedUser.toString());
			
			
			Address.Create crAddress = new Address.Create(COptional.of("AKK ES"), "Krastraße", "125", "de", "76253", "städten");
			sender.sendAddress(crAddress);
			Log.sendMessage("sentAddress: " + crAddress.toString());
			Address receivedAddress = receiver.receiveAddress();
			Log.sendMessage("receivedAddress: " + receivedAddress.toString());
			Log.sendMessage("Successfully created Address!");

			
			
			Insurance.Create crInsurance = new Insurance.Create("108018520", "AKK ES", Reference.to(receivedAddress.id().value().toString())); 
			sender.sendInsurance(crInsurance);
			Log.sendMessage("sentInsurance: " + crInsurance.toString());
			Insurance receivedInsurance = receiver.receiveInsurance();
			Log.sendMessage("receivedInsurance: " + receivedInsurance.toString());
			Log.sendMessage("Successfully created Insurance!");

			//TODO PROJECT - Current InsuranceData of Patient as separate Table?
			
			InsuranceData.Create crInsuranceData = new InsuranceData.Create(Reference.to("R920485575"), Reference.to(receivedInsurance.id().value().toString()), 1000000);
			sender.sendInsuranceData(crInsuranceData);
			Log.sendMessage("sentInsuranceData: " + crInsuranceData.toString());
			InsuranceData receivedInsuranceData = receiver.receiveInsuranceData();
			Log.sendMessage("receivedInsuranceData: " + receivedInsuranceData.toString());
			Log.sendMessage("Successfully created InsuranceData!");

			Patient.Create crPatient = new Patient.Create("R920485575", Reference.to(receivedInsuranceData.id().value().toString()), 
					"Paroten", "Heinse", Date.valueOf("1919-10-17"), Reference.to(receivedUser.address().id().value().toString()));
			sender.sendPatient(crPatient);
			Log.sendMessage("sentPatient: " + crPatient.toString());
			Patient receivedPatient = receiver.receivePatient();
			Log.sendMessage("receivedPatient: " + receivedPatient.toString());
			Log.sendMessage("Successfully created Patient!");

			
			
			ServiceProvider.Create crServiceProvider = new ServiceProvider.Create("465869453", "SeriosTranspots", "RD", false, true, Reference.to(receivedAddress.id().value().toString()), COptional.empty());
			sender.sendServiceProvider(crServiceProvider);
			Log.sendMessage("sentServiceProvider: " + crServiceProvider.toString());
			ServiceProvider receivedServiceProvider = receiver.receiveServiceProvider();
			Log.sendMessage("receivedServiceProvider: " + receivedServiceProvider.toString());
			Log.sendMessage("Successfully created ServiceProvider!");
			ServiceProvider td = receivedServiceProvider;
			
			crServiceProvider = new ServiceProvider.Create("748697434", "CompetenzClinics", "KH", true, false, Reference.to(receivedAddress.id().value().toString()), COptional.empty());
			sender.sendServiceProvider(crServiceProvider);
			Log.sendMessage("sentServiceProvider: " + crServiceProvider.toString());
			receivedServiceProvider = receiver.receiveServiceProvider();
			Log.sendMessage("receivedServiceProvider: " + receivedServiceProvider.toString());
			Log.sendMessage("Successfully created ServiceProvider!");
			ServiceProvider kh = receivedServiceProvider;

			TransportDocument.Create crTransportDocument = new TransportDocument.Create(
					COptional.of(Reference.to(receivedPatient.insuranceNumber().value().toString())), 
					COptional.of(Reference.to(receivedPatient.insuranceData().id().value().toString())),
					TransportReason.EmergencyTransport,
					Date.valueOf("2024-11-29"),
					COptional.empty(),
					COptional.empty(),
					Reference.to(kh.id().value().toString()),
					TransportationType.KTW,
					COptional.empty(),
					Reference.to(loginUser.id().value().toString()));	
			sender.sendTransportDocument(crTransportDocument);
			Log.sendMessage("sentTransportDocument: " + crTransportDocument.toString());
			TransportDocument receivedTransportDocument = receiver.receiveTransportDocument();
			Log.sendMessage("receivedTransportDocument: " + receivedTransportDocument.toString());
			Log.sendMessage("Successfully created TransportDocument!");
			
			
			TransportDetails.Create crTransportDetails = new TransportDetails.Create(			
					Reference.to(receivedTransportDocument.id().value().toString()),
					Date.valueOf("2024-11-29"),
					Reference.to(td.id().value().toString()));
			
			sender.sendTransportDetails(crTransportDetails);
			Log.sendMessage("sentTransportDetails: " + crTransportDetails.toString());
			TransportDetails receivedTransportDetails = receiver.receiveTransportDetails();
			Log.sendMessage("receivedTransportDetails: " + receivedTransportDetails.toString());
			Log.sendMessage("Successfully created TransportDetails!");
			
			Thread.sleep(5000);
			
			Log.sendMessage("Finished ClientMain!");
//			ComReceiver receiver = new ComReceiver(server, null);
			
		}catch(Exception e) {
			Log.sendException(e);
		}
	}
}
