package de.ehealth.evek.data;

import java.util.Iterator;
import java.util.List;
//import java.util.ServiceLoader;
import java.util.ServiceLoader;

import de.ehealth.evek.type.Id;
import de.ehealth.evek.util.COptional;
import de.ehealth.evek.util.Debug;
import de.ehealth.evek.util.Log;
import de.ehealth.evek.db.JDBCRepository;
import de.ehealth.evek.entity.Address;
import de.ehealth.evek.entity.Insurance;
import de.ehealth.evek.entity.InsuranceData;
import de.ehealth.evek.entity.Patient;
import de.ehealth.evek.entity.ServiceProvider;
import de.ehealth.evek.entity.TransportDetails;
import de.ehealth.evek.entity.TransportDocument;
import de.ehealth.evek.entity.User;
import de.ehealth.evek.exception.ProviderNotFoundException;

public interface IRepository {

	/**
	 * AddressID
	 * 
	 * Method to create an individual ID for an Address
	 * 
	 * @return ID - the created ID for the Address
	 */
	Id<Address> AddressID();
	
	/**
	 * InsuranceID
	 * 
	 * Method to set their individual ID for an Insurance
	 * 
	 * @param insuranceIdentifier - the Insurance Identify Number to use as ID
	 * @return ID - the created ID for the Insurance
	 */
	Id<Insurance> InsuranceID(String insuranceIdentifier);
	
	/**
	 * InsuranceDataID
	 * 
	 * Method to create an individual ID for Insurance Data
	 * 
	 * @return ID - the created ID for the Insurance Data
	 */
	Id<InsuranceData> InsuranceDataID();
	
//	/**
//	 * InvoiceID()
//	 *
//	 * Method to create an individual ID for an Invoice
//	 * 
//	 * @return ID - the created ID for the Invoice
//	 */
//	Id<Invoice> InvoiceID();
	
	/**
	 * PatientID
	 * 
	 * Method to create an individual ID for a Patient
	 * 
	 * @param insuranceNumber - the Insurance Number to use as ID
	 * @return ID - the created ID for the Patient
	 */
	Id<Patient> PatientID(String insuranceNumber);
	
//	/**
//	 * ProtocolID()
//	 * 
//	 * Method to create an individual ID for a Protocol
//	 * 
//	 * @return ID - the created ID for the Protocol
//	 */
//	Id<Protocol> ProtocolID();
	
	/**
	 * ServiceProviderID
	 * 
	 * Method to create an individual ID for a Service Provider
	 * 
	 * @param providerIdentifier - the Identify Number to use as ID
	 * @return ID - the created ID for the Service Provider
	 */
	Id<ServiceProvider> ServiceProviderID(String providerIdentifier);
	
	/**
	 * TransportDetailsID
	 * 
	 * Method to create an individual ID for Transport Details
	 * 
	 * @return ID - the created ID for the Transport Details
	 */
	Id<TransportDetails> TransportDetailsID();
	
	/**
	 * TransportDocumentID
	 * 
	 * Method to create an individual ID for a Transport Document
	 * 
	 * @return ID - the created ID for the Transport Document
	 */
	Id<TransportDocument> TransportDocumentID();
	
	/**
	 * UserID
	 * 
	 * Method to create an individual ID for a User
	 * 
	 * @return ID - the created ID for the User
	 */
	Id<User> UserID(String userName);
	
	
	
	
	
	/**
	 * save
	 * 
	 * Method to save an Address to the Database
	 * 
	 * @param Address - the Address to save
	 * @throws Exception - Exception thrown when saving fails
	 */
	void save(Address address) throws Exception;
	
	/**
	 * save
	 * 
	 * Method to save an Insurance to the Database
	 * 
	 * @param insurance - the Insurance to save
	 * @throws Exception - Exception thrown when saving fails
	 */
	void save(Insurance insurance) throws Exception;
	
