package de.ehealth.evek.entity;

import java.util.List;
import java.util.Optional;

import de.ehealth.evek.type.Id;
import de.ehealth.evek.type.Reference;

public record InsuranceData(
		Id<InsuranceData> id, 
		Reference<Patient> patient,
		Reference<Insurance> insurance, 
		int insuranceStatus) {
	
	public static sealed interface Command permits Create, Delete{
	}
	
	public static record Create(
			Reference<Patient> patient,
			Reference<Insurance> insurance, 
			int insuranceStatus) implements Command {
	}

	public static record Delete(Id<InsuranceData> id) implements Command {
	}

	public static record Filter(
			Optional<Reference<Patient>> patient,
			Optional<Reference<Insurance>> insurance) {
	}

	public static interface Operations {
		InsuranceData process(Command cmd) throws Exception;

		List<InsuranceData> getInsuranceData(Filter filter);

		InsuranceData getInsuranceData(Id<InsuranceData> id);
	}
	
	public String toString() {
		return String.format(
				"InsuranceData[id=%s, patient=%s, insurance=%s, "
				+ "insuranceStatus=%d]", 
				id, 
				patient.id(),
				insurance.toString(), 
				insuranceStatus);
	}
}