package de.ehealth.evek.server.data;

import static de.ehealth.evek.api.type.UserRole.HealthcareAdmin;
import static de.ehealth.evek.api.type.UserRole.HealthcareDoctor;
import static de.ehealth.evek.api.type.UserRole.HealthcareUser;
import static de.ehealth.evek.api.type.UserRole.InsuranceAdmin;
import static de.ehealth.evek.api.type.UserRole.InsuranceUser;
import static de.ehealth.evek.api.type.UserRole.SuperUser;
import static de.ehealth.evek.api.type.UserRole.TransportAdmin;
import static de.ehealth.evek.api.type.UserRole.TransportDoctor;
import static de.ehealth.evek.api.type.UserRole.TransportInvoice;
import static de.ehealth.evek.api.type.UserRole.TransportUser;

import java.util.ArrayList;
import java.util.List;

import de.ehealth.evek.api.entity.Address;
import de.ehealth.evek.api.entity.Insurance;
import de.ehealth.evek.api.entity.InsuranceData;
import de.ehealth.evek.api.entity.Patient;
import de.ehealth.evek.api.entity.ServiceProvider;
import de.ehealth.evek.api.entity.TransportDetails;
import de.ehealth.evek.api.entity.TransportDocument;
import de.ehealth.evek.api.entity.User;
import de.ehealth.evek.api.exception.GetListThrowable;
import de.ehealth.evek.api.exception.IllegalProcessException;
import de.ehealth.evek.api.exception.IsArchivedException;
import de.ehealth.evek.api.exception.ProcessingException;
import de.ehealth.evek.api.exception.UserNameAlreadyUsedException;
import de.ehealth.evek.api.exception.UserNotAllowedException;
import de.ehealth.evek.api.exception.UserNotFoundException;
import de.ehealth.evek.api.type.Id;
import de.ehealth.evek.api.type.Reference;
import de.ehealth.evek.api.util.COptional;
import de.ehealth.evek.api.util.Log;


public class TransportManagementService implements ITransportManagementService {
	
	
	private final IRepository repo;

