package de.ehealth.evek.entity;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import de.ehealth.evek.type.Direction;
import de.ehealth.evek.type.Id;
import de.ehealth.evek.type.PatientCondition;
import de.ehealth.evek.type.Reference;

public record TransportDetails (
		Id<TransportDetails> id,
		Reference<TransportDocument> transportDocument,
		Date transportDate,
		Optional<Reference<Adress>> startAdress,
		Optional<Reference<Adress>> endAdress,
		Optional<Direction> direction,
		Optional<PatientCondition> patientCondition,
		Reference<ServiceProvider> transportProvider,
		Optional<String> tourNumber,
		Optional<Boolean> paymentExemption,
		Optional<String> patientSignature,
		Optional<Date> patientSignatureDate,
		Optional<String> transporterSignature,
		Optional<Date> transporterSignatureDate
		) {

	public static sealed interface Command permits Create, Delete, Update, UpdatePatientSignature, UpdateTransporterSignature{
	}

	public static record Create( Id<TransportDetails> id, 
			Reference<TransportDocument> transportDocument, 
			Date transportDate, 
			Optional<Reference<Adress>> startAdress,
			Optional<Reference<Adress>> endAdress,
			Optional<Direction> direction,
			Optional<PatientCondition> patientCondition,
			Reference<ServiceProvider> transportProvider,
			Optional<String> tourNumber,
			Optional<Boolean> paymentExemption,
			Optional<String> patientSignature,
			Optional<Date> patientSignatureDate,
			Optional<String> transporterSignature,
			Optional<Date> transporterSignatureDate
			) implements Command {
	}

	public static record Delete(Id<TransportDetails> id) implements Command {
	}

	public static record Update(Id<TransportDetails> id,
			Optional<Reference<Adress>> startAdress,
			Optional<Reference<Adress>> endAdress,
			Optional<Direction> direction,
			Optional<PatientCondition> patientCondition,
			Optional<String> tourNumber,
			Optional<Boolean> paymentExemption) implements Command {
	}

	public static record UpdatePatientSignature(Id<TransportDetails> id,
			String patientSignature,
			Date patientSignatureDate) implements Command {
	}
	
	public static record UpdateTransporterSignature(Id<TransportDetails> id,
			String transporterSignature,
			Date transporterSignatureDate) implements Command {
	}
	
	public static record Filter(
			Optional<Reference<TransportDocument>> transportDocument,
			Optional<Date> transportDate, 
			Optional<Reference<Adress>> adress,
			Optional<Direction> direction,
			Optional<Reference<ServiceProvider>> transportProvider) {
	}

	public static interface Operations {
		TransportDetails process(Command cmd) throws Exception;

		List<TransportDetails> getTransportDetails(Filter filter);

		TransportDetails getTransportDetails(Id<TransportDetails> id);
	}

	public TransportDetails updateWith(
			Optional<Reference<Adress>> newStartAdress,
			Optional<Reference<Adress>>newEndAdress,
			Optional<Direction> newDirection,
			Optional<PatientCondition> newPatientCondition,
			Optional<String> newTourNumber,
			Optional<Boolean> newPaymentExemption) {
		return new TransportDetails(
				this.id, 
				this.transportDocument,
				this.transportDate,
				newStartAdress, 
				newEndAdress, 
				newDirection, 
				newPatientCondition,
				this.transportProvider,
				newTourNumber,
				newPaymentExemption,
				this.patientSignature, 
				this.patientSignatureDate,
				this.transporterSignature,
				this.transporterSignatureDate);
	}
	
	public TransportDetails updatePatientSignature(
			String newPatientSignature,
			Date newPatientSignatureDate){
		return new TransportDetails(
				this.id, 
				this.transportDocument,
				this.transportDate,
				this.startAdress, 
				this.endAdress, 
				this.direction, 
				this.patientCondition,
				this.transportProvider,
				this.tourNumber,
				this.paymentExemption, 
				Optional.of(newPatientSignature), 
				Optional.of(newPatientSignatureDate),
				this.transporterSignature,
				this.transporterSignatureDate);
	}
	
	public TransportDetails updateTransporterSignature(
			String newTransporterSignature,
			Date newTransporterSignatureDate){
		return new TransportDetails(
				this.id, 
				this.transportDocument,
				this.transportDate,
				this.startAdress, 
				this.endAdress, 
				this.direction, 
				this.patientCondition,
				this.transportProvider,
				this.tourNumber,
				this.paymentExemption, 
				this.patientSignature, 
				this.patientSignatureDate,
				Optional.of(newTransporterSignature),
				Optional.of(newTransporterSignatureDate));
	}
	
	public String toString() {
		return String.format(
				"TransportDetails[id=%S, transportDocument=%S, transportDate=%S, startAdress=%S, "
				+ "endAdress=%S, direction=%S, patientCondition=%S, transportProvider=%S, "
				+ "tourNumber=%s, paymentExemption=%S, patientSignature=%S, patientSignatureDate=%S, "
				+ "transporterSignature=%S, transporterSignatureDate=%s]", 
				id,
				transportDocument.toString(),
				transportDate.toString(),
				startAdress.toString(),
				endAdress.toString(),
				direction.toString(),
				patientCondition.toString(),
				transportProvider.toString(),
				tourNumber,
				paymentExemption.toString(),
				patientSignature,
				patientSignatureDate.toString(),
				transporterSignature,
				transporterSignatureDate.toString());
	}	
}
