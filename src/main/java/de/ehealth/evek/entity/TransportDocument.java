package de.ehealth.evek.entity;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import de.ehealth.evek.type.Id;
import de.ehealth.evek.type.Reference;
import de.ehealth.evek.type.TransportReason;
import de.ehealth.evek.type.TransportationType;
import de.ehealth.evek.util.COptional;

public record TransportDocument(
		Id<TransportDocument> id,
		COptional<Reference<Patient>> patient,
		Reference<InsuranceData> insuranceData,
		TransportReason transportReason,
		Date startDate,
		COptional<Date> endDate,
		COptional<Integer> weeklyFrequency,
		Reference<ServiceProvider> healthcareServiceProvider,
		TransportationType transportationType,
		COptional<String> additionalInfo,
		Reference<User> signature
		) implements Serializable{
	
	public static sealed interface Command extends Serializable permits Create, Update, AssignPatient, Delete{
	}
	
	public static record Create(
			COptional<Reference<Patient>> patient,
			Reference<InsuranceData> insuranceData,
			TransportReason transportReason,
			Date startDate,
			COptional<Date> endDate,
			COptional<Integer> weeklyFrequency,
			Reference<ServiceProvider> healthcareServiceProvider,
			TransportationType transportationType,
			COptional<String> additionalInfo,
			Reference<User> signature) implements Command {
	}

	public static record Delete(Id<TransportDocument> id) implements Command {
	}

	public static record Update(
			Id<TransportDocument> id,
			TransportReason transportReason,
			Date startDate,
			COptional<Date> endDate,
			COptional<Integer> weeklyFrequency,
			Reference<ServiceProvider> healthcareServiceProvider,
			TransportationType transportationType,
			COptional<String> additionalInfo,
			Reference<User> signature) implements Command {
	}
	
	public static record AssignPatient(
			Id<TransportDocument> id,
			Reference<Patient> patient,
			Reference<InsuranceData> insuranceData) implements Command {
	}

	public static record Filter(COptional<Reference<Patient>> patient,
			COptional<InsuranceData> insuranceData,
			COptional<Date> startDate, COptional<Date> endDate,
			COptional<Reference<ServiceProvider>> healthcareServiceProvider,
			COptional<TransportationType> transportationType,
			COptional<Reference<User>> signature) {
	}

	public static interface Operations {
		TransportDocument process(Command cmd) throws Exception;

		List<TransportDocument> getTransportDocument(Filter filter);

		TransportDocument getTransportDocument(Id<TransportDocument> id);
	}

	public TransportDocument updateWith(
			TransportReason newTransportReason,
			Date newStartDate, 
			COptional<Date> newEndDate,
			COptional<Integer> newWeeklyFrequency,
			Reference<ServiceProvider> newServiceProvider,
			TransportationType newTransportationType,
			COptional<String> newAdditionalInfo,
			Reference<User> newSignature) {
		
		return new TransportDocument(
				this.id, this.patient, insuranceData, newTransportReason, 
				newStartDate, newEndDate, newWeeklyFrequency, 
				newServiceProvider, newTransportationType, 
				newAdditionalInfo, newSignature);
	}
	
	public TransportDocument assignPatient(
			Reference<Patient> newPatient, 
			Reference<InsuranceData> newInsuranceData) {
		return new TransportDocument(
				this.id, COptional.of(newPatient), 
				newInsuranceData,
				this.transportReason, this.startDate, 
				this.endDate, this.weeklyFrequency, 
				this.healthcareServiceProvider, 
				this.transportationType, 
				this.additionalInfo, this.signature);
	}
	
	public TransportDocument assignSignature(Reference<User> newSignature) {
		return new TransportDocument(
				this.id, this.patient,
				this.insuranceData,
				this.transportReason, this.startDate, 
				this.endDate, this.weeklyFrequency, 
				this.healthcareServiceProvider, 
				this.transportationType, 
				this.additionalInfo, newSignature);
	}
	
	
	public String toString() {
		return String.format(
				"TransportDocument[id=%s, patient=%s, insuranceData=%s, transportReason=%s, "
				+ "startDate=%s, endDate=%s, weeklyFrequency=%d, "
				+ "healthcareServiceProvider=%s, transportationType=%s, "
				+ "additionalInfo=%s, signature=%s]", 
				id, patient.toString(), insuranceData.toString(), 
				transportReason.toString(), startDate.toString(), endDate.toString(),
				weeklyFrequency, healthcareServiceProvider.toString(), 
				transportationType.toString(), additionalInfo.toString(), 
				signature.toString());
	}
}