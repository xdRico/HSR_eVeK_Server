package de.ehealth.evek.entity;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import de.ehealth.evek.type.Direction;
import de.ehealth.evek.type.Id;
import de.ehealth.evek.type.PatientCondition;
import de.ehealth.evek.type.Reference;
import de.ehealth.evek.util.COptional;

public record TransportDetails (
		Id<TransportDetails> id,
		Reference<TransportDocument> transportDocument,
		Date transportDate,
		COptional<Reference<Address>> startAddress,
		COptional<Reference<Address>> endAddress,
		COptional<Direction> direction,
		COptional<PatientCondition> patientCondition,
		Reference<ServiceProvider> transportProvider,
		COptional<String> tourNumber,
		COptional<Boolean> paymentExemption,
		COptional<String> patientSignature,
		COptional<Date> patientSignatureDate,
		COptional<String> transporterSignature,
		COptional<Date> transporterSignatureDate
		) implements Serializable {
	
    private static final long serialVersionUID = 6359875465658792642L;

	public static sealed interface Command extends Serializable permits Create, Delete, Update, UpdatePatientSignature, UpdateTransporterSignature, Get, GetList{
	}

	public static record Create( 
			Reference<TransportDocument> transportDocument, 
			Date transportDate, 
			Reference<ServiceProvider> transportProvider
			) implements Command {
	}

	public static record Delete(Id<TransportDetails> id) implements Command {
	}

	public static record Update(Id<TransportDetails> id,
			COptional<Reference<Address>> startAddress,
			COptional<Reference<Address>> endAddress,
			COptional<Direction> direction,
			COptional<PatientCondition> patientCondition,
			COptional<String> tourNumber,
			COptional<Boolean> paymentExemption) implements Command {
	}

	public static record UpdatePatientSignature(Id<TransportDetails> id,
			String patientSignature,
			Date patientSignatureDate) implements Command {
	}
	
	public static record UpdateTransporterSignature(Id<TransportDetails> id,
			String transporterSignature,
			Date transporterSignatureDate) implements Command {
	}
	
	public static record Get(Id<TransportDetails> id) implements Command {
	}
	public static record GetList(Filter filter) implements Command {
	}
	
	public static record Filter(
			COptional<Reference<TransportDocument>> transportDocument,
			COptional<Date> transportDate, 
			COptional<Reference<Address>> address,
			COptional<Direction> direction,
			COptional<Reference<ServiceProvider>> transportProvider) {
	}

	public static interface Operations {
		TransportDetails process(Command cmd, Reference<User> processingUser) throws Throwable;

		List<TransportDetails> getTransportDetails(Filter filter);

		TransportDetails getTransportDetails(Id<TransportDetails> id);
	}

	public TransportDetails updateWith(
			COptional<Reference<Address>> newStartAddress,
			COptional<Reference<Address>>newEndAddress,
			COptional<Direction> newDirection,
			COptional<PatientCondition> newPatientCondition,
			COptional<String> newTourNumber,
			COptional<Boolean> newPaymentExemption) {
		return new TransportDetails(
				this.id, 
				this.transportDocument,
				this.transportDate,
				newStartAddress, 
				newEndAddress, 
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
				this.startAddress, 
				this.endAddress, 
				this.direction, 
				this.patientCondition,
				this.transportProvider,
				this.tourNumber,
				this.paymentExemption, 
				COptional.of(newPatientSignature), 
				COptional.of(newPatientSignatureDate),
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
				this.startAddress, 
				this.endAddress, 
				this.direction, 
				this.patientCondition,
				this.transportProvider,
				this.tourNumber,
				this.paymentExemption, 
				this.patientSignature, 
				this.patientSignatureDate,
				COptional.of(newTransporterSignature),
				COptional.of(newTransporterSignatureDate));
	}
	
	public String toString() {
		return String.format(
				"TransportDetails[id=%S, transportDocument=%S, transportDate=%S, startAddress=%S, "
				+ "endAddress=%S, direction=%S, patientCondition=%S, transportProvider=%S, "
				+ "tourNumber=%s, paymentExemption=%S, patientSignature=%S, patientSignatureDate=%S, "
				+ "transporterSignature=%S, transporterSignatureDate=%s]", 
				id,
				transportDocument.toString(),
				transportDate.toString(),
				startAddress.toString(),
				endAddress.toString(),
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
