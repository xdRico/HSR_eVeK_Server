package de.ehealth.evek.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.ehealth.evek.entity.Address;
import de.ehealth.evek.entity.Insurance;
import de.ehealth.evek.entity.InsuranceData;
import de.ehealth.evek.entity.Patient;
import de.ehealth.evek.entity.ServiceProvider;
import de.ehealth.evek.entity.TransportDetails;
import de.ehealth.evek.entity.TransportDocument;
import de.ehealth.evek.entity.User;

public enum UserRole {
	HealthcareAdmin(new Object[] {
			Address.Update.class,
			ServiceProvider.CreateFull.class,
			ServiceProvider.Move.class,
			ServiceProvider.Update.class,
			User.Create.class,
			User.Delete.class,
			User.UpdateRole.class
	}),
	HealthcareDoctor(new Object[] {
			Insurance.Create.class,
			Insurance.Update.class,
			Insurance.Move.class,
			InsuranceData.Create.class,
			TransportDocument.AssignPatient.class,
			TransportDocument.Create.class,
			TransportDocument.Delete.class,
			TransportDocument.Update.class
	}),
	HealthcareUser(new Object[] {
			Insurance.Create.class,
			Insurance.Update.class,
			Insurance.Move.class,
			InsuranceData.Create.class,
			TransportDocument.AssignPatient.class,
			TransportDocument.Create.class,
			TransportDocument.Delete.class,
			TransportDocument.Update.class
	}),
	TransportAdmin(new Object[] {
			Address.Update.class,
			ServiceProvider.Move.class,
			ServiceProvider.Update.class,
			User.Create.class,
			User.Delete.class,
			User.UpdateRole.class
	}),
	TransportDoctor(new Object[] {
			Insurance.Create.class,
			Insurance.Update.class,
			Insurance.Move.class,
			InsuranceData.Create.class,
			TransportDetails.Create.class,
			TransportDetails.Delete.class,
			TransportDetails.Update.class,
			TransportDetails.UpdatePatientSignature.class,
			TransportDetails.UpdateTransporterSignature.class,
			TransportDocument.AssignPatient.class,
			TransportDocument.Create.class,
			TransportDocument.Delete.class,
			TransportDocument.Update.class
	}),
	TransportInvoice(new Object[] {
			TransportDetails.Create.class,
			TransportDetails.Delete.class,
			TransportDetails.Update.class,
			TransportDetails.UpdatePatientSignature.class,
			TransportDetails.UpdateTransporterSignature.class
	}),
	TransportUser(new Object[] {
			TransportDetails.Create.class,
			TransportDetails.Delete.class,
			TransportDetails.Update.class,
			TransportDetails.UpdatePatientSignature.class,
			TransportDetails.UpdateTransporterSignature.class
	}),
	InsuranceAdmin(new Object[] {
			Address.Update.class,
			Insurance.Update.class,
			Insurance.Move.class,
			User.Create.class,
			User.Delete.class,
			User.UpdateRole.class
	}),
	InsuranceUser(new Object[] {
			InsuranceData.Create.class,
			Patient.Create.class,
			Patient.Move.class,
			Patient.Update.class,
			TransportDocument.Archive.class
			
	}),
	SuperUser(new Object[] {
			Address.Update.class,
			Address.Delete.class,
			Insurance.Create.class,
			Insurance.Update.class,
			Insurance.Move.class,
			Insurance.Delete.class,
			InsuranceData.Create.class,
			InsuranceData.Delete.class,
			Patient.Create.class,
			Patient.Move.class,
			Patient.Update.class,
			Patient.Delete.class,
			ServiceProvider.Create.class,
			ServiceProvider.CreateFull.class,
			ServiceProvider.Move.class,
			ServiceProvider.Delete.class,
			ServiceProvider.Update.class,
			ServiceProvider.UpdateService.class,
			TransportDetails.Create.class,
			TransportDetails.Delete.class,
			TransportDetails.Update.class,
			TransportDetails.UpdatePatientSignature.class,
			TransportDetails.UpdateTransporterSignature.class,
			TransportDocument.AssignPatient.class,
			TransportDocument.Create.class,
			TransportDocument.Delete.class,
			TransportDocument.Update.class,
			User.Create.class,
			User.CreateFull.class,
			User.Delete.class,
			User.UpdateRole.class
	});
	
	private List<Object> allowedCommands = new ArrayList<>();
	
	private UserRole(Object[] commands) {
		for(Object o : commands)
			allowedCommands.add(o);
		allowedCommands.add(Address.Create.class);
		allowedCommands.add(Address.GetList.class);
		allowedCommands.add(Address.Get.class);
		allowedCommands.add(Insurance.GetList.class);
		allowedCommands.add(Insurance.Get.class);
		allowedCommands.add(InsuranceData.GetList.class);
		allowedCommands.add(InsuranceData.Get.class);
		allowedCommands.add(Patient.GetList.class);
		allowedCommands.add(Patient.Get.class);
		allowedCommands.add(ServiceProvider.GetList.class);
		allowedCommands.add(ServiceProvider.Get.class);
		allowedCommands.add(TransportDetails.GetList.class);
		allowedCommands.add(TransportDetails.Get.class);
		allowedCommands.add(TransportDocument.GetList.class);
		allowedCommands.add(TransportDocument.Get.class);
		allowedCommands.add(TransportDocument.GetList.class);
		allowedCommands.add(TransportDocument.Get.class);
		allowedCommands.add(User.GetList.class);
		allowedCommands.add(User.Get.class);
		allowedCommands.add(User.LoginUser.class);
		allowedCommands.add(User.Update.class);
		allowedCommands.add(User.UpdateCredentials.class);
	}
	
	public List<?> getAllowedActions(){
		return allowedCommands;
	}
}