	/**
	 * save
	 * 
	 * Method to save an InsuranceData to the Database
	 * 
	 * @param insuranceData - the InsuranceData to save
	 * @throws Exception - Exception thrown when saving fails
	 */
	void save(InsuranceData insuranceData) throws Exception;
	
//	/**
//	 * save
//	 * 
//	 * Method to save an Invoice to the Database
//	 * 
//	 * @param invoice - the Invoice to save
//	 * @throws Exception - Exception thrown when saving fails
//	 */
//	void save(Invoice invoice) throws Exception;
	
	/**
	 * save
	 * 
	 * Method to save a Patient to the Database
	 * 
	 * @param patient - the Patient to save
	 * @throws Exception - Exception thrown when saving fails
	 */
	void save(Patient patient) throws Exception;
	
//	/**
//	 * save
//	 * 
//	 * Method to save a Protocol to the Database
//	 * 
//	 * @param protocol - the Protocol to save
//	 * @throws Exception - Exception thrown when saving fails
//	 */
//	void save(Protocol protocol) throws Exception;
	
	/**
	 * save
	 * 
	 * Method to save a Service Provider to the Database
	 * 
	 * @param serviceProvider - the Service Provider to save
	 * @throws Exception - Exception thrown when saving fails
	 */
	void save(ServiceProvider serviceProvider) throws Exception;
	
	/**
	 * save
	 * 
	 * Method to save Transport Details to the Database
	 * 
	 * @param transportDetails - the Transport Details to save
	 * @throws Exception - Exception thrown when saving fails
	 */
	void save(TransportDetails transportDetails) throws Exception;
	
	/**
	 * save
	 * 
	 * Method to save a Transport Document to the Database
	 * 
	 * @param transportDocument - the Transport Document to save
	 * @throws Exception - Exception thrown when saving fails
	 */
	void save(TransportDocument transportDocument) throws Exception;
	
	/**
	 * save
	 * 
	 * Method to save an User to the Database
	 * 
	 * @param user - the User to save
	 * @throws Exception - Exception thrown when saving fails
	 */
	void save(User user) throws Exception;
	
	
	
	
	
	
	
	/**
	 * delete
	 * 
	 * Method to delete an Address from the database
	 * 
	 * @param Address - the Address+ to be deleted
	 * @throws Exception - Exception thrown when deleting fails
	 */
	void delete(Address address) throws Exception;	
	
	/**
	 * delete
	 * 
	 * Method to delete an Insurance from the database
	 * 
	 * @param insurance - the Insurance to be deleted
	 * @throws Exception - Exception thrown when deleting fails
	 */
	void delete(Insurance insurance) throws Exception;	
	
	/**
	 * delete
	 * 
	 * Method to delete an Insurance Data from the database
	 * 
	 * @param insuranceData - the Insurance Data to be deleted
	 * @throws Exception - Exception thrown when deleting fails
	 */
	void delete(InsuranceData insuranceData) throws Exception;	
	
//	/**
//	 * delete
//	 * 
//	 * Method to delete an Invoice from the database
//	 * 
//	 * @param invoice - the Invoice to be deleted
//	 * @throws Exception - Exception thrown when deleting fails
//	 */
//	void delete(Invoice invoice) throws Exception;
	
	/**
	 * delete
	 * 
	 * Method to delete a Patient from the database
	 * 
	 * @param patient - the Patient to be deleted
	 * @throws Exception - Exception thrown when deleting fails
	 */
	void delete(Patient patient) throws Exception;	
	
//	/**
//	 * delete
//	 * 
//	 * Method to delete a Protocol from the database
//	 * 
//	 * @param protocol - the Protocol to be deleted
//	 * @throws Exception - Exception thrown when deleting fails
//	 */
//	void delete(Protocol protocol) throws Exception;	
	
	/**
	 * delete
	 * 
	 * Method to delete a Service Provider from the database
	 * 
	 * @param serviceProvider - the ServiceProvider to be deleted
	 * @throws Exception - Exception thrown when deleting fails
	 */
	void delete(ServiceProvider serviceProvider) throws Exception;	
	
