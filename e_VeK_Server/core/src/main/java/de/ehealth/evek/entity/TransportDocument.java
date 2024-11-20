package de.ehealth.evek.entity;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import de.ehealth.evek.type.Id;
import de.ehealth.evek.type.Reference;
import de.ehealth.evek.type.TransportReason;
import de.ehealth.evek.type.TransportationType;

public record TransportDocument(
		Id<TransportDocument> id,
		Optional<Reference<Patient>> patient,
		Reference<InsuranceData> insuranceData,
		TransportReason reason,
		Date startDate,
		Optional<Date> endDate,
		Optional<Integer> weeklyFrequency,
		Reference<ServiceProvider> serviceProvider,
		TransportationType transportationType,
		Optional<String> additionalInfo,
		Reference<User> signature) {
	
	public static sealed interface Command permits Create, Update, Delete{
	}
	
	public static record Create(
			Optional<Reference<Patient>> patient,
			Reference<InsuranceData> insuranceData,
			TransportReason reason,
			Date startDate,
			Optional<Date> endDate,
			Optional<Integer> weeklyFrequency,
			Reference<ServiceProvider> serviceProvider,
			TransportationType transportationType,
			Optional<String> additionalInfo,
			Reference<User> signature) implements Command {
	}

	public static record Delete(Id<TransportDocument> id) implements Command {
	}

	public static record Update(
			Id<TransportDocument> id,
			Optional<Reference<Patient>> patient,
			Reference<InsuranceData> insuranceData,
			TransportReason reason,
			Date startDate,
			Optional<Date> endDate,
			Optional<Integer> weeklyFrequency,
			Reference<ServiceProvider> serviceProvider,
			TransportationType transportationType,
			Optional<String> additionalInfo,
			Reference<User> signature) implements Command {
	}

	public static record Filter(Optional<Reference<Patient>> patient,
			Optional<InsuranceData> insuranceData,
			Optional<Date> startDate, Optional<Date> endDate,
			Optional<Reference<ServiceProvider>> serviceProvider,
			Optional<TransportationType> transportationType,
			Optional<Reference<User>> signature) {
	}

	public static interface Operations {
		TransportDocument process(Command cmd) throws Exception;

		List<TransportDocument> getTransportDocument(Filter filter);

		TransportDocument getTransportDocument(Id<TransportDocument> id);
	}

	public TransportDocument updateWith(TransportReason newReason,
			Date newStartDate, Optional<Date> newEndDate,
			Optional<Integer> newWeeklyFrequency,
			Reference<ServiceProvider> newServiceProvider,
			TransportationType newTransportationType,
			Optional<String> newAdditionalInfo) {
		
		return new TransportDocument(
				this.id, this.patient, insuranceData, newReason, 
				newStartDate, newEndDate, newWeeklyFrequency, 
				newServiceProvider, newTransportationType, 
				newAdditionalInfo, this.signature);
	}
	
	public TransportDocument assignPatient(
			Reference<Patient> newPatient, 
			Reference<InsuranceData> newInsuranceData) {
		return new TransportDocument(
				this.id, Optional.of(newPatient), 
				newInsuranceData,
				this.reason, this.startDate, 
				this.endDate, this.weeklyFrequency, 
				this.serviceProvider, 
				this.transportationType, 
				this.additionalInfo, this.signature);
	}
	
	public TransportDocument assignSignature(Reference<User> newSignature) {
		return new TransportDocument(
				this.id, this.patient,
				this.insuranceData,
				this.reason, this.startDate, 
				this.endDate, this.weeklyFrequency, 
				this.serviceProvider, 
				this.transportationType, 
				this.additionalInfo, newSignature);
	}
	
	
	public String toString() {
		return String.format(
				"TransportDocument[id=%s, patient=%s, insuranceData=%s, reason=%s, "
				+ "startDate=%s, endDate=%s, weeklyFrequency=%d, "
				+ "serviceProvider=%s, transportationType=%s, "
				+ "additionalInfo=%s, signature=%s]", 
				id, patient.toString(), insuranceData.toString(), 
				reason.toString(), startDate.toString(), endDate.toString(),
				weeklyFrequency, serviceProvider.toString(), 
				transportationType.toString(), additionalInfo.toString(), 
				signature.toString());
	}
}
