package de.ehealth.evek.entity;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import de.ehealth.evek.type.Id;
import de.ehealth.evek.type.Reference;

public record Patient (
		Id<Patient> insuranceNumber,
		Reference<InsuranceData> insuranceData,
		String lastName,
		String firstName,
		Date birthDate,
		Reference<Adress> adress){

	public static sealed interface Command permits Create, Delete, Move, Update{
	}

	public static record Create(String insuranceNumber, 
			Reference<InsuranceData> insuranceData, String lastName,
			String firstName, Date birthDate, Reference<Adress> adress) implements Command {
	}

	public static record Delete(Id<Patient> insuranceNumber) implements Command {
	}
	
	public static record Move(Id<Patient> insuranceNumber, 
			Reference<Adress> adress) implements Command {
	}

	public static record Update(Id<Patient> insuranceNumber, 
			Reference<InsuranceData> insuranceData, String lastName,
			String firstName, Reference<Adress> adress) implements Command {
	}

	public static record Filter(Optional<Reference<Adress>> adress, 
			Optional<String> lastName, Optional<String> firstName, 
			Optional<Date> birthDate, 
			Optional<Reference<InsuranceData>> insuranceData) {
	}

	public static interface Operations {
		Patient process(Command cmd) throws Exception;

		List<Patient> getPatient(Filter filter);

		Patient getPatient(Id<Patient> insuranceNumber);
	}

	public Patient updateWith(Reference<InsuranceData> newInsuranceData, 
			String newLastName, String newFirstName, Reference<Adress> newAdress) {
		return new Patient(this.insuranceNumber, newInsuranceData, newLastName, newFirstName, this.birthDate, newAdress);
	}
	
	public Patient updateWith(Reference<Adress> newAdress) {
		return new Patient(this.insuranceNumber, this.insuranceData, this.lastName, this.firstName, this.birthDate, newAdress);
	}
	
	public String toString() {
		return String.format(
				"Patient[insuranceNumber=%s, "
				+ "insuranceData=%s, lastName=%s, "
				+ "firstName=%s, birthDate=%s, adress=%s]", 
				insuranceNumber, insuranceData.toString(), 
				lastName, firstName, birthDate, adress.toString());
	}
}