	/**
	 * delete
	 * 
	 * Method to delete Transport Details from the database
	 * 
	 * @param transportDetails - the Transport Details to be deleted
	 * @throws Exception - Exception thrown when deleting fails
	 */
	void delete(TransportDetails transportDetails) throws Exception;	
	
	/**
	 * delete
	 * 
	 * Method to delete a Transport Document from the database
	 * 
	 * @param transportDocument - the Transport Document to be deleted
	 * @throws Exception - Exception thrown when deleting fails
	 */
	void delete(TransportDocument transportDocument) throws Exception;	
	
	/**
	 * delete
	 * 
	 * Method to delete an User from the database
	 * 
	 * @param user - the User to be deleted
	 * @throws Exception - Exception thrown when deleting fails
	 */
	void delete(User user) throws Exception;
	
	
	
	
	
	/**
	 * getAddress
	 * 
	 * Method to filter Addresses
	 * 
	 * @param filter - the Filters to be applied
	 * @return List - the List of Resulting Addresses
	 */
	List<Address> getAddress(Address.Filter filter);
	
	/**
	 * getInsurance
	 * 
	 * Method to filter Insurances
	 * 
	 * @param filter - the Filters to be applied
	 * @return List - the List of Resulting Insurances
	 */
	List<Insurance> getInsurance(Insurance.Filter filter);
	
	/**
	 * getInsuranceData
	 * 
	 * Method to filter Insurance Data
	 * 
	 * @param filter - the Filters to be applied
	 * @return List - the List of Resulting Insurance Data
	 */
	List<InsuranceData> getInsuranceData(InsuranceData.Filter filter);
	
//	/**
//	 * getInvoice
//	 * 
//	 * Method to filter Invoices
//	 * 
//	 * @param filter - the Filters to be applied
//	 * @return List - the List of Resulting Invoices
//	 */
//	List<Invoice> getInvoice(Invoice.Filter filter);
	
	/**
	 * getPatient
	 * 
	 * Method to filter Patients
	 * 
	 * @param filter - the Filters to be applied
	 * @return List - the List of Resulting Patients
	 */
	List<Patient> getPatient(Patient.Filter filter);
	
//	/**
//	 * getProtocol
//	 * 
//	 * Method to filter Protocols
//	 * 
//	 * @param filter - the Filters to be applied
//	 * @return List - the List of Resulting Protocols
//	 */
//	List<Protocol> getProtocol(Protocol.Filter filter);
	
	/**
	 * getServiceProvider
	 * 
	 * Method to filter Service Providers
	 * 
	 * @param filter - the Filters to be applied
	 * @return List - the List of Resulting Service Providers
	 */
	List<ServiceProvider> getServiceProvider(ServiceProvider.Filter filter);
	
	/**
	 * getTransportDetails
	 * 
	 * Method to filter Transport Details
	 * 
	 * @param filter - the Filters to be applied
	 * @return List - the List of Resulting Transport Details
	 */
	List<TransportDetails> getTransportDetails(TransportDetails.Filter filter);
	
	/**
	 * getTransportDocument
	 * 
	 * Method to filter Transport Documents
	 * 
	 * @param filter - the Filters to be applied
	 * @return List - the List of Resulting Transport Documents
	 */
	List<TransportDocument> getTransportDocument(TransportDocument.Filter filter);
	
	/**
	 * getUser
	 * 
	 * Method to filter Users
	 * 
	 * @param filter - the Filters to be applied
	 * @return List - the List of Resulting Users
	 */
	List<User> getUser(User.Filter filter);
	
	
	
	
	
	/**
	 * getAddress
	 * 
	 * Method to get an Address by its ID
	 * 
	 * @param id - the id of the Address
	 * @return Address - the Address with the given ID
	 */
	COptional<Address> getAddress(Id<Address> id);
	
