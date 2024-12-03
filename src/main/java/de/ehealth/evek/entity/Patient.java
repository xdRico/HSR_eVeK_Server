package de.ehealth.evek.entity;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import de.ehealth.evek.type.Id;
import de.ehealth.evek.type.Reference;
import de.ehealth.evek.util.COptional;

public record Patient (
		Id<Patient> insuranceNumber,
		Reference<InsuranceData> insuranceData,
		String lastName,
		String firstName,
		Date birthDate,
		Reference<Address> address
		) implements Serializable {

    private static final long serialVersionUID = -6497523187564893515L;

	
	public static sealed interface Command extends Serializable permits Create, 
	CreateWithInsuranceData, Delete, Move, Update, UpdateInsuranceData, Get, GetList{
	}

	public static record Create(
			String insuranceNumber, 
			Reference<InsuranceData> insuranceData, 
			String lastName,
			String firstName, 
			Date birthDate, 
			Reference<Address> address) implements Command {
	}
	
	public static record CreateWithInsuranceData(
			String insuranceNumber, 
			Reference<Insurance> insurance, 
			int insuranceStatus,
			String lastName,
			String firstName, 
			Date birthDate, 
			Reference<Address> address) implements Command {
	}
	
	public static record Delete(
			Id<Patient> insuranceNumber) implements Command {
	}
	
	public static record Move(
			Id<Patient> insuranceNumber, 
			Reference<Address> address) implements Command {
	}

	public static record Update(
			Id<Patient> insuranceNumber, 
			String lastName,
			String firstName, 
			Reference<Address> address) implements Command {
	}

	public static record UpdateInsuranceData(
			Id<Patient> insuranceNumber, 
			Reference<InsuranceData> insuranceData) implements Command {
		
	}
	
	public static record Get(Id<Patient> id) implements Command {
	}
	public static record GetList(Filter filter) implements Command {
	}
	
	public static record Filter(COptional<Reference<Address>> address, 
			COptional<String> lastName, COptional<String> firstName, 
			COptional<Date> birthDate, 
			COptional<Reference<InsuranceData>> insuranceData) {
	}

	public static interface Operations {
		Patient process(Command cmd, Reference<User> processingUser) throws Throwable;

		List<Patient> getPatient(Filter filter);

		Patient getPatient(Id<Patient> insuranceNumber);
	}

	public Patient updateWith(String newLastName, String newFirstName, Reference<Address> newAddress) {
		return new Patient(this.insuranceNumber, this.insuranceData, newLastName, newFirstName, this.birthDate, newAddress);
	}
	
	public Patient updateAddress(Reference<Address> newAddress) {
		return new Patient(this.insuranceNumber, this.insuranceData, this.lastName, this.firstName, this.birthDate, newAddress);
	}
	public Patient updateInsuranceData(Reference<InsuranceData> newInsuranceData) {
		return new Patient(this.insuranceNumber, newInsuranceData, this.lastName, this.firstName, this.birthDate, this.address);
	}
	
	public String toString() {
		return String.format(
				"Patient[insuranceNumber=%s, "
				+ "insuranceData=%s, lastName=%s, "
				+ "firstName=%s, birthDate=%s, address=%s]", 
				insuranceNumber, insuranceData.toString(), 
				lastName, firstName, birthDate, address.toString());
	}
}