package de.ehealth.evek.data;

import java.util.List;

import de.ehealth.evek.entity.Address;
import de.ehealth.evek.entity.Insurance;
import de.ehealth.evek.entity.InsuranceData;
import de.ehealth.evek.entity.Patient;
import de.ehealth.evek.entity.ServiceProvider;
import de.ehealth.evek.entity.TransportDetails;
import de.ehealth.evek.entity.TransportDocument;
import de.ehealth.evek.entity.User;
import de.ehealth.evek.type.Id;
import de.ehealth.evek.type.Reference;
import de.ehealth.evek.util.COptional;

public class TransportManagementService implements ITransportManagementService {

	private final IRepository repo;

	public TransportManagementService(IRepository repo){ 
		this.repo = repo;
	}
	
	@Override
	public Address process(Address.Command cmd) throws Exception {
		return switch(cmd){
		
			case Address.Create create -> { 
				
				var obj = new Address(repo.AddressID(), 
						create.name(),
						create.streetName(),
						create.houseNumber(),
						create.country(),
						create.postCode(),
						create.city());
				
				repo.save(obj);
				
				yield obj;
			}
			
			case Address.Delete delete -> {
				
				var deleteObj = repo.getAddress(delete.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Address ID"));
				
				repo.delete(deleteObj);
				
				yield deleteObj;
			}
			
			case Address.Update update -> {
				
				var updateObj  = repo.getAddress(update.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Address ID"))
						.updateWith(update.name(),
								update.streetName(),
								update.houseNumber(),
								update.postCode(),
								update.city());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
		};
	}

	
	//TODO TransportManagementService - Security: Roles and Rights!

	@Override
	public List<Address> getAddress(Address.Filter filter) {	
		return repo.getAddress(filter);
	}

	@Override
	public Address getAddress(Id<Address> id) {
		Address obj = null;
		obj = repo.getAddress(id).orElseThrow(() -> new IllegalArgumentException("Invalid Address ID")); 
		return obj;
	}
	
	@Override
	public Insurance process(Insurance.Command cmd) throws Exception {
		return switch(cmd){
		
			case Insurance.Create create -> { 
				
				var obj = new Insurance(repo.InsuranceID(create.insuranceId()), 
						create.name(),
						create.address());
				
				repo.save(obj);
				
				yield obj;
			}
			
			case Insurance.Delete delete -> {
				
				var deleteObj = repo.getInsurance(delete.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Insurance ID"));
				
				repo.delete(deleteObj);
				
				yield deleteObj;
			}
			
			case Insurance.Update update -> {
				
				var updateObj  = repo.getInsurance(update.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Insurance ID"))
						.updateWith(update.name());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
			case Insurance.Move move -> {
				var updateObj  = repo.getInsurance(move.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Insurance ID"))
						.updateWith(move.address());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
		};
	}


	@Override
	public List<Insurance> getInsurance(Insurance.Filter filter) {	
		return repo.getInsurance(filter);
	}

	@Override
	public Insurance getInsurance(Id<Insurance> id) {
		Insurance obj = null;
		obj = repo.getInsurance(id).orElseThrow(() -> new IllegalArgumentException("Invalid Insurance ID")); 
		return obj;
	}
	
	@Override
	public InsuranceData process(InsuranceData.Command cmd) throws Exception {
		return switch(cmd){
		
			case InsuranceData.Create create -> { 
				
				var obj = new InsuranceData(repo.InsuranceDataID(), 
						create.patient(),
						create.insurance(),
						create.insuranceStatus());
				
				repo.save(obj);
				
				yield obj;
			}
			
			case InsuranceData.Delete delete -> {
				
				var deleteObj = repo.getInsuranceData(delete.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Insurance Data ID"));
				
				repo.delete(deleteObj);
				
				yield deleteObj;
			}
		};
	}


	@Override
	public List<InsuranceData> getInsuranceData(InsuranceData.Filter filter) {	
		return repo.getInsuranceData(filter);
	}

	@Override
	public InsuranceData getInsuranceData(Id<InsuranceData> id) {
		InsuranceData obj = null;
		obj = repo.getInsuranceData(id).orElseThrow(() -> new IllegalArgumentException("Invalid Insurance Data ID")); 
		return obj;
	}
	
//	@Override
//	public Insurance process(Invoice.Command cmd) throws Exception {
//		return switch(cmd){
//		
//			case Invoice.Create create -> { 
//				
//				var obj = new Invoice(repo.InvoiceID(create.insuranceId()), 
//						create.name(),
//						create.address());
//				
//				repo.save(obj);
//				
//				yield obj;
//			}
//			
//			case Invoice.Delete delete -> {
//				
//				var deleteObj = repo.getInvoice(delete.id())
//						.orElseThrow(() -> new IllegalArgumentException("Invalid Invoice ID"));
//				
//				repo.delete(deleteObj);
//				
//				yield deleteObj;
//			}
//			
//			case Invoice.Update update -> {
//				
//				var updateObj  = repo.getInvoice(update.id())
//						.orElseThrow(() -> new IllegalArgumentException("Invalid Invoice ID"))
//						.updateWith(update.name());
//				
//				repo.save(updateObj);
//			
//				yield updateObj;
//			}
//			case Invoice.Move move -> {
//				var updateObj  = repo.getInvoice(move.id())
//						.orElseThrow(() -> new IllegalArgumentException("Invalid Invoice ID"))
//						.updateWith(move.address());
//				
//				repo.save(updateObj);
//			
//				yield updateObj;
//			}
//		};
//	}
//
//
//	@Override
//	public List<Invoice> getInvoice(Invoice.Filter filter) {	
//		return repo.getInvoice(filter);
//	}
//
//	@Override
//	public Invoice getInvoice(Id<Invoice> id) {
//		Invoice obj = null;
//		obj = repo.getInvoice(id).orElseThrow(() -> new IllegalArgumentException("Invalid Invoice ID")); 
//		return obj;
//	}
	
	@Override
	public Patient process(Patient.Command cmd) throws Exception {
		return switch(cmd){
		
			case Patient.Create create -> { 
				
				var obj = new Patient(repo.PatientID(create.insuranceNumber()), 
						create.insuranceData(),
						create.lastName(),
						create.firstName(),
						create.birthDate(),
						create.address());
				
				repo.save(obj);
				
				yield obj;
			}
			
			case Patient.Delete delete -> {
				
				var deleteObj = repo.getPatient(delete.insuranceNumber())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Patient ID"));
				
				repo.delete(deleteObj);
				
				yield deleteObj;
			}
			
			case Patient.Update update -> {
				
				var updateObj  = repo.getPatient(update.insuranceNumber())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Patient ID"))
						.updateWith(update.insuranceData(),
								update.lastName(),
								update.firstName(),
								update.address());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
			case Patient.Move move -> {
				var updateObj  = repo.getPatient(move.insuranceNumber())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Patient ID"))
						.updateWith(move.address());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
		};
	}

	@Override
	public List<Patient> getPatient(Patient.Filter filter) {	
		return repo.getPatient(filter);
	}

	@Override
	public Patient getPatient(Id<Patient> id) {
		Patient obj = null;
		obj = repo.getPatient(id).orElseThrow(() -> new IllegalArgumentException("Invalid Patient ID")); 
		return obj;
	}
	
//	@Override
//	public Protocol process(Protocol.Command cmd) throws Exception {
//		return switch(cmd){
//		
//			case Protocol.Create create -> { 
//				
//				var obj = new Protocol(repo.ProtocolID(create.insuranceId()), 
//						create.name(),
//						create.address());
//				
//				repo.save(obj);
//				
//				yield obj;
//			}
//			
//			case Protocol.Delete delete -> {
//				
//				var deleteObj = repo.getProtocol(delete.id())
//						.orElseThrow(() -> new IllegalArgumentException("Invalid Protocol ID"));
//				
//				repo.delete(deleteObj);
//				
//				yield deleteObj;
//			}
//			
//			case Protocol.Update update -> {
//				
//				var updateObj  = repo.getProtocol(update.id())
//						.orElseThrow(() -> new IllegalArgumentException("Invalid Protocol ID"))
//						.updateWith(update.name());
//				
//				repo.save(updateObj);
//			
//				yield updateObj;
//			}
//		};
//	}
//
//
//	@Override
//	public List<Protocol> getProtocol(Protocol.Filter filter) {	
//		return repo.getProtocol(filter);
//	}
//
//	@Override
//	public Protocol getProtocol(Id<Protocol> id) {
//		Protocol obj = null;
//		obj = repo.getProtocole(id).orElseThrow(() -> new IllegalArgumentException("Invalid Protocol ID")); 
//		return obj;
//	}
	
	@Override
	public ServiceProvider process(ServiceProvider.Command cmd) throws Exception {
		return switch(cmd){
		
			case ServiceProvider.Create create -> { 
				
				var obj = new ServiceProvider(repo.ServiceProviderID(create.serviceProviderId()), 
						create.name(),
						create.type(),
						create.isHealthcareProvider(),
						create.isTransportProvider(),
						create.address(),
						create.contactInfo());
				
				repo.save(obj);
				
				yield obj;
			}
			
			case ServiceProvider.CreateFull createFull -> { 

				Address address = process(createFull.addressCmd());
				
				var obj = new ServiceProvider(repo.ServiceProviderID(createFull.serviceProviderId()), 
						createFull.name(),
						createFull.type(),
						createFull.isHealthcareProvider(),
						createFull.isTransportProvider(),
						Reference.to(address.id().value().toString()),
						createFull.contactInfo());
				
				repo.save(obj);
				
				yield obj;
			}
			
			
			case ServiceProvider.Delete delete -> {
				
				var deleteObj = repo.getServiceProvider(delete.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Service Provider ID"));
				
				repo.delete(deleteObj);
				
				yield deleteObj;
			}
			
			case ServiceProvider.Update update -> {
				
				var updateObj  = repo.getServiceProvider(update.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Service Provider ID"))
						.updateWith(update.name(),
								update.type(),
								update.contactInfo());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
			case ServiceProvider.Move move -> {
				var updateObj  = repo.getServiceProvider(move.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Service Provider ID"))
						.updateWith(move.address());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
			case ServiceProvider.UpdateService update -> {
				var updateObj  = repo.getServiceProvider(update.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Service Provider ID"))
						.updateWith(update.providesHealthcare(),
								update.providesTransport());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
		};
	}
	
	@Override
	public List<ServiceProvider> getServiceProvider(ServiceProvider.Filter filter) {	
		return repo.getServiceProvider(filter);
	}

	@Override
	public ServiceProvider getServiceProvider(Id<ServiceProvider> id) {
		ServiceProvider obj = null;
		obj = repo.getServiceProvider(id).orElseThrow(() -> new IllegalArgumentException("Invalid Service Provider ID")); 
		return obj;
	}
	
	@Override
	public TransportDetails process(TransportDetails.Command cmd) throws Exception {
		return switch(cmd){
		
			case TransportDetails.Create create -> { 
				
				var obj = new TransportDetails(repo.TransportDetailsID(), 
						create.transportDocument(),
						create.transportDate(),
						COptional.empty(),
						COptional.empty(),
						COptional.empty(),
						COptional.empty(),
						create.transportProvider(),
						COptional.empty(),
						COptional.empty(),
						COptional.empty(),
						COptional.empty(),
						COptional.empty(),
						COptional.empty());
				
				repo.save(obj);
				
				yield obj;
			}
			
			case TransportDetails.Delete delete -> {
				
				var deleteObj = repo.getTransportDetails(delete.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Transport Details ID"));
				
				repo.delete(deleteObj);
				
				yield deleteObj;
			}
			case TransportDetails.Update update -> {
				var updateObj  = repo.getTransportDetails(update.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Transport Details ID"))
						.updateWith(update.startAddress(),
								update.endAddress(),
								update.direction(),
								update.patientCondition(),
								update.tourNumber(),
								update.paymentExemption());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
			case TransportDetails.UpdatePatientSignature update -> {
				var updateObj  = repo.getTransportDetails(update.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Transport Details ID"))
						.updatePatientSignature(update.patientSignature(),
								update.patientSignatureDate());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
			case TransportDetails.UpdateTransporterSignature update -> {
				var updateObj  = repo.getTransportDetails(update.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Transport Details ID"))
						.updatePatientSignature(update.transporterSignature(),
								update.transporterSignatureDate());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
			
		};
	}


	@Override
	public List<TransportDetails> getTransportDetails(TransportDetails.Filter filter) {	
		return repo.getTransportDetails(filter);
	}

	@Override
	public TransportDetails getTransportDetails(Id<TransportDetails> id) {
		TransportDetails obj = null;
		obj = repo.getTransportDetails(id).orElseThrow(() -> new IllegalArgumentException("Invalid Transport Details ID")); 
		return obj;
	}
	
	@Override
	public TransportDocument process(TransportDocument.Command cmd) throws Exception {
		return switch(cmd){
		
			case TransportDocument.Create create -> { 
				
				var obj = new TransportDocument(repo.TransportDocumentID(), 
						create.patient(),
						create.insuranceData(),
						create.transportReason(),
						create.startDate(),
						create.endDate(),
						create.weeklyFrequency(),
						create.healthcareServiceProvider(),
						create.transportationType(),
						create.additionalInfo(),
						create.signature());
				
				repo.save(obj);
				
				yield obj;
			}
			
			case TransportDocument.Delete delete -> {
				
				var deleteObj = repo.getTransportDocument(delete.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Transport Document ID"));
				
				repo.delete(deleteObj);
				
				yield deleteObj;
			}
			
			case TransportDocument.Update update -> {
				
				var updateObj  = repo.getTransportDocument(update.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Transport Document ID"))
						.updateWith(update.transportReason(),
								update.startDate(),
								update.endDate(),
								update.weeklyFrequency(),
								update.healthcareServiceProvider(),
								update.transportationType(),
								update.additionalInfo(),
								update.signature());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
			case TransportDocument.AssignPatient update -> {
				var updateObj  = repo.getTransportDocument(update.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Transport Document ID"))
						.assignPatient(update.patient(),
								update.insuranceData());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
		};
	}

	@Override
	public List<TransportDocument> getTransportDocument(TransportDocument.Filter filter) {	
		return repo.getTransportDocument(filter);
	}

	@Override
	public TransportDocument getTransportDocument(Id<TransportDocument> id) {
		TransportDocument obj = null;
		obj = repo.getTransportDocument(id).orElseThrow(() -> new IllegalArgumentException("Invalid Transport Document ID")); 
		return obj;
	}
	
	@Override
	public User process(User.Command cmd) throws Exception {
		return switch(cmd){
		
			case User.Create create -> { 
				
				var obj = new User(repo.UserID(create.userName()), 
						create.lastName(),
						create.firstName(),
						create.address(),
						create.serviceProvider(),
						create.role());
				
				repo.save(obj);
				
				yield obj;
			}
			
			case User.CreateFull create -> { 
				
				Address address = process(create.addressCmd());
				ServiceProvider sp = process(create.serviceProviderCmd());
				
				var obj = new User(repo.UserID(create.userName()), 
						create.lastName(),
						create.firstName(),
						Reference.to(address.id().value().toString()),
						Reference.to(sp.id().value().toString()),
						create.role());
				
				repo.save(obj);
				
				yield obj;
			}
			
			case User.Delete delete -> {
				
				var deleteObj = repo.getUser(delete.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid User ID"));
				
				repo.delete(deleteObj);
				
				yield deleteObj;
			}
			
			case User.Update update -> {
				
				var updateObj  = repo.getUser(update.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid User ID"))
						.updateWith(update.lastName(),
								update.firstName(),
								update.address(),
								update.serviceProvider());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
			case User.UpdateRole update -> {
				
				var updateObj  = repo.getUser(update.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid User ID"))
						.updateWith(update.role());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
			case User.LoginUser login ->{
				var loginObj  = repo.getUser(login.userName())
						.orElseThrow(() -> new IllegalArgumentException("Invalid User ID"));
				yield loginObj;
			}
		};
	}

	@Override
	public List<User> getUser(User.Filter filter) {	
		return repo.getUser(filter);
	}

	@Override
	public User getUser(Id<User> id) {
		User obj = null;
		obj = repo.getUser(id).orElseThrow(() -> new IllegalArgumentException("Invalid User ID")); 
		return obj;
	}
	
}