	/**
	 * getInsurance
	 * 
	 * Method to get an Insurance by its ID
	 * 
	 * @param id - the id of the Insurance
	 * @return Insurance - the Insurance with the given ID
	 */
	COptional<Insurance> getInsurance(Id<Insurance> id);
	
	/**
	 * getInsuranceData
	 * 
	 * Method to get Insurance Data by its ID
	 * 
	 * @param id - the id of the Insurance Data
	 * @return InsuranceData - the Insurance Data with the given ID
	 */
	COptional<InsuranceData> getInsuranceData(Id<InsuranceData> id);
	
//	/**
//	 * getInvoice
//	 *
//	 * Method to get an Invoice by its ID
//	 * 
//	 * @param id - the id of the Invoice
//	 * @return Invoice - the Invoice with the given ID
//	 */
//	COptional<Invoice> getInvoice(Id<Invoice> id);
	
	/**
	 * getPatient
	 * 
	 * Method to get a Patient by its ID
	 * 
	 * @param id - the id of the Patient
	 * @return Patient - the Patient with the given ID
	 */
	COptional<Patient> getPatient(Id<Patient> id);
	
//	/**
//	 * getProtocol
//	 *	
//	 * Method to get a Protocol by its ID
//	 * 
//	 * @param id - the id of the Protocol
//	 * @return Protocol - the Protocol with the given ID
//	 */
//	COptional<Protocol> getProtocol(Id<Protocol> id);
	
	/**
	 * getServiceProvider
	 * 
	 * Method to get a Service Provider by its ID
	 * 
	 * @param id - the id of the Service Provider
	 * @return ServiceProvider - the Service Provider with the given ID
	 */
	COptional<ServiceProvider> getServiceProvider(Id<ServiceProvider> id);
	
	/**
	 * getTransportDetails
	 * 
	 * Method to get Transport Details by their ID
	 * 
	 * @param id - the id of the Transport Details
	 * @return TransportDetails - the Transport Details with the given ID
	 */
	COptional<TransportDetails> getTransportDetails(Id<TransportDetails> id);
	
	/**
	 * getTransportDocument
	 * 
	 * Method to get a Transport Document by its ID
	 * 
	 * @param id - the id of the Transport Document
	 * @return TransportDocument - the Transport Document with the given ID
	 */
	COptional<TransportDocument> getTransportDocument(Id<TransportDocument> id);
	
	/**
	 * getUser
	 * 
	 * Method to get an User by its ID
	 * 
	 * @param id - the id of the User
	 * @return User - the User with the given ID
	 */
	COptional<User> getUser(Id<User> id);
	
	/**
	 * getUser
	 * 
	 * Method to get an User by its ID
	 * 
	 * @param id - the id of the User as String
	 * @return User - the User with the given ID
	 */
	COptional<User> getUser(String id);
	
	/**
	 * getUser
	 * 
	 * Method to get an User by its ID
	 * 
	 * @param login - the User.LoginUser Command used for login
	 * @return Boolean - the Boolean representing if the Credentials are right
	 */
	Boolean loginCredentials(User.LoginUser login);
	
	
	
	
	/**
	 * interface for
	 */
	public static interface IRepositoryProvider {
		IRepository instance();
	}

	public static IRepository loadInstance(){
		
		Exception e = new ProviderNotFoundException("RepositoryProvider");
		
		try {
			Iterator<IRepositoryProvider> iterator = ServiceLoader.load(IRepositoryProvider.class).iterator();
			if(iterator.hasNext()) {
				IRepository repo = iterator.next().instance();
				Log.sendMessage(String.format("	ServiceLoader successfully loaded %s as IRepositoryProvider!", repo.getClass().getName()));
				return repo;
			}
		}catch(Exception ex) {
			e = ex;
		}
		Debug.sendException(e);
		Log.sendMessage("	Could not load custom RepositoryProvider! ");
		Log.sendMessage("	- Fallback to internal JDBCRepository as IRepositoryProvider!");
		return JDBCRepository.instance();	
	}
}