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
import de.ehealth.evek.exception.GetListThrowable;
import de.ehealth.evek.exception.UserNameAlreadyUsedException;
import de.ehealth.evek.exception.UserNotAllowedException;
import de.ehealth.evek.type.Id;
import de.ehealth.evek.type.Reference;
import de.ehealth.evek.util.COptional;
import static de.ehealth.evek.type.UserRole.*;


public class TransportManagementService implements ITransportManagementService {
	
	
	private final IRepository repo;

	public TransportManagementService(IRepository repo){ 
		this.repo = repo;
	}
	
	
	private InsuranceData process(InsuranceData.Create create, User user, boolean isInternal) throws Exception{
		COptional<InsuranceData> optInsuranceData = repo.getInsuranceData(create);
		if(optInsuranceData.isPresent())
			return optInsuranceData.get();
		
		if(!isInternal) {
			var obj = repo.getPatient(create.patient().id())
					.orElseThrow(() -> new IllegalArgumentException("Invalid Patient ID"));
			var objIns = repo.getInsuranceData(obj.insuranceData().id())
					.orElseThrow(() -> new IllegalArgumentException("Invalid Insurance Data ID"));
		
			if(user.role() != SuperUser 
					&& user.serviceProvider().id().value().toString() != create.insurance().id().value().toString()
					&& objIns.insurance().id().value().toString() != create.insurance().id().value().toString())
				throw new UserNotAllowedException("User can't create Patients for another Insurance!", user.id(), user.role());
		}
		var objCreate = new InsuranceData(repo.InsuranceDataID(), 
				create.patient(),
				create.insurance(),
				create.insuranceStatus());
		
		repo.save(objCreate);
		
		return objCreate;
	}