	public TransportManagementService(IRepository repo){ 
		this.repo = repo;
	}
	
	
	private InsuranceData process(InsuranceData.Create create, User user, boolean isInternal) throws IllegalProcessException, ProcessingException {
		try {
			COptional<InsuranceData> optInsuranceData = repo.getInsuranceData(create);
			if(optInsuranceData.isPresent())
				return optInsuranceData.get();
			
			if(!isInternal) {
				var obj = repo.getPatient(create.patient().id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Patient ID"));
				var objIns = repo.getInsuranceData(obj.insuranceData().id())
						.orElseThrow(() -> new IllegalArgumentException("Invalid Insurance Data ID"));
			
				if(user.role() != SuperUser 
						&& !user.serviceProvider().id().value().equalsIgnoreCase(create.insurance().id().value())
						&& !objIns.insurance().id().value().equals(create.insurance().id().value()))
					throw new UserNotAllowedException("User can't create Patients for another Insurance!", user.id(), user.role());
			}
			var objCreate = new InsuranceData(repo.InsuranceDataID(), 
					create.patient(),
					create.insurance(),
					create.insuranceStatus());
			
			repo.save(objCreate);
			
			return objCreate;
		} catch(IllegalArgumentException e) {
			Log.sendException(e);
			throw new IllegalProcessException(e);
		}catch(IllegalProcessException e) {
			throw e;
		} catch(Exception e) {
			Log.sendException(e);
			throw new ProcessingException(e);
		}
	}


	@Override
	public Address process(Address.Command cmd, Reference<User> processingUser) throws IllegalProcessException, ProcessingException, GetListThrowable {
		try {
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
		} catch(IllegalArgumentException e) {
			Log.sendException(e);
			throw new IllegalProcessException(e);
		}catch(IllegalProcessException e) {
			throw e;
		} catch(Exception e) {
			Log.sendException(e);
			throw new ProcessingException(e);
		}
	}
	
	@Override
	public Insurance process(Insurance.Command cmd, Reference<User> processingUser) throws IllegalProcessException, ProcessingException, GetListThrowable {
		try {
			User user = repo.getUser(processingUser.id())
					.orElseThrow(() -> new UserNotFoundException(processingUser.id(), "Processing user not found! - was the user deleted?"));
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
					
					if(user.role() != SuperUser 
							&& !user.serviceProvider().id().value().equalsIgnoreCase(delete.id().value()))
						throw new UserNotAllowedException("User can't create Patients for another Insurance!", user.id(), user.role());
					
					var deleteObj = repo.getInsurance(delete.id())
							.orElseThrow(() -> new IllegalArgumentException("Invalid Insurance ID"));
					
					repo.delete(deleteObj);
					
					yield deleteObj;
				}
				
				case Insurance.Update update -> {
					
					if(user.role() != SuperUser 
							&& !user.serviceProvider().id().value().equalsIgnoreCase(update.id().value()))
						throw new UserNotAllowedException("User can't create Patients for another Insurance!", user.id(), user.role());
					
					
					var updateObj  = repo.getInsurance(update.id())
							.orElseThrow(() -> new IllegalArgumentException("Invalid Insurance ID"))
							.updateWith(update.name());
					
					repo.save(updateObj);
				
					yield updateObj;
				}
				case Insurance.Move move -> {
					
					if(user.role() != SuperUser 
							&& !user.serviceProvider().id().value().equalsIgnoreCase(move.id().value()))
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
		} catch(IllegalArgumentException e) {
			Log.sendException(e);
			throw new IllegalProcessException(e);
		}catch(IllegalProcessException e) {
			throw e;
		} catch(Exception e) {
			Log.sendException(e);
			throw new ProcessingException(e);
		}
	}

	@Override
	public InsuranceData process(InsuranceData.Command cmd, Reference<User> processingUser) throws IllegalProcessException, ProcessingException, GetListThrowable {
		try {
			User user = repo.getUser(processingUser.id())
					.orElseThrow(() -> new UserNotFoundException(processingUser.id(), "Processing user not found! - was the user deleted?"));
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
		} catch(IllegalArgumentException e) {
			Log.sendException(e);
			throw new IllegalProcessException(e);
		}catch(IllegalProcessException e) {
			throw e;
		} catch(Exception e) {
			Log.sendException(e);
			throw new ProcessingException(e);
		}
	}
	
//	@Override
//	public Insurance process(Invoice.Command cmd) throws IllegalProcessException, ProcessingException {
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
	
	@Override
	public Patient process(Patient.Command cmd, Reference<User> processingUser) throws IllegalProcessException, ProcessingException, GetListThrowable {
		try {
			User user = repo.getUser(processingUser.id())
					.orElseThrow(() -> new UserNotFoundException(processingUser.id(), "Processing user not found! - was the user deleted?"));
			if(!user.role().getAllowedActions().contains(cmd.getClass()))
				throw new UserNotAllowedException(user.id(), user.role());
			
			return switch(cmd){
			
				case Patient.Create create -> { 
					
					var objIns = repo.getInsuranceData(create.insuranceData().id())
							.orElseThrow(() -> new IllegalArgumentException("Invalid Insurance Data ID"));
					
					if(user.role() != SuperUser 
							&& !user.serviceProvider().id().value().equalsIgnoreCase(objIns.insurance().id().value()))
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
					
					if(user.role() != SuperUser 
							&& !user.serviceProvider().id().value().equalsIgnoreCase(create.insurance().id().value()))
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
					
					if(user.role() != SuperUser 
							&& !user.serviceProvider().id().value().equalsIgnoreCase(objIns.insurance().id().value()))
						throw new UserNotAllowedException("User can't delete Patients of another Insurance!", user.id(), user.role());
						
					
					repo.delete(deleteObj);
					
					yield deleteObj;
				}
				
				case Patient.Update update -> {
					
					var obj  = repo.getPatient(update.insuranceNumber())
							.orElseThrow(() -> new IllegalArgumentException("Invalid Patient ID"));
					var objIns = repo.getInsuranceData(obj.insuranceData().id())
							.orElseThrow(() -> new IllegalArgumentException("Invalid Insurance Data ID"));
					
					if(user.role() != SuperUser 
							&& !user.serviceProvider().id().value().equalsIgnoreCase(objIns.insurance().id().value()))
						throw new UserNotAllowedException("User can't update Patients of another Insurance!", user.id(), user.role());
						
					var updateObj = obj.updateWith(
									update.lastName(),
									update.firstName());
					
					repo.save(updateObj);
				
					yield updateObj;
				}
				case Patient.UpdateInsuranceData update -> {
					var obj = repo.getPatient(update.insuranceNumber())
							.orElseThrow(() -> new IllegalArgumentException("Invalid Patient ID"));
					var objIns = repo.getInsuranceData(obj.insuranceData().id())
							.orElseThrow(() -> new IllegalArgumentException("Invalid Insurance Data ID"));
					
					if(user.role() != SuperUser 
							&& !user.serviceProvider().id().value().equalsIgnoreCase(objIns.insurance().id().value()))
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
					
					if(user.role() != SuperUser 
							&& !user.serviceProvider().id().value().equalsIgnoreCase(objIns.insurance().id().value()))
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
		} catch(IllegalArgumentException e) {
			Log.sendException(e);
			throw new IllegalProcessException(e);
		}catch(IllegalProcessException e) {
			throw e;
		} catch(Exception e) {
			Log.sendException(e);
			throw new ProcessingException(e);
		}
	}
	
//	@Override
//	public Protocol process(Protocol.Command cmd) throws IllegalProcessException, ProcessingException {
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
	public ServiceProvider process(ServiceProvider.Command cmd, Reference<User> processingUser) throws IllegalProcessException, ProcessingException, GetListThrowable {
		try {
			User user;
			if(!(!repo.hasUsers() && processingUser == null)) {
				user = repo.getUser(processingUser.id())
						.orElseThrow(() -> new UserNotFoundException(processingUser.id(), "Processing user not found! - was the user deleted?"));
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
					
					if(user.role() != SuperUser 
							&& !user.serviceProvider().id().value().equalsIgnoreCase(delete.id().value()))
						throw new UserNotAllowedException("User can't delete another Service Provider!", user.id(), user.role());
	
					
					repo.delete(deleteObj);
					
					yield deleteObj;
				}
				
				case ServiceProvider.Update update -> {
					
					var obj = repo.getServiceProvider(update.id())
							.orElseThrow(() -> new IllegalArgumentException("Invalid Service Provider ID"));
					
					if(user.role() != SuperUser 
							&& !user.serviceProvider().id().value().equalsIgnoreCase(obj.id().value()))
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
							
					if(user.role() != SuperUser 
							&& !user.serviceProvider().id().value().equalsIgnoreCase(obj.id().value()))
						throw new UserNotAllowedException("User can't update another Service Provider!", user.id(), user.role());
	
							
					var updateObj = obj.updateWith(move.address());
					
					repo.save(updateObj);
				
					yield updateObj;
				}
				case ServiceProvider.UpdateService update -> {
					var obj = repo.getServiceProvider(update.id())
							.orElseThrow(() -> new IllegalArgumentException("Invalid Service Provider ID"));
					
					if(user.role() != SuperUser 
							&& !user.serviceProvider().id().value().equalsIgnoreCase(obj.id().value()))
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
		} catch(IllegalArgumentException e) {
			Log.sendException(e);
			throw new IllegalProcessException(e);
		}catch(IllegalProcessException e) {
			throw e;
		} catch(Exception e) {
			Log.sendException(e);
			throw new ProcessingException(e);
		}
	}
	
	@Override
	public TransportDetails process(TransportDetails.Command cmd, Reference<User> processingUser) throws IllegalProcessException, ProcessingException, GetListThrowable {
		try {
			User user = repo.getUser(processingUser.id())
					.orElseThrow(() -> new UserNotFoundException(processingUser.id(), "Processing user not found! - was the user deleted?"));
			if(!user.role().getAllowedActions().contains(cmd.getClass()))
				throw new UserNotAllowedException(user.id(), user.role());
			
			return switch(cmd){
			
				case TransportDetails.Create create -> { 
				
					TransportDocument doc = repo.getTransportDocument(create.transportDocument().id())
							.orElseThrow(() -> new IllegalArgumentException("Invalid Transport Document ID"));
					
					if(doc.isArchived())
						throw new IsArchivedException(doc.id(), "Transport Document is already archived!");
					
					COptional<User> signatureUser = repo.getUser(doc.signature().id());
					
					if((user.role() != SuperUser) 
							&& (signatureUser.isEmpty() || !signatureUser.get().id().value().equals(user.id().value()))
							&& (signatureUser.isEmpty() 
									|| !signatureUser.get().serviceProvider().id().value().equals(user.serviceProvider().id().value())))
						throw new UserNotAllowedException("User can't create Transport Details for another Service Providers Transport Document!", user.id(), user.role());
					
					var obj = new TransportDetails(repo.TransportDetailsID(), 
							create.transportDocument(),
							create.transportDate(),
							COptional.empty(),
							COptional.empty(),
							COptional.empty(),
							COptional.empty(),
							COptional.empty(),
							COptional.empty(),
							COptional.empty(),
							COptional.empty(),
							COptional.empty(),
							COptional.empty(),
							COptional.empty());
					
					repo.save(obj);
					
					yield obj;
				}
				case TransportDetails.AssignTransportProvider update -> {
					
					var obj = repo.getTransportDetails(update.id())
							.orElseThrow(() -> new IllegalArgumentException("Invalid Transport Details ID"));
							
					if(obj.transportProvider().isPresent())
						throw new IllegalArgumentException("Transport has already been assigned to " + obj.transportProvider().get().id().value() + "!");
					
					var updateObj  = obj.updateTransportProvider(update.transportProvider());
					
					repo.save(updateObj);
				
					yield updateObj;
				}
				case TransportDetails.Delete delete -> {
					
					var deleteObj = repo.getTransportDetails(delete.id())
							.orElseThrow(() -> new IllegalArgumentException("Invalid Transport Details ID"));
					
					if(user.role() != SuperUser 
							&& deleteObj.transportProvider().isPresent()
							&& !user.serviceProvider().id().value().equalsIgnoreCase(deleteObj.transportProvider().get().id().value()))
						throw new UserNotAllowedException("User can't update Transport Details for another Service Provider!", user.id(), user.role());
	
					if(deleteObj.transportProvider() != null 
							&& deleteObj.transportProvider().isPresent())
						throw new UserNotAllowedException("Assigned Transports can not be deleted!", user.id(), user.role());
					
					repo.delete(deleteObj);
					
					yield deleteObj;
				}
				case TransportDetails.Update update -> {
					
					var obj = repo.getTransportDetails(update.id())
							.orElseThrow(() -> new IllegalArgumentException("Invalid Transport Details ID"));
							
					if(user.role() != SuperUser 
							&& obj.transportProvider().isPresent()
							&& !user.serviceProvider().id().value().equalsIgnoreCase(obj.transportProvider().get().id().value()))
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
					
					if(user.role() != SuperUser 
							&& obj.transportProvider().isPresent()
							&& !user.serviceProvider().id().value().equalsIgnoreCase(obj.transportProvider().get().id().value()))
						throw new UserNotAllowedException("User can't update Transport Details for another Service Provider!", user.id(), user.role());
	
					var updateObj = obj.updatePatientSignature(update.patientSignature(),
									update.patientSignatureDate());
					
					repo.save(updateObj);
				
					yield updateObj;
				}
				case TransportDetails.UpdateTransporterSignature update -> {
					
					var obj = repo.getTransportDetails(update.id())
							.orElseThrow(() -> new IllegalArgumentException("Invalid Transport Details ID"));
					
					if(user.role() != SuperUser 
							&& obj.transportProvider().isPresent()
							&& !user.serviceProvider().id().value().equalsIgnoreCase(obj.transportProvider().get().id().value()))
						throw new UserNotAllowedException("User can't update Transport Details for another Service Provider!", user.id(), user.role());
	
					var updateObj  = obj.updateTransporterSignature(update.transporterSignature(),
									update.transporterSignatureDate());
					
					repo.save(updateObj);
				
					yield updateObj;
				}
				case TransportDetails.Get get -> {
					yield repo.getTransportDetails(get.id())
					.orElseThrow(() -> new IllegalArgumentException("Invalid Transport Details ID"));
				}
				case TransportDetails.GetList get -> {
					
					COptional<Reference<ServiceProvider>> tp = get.filter().transportProvider();
					
					if(!(user.role() == SuperUser || user.role() == TransportDoctor || user.role() == HealthcareDoctor 
							|| user.role() == HealthcareUser || user.role() == InsuranceUser))
						tp = COptional.of(user.serviceProvider());
					
					TransportDetails.Filter newFilter = new TransportDetails.Filter(get.filter().transportDocument(), 
							get.filter().transportDate(), get.filter().address(), get.filter().direction(), tp);
					
					throw new GetListThrowable(repo.getTransportDetails(newFilter));
				}
				case TransportDetails.GetListByIDList get -> {
					List<TransportDetails> elements = new ArrayList<TransportDetails>();
					for(Id<TransportDetails> elementId : get.idList()) {
						COptional<TransportDetails> element = repo.getTransportDetails(elementId);
						if(element.isPresent())
							elements.add(element.get());
					}
					throw new GetListThrowable(elements);
				}
			};
		} catch(IllegalArgumentException e) {
			Log.sendException(e);
			throw new IllegalProcessException(e);
		}catch(IllegalProcessException | GetListThrowable e) {
			throw e;
		} catch(Exception e) {
			Log.sendException(e);
			throw new ProcessingException(e);
		}
	}

	@Override
	public TransportDocument process(TransportDocument.Command cmd, Reference<User> processingUser) throws IllegalProcessException, ProcessingException, GetListThrowable {
		try {
			User user = repo.getUser(processingUser.id())
					.orElseThrow(() -> new UserNotFoundException(processingUser.id(), "Processing user not found! - was the user deleted?"));
			if(!user.role().getAllowedActions().contains(cmd.getClass()))
				throw new UserNotAllowedException(user.id(), user.role());
			
			return switch(cmd){
			
				case TransportDocument.Create create -> { 
					
					if((user.role() != SuperUser && user.role() != TransportDoctor)  
							&& !user.serviceProvider().id().value().equalsIgnoreCase(create.healthcareServiceProvider().id().value()))
						throw new UserNotAllowedException("User can't create a Transport Document for another Service Provider!", user.id(), user.role());
					COptional<Reference<InsuranceData>> insuranceData = create.insuranceData();
					COptional<Reference<Patient>> patient =  create.patient();
					
					if(insuranceData == null || insuranceData.isEmpty()) {
						if(patient != null && patient.isPresent()) {
							insuranceData = COptional.of(
									Reference.to(
											repo.getPatient(patient.get().id())
											.get().insuranceData().id().value()));
						} else {
							insuranceData = COptional.empty();
							patient = COptional.empty();
						}
					}
					var obj = new TransportDocument(repo.TransportDocumentID(), 
							patient,
							insuranceData,
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
					
					if((user.role() != SuperUser && user.role() != TransportDoctor)  
							&& !user.serviceProvider().id().value().equalsIgnoreCase(deleteObj.healthcareServiceProvider().id().value()))
						throw new UserNotAllowedException("User can't delete a Transport Document for another Service Provider!", user.id(), user.role());
	
					
					repo.delete(deleteObj);
					
					yield deleteObj;
				}
				
				case TransportDocument.Update update -> {
					
					var obj  = repo.getTransportDocument(update.id())
							.orElseThrow(() -> new IllegalArgumentException("Invalid Transport Document ID"));
	
					if((user.role() != SuperUser && user.role() != TransportDoctor)  
							&& !user.serviceProvider().id().value().equalsIgnoreCase(obj.healthcareServiceProvider().id().value()))
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
	
//					if((user.role() != SuperUser && user.role() != TransportDoctor)  
//							&& !user.serviceProvider().id().value().equalsIgnoreCase(obj.healthcareServiceProvider().id().value()))
//						throw new UserNotAllowedException("User can't update a Transport Document for another Service Provider!", user.id(), user.role());
	
					
						var updateObj = obj.assignPatient(update.patient(),
									update.insuranceData());
					
					repo.save(updateObj);
				
					yield updateObj;
				}
				case TransportDocument.Get get -> {
					yield repo.getTransportDocument(get.id())
					.orElseThrow(() -> new IllegalArgumentException("Invalid Transport Document ID"));
				}
				
				case TransportDocument.GetList get -> {
					throw new GetListThrowable(repo.getTransportDocument(get.filter()));
				}
				case TransportDocument.GetListByIDList get -> {
					List<TransportDocument> elements = new ArrayList<TransportDocument>();
					for(Id<TransportDocument> elementId : get.idList()) {
						COptional<TransportDocument> element = repo.getTransportDocument(elementId);
						if(element.isPresent())
							elements.add(element.get());
					}
					throw new GetListThrowable(elements);
				}
				case TransportDocument.Archive archive -> {
					yield repo.getTransportDocument(archive.id())
					.orElseThrow(() -> new IllegalArgumentException("Invalid Transport Document ID")).archive();
				}
			};
		} catch(IllegalArgumentException e) {
			Log.sendException(e);
			throw new IllegalProcessException(e);
		}catch(IllegalProcessException | GetListThrowable e) {
			throw e;
		} catch(Exception e) {
			Log.sendException(e);
			throw new ProcessingException(e);
		}
	}
	
	@Override
	public User process(User.Command cmd, Reference<User> processingUser) throws IllegalProcessException, ProcessingException, GetListThrowable {
		try {
			User user = null;
			if(!(processingUser == null
					&& (cmd instanceof User.LoginUser 
							|| (cmd instanceof User.CreateFull
									&&  !repo.hasUsers())))) {
				user = repo.getUser(processingUser.id())
						.orElseThrow(() -> new UserNotFoundException(processingUser.id(), "Processing user not found! - was the user deleted?"));
				if(!user.role().getAllowedActions().contains(cmd.getClass()))
					throw new UserNotAllowedException(user.id(), user.role());
			}else if(!repo.hasUsers() 
					&& cmd instanceof User.CreateFull)
				user = new User(null, null, null, null, null, SuperUser);
			
			return switch(cmd){
			
				case User.Create create -> { 
					if(user.role() != SuperUser 
							&& !user.serviceProvider().id().value().equalsIgnoreCase(create.serviceProvider().id().value()))
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
					if(user.role() != SuperUser 
							&& !user.serviceProvider().id().value().equalsIgnoreCase(create.serviceProviderCmd().serviceProviderId()))
						throw new UserNotAllowedException("User can't create a user for another Service Provider!", 
								user.id(), user.role());
					
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
							&& !user.serviceProvider().id().value().equalsIgnoreCase(deleteObj.serviceProvider().id().value()))
						throw new UserNotAllowedException("User can't delete a user for another Service Provider!", 
								user.id(), user.role());
					repo.delete(deleteObj);
					
					yield deleteObj;
				}
				
				case User.Update update -> {
					if(user.role() != SuperUser 
							&& !user.serviceProvider().id().value().equalsIgnoreCase(update.serviceProvider().id().value()))
						throw new UserNotAllowedException("User can't delete a user for another Service Provider!", 
								user.id(), user.role());
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
							.orElseThrow(() -> new IllegalArgumentException("Invalid User ID"));
					if(user.role() != SuperUser 
							&& !user.serviceProvider().id().value().equalsIgnoreCase(updateObj.serviceProvider().id().value()))
						throw new UserNotAllowedException("User can't delete a user for another Service Provider!", 
								user.id(), user.role());
					updateObj = updateObj.updateWith(update.role());

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
		} catch(IllegalArgumentException e) {
			Log.sendException(e);
			throw new IllegalProcessException(e);
		}catch(IllegalProcessException e) {
			throw e;
		} catch(Exception e) {
			Log.sendException(e);
			throw new ProcessingException(e);
		}
	}
}