	@Override
	public Address process(Address.Command cmd, Reference<User> processingUser) throws Throwable {
		
		User user;
		if(!(!repo.hasUsers() && processingUser == null)) {
			user = repo.getUser(processingUser.id())
					.orElseThrow(() -> new RuntimeException("Processing user not found!"));
			if(!user.role().getAllowedActions().contains(cmd.getClass()))
				throw new UserNotAllowedException(user.id(), user.role());
		}else user = new User(null, null, null, null, null, SuperUser);
		
		return switch(cmd){
		
			case Address.Create create -> { 
				
				COptional<Address> optAddress = repo.getAddress(create);
				if(optAddress.isPresent())
					yield optAddress.get();
				
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
						.updateWith(update.name());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
			case Address.Get get -> {
				yield repo.getAddress(get.id())
				.orElseThrow(() -> new IllegalArgumentException("Invalid Address ID"));
			}
			case Address.GetList get -> {
				throw new GetListThrowable(repo.getAddress(get.filter()));
			}
		};
	}

	
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
	public Insurance process(Insurance.Command cmd, Reference<User> processingUser) throws Throwable {
		
		User user = repo.getUser(processingUser.id())
				.orElseThrow(() -> new RuntimeException("Processing user not found!"));
		if(!user.role().getAllowedActions().contains(cmd.getClass()))
			throw new UserNotAllowedException(user.id(), user.role());
		
		return switch(cmd){
		
			case Insurance.Create create -> { 
				
				var obj = new Insurance(repo.InsuranceID(create.insuranceId()), 
						create.name(),
						create.address());
				
				repo.save(obj);
				
				yield obj;
			}
			
			case Insurance.Delete delete -> {
				
				if(user.role() != SuperUser && user.serviceProvider().id().value().toString() != delete.id().value().toString())
					throw new UserNotAllowedException("User can't create Patients for another Insurance!", user.id(), user.role());
				
				var deleteObj = repo.getInsurance(delete.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Insurance ID"));
				
				repo.delete(deleteObj);
				
				yield deleteObj;
			}
			
			case Insurance.Update update -> {
				
				if(user.role() != SuperUser && user.serviceProvider().id().value().toString() != update.id().value().toString())
					throw new UserNotAllowedException("User can't create Patients for another Insurance!", user.id(), user.role());
				
				
				var updateObj  = repo.getInsurance(update.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Insurance ID"))
						.updateWith(update.name());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
			case Insurance.Move move -> {
				
				if(user.role() != SuperUser && user.serviceProvider().id().value().toString() != move.id().value().toString())
					throw new UserNotAllowedException("User can't create Patients for another Insurance!", user.id(), user.role());
				
				var updateObj  = repo.getInsurance(move.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Insurance ID"))
						.updateWith(move.address());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
			case Insurance.Get get -> {
				yield repo.getInsurance(get.id())
				.orElseThrow(() -> new IllegalArgumentException("Invalid Insurance ID"));
			}
			case Insurance.GetList get -> {
				throw new GetListThrowable(repo.getInsurance(get.filter()));
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
	public InsuranceData process(InsuranceData.Command cmd, Reference<User> processingUser) throws Throwable {
		
		User user = repo.getUser(processingUser.id())
				.orElseThrow(() -> new RuntimeException("Processing user not found!"));
		if(!user.role().getAllowedActions().contains(cmd.getClass()))
			throw new UserNotAllowedException(user.id(), user.role());
		
		return switch(cmd){
		
			case InsuranceData.Create create -> { 
				
				yield process(create, user, false);
			}
			
			case InsuranceData.Delete delete -> {
				
				var deleteObj = repo.getInsuranceData(delete.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Insurance Data ID"));
			
				repo.delete(deleteObj);
				
				yield deleteObj;
			}
			case InsuranceData.Get get -> {
				yield repo.getInsuranceData(get.id())
				.orElseThrow(() -> new IllegalArgumentException("Invalid Insurance Data ID"));
			}
			case InsuranceData.GetList get -> {
				throw new GetListThrowable(repo.getInsuranceData(get.filter()));
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
	public Patient process(Patient.Command cmd, Reference<User> processingUser) throws Throwable {
		
		User user = repo.getUser(processingUser.id())
				.orElseThrow(() -> new RuntimeException("Processing user not found!"));
		if(!user.role().getAllowedActions().contains(cmd.getClass()))
			throw new UserNotAllowedException(user.id(), user.role());
		
		return switch(cmd){
		
			case Patient.Create create -> { 
				
				var objIns = repo.getInsuranceData(create.insuranceData().id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Insurance Data ID"));
				
				if(user.role() != SuperUser && user.serviceProvider().id().value().toString() != objIns.insurance().id().value().toString())
					throw new UserNotAllowedException("User can't create Patients for another Insurance!", user.id(), user.role());
					
				
				var obj = new Patient(repo.PatientID(create.insuranceNumber()), 
						create.insuranceData(),
						create.lastName(),
						create.firstName(),
						create.birthDate(),
						create.address());
				
				repo.save(obj);
				
				yield obj;
			}
			
			case Patient.CreateWithInsuranceData create -> { 
				
				if(user.role() != SuperUser && user.serviceProvider().id().value().toString() != create.insurance().id().value().toString())
					throw new UserNotAllowedException("User can't create Patients for another Insurance!", user.id(), user.role());
				InsuranceData insuranceData = process(
						new InsuranceData.Create(Reference.to(create.insuranceNumber()), create.insurance(), create.insuranceStatus()), 
						user, true);
				
				var obj = new Patient(repo.PatientID(create.insuranceNumber()), 
						Reference.to(insuranceData.id().value().toString()),
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
				
				var objIns = repo.getInsuranceData(deleteObj.insuranceData().id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Insurance Data ID"));
				
				if(user.role() != SuperUser && user.serviceProvider().id().value().toString() != objIns.insurance().id().value().toString())
					throw new UserNotAllowedException("User can't delete Patients of another Insurance!", user.id(), user.role());
					
				
				repo.delete(deleteObj);
				
				yield deleteObj;
			}
			
			case Patient.Update update -> {
				
				var obj  = repo.getPatient(update.insuranceNumber())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Patient ID"));
				var objIns = repo.getInsuranceData(obj.insuranceData().id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Insurance Data ID"));
				
				if(user.role() != SuperUser && user.serviceProvider().id().value().toString() != objIns.insurance().id().value().toString())
					throw new UserNotAllowedException("User can't update Patients of another Insurance!", user.id(), user.role());
					
				var updateObj = obj.updateWith(
								update.lastName(),
								update.firstName(),
								update.address());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
			case Patient.UpdateInsuranceData update -> {
				var obj = repo.getPatient(update.insuranceNumber())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Patient ID"));
				var objIns = repo.getInsuranceData(obj.insuranceData().id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Insurance Data ID"));
				
				if(user.role() != SuperUser && user.serviceProvider().id().value().toString().equals(
						objIns.insurance().id().value().toString()))
					throw new UserNotAllowedException("User can't update Patients of another Insurance!", user.id(), user.role());
						
				var updateObj = obj.updateInsuranceData(Reference.to(update.insuranceData().id().value().toString()));
				
				repo.save(updateObj);
			
				yield updateObj;
			}
			
			case Patient.Move move -> {
				var obj = repo.getPatient(move.insuranceNumber())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Patient ID"));
				var objIns = repo.getInsuranceData(obj.insuranceData().id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Insurance Data ID"));
				
				if(user.role() != SuperUser && user.serviceProvider().id().value().toString().equals(
						objIns.insurance().id().value().toString()))
					throw new UserNotAllowedException("User can't update Patients of another Insurance!", user.id(), user.role());
						
				var updateObj = obj.updateAddress(move.address());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
			case Patient.Get get -> {
				yield repo.getPatient(get.id())
				.orElseThrow(() -> new IllegalArgumentException("Invalid Patient ID"));
			}
			case Patient.GetList get -> {
				throw new GetListThrowable(repo.getPatient(get.filter()));
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
	public ServiceProvider process(ServiceProvider.Command cmd, Reference<User> processingUser) throws Throwable {
		User user;
		if(!(!repo.hasUsers() && processingUser == null)) {
			user = repo.getUser(processingUser.id())
					.orElseThrow(() -> new RuntimeException("Processing user not found!"));
			if(!user.role().getAllowedActions().contains(cmd.getClass()))
				throw new UserNotAllowedException(user.id(), user.role());
		}else user = new User(null, null, null, null, null, SuperUser);
		
		
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

				Address address = process(createFull.addressCmd(), processingUser);
				
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
				
				if(user.role() != SuperUser && user.serviceProvider().id() != delete.id())
					throw new UserNotAllowedException("User can't delete another Service Provider!", user.id(), user.role());

				
				repo.delete(deleteObj);
				
				yield deleteObj;
			}
			
			case ServiceProvider.Update update -> {
				
				var obj = repo.getServiceProvider(update.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Service Provider ID"));
				
				if(user.role() != SuperUser && user.serviceProvider().id() != obj.id())
					throw new UserNotAllowedException("User can't update another Service Provider!", user.id(), user.role());

				var updateObj = obj.updateWith(update.name(),
								update.type(),
								update.contactInfo());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
			case ServiceProvider.Move move -> {
				var obj = repo.getServiceProvider(move.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Service Provider ID"));
						
				if(user.role() != SuperUser && user.serviceProvider().id() != obj.id())
					throw new UserNotAllowedException("User can't update another Service Provider!", user.id(), user.role());

						
				var updateObj = obj.updateWith(move.address());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
			case ServiceProvider.UpdateService update -> {
				var obj = repo.getServiceProvider(update.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Service Provider ID"));
				
				if(user.role() != SuperUser && user.serviceProvider().id() != obj.id())
					throw new UserNotAllowedException("User can't update another Service Provider!", user.id(), user.role());

				var updateObj = obj.updateWith(update.providesHealthcare(),
								update.providesTransport());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
			case ServiceProvider.Get get -> {
				yield repo.getServiceProvider(get.id())
				.orElseThrow(() -> new IllegalArgumentException("Invalid Service Provider ID"));
			}
			case ServiceProvider.GetList get -> {
				throw new GetListThrowable(repo.getServiceProvider(get.filter()));
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
	public TransportDetails process(TransportDetails.Command cmd, Reference<User> processingUser) throws Throwable {
		
		User user = repo.getUser(processingUser.id())
				.orElseThrow(() -> new RuntimeException("Processing user not found!"));
		if(!user.role().getAllowedActions().contains(cmd.getClass()))
			throw new UserNotAllowedException(user.id(), user.role());
		
		return switch(cmd){
		
			case TransportDetails.Create create -> { 
				
				if(user.role() != SuperUser && user.serviceProvider() != create.transportProvider())
					throw new UserNotAllowedException("User can't update Transport Details for another Service Provider!", user.id(), user.role());

				
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
				
				if(user.role() != SuperUser && user.serviceProvider() != deleteObj.transportProvider())
					throw new UserNotAllowedException("User can't update Transport Details for another Service Provider!", user.id(), user.role());

				
				repo.delete(deleteObj);
				
				yield deleteObj;
			}
			case TransportDetails.Update update -> {
				
				var obj = repo.getTransportDetails(update.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Transport Details ID"));
						
				if(user.role() != SuperUser && user.serviceProvider() != obj.transportProvider())
					throw new UserNotAllowedException("User can't update Transport Details for another Service Provider!", user.id(), user.role());

				var updateObj  = obj.updateWith(update.startAddress(),
								update.endAddress(),
								update.direction(),
								update.patientCondition(),
								update.tourNumber(),
								update.paymentExemption());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
			case TransportDetails.UpdatePatientSignature update -> {
				
				var obj = repo.getTransportDetails(update.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Transport Details ID"));
				
				if(user.role() != SuperUser && user.serviceProvider() != obj.transportProvider())
					throw new UserNotAllowedException("User can't update Transport Details for another Service Provider!", user.id(), user.role());

				var updateObj = obj.updatePatientSignature(update.patientSignature(),
								update.patientSignatureDate());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
			case TransportDetails.UpdateTransporterSignature update -> {
				
				var obj = repo.getTransportDetails(update.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Transport Details ID"));
				
				if(user.role() != SuperUser && user.serviceProvider() != obj.transportProvider())
					throw new UserNotAllowedException("User can't update Transport Details for another Service Provider!", user.id(), user.role());

				var updateObj  = obj.updatePatientSignature(update.transporterSignature(),
								update.transporterSignatureDate());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
			case TransportDetails.Get get -> {
				yield repo.getTransportDetails(get.id())
				.orElseThrow(() -> new IllegalArgumentException("Invalid Transport Details ID"));
			}
			case TransportDetails.GetList get -> {
				throw new GetListThrowable(repo.getTransportDetails(get.filter()));
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
	public TransportDocument process(TransportDocument.Command cmd, Reference<User> processingUser) throws Throwable {
		
		User user = repo.getUser(processingUser.id())
				.orElseThrow(() -> new RuntimeException("Processing user not found!"));
		if(!user.role().getAllowedActions().contains(cmd.getClass()))
			throw new UserNotAllowedException(user.id(), user.role());
		
		return switch(cmd){
		
			case TransportDocument.Create create -> { 
				
				if(user.role() != SuperUser && user.serviceProvider() != create.healthcareServiceProvider())
					throw new UserNotAllowedException("User can't create a Transport Document for another Service Provider!", user.id(), user.role());

				
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
						create.signature(),
						false);
				
				repo.save(obj);
				
				yield obj;
			}
			
			case TransportDocument.Delete delete -> {
				var deleteObj = repo.getTransportDocument(delete.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Transport Document ID"));
				
				if(user.role() != SuperUser && user.serviceProvider() != deleteObj.healthcareServiceProvider())
					throw new UserNotAllowedException("User can't delete a Transport Document for another Service Provider!", user.id(), user.role());

				
				repo.delete(deleteObj);
				
				yield deleteObj;
			}
			
			case TransportDocument.Update update -> {
				
				var obj  = repo.getTransportDocument(update.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Transport Document ID"));

				if(user.role() != SuperUser && user.serviceProvider() != obj.healthcareServiceProvider())
					throw new UserNotAllowedException("User can't update a Transport Document for another Service Provider!", user.id(), user.role());

				
					var updateObj = obj.updateWith(update.transportReason(),
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
				
				var obj  = repo.getTransportDocument(update.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Transport Document ID"));

				if(user.role() != SuperUser && user.serviceProvider() != obj.healthcareServiceProvider())
					throw new UserNotAllowedException("User can't update a Transport Document for another Service Provider!", user.id(), user.role());

				
					var updateObj = obj.assignPatient(update.patient(),
								update.insuranceData());
				
				repo.save(updateObj);
			
				yield updateObj;
			}
			case TransportDocument.Get get -> {
				yield repo.getTransportDocument(get.id())
				.orElseThrow(() -> new IllegalArgumentException("Invalid Address ID"));
			}
			case TransportDocument.GetList get -> {
				throw new GetListThrowable(repo.getTransportDocument(get.filter()));
			}
			case TransportDocument.Archive archive -> {
				yield repo.getTransportDocument(archive.id())
				.orElseThrow(() -> new IllegalArgumentException("Invalid Address ID")).archive();
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
	public User process(User.Command cmd, Reference<User> processingUser) throws Throwable {
		User user = null;
		if(!(processingUser == null
				&& (cmd instanceof User.LoginUser 
						|| (cmd instanceof User.CreateFull
								&&  !repo.hasUsers())))) {
			user = repo.getUser(processingUser.id())
					.orElseThrow(() -> new RuntimeException("Processing user not found!"));
			if(!user.role().getAllowedActions().contains(cmd.getClass()))
				throw new UserNotAllowedException(user.id(), user.role());
		}else if(!repo.hasUsers() 
				&& cmd instanceof User.CreateFull)
			user = new User(null, null, null, null, null, SuperUser);
		
		return switch(cmd){
		
			case User.Create create -> { 
				if(user.role() != SuperUser 
						&& user.serviceProvider() != create.serviceProvider())
					throw new UserNotAllowedException("User can't create a user for another Service Provider!", 
							user.id(), user.role());
				if((user.role() == HealthcareAdmin 
						&& !(create.role() == HealthcareAdmin 
							|| create.role() == HealthcareDoctor 
							|| create.role() == HealthcareUser))
						
						|| (user.role() == TransportAdmin
							&& !(create.role() == TransportAdmin 
							|| create.role() == TransportDoctor 
							|| create.role() == TransportInvoice
							|| create.role() == TransportUser))
						
						|| (user.role() == InsuranceAdmin
							&& !(create.role() == InsuranceAdmin 
							|| create.role() == InsuranceUser )))
					throw new UserNotAllowedException("User can't create a user role for another Institution type!", 
							user.id(), user.role());

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
				
				
				Address address = process(create.addressCmd(), processingUser);
				ServiceProvider sp = process(create.serviceProviderCmd(), processingUser);
				
				var obj = new User(repo.UserID(create.userName()), 
						create.lastName(),
						create.firstName(),
						Reference.to(address.id().value().toString()),
						Reference.to(sp.id().value().toString()),
						create.role());
				repo.save(obj);
				
				var loginObj = new User.LoginUser(create.userName(), create.password());
				repo.save(loginObj);
				
				yield obj;
			}
			
			case User.Delete delete -> {
				var deleteObj = repo.getUser(delete.id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid User ID"));
				if(user.role() != SuperUser 
						&& user.serviceProvider() != deleteObj.serviceProvider())
					throw new UserNotAllowedException("User can't delete a user for another Service Provider!", 
							user.id(), user.role());
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
			case User.UpdateCredentials update ->{
				repo.loginCredentials(new User.LoginUser(update.oldUserName().value().toString(), update.oldPassword()));
				if(repo.getUser(update.newUserName()) != null)
					throw new UserNameAlreadyUsedException(update.newUserName());
				var updateObj  = repo.getUser(update.oldUserName())
						.orElseThrow(() -> new IllegalArgumentException("Invalid User ID")).updateCredentials(update.oldUserName(), repo.UserID(update.newUserName()));
				
				repo.save(updateObj);
			
				yield updateObj;
			}
			case User.LoginUser login ->{
				yield repo.loginCredentials(login);
			}
			case User.Get get -> {
				yield repo.getUser(get.id())
				.orElseThrow(() -> new IllegalArgumentException("Invalid Address ID"));
			}
			case User.GetList get -> {
				throw new GetListThrowable(repo.getUser(get.filter()));
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
