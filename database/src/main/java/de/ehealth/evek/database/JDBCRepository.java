package de.ehealth.evek.database;

import static de.ehealth.evek.database.SQL.DELETE_FROM;
import static de.ehealth.evek.database.SQL.INSERT_INTO;
import static de.ehealth.evek.database.SQL.SELECT;
import static de.ehealth.evek.database.SQL.UPDATE;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import de.ehealth.evek.data.IRepository;
import de.ehealth.evek.database.SQL.InsertBuilder;
import de.ehealth.evek.database.SQL.UpdateBuilder;
import de.ehealth.evek.entity.Adress;
import de.ehealth.evek.entity.Insurance;
import de.ehealth.evek.entity.InsuranceData;
import de.ehealth.evek.entity.Patient;
import de.ehealth.evek.entity.ServiceProvider;
import de.ehealth.evek.entity.TransportDetails;
import de.ehealth.evek.entity.TransportDocument;
import de.ehealth.evek.entity.User;
import de.ehealth.evek.type.Direction;
import de.ehealth.evek.type.Id;
import de.ehealth.evek.type.PatientCondition;
import de.ehealth.evek.type.Reference;
import de.ehealth.evek.type.TransportReason;
import de.ehealth.evek.type.TransportationType;
import de.ehealth.evek.type.UserRole;

public class JDBCRepository implements IRepository {

	//Service Provider Interface (SPI)
	public static final class RepositoryProvider implements IRepository.RepositoryProvider {

		@Override
		public IRepository instance() {
			
			return JDBCRepository.instance();
		}
		
	}
	
	private final Connection conn;
	
	private JDBCRepository(Connection conn) {
		this.conn = conn;
	}
	
	/**
	 * instance
	 * 
	 * Method creating a new Instance of JDBCRepository and providing Connection Details
	 * 
	 * @return JDBCRepository - new instance of JDBCRepository
	 */
	public static JDBCRepository instance() {
		try {
			
			class ConnectionDetails {
				String url, username, password;
				
				ConnectionDetails(String url, String username, String password){
					setConnectionInformation(url, username, password);
				}
				void setConnectionInformation(String url, String username, String password){
					this.url = url;
					this.username = username;
					this.password = password;
				}
				String getURL() { return url; }
				String getUsername() { return username; }
				String getPassword() { return password; }
			}
			
			ConnectionDetails cd = new ConnectionDetails("jdbc:postgresql:evek", "postgres", "root");
			
			if(System.getProperty("evek.repo.jdbc.url") != null 
				&& System.getProperty("evek.repo.jdbc.user") != null
				&& System.getProperty("evek.repo.jdbc.password") != null)
				cd.setConnectionInformation(System.getProperty("evek.repo.jdbc.url"),
						System.getProperty("evek.repo.jdbc.user"),
						System.getProperty("evek.repo.jdbc.password"));
				
			var conn = DriverManager.getConnection(cd.getURL(),
						cd.getUsername(),
						cd.getPassword());
			
			
			var repo = new JDBCRepository(conn);
			
			repo.setup();
			
			return repo;
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	// ToDo 
	private static final String CREATE_TABLE_ADRESS = """
			CREATE TABLE IF NOT EXISTS "adress" (
			        "adressId" UUID PRIMARY KEY,
			        "name" VARCHAR(255),
					"streetName" VARCHAR(255) NOT NULL,
					"houseNumber" VARCHAR(10) NOT NULL,
					"country" VARCHAR(63) NOT NULL,
					"postCode" INTEGER NOT NULL,
					"city" VARCHAR(255) NOT NULL
				);
				""";

		private static final String CREATE_TABLE_INSURANCE = """
			CREATE TABLE IF NOT EXISTS "insurance" (
					"insuranceId" UUID NOT NULL PRIMARY KEY,
					"name" VARCHAR(255) NOT NULL,
					"adress" UUID NOT NULL REFERENCES public."adress" ("adressId")
				);
				""";

		private static final String CREATE_TABLE_INSURANCEDATA = """
			CREATE TABLE IF NOT EXISTS "insuranceData" (
					"insuranceDataId" UUID NOT NULL PRIMARY KEY,
					"patient" UUID NOT NULL,
					"insurance" UUID NOT NULL REFERENCES "insurance"("insuranceId"),
					"insuranceStatus" INTEGER NOT NULL
				);
				""";

//		private static final String CREATE_TABLE_INVOICE = "";

		private static final String CREATE_TABLE_PATIENT = """
			CREATE TABLE IF NOT EXISTS "patient" (
					"patientId" UUID NOT NULL PRIMARY KEY,
					"insuranceData" UUID NOT NULL REFERENCES "insuranceData"("insuranceDataId"),
					"lastName" VARCHAR(127) NOT NULL,
					"firstName" VARCHAR(63) NOT NULL,
					"birthDate" DATE NOT NULL,
					"adress" UUID NOT NULL REFERENCES "adress"("adressId")
				);
				""";
		
		//		private static final String CREATE_TABLE_PROTOCOL = "";

		private static final String CREATE_TABLE_SERVICEPROVIDER = """
			CREATE TABLE IF NOT EXISTS "serviceProvider" (
					"serviceProviderId" UUID NOT NULL PRIMARY KEY,
					"name" VARCHAR(255) NOT NULL,
					"type" VARCHAR(127) NOT NULL,
					"isHealthcareProvider" BOOLEAN NOT NULL,
					"isTransportProvider" BOOLEAN NOT NULL,
					"adress" UUID NOT NULL REFERENCES "adress"("adressId"),
					"contactInfo" VARCHAR(255)
				);
				""";

		private static final String CREATE_TABLE_TRANSPORTDETAILS = """
			CREATE TABLE IF NOT EXISTS "transportDetails" (
					"transportId" UUID NOT NULL PRIMARY KEY,
					"transportDocument" UUID NOT NULL REFERENCES "transportDocument"("transportDocumentId"),
					"transportDate" DATE NOT NULL,
					"startAdress" UUID references "adress"("adressId"),
					"endAdress" UUID references "adress"("adressId"),
					"direction" VARCHAR(15),
					"patientCondition" VARCHAR(15),
					"transportServiceProvider" UUID NOT NULL REFERENCES "serviceProvider"("serviceProviderId"),
					"tourNumber" VARCHAR(255),
					"paymentExemption" BOOLEAN,
					"patientSignature" VARCHAR(255),
					"patientSignatureDate" DATE,
					"transporterSignature" VARCHAR(255),
					"transporterSignatureDate" DATE
				);
				""";

		private static final String CREATE_TABLE_TRANSPORTDOCUMENT = """
			CREATE TABLE IF NOT EXISTS "transportDocument" (
					"transportDocumentId" UUID NOT NULL PRIMARY KEY,
					"parient" UUID REFERENCES "patient"("patientId"),
					"insuranceData" UUID NOT NULL REFERENCES "insuranceData"("insuranceDataId"),
					"transportReason" VARCHAR(63) NOT NULL,
					"startDate" DATE NOT NULL,
					"endDate" DATE,
					"weeklyFrequency" INTEGER,
					"healthcareServiceProvider" UUID NOT NULL REFERENCES "serviceProvider"("serviceProviderId"),
					"transportationType" VARCHAR(15) NOT NULL,
					"additionalInfo" VARCHAR(255),
					"signature" UUID NOT NULL REFERENCES "user"("userId")
				);
				""";

		private static final String CREATE_TABLE_USER = """
			CREATE TABLE IF NOT EXISTS "user" (
					"userId" UUID NOT NULL PRIMARY KEY,
					"lastName" VARCHAR(127),
					"firstName" VARCHAR(63),
					"adress" UUID NOT NULL REFERENCES "adress"("adressId"),
					"userName" VARCHAR(63) NOT NULL,
					"serviceProvider" UUID NOT NULL REFERENCES "serviceProvider"("serviceProviderId"),
					"role" VARCHAR(63)
				);
				""";

		private static final String ALTER_TABLE_INSURANCEDATA = """
			ALTER TABLE IF EXISTS "insuranceData"
					ADD FOREIGN KEY ("patient") REFERENCES "patient"("patientId");
				""";	
		
//		private static String insertSQL(Invoice invoice) {
//			 return INSERT_INTO("invoice")
//					 .VALUE("...", invoice.toString())
//					 .toString();
//		}
//		/**
//		 * static insertSQL
//		 * 
//		 * Method returning the SQL Command for inserting a Protocol to the database 
//		 * 
//		 * @param protocol - the Protocol to be added
//		 * @return String - the SQL Command as String
//		 */
//		private static String insertSQL(Protocol protocol) {
//			 return INSERT_INTO("protocol")
//					 .VALUE("...", protocol.toString())
//					 .toString();
//		}
		
		
		
		
		
		
		
		
		
		
		

		/**
		 * setup
		 * 
		 * Method setting up the database at startup 
		 */
		private void setup() {
			try (var stmt = conn.createStatement()) {

				stmt.execute(CREATE_TABLE_ADRESS);
				stmt.execute(CREATE_TABLE_INSURANCE);
				stmt.execute(CREATE_TABLE_INSURANCEDATA);
//				stmt.execute(CREATE_TABLE_INVOICE);
				stmt.execute(CREATE_TABLE_PATIENT);
//				stmt.execute(CREATE_TABLE_PROTOCOL);
				stmt.execute(CREATE_TABLE_SERVICEPROVIDER);
				stmt.execute(CREATE_TABLE_USER);
				stmt.execute(CREATE_TABLE_TRANSPORTDOCUMENT);
				stmt.execute(CREATE_TABLE_TRANSPORTDETAILS);
				stmt.execute(ALTER_TABLE_INSURANCEDATA);

				
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * static insertSQL
		 * 
		 * Method returning the SQL Command for inserting an Adress to the database 
		 * 
		 * @param adress - the Adress to be added
		 * @return String - the SQL Command as String
		 */
		private static String insertSQL(Adress adress) {
			
			InsertBuilder ib = INSERT_INTO("adress")
					.VALUE("adressId", adress.id().value().toString())
					.VALUE("name", adress.name())
					.VALUE("streetName", adress.streetName())
					.VALUE("houseNumber", adress.houseNumber())
					.VALUE("country", adress.country())
					.VALUE("postCode", adress.postCode())
					.VALUE("city", adress.city());
			
			if(adress.name() != null 
					&& adress.name().isPresent())
				ib.VALUE("name", adress.name());
			
			return ib.toString();
		}

		/**
		 * static insertSQL
		 * 
		 * Method returning the SQL Command for inserting an Insurance to the database 
		 * 
		 * @param insurance - the Insurance to be added
		 * @return String - the SQL Command as String
		 */
		private static String insertSQL(Insurance insurance) {
			return INSERT_INTO("insurance")
					.VALUE("insuranceId", insurance.id().value().toString())
					.VALUE("name", insurance.name())
					.VALUE("adress", insurance.adress().id().value().toString())
					.toString();
		}

		/**
		 * static insertSQL
		 * 
		 * Method returning the SQL Command for inserting Insurance Data to the database 
		 * 
		 * @param insuranceData - the Insurance Data to be added
		 * @return String - the SQL Command as String
		 */
		private static String insertSQL(InsuranceData insuranceData) {
			 return INSERT_INTO("insuranceData")
					 .VALUE("insuranceDataId", insuranceData.id().value().toString())
					 .VALUE("patient", insuranceData.patient().id().value().toString())
					 .VALUE("insurance", insuranceData.insurance().id().value().toString())
					 .VALUE("insuranceStatus", insuranceData.insuranceStatus())
					 .toString();
		}
		
//		/**
//		 * static insertSQL
//		 * 
//		 * Method returning the SQL Command for inserting an Invoice to the database 
//		 * 
//		 * @param invoice - the Invoice to be added
//		 * @return String - the SQL Command as String
//		 */
//		private static String insertSQL(Invoice invoice) {
//			 return INSERT_INTO("invoice")
//					 .VALUE("...", invoice.toString())
//					 .toString();
//		}
		
		/**
		 * static insertSQL
		 * 
		 * Method returning the SQL Command for inserting a Patient to the database 
		 * 
		 * @param patient - the Patient to be added
		 * @return String - the SQL Command as String
		 */
		private static String insertSQL(Patient patient) {
			 return INSERT_INTO("patient")
					 .VALUE("patientId", patient.insuranceNumber().value().toString())
					 .VALUE("insuranceData", patient.insuranceData().id().value().toString())
					 .VALUE("lastName", patient.lastName())
					 .VALUE("firstName", patient.firstName())
					 .VALUE("birthDate", patient.birthDate())
					 .VALUE("adress", patient.adress().id().value().toString())
					 .toString();
		}
		
//		/**
//		 * static insertSQL
//		 * 
//		 * Method returning the SQL Command for inserting a Protocol to the database 
//		 * 
//		 * @param protocol - the Protocol to be added
//		 * @return String - the SQL Command as String
//		 */
//		private static String insertSQL(Protocol protocol) {
//			 return INSERT_INTO("protocol")
//					 .VALUE("...", protocol.toString())
//					 .toString();
//		}
		
		/**
		 * static insertSQL
		 * 
		 * Method returning the SQL Command for inserting a Service Provider to the database 
		 * 
		 * @param serviceProvider - the Service Provider to be added
		 * @return String - the SQL Command as String
		 */
		private static String insertSQL(ServiceProvider serviceProvider) {
			 return INSERT_INTO("serviceProvider")
					 .VALUE("serviceProviderId", serviceProvider.id().value().toString())
					 .VALUE("name", serviceProvider.name())
					 .VALUE("type", serviceProvider.type())
					 .VALUE("isHealthcareProvider", serviceProvider.isHealthcareProvider())
					 .VALUE("isTransportProvider", serviceProvider.isTransportProvider())
					 .VALUE("adress", serviceProvider.adress().id().value().toString())
					 .VALUE("contactInfo", serviceProvider.contactInfo())
					 .toString();
		}
		
		/**
		 * static insertSQL
		 * 
		 * Method returning the SQL Command for inserting Transport Details to the database 
		 * 
		 * @param transportDetails - the Transport Details to be added
		 * @return String - the SQL Command as String
		 */
		private static String insertSQL(TransportDetails transportDetails) {
			InsertBuilder ib = INSERT_INTO("transportDetails")
					 .VALUE("transportId", transportDetails.id().value().toString())
					 .VALUE("transportDocument", transportDetails.transportDocument().id().value().toString())
					 .VALUE("transportDate", transportDetails.transportDate())
					 .VALUE("transportServiceProvider", transportDetails.transportProvider().id().value().toString());
			
			if(transportDetails.startAdress() != null 
					&& transportDetails.startAdress().isPresent())
				ib.VALUE("startAdress", transportDetails.startAdress().get().id().value().toString());
			
			if(transportDetails.endAdress() != null 
					&& transportDetails.endAdress().isPresent())
				ib.VALUE("endAdress", transportDetails.endAdress().get().id().value().toString());
			
			if(transportDetails.direction() != null 
					&& transportDetails.direction().isPresent())
				ib.VALUE("direction", transportDetails.direction().get());
			
			if(transportDetails.patientCondition() != null 
					&& transportDetails.patientCondition().isPresent())
				ib.VALUE("patientCondition", transportDetails.patientCondition().get());
			
			if(transportDetails.tourNumber() != null 
					&& transportDetails.tourNumber().isPresent())
				ib.VALUE("tourNumber", transportDetails.tourNumber().get());
			
			if(transportDetails.paymentExemption() != null 
					&& transportDetails.paymentExemption().isPresent())
				ib.VALUE("paymentExemption", transportDetails.paymentExemption().get());
			
			
			if(transportDetails.patientSignature() != null 
					&& transportDetails.patientSignature().isPresent()
					&& transportDetails.patientSignatureDate() != null 
					&& transportDetails.patientSignatureDate().isPresent()) {
				ib.VALUE("patientSignature", transportDetails.patientSignature().get());
				ib.VALUE("patientSignatureDate", transportDetails.patientSignatureDate().get());
			}
		
			if(transportDetails.transporterSignature() != null 
					&& transportDetails.transporterSignature().isPresent()
					&& transportDetails.transporterSignatureDate() != null 
					&& transportDetails.transporterSignatureDate().isPresent()) {
				ib.VALUE("transporterSignature", transportDetails.transporterSignature().get());
				ib.VALUE("transporterSignatureDate", transportDetails.transporterSignatureDate().get());
			}
			
			return ib.toString();
		}
		
		/**
		 * static insertSQL
		 * 
		 * Method returning the SQL Command for inserting a Transport Document to the database 
		 * 
		 * @param transportDocument - the Transport Document to be added
		 * @return String - the SQL Command as String
		 */
		private static String insertSQL(TransportDocument transportDocument) {
			InsertBuilder ib = INSERT_INTO("transportDocument")
					.VALUE("transportDocumentId", transportDocument.id().value().toString())
					.VALUE("insuranceData", transportDocument.insuranceData().id().value().toString())
					.VALUE("transportReason", transportDocument.transportReason())
					.VALUE("startDate", transportDocument.startDate())
					.VALUE("healthcareServiceProvider", transportDocument.healthcareServiceProvider())
					.VALUE("transportationType", transportDocument.transportationType())
					.VALUE("signature", transportDocument.signature().id().value().toString());
			
			if(transportDocument.patient() != null 
					&& transportDocument.patient().isPresent())
				ib.VALUE("patient", transportDocument.patient().get().id().value().toString());
			
			if(transportDocument.endDate() != null 
					&& transportDocument.endDate().isPresent()
					&& transportDocument.weeklyFrequency() != null 
					&& transportDocument.weeklyFrequency().isPresent()) {
				ib.VALUE("endDate", transportDocument.endDate().get());
				ib.VALUE("weeklyFrequency", transportDocument.weeklyFrequency().get());
			}
			
			if(transportDocument.additionalInfo() != null 
					&& transportDocument.additionalInfo().isPresent())
				ib.VALUE("additionalInfo", transportDocument.additionalInfo().get());
			
			return ib.toString();
		}
		
		/**
		 * static insertSQL
		 * 
		 * Method returning the SQL Command for inserting a User to the database 
		 * 
		 * @param user - the User to be added
		 * @return String - the SQL Command as String
		 */
		private static String insertSQL(User user) {
			 return INSERT_INTO("user")
					 .VALUE("userId", user.id().value().toString())
					 .VALUE("lastName", user.lastName())
					 .VALUE("firstName", user.firstName())
					 .VALUE("adress", user.adress().id().value().toString())
					 .VALUE("userName", user.userName())
					 .VALUE("serviceProvider", user.serviceProvider().id().value().toString())
					 .VALUE("role", user.role())
					 .toString();
		}
		
		
		
		
		
		/**
		 * static updateSQL
		 * 
		 * Method returning the SQL Command for updating an Adress in the database 
		 * 
		 * @param adress - the Adress to be updated
		 * @return String - the SQL Command as String
		 */
		private static String updateSQL(Adress adress) {
			
			UpdateBuilder ub = UPDATE("adress")
					.WHERE("adressId", adress.id().value().toString())
					.SET("name", adress.name())
					.SET("streetName", adress.streetName())
					.SET("houseNumber", adress.houseNumber())
					.SET("country", adress.country())
					.SET("postCode", adress.postCode())
					.SET("city", adress.city());
			
			if(adress.name() != null
					&& adress.name().isPresent())
				ub.SET("name", adress.name());
			
			return ub.toString();
		}

		/**
		 * static updateSQL
		 * 
		 * Method returning the SQL Command for updating an Insurance in the database 
		 * 
		 * @param insurance - the Insurance to be updated
		 * @return String - the SQL Command as String
		 */
		private static String updateSQL(Insurance insurance) {
			return UPDATE("insurance")
					.WHERE("insuranceId", insurance.id().value().toString())
					.SET("name", insurance.name())
					.SET("adress", insurance.adress().id().value().toString())
					.toString();
		}

		/**
		 * static updateSQL
		 * 
		 * Method returning the SQL Command for updating Insurance Data in the database 
		 * 
		 * @param insuranceData - the Insurance Data to be updated
		 * @return String - the SQL Command as String
		 */
		private static String updateSQL(InsuranceData insuranceData) {
			 return UPDATE("insuranceData")
					 .WHERE("insuranceDataId", insuranceData.id().value().toString())
					 .SET("patient", insuranceData.patient().id().value().toString())
					 .SET("insurance", insuranceData.insurance().id().value().toString())
					 .SET("insuranceStatus", insuranceData.insuranceStatus())
					 .toString();
		}
		
//		/**
//		 * static updateSQL
//		 * 
//		 * Method returning the SQL Command for updating an Invoice in the database 
//		 * 
//		 * @param invoice - the Invoice to be updated
//		 * @return String - the SQL Command as String
//		 */
//		private static String updateSQL(Invoice invoice) {
//			return UPDATE("invoice")
//		 			.WHERE("...", invoice.toString())
//					.SET("...", invoice.toString())
//					.toString();
//		}
		
		/**
		 * static updateSQL
		 * 
		 * Method returning the SQL Command for updating a Patient in the database 
		 * 
		 * @param patient - the Patient to be updated
		 * @return String - the SQL Command as String
		 */
		private static String updateSQL(Patient patient) {
			 return UPDATE("patient")
					 .WHERE("patientId", patient.insuranceNumber().value().toString())
					 .SET("insuranceData", patient.insuranceData().id().value().toString())
					 .SET("lastName", patient.lastName())
					 .SET("firstName", patient.firstName())
					 .SET("birthDate", patient.birthDate())
					 .SET("adress", patient.adress().id().value().toString())
					 .toString();
		}
		
//		/**
//		 * static updateSQL
//		 * 
//		 * Method returning the SQL Command for updating a Protocol in the database 
//		 * 
//		 * @param protocol - the Protocol to be updated
//		 * @return String - the SQL Command as String
//		 */
//		private static String updateSQL(Protocol protocol) {
//			return UPDATE("protocol")
//					.WHERE("...", protocol.toString())
//					.SET("...", protocol.toString())
//					.toString();
//		}
		
		/**
		 * static updateSQL
		 * 
		 * Method returning the SQL Command for updating a Service Provider in the database 
		 * 
		 * @param serviceProvider - the Service Provider to be updated
		 * @return String - the SQL Command as String
		 */
		private static String updateSQL(ServiceProvider serviceProvider) {
			 return UPDATE("serviceProvider")
					 .WHERE("serviceProviderId", serviceProvider.id().value().toString())
					 .SET("name", serviceProvider.name())
					 .SET("type", serviceProvider.type())
					 .SET("isHealthcareProvider", serviceProvider.isHealthcareProvider())
					 .SET("isTransportProvider", serviceProvider.isTransportProvider())
					 .SET("adress", serviceProvider.adress().id().value().toString())
					 .SET("contactInfo", serviceProvider.contactInfo())
					 .toString();
		}
		
		/**
		 * static updateSQL
		 * 
		 * Method returning the SQL Command for updating Transport Details in the database 
		 * 
		 * @param transportDetails - the Transport Details to be updated
		 * @return String - the SQL Command as String
		 */
		private static String updateSQL(TransportDetails transportDetails) {
			UpdateBuilder ub = UPDATE("transportDetails")
					 .WHERE("transportId", transportDetails.id().value().toString())
					 .SET("transportDocument", transportDetails.transportDocument().id().value().toString())
					 .SET("transportDate", transportDetails.transportDate())
					 .SET("transportServiceProvider", transportDetails.transportProvider().id().value().toString());
			
			if(transportDetails.startAdress() != null 
					&& transportDetails.startAdress().isPresent())
				ub.SET("startAdress", transportDetails.startAdress().get().id().value().toString());
			
			if(transportDetails.endAdress() != null 
					&& transportDetails.endAdress().isPresent())
				ub.SET("endAdress", transportDetails.endAdress().get().id().value().toString());
			
			if(transportDetails.direction() != null 
					&& transportDetails.direction().isPresent())
				ub.SET("direction", transportDetails.direction().get());
			
			if(transportDetails.patientCondition() != null 
					&& transportDetails.patientCondition().isPresent())
				ub.SET("patientCondition", transportDetails.patientCondition().get());
			
			if(transportDetails.tourNumber() != null 
					&& transportDetails.tourNumber().isPresent())
				ub.SET("tourNumber", transportDetails.tourNumber().get());
			
			if(transportDetails.paymentExemption() != null 
					&& transportDetails.paymentExemption().isPresent())
				ub.SET("paymentExemption", transportDetails.paymentExemption().get());
			
			
			if(transportDetails.patientSignature() != null 
					&& transportDetails.patientSignature().isPresent()
					&& transportDetails.patientSignatureDate() != null 
					&& transportDetails.patientSignatureDate().isPresent()) {
				ub.SET("patientSignature", transportDetails.patientSignature().get());
				ub.SET("patientSignatureDate", transportDetails.patientSignatureDate().get());
			}
		
			if(transportDetails.transporterSignature() != null 
					&& transportDetails.transporterSignature().isPresent()
					&& transportDetails.transporterSignatureDate() != null 
					&& transportDetails.transporterSignatureDate().isPresent()) {
				ub.SET("transporterSignature", transportDetails.transporterSignature().get());
				ub.SET("transporterSignatureDate", transportDetails.transporterSignatureDate().get());
			}
			
			return ub.toString();
		}
		
		/**
		 * static updateSQL
		 * 
		 * Method returning the SQL Command for updating a Transport Document in the database 
		 * 
		 * @param transportDocument - the Transport Document to be updated
		 * @return String - the SQL Command as String
		 */
		private static String updateSQL(TransportDocument transportDocument) {
			UpdateBuilder ub = UPDATE("transportDocument")
					.WHERE("transportDocumentId", transportDocument.id().value().toString())
					.SET("insuranceData", transportDocument.insuranceData().id().value().toString())
					.SET("transportReason", transportDocument.transportReason())
					.SET("startDate", transportDocument.startDate())
					.SET("healthcareServiceProvider", transportDocument.healthcareServiceProvider())
					.SET("transportationType", transportDocument.transportationType())
					.SET("signature", transportDocument.signature().id().value().toString());
			
			if(transportDocument.patient() != null 
					&& transportDocument.patient().isPresent())
				ub.SET("patient", transportDocument.patient().get().id().value().toString());
			
			if(transportDocument.endDate() != null 
					&& transportDocument.endDate().isPresent()
					&& transportDocument.weeklyFrequency() != null 
					&& transportDocument.weeklyFrequency().isPresent()) {
				ub.SET("endDate", transportDocument.endDate().get());
				ub.SET("weeklyFrequency", transportDocument.weeklyFrequency().get());
			}
			
			if(transportDocument.additionalInfo() != null 
					&& transportDocument.additionalInfo().isPresent())
				ub.SET("additionalInfo", transportDocument.additionalInfo().get());
			
			return ub.toString();
		}
		
		/**
		 * static updateSQL
		 * 
		 * Method returning the SQL Command for updating a User in the database 
		 * 
		 * @param user - the User to be updated
		 * @return String - the SQL Command as String
		 */
		private static String updateSQL(User user) {
			 return UPDATE("user")
					 .WHERE("userId", user.id().value().toString())
					 .SET("lastName", user.lastName())
					 .SET("firstName", user.firstName())
					 .SET("adress", user.adress().id().value().toString())
					 .SET("userName", user.userName())
					 .SET("serviceProvider", user.serviceProvider().id().value().toString())
					 .SET("role", user.role())
					 .toString();
		}
		
		
		
		
		
		/**
		 * static deleteSQL
		 * 
		 * Method returning the SQL Command for deleting an Adress from the database 
		 * 
		 * @param adress - the Adress to be deleted
		 * @return String - the SQL Command as String
		 */
		private static String deleteSQL(Adress adress) {
			
			return DELETE_FROM("adress")
					.WHERE("adressId", adress.id().value().toString()).toString();
		}

		/**
		 * static deleteSQL
		 * 
		 * Method returning the SQL Command for deleting an Insurance from the database 
		 * 
		 * @param insurance - the Insurance to be deleted
		 * @return String - the SQL Command as String
		 */
		private static String deleteSQL(Insurance insurance) {
			return DELETE_FROM("insurance")
					.WHERE("insuranceId", insurance.id().value().toString())
					.toString();
		}

		/**
		 * static deleteSQL
		 * 
		 * Method returning the SQL Command for deleting Insurance Data from the database 
		 * 
		 * @param insuranceData - the Insurance Data to be deleted
		 * @return String - the SQL Command as String
		 */
		private static String deleteSQL(InsuranceData insuranceData) {
			 return DELETE_FROM("insuranceData")
					 .WHERE("insuranceDataId", insuranceData.id().value().toString())
					 .toString();
		}
		
//		/**
//		 * static deleteSQL
//		 * 
//		 * Method returning the SQL Command for deleting an Invoice from the database 
//		 * 
//		 * @param invoice - the Invoice to be deleted
//		 * @return String - the SQL Command as String
//		 */
//		private static String deleteSQL(Invoice invoice) {
//			return DELETE_FROM("invoice")
//		 			.WHERE("...", invoice.toString())
//					.toString();
//		}
		
		/**
		 * static deleteSQL
		 * 
		 * Method returning the SQL Command for deleting a Patient from the database 
		 * 
		 * @param patient - the Patient to be deleted
		 * @return String - the SQL Command as String
		 */
		private static String deleteSQL(Patient patient) {
			 return DELETE_FROM("patient")
					 .WHERE("patientId", patient.insuranceNumber().value().toString())
					 .toString();
		}
		
//		/**
//		 * static deleteSQL
//		 * 
//		 * Method returning the SQL Command for deleting a Protocol from the database 
//		 * 
//		 * @param protocol - the Protocol to be deleted
//		 * @return String - the SQL Command as String
//		 */
//		private static String deleteSQL(Protocol protocol) {
//			return DELETE_FROM("protocol")
//					.WHERE("...", protocol.toString())
//					.toString();
//		}
		
		/**
		 * static deleteSQL
		 * 
		 * Method returning the SQL Command for deleting a Service Provider from the database 
		 * 
		 * @param serviceProvider - the Service Provider to be deleted
		 * @return String - the SQL Command as String
		 */
		private static String deleteSQL(ServiceProvider serviceProvider) {
			 return DELETE_FROM("serviceProvider")
					 .WHERE("serviceProviderId", serviceProvider.id().value().toString())
					 .toString();
		}
		
		/**
		 * static deleteSQL
		 * 
		 * Method returning the SQL Command for deleting Transport Details from the database 
		 * 
		 * @param transportDetails - the Transport Details to be deleted
		 * @return String - the SQL Command as String
		 */
		private static String deleteSQL(TransportDetails transportDetails) {
			return DELETE_FROM("transportDetails")
					 .WHERE("transportId", transportDetails.id().value().toString())
					 .toString();
		}
		
		/**
		 * static deleteSQL
		 * 
		 * Method returning the SQL Command for deleting a Transport Document from the database 
		 * 
		 * @param transportDocument - the Transport Document to be deleted
		 * @return String - the SQL Command as String
		 */
		private static String deleteSQL(TransportDocument transportDocument) {
			return DELETE_FROM("transportDocument")
					.WHERE("transportDocumentId", transportDocument.id().value().toString())
					.toString();
		}
		
		/**
		 * static deleteSQL
		 * 
		 * Method returning the SQL Command for deleting a User from the database 
		 * 
		 * @param user - the User to be deleted
		 * @return String - the SQL Command as String
		 */
		private static String deleteSQL(User user) {
			 return DELETE_FROM("user")
					 .WHERE("userId", user.id().value().toString())
					 .toString();
		}
		
		
		
		

		/**
		 * static readAdress
		 * 
		 * Method returning an existing Adress from the Database or throwing an SQLException
		 * 
		 * @param rs - the ResultSet returned by the database
		 * @return Adress - the existing Adress
		 * @throws SQLException - if no Result was found
		 */
		private static Adress readAdress(ResultSet rs) throws SQLException {
			Optional<String> name = Optional.empty();
			if(rs.getString("name") != null 
					&& !rs.getString("name").equalsIgnoreCase("")) {
				name = Optional.of(rs.getString("name"));
			}
				
			return new Adress(new Id<>(rs.getString("adressId")), 
					name,
					rs.getString("streetName"),
					rs.getString("houseNumber"),
					rs.getString("country"),
					rs.getString("streetName"),
					rs.getString("city"));
		}
		
		/**
		 * static readInsurance
		 * 
		 * Method returning an existing Insurance from the Database or throwing an SQLException
		 * 
		 * @param rs - the ResultSet returned by the database
		 * @return Insurance - the existing Insurance
		 * @throws SQLException - if no Result was found
		 */
		private static Insurance readInsurance(ResultSet rs) throws SQLException {
			return new Insurance(new Id<>(rs.getString("insuranceId")), 
					rs.getString("name"),
					Reference.to(rs.getString("adress")));
		}
		
		/**
		 * static readInsuranceData
		 * 
		 * Method returning an existing Insurance Data from the Database or throwing an SQLException
		 * 
		 * @param rs - the ResultSet returned by the database
		 * @return InsuranceData - the existing Insurance Data
		 * @throws SQLException - if no Result was found
		 */
		private static InsuranceData readInsuranceData(ResultSet rs) throws SQLException {
			return new InsuranceData(new Id<>(rs.getString("insuranceDataId")), 
					Reference.to(rs.getString("patient")),
					Reference.to(rs.getString("insurance")),
					rs.getInt("insuranceStatus"));
		}
		
//		/**
//		 * static readInvoice
//		 * 
//		 * Method returning an existing Invoice from the Database or throwing an SQLException
//		 * 
//		 * @param rs - the ResultSet returned by the database
//		 * @return Invoice - the existing Invoice
//		 * @throws SQLException - if no Result was found
//		 */
//		private static Invoice readInvoice(ResultSet rs) throws SQLException {
//			return new Invoice(new Id<>(rs.getString("invoiceId")));
//		}
		
		/**
		 * static readPatient
		 * 
		 * Method returning an existing Patient from the Database or throwing an SQLException
		 * 
		 * @param rs - the ResultSet returned by the database
		 * @return Patient - the existing Patient
		 * @throws SQLException - if no Result was found
		 */
		private static Patient readPatient(ResultSet rs) throws SQLException {
			return new Patient(new Id<>(rs.getString("patientId")), 
					Reference.to(rs.getString("insuranceData")),
					rs.getString("lastName"),
					rs.getString("firstName"),
					rs.getDate("birthDate"),
					Reference.to(rs.getString("adress")));
		}
		
		/**
//		 * static readProtocol
//		 * 
//		 * Method returning an existing Protocol from the Database or throwing an SQLException
//		 * 
//		 * @param rs - the ResultSet returned by the database
//		 * @return Protocol - the existing Protocol
//		 * @throws SQLException - if no Result was found
//		 */
//		private static Protocol readProtocol(ResultSet rs) throws SQLException {
//			return new Protocol(new Id<>(rs.getString("protocolId")));
//		}
		
		/**
		 * static readServiceProvider
		 * 
		 * Method returning an existing Service Provider from the Database or throwing an SQLException
		 * 
		 * @param rs - the ResultSet returned by the database
		 * @return ServiceProvider - the existing Service Provider
		 * @throws SQLException - if no Result was found
		 */
		private static ServiceProvider readServiceProvider(ResultSet rs) throws SQLException {
			Optional<String> contactInfo = Optional.empty();
			if(rs.getString("contactInfo") != null 
					&& !rs.getString("contactInfo").equalsIgnoreCase("")) {
				contactInfo = Optional.of(rs.getString("contactInfo"));
			}
			return new ServiceProvider(new Id<>(rs.getString("serviceProviderId")), 
					rs.getString("name"),
					rs.getString("type"),
					rs.getBoolean("isHelathcareProvider"),
					rs.getBoolean("isTransportProvider"),
					Reference.to(rs.getString("adress")),
					contactInfo);
		}
		
		/**
		 * static readTransportDetails
		 * 
		 * Method returning existing Transport Details from the Database or throwing an SQLException
		 * 
		 * @param rs - the ResultSet returned by the database
		 * @return TransportDetails - the existing Transport Details
		 * @throws SQLException - if no Result was found
		 */
		private static TransportDetails readTransportDetails(ResultSet rs) throws SQLException {
			Optional<Reference<Adress>> startAdress = Optional.empty();
			Optional<Reference<Adress>> endAdress = Optional.empty();
			Optional<Direction> direction = Optional.empty();
			Optional<PatientCondition> patientCondition = Optional.empty();
			Optional<String> tourNumber = Optional.empty();
			Optional<Boolean> paymentExemption = Optional.empty();
			Optional<String> patientSignature = Optional.empty();
			Optional<Date> patientSignatureDate = Optional.empty();
			Optional<String> transporterSignature = Optional.empty();
			Optional<Date> transporterSignatureDate = Optional.empty();
			if(rs.getString("startAdress") != null 
					&& !rs.getString("startAdress").equalsIgnoreCase("")) {
				startAdress = Optional.of(Reference.to(rs.getString("startAdress")));
			}
			if(rs.getString("endAdress") != null 
					&& !rs.getString("endAdress").equalsIgnoreCase("")) {
				endAdress = Optional.of(Reference.to(rs.getString("endAdress")));
			}
			if(rs.getString("direction") != null 
					&& !rs.getString("direction").equalsIgnoreCase("")) {
				direction = Optional.of(Direction.valueOf(rs.getString("direction")));
			}
			if(rs.getString("patientCondition") != null 
					&& !rs.getString("patientCondition").equalsIgnoreCase("")) {
				patientCondition = Optional.of(PatientCondition.valueOf(rs.getString("patientCondition")));
			}
			if(rs.getString("tourNumber") != null 
					&& !rs.getString("tourNumber").equalsIgnoreCase("")) {
				tourNumber = Optional.of(rs.getString("tourNumber"));
			}
			if(rs.getString("paymentExemption") != null 
					&& !rs.getString("paymentExemption").equalsIgnoreCase("")) {
				paymentExemption = Optional.of(rs.getBoolean("paymentExemption"));
			}	
			if(rs.getString("patientSignature") != null 
					&& !rs.getString("patientSignature").equalsIgnoreCase("")) {
				patientSignature = Optional.of(rs.getString("patientSignature"));
			}
			if(rs.getString("patientSignatureDate") != null 
					&& !rs.getString("patientSignatureDate").equalsIgnoreCase("")) {
				patientSignatureDate = Optional.of(rs.getDate("patientSignatureDate"));
			}
			if(rs.getString("transporterSignature") != null 
					&& !rs.getString("transporterSignature").equalsIgnoreCase("")) {
				transporterSignature = Optional.of(rs.getString("transporterSignature"));
			}
			if(rs.getString("transporterSignatureDate") != null 
					&& !rs.getString("transporterSignatureDate").equalsIgnoreCase("")) {
				transporterSignatureDate = Optional.of(rs.getDate("transporterSignatureDate"));
			}
			return new TransportDetails(new Id<>(rs.getString("transportId")), 
					Reference.to(rs.getString("transportDocument")),
					rs.getDate("transportDate"),
					startAdress,
					endAdress,
					direction,
					patientCondition,
					Reference.to(rs.getString("transportServiceProvider")),
					tourNumber,
					paymentExemption,
					patientSignature,
					patientSignatureDate,
					transporterSignature,
					transporterSignatureDate);
		}

		/**
		 * static readTransportDocument
		 * 
		 * Method returning an existing Transport Document from the Database or throwing an SQLException
		 * 
		 * @param rs - the ResultSet returned by the database
		 * @return TransportDocument - the existing TransportDocument
		 * @throws SQLException - if no Result was found
		 */
		private static TransportDocument readTransportDocument(ResultSet rs) throws SQLException {
			Optional<Reference<Patient>> patient = Optional.empty();
			Optional<Date> endDate = Optional.empty();
			Optional<Integer> weeklyFrequency = Optional.empty();
			Optional<String> additionalInfo = Optional.empty();
			
			if(rs.getString("patient") != null 
					&& !rs.getString("patient").equalsIgnoreCase("")) {
				patient = Optional.of(Reference.to(rs.getString("patient")));
			}
			if(rs.getDate("endDate") != null
					&& rs.getInt("weeklyFrequency") > -1) {
				endDate = Optional.of(rs.getDate("endDate"));
				weeklyFrequency = Optional.of(rs.getInt("weeklyFrequency"));
			}
			if(rs.getString("additionalInfo") != null 
					&& !rs.getString("additionalInfo").equalsIgnoreCase("")) {
				additionalInfo = Optional.of(rs.getString("additionalInfo"));
			}
			
			return new TransportDocument(new Id<>(rs.getString("transportDocumentId")), 
					patient,
					Reference.to(rs.getString("insuranceData")),
					TransportReason.valueOf(rs.getString("transportReason")),
					rs.getDate("startDate"),
					endDate,
					weeklyFrequency,
					Reference.to(rs.getString("healthcareServiceProvider")),
					TransportationType.valueOf(rs.getString("transportationType")),
					additionalInfo,
					Reference.to(rs.getString("signature")));
		}
		
		/**
		 * static readUser
		 * 
		 * Method returning an existing User from the Database or throwing an SQLException
		 * 
		 * @param rs - the ResultSet returned by the database
		 * @return User - the existing User
		 * @throws SQLException - if no Result was found
		 */
		private static User readUser(ResultSet rs) throws SQLException {
			return new User(new Id<>(rs.getString("userId")), 
					rs.getString("lastName"),
					rs.getString("firstName"),
					Reference.to(rs.getString("adress")),
					rs.getString("userName"),
					Reference.to(rs.getString("serviceProvider")),
					UserRole.valueOf(rs.getString("role")));
		}	
		
		
		
		
		
		@Override
		public Id<Adress> AdressID() {
			return new Id<>(UUID.randomUUID().toString());
		}

		@Override
		public Id<Insurance> InsuranceID(String insuranceIdentifier) {
			return new Id<>(insuranceIdentifier);
		}
		
		@Override
		public Id<InsuranceData> InsuranceDataID() {
			return new Id<>(UUID.randomUUID().toString());
		}

//		@Override
//		public Id<Invoice> Invoice() {
//			return new Id<>(UUID.randomUUID().toString());
//		}
		
		@Override
		public Id<Patient> PatientID(String insuranceNumber) {
			return new Id<>(insuranceNumber);
		}
		
//		@Override
//		public Id<Protocol> ProtocolID() {
//			return new Id<>(UUID.randomUUID().toString());
//		}
		
		@Override
		public Id<ServiceProvider> ServiceProviderID(String providerIdentifier) {
			return new Id<>(providerIdentifier);
		}
		
		@Override
		public Id<TransportDetails> TransportDetailsID() {
			return new Id<>(UUID.randomUUID().toString());
		}
		
		@Override
		public Id<TransportDocument> TransportDocumentID() {
			return new Id<>(UUID.randomUUID().toString());
		}
		
		@Override
		public Id<User> UserID() {
			return new Id<>(UUID.randomUUID().toString());
		}
		
		
		
		
		
		
		@Override
		public void save(Adress adress) throws Exception {
			try (var stmt = conn.createStatement()) {
				var sql = getAdress(adress.id()).isPresent() 
						? updateSQL(adress) : insertSQL(adress);
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public void save(Insurance insurance) throws Exception {
			try (var stmt = conn.createStatement()) {
				var sql = getInsurance(insurance.id()).isPresent() 
						? updateSQL(insurance) : insertSQL(insurance);
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void save(InsuranceData insuranceData) throws Exception {
			try (var stmt = conn.createStatement()) {
				var sql = getInsuranceData(insuranceData.id()).isPresent() 
						? updateSQL(insuranceData) : insertSQL(insuranceData);
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

//		@Override
//		public void save(Invoice invoice) throws Exception {
//			try (var stmt = conn.createStatement()) {
//				var sql = getInvoice(invoice.id()).isPresent() ? updateSQL(invoice) : insertSQL(invoice);
//				stmt.executeUpdate(sql);
//			} catch (SQLException e) {
//				throw new RuntimeException(e);
//			}
//		}
		
		@Override
		public void save(Patient patient) throws Exception {
			try (var stmt = conn.createStatement()) {
				var sql = getPatient(patient.insuranceNumber()).isPresent() 
						? updateSQL(patient) : insertSQL(patient);
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
//		@Override
//		public void save(Protocol protocol) throws Exception {
//			try (var stmt = conn.createStatement()) {
//				var sql = getProtocol(protocol.id()).isPresent() ? updateSQL(protocol) : insertSQL(protocol);
//				stmt.executeUpdate(sql);
//			} catch (SQLException e) {
//				throw new RuntimeException(e);
//			}
//		}
		
		@Override
		public void save(ServiceProvider serviceProvider) throws Exception {
			try (var stmt = conn.createStatement()) {
				var sql = getServiceProvider(serviceProvider.id()).isPresent() 
						? updateSQL(serviceProvider) : insertSQL(serviceProvider);
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public void save(TransportDetails transportDetails) throws Exception {
			try (var stmt = conn.createStatement()) {
				var sql = getTransportDetails(transportDetails.id()).isPresent() 
						? updateSQL(transportDetails) : insertSQL(transportDetails);
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}		
		
		@Override
		public void save(TransportDocument transportDocument) throws Exception {
			try (var stmt = conn.createStatement()) {
				var sql = getTransportDocument(transportDocument.id()).isPresent() 
						? updateSQL(transportDocument) : insertSQL(transportDocument);
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public void save(User user) throws Exception {
			try (var stmt = conn.createStatement()) {
				var sql = getUser(user.id()).isPresent() 
						? updateSQL(user) : insertSQL(user);
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		
		
		
		
		@Override
		public void delete(Adress adress) {
			try (var stmt = conn.createStatement()) {
				if(!getAdress(adress.id()).isPresent())
					return;
				var sql = deleteSQL(adress);
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void delete(Insurance insurance) {
			try (var stmt = conn.createStatement()) {
				if(!getInsurance(insurance.id()).isPresent())
					return;
				var sql = deleteSQL(insurance);
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public void delete(InsuranceData insuranceData) {
			try (var stmt = conn.createStatement()) {
				if(!getInsuranceData(insuranceData.id()).isPresent())
					return;
				var sql = deleteSQL(insuranceData);
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
//		@Override
//		public void delete(Invoice invoice) {
//			try (var stmt = conn.createStatement()) {
//				if(!getInvoice(invoice.id()).isPresent())
//					return;
//				var sql = deleteSQL(invoice);
//				stmt.executeUpdate(sql);
//			} catch (SQLException e) {
//				throw new RuntimeException(e);
//			}
//		}
		
		@Override
		public void delete(Patient patient) {
			try (var stmt = conn.createStatement()) {
				if(!getPatient(patient.insuranceNumber()).isPresent())
					return;
				var sql = deleteSQL(patient);
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
//		@Override
//		public void delete(Protocol protocol) {
//			try (var stmt = conn.createStatement()) {
//				if(!getProtocol(protocol.id()).isPresent())
//					return;
//				var sql = deleteSQL(protocol);
//				stmt.executeUpdate(sql);
//			} catch (SQLException e) {
//				throw new RuntimeException(e);
//			}
//		}
		
		@Override
		public void delete(ServiceProvider serviceProvider) {
			try (var stmt = conn.createStatement()) {
				if(!getServiceProvider(serviceProvider.id()).isPresent())
					return;
				var sql = deleteSQL(serviceProvider);
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public void delete(TransportDetails transportDetails) {
			try (var stmt = conn.createStatement()) {
				if(!getTransportDetails(transportDetails.id()).isPresent())
					return;
				var sql = deleteSQL(transportDetails);
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void delete(TransportDocument transportDocument) {
			try (var stmt = conn.createStatement()) {
				if(!getTransportDocument(transportDocument.id()).isPresent())
					return;
				var sql = deleteSQL(transportDocument);
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public void delete(User user) {
			try (var stmt = conn.createStatement()) {
				if(!getUser(user.id()).isPresent())
					return;
				var sql = deleteSQL(user);
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		
		
		
		@Override
		public List<Adress> getAdress(Adress.Filter filter) {

			var query = SELECT("*").FROM("adress");

			filter.streetName().ifPresent(ref -> query.WHERE("streetName", ref));
			filter.postCode().ifPresent(ref -> query.WHERE("postCode", ref));
			filter.city().ifPresent(ref -> query.WHERE("city", ref));
			filter.name().ifPresent(ref -> query.WHERE("name", ref));

			try (var resultSet = conn.createStatement().executeQuery(query.toString())) {
				var tcs = new ArrayList<Adress>();
				while (resultSet.next()) {
					tcs.add(readAdress(resultSet));
				}
				return tcs;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public List<Insurance> getInsurance(Insurance.Filter filter) {

			var query = SELECT("*").FROM("insurance");

			filter.adress().ifPresent(ref -> query.WHERE("adress", ref.id().value()));
			filter.name().ifPresent(ref -> query.WHERE("name", ref));

			try (var resultSet = conn.createStatement().executeQuery(query.toString())) {
				var tcs = new ArrayList<Insurance>();
				while (resultSet.next()) {
					tcs.add(readInsurance(resultSet));
				}
				return tcs;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public List<InsuranceData> getInsuranceData(InsuranceData.Filter filter) {

			var query = SELECT("*").FROM("insuranceData");

			filter.patient().ifPresent(ref -> query.WHERE("adress", ref.id().value()));
			filter.insurance().ifPresent(ref -> query.WHERE("name", ref.id().value()));

			try (var resultSet = conn.createStatement().executeQuery(query.toString())) {
				var tcs = new ArrayList<InsuranceData>();
				while (resultSet.next()) {
					tcs.add(readInsuranceData(resultSet));
				}
				return tcs;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
//		@Override
//		public List<Invoice> getInvoice(Invoice.Filter filter) {
//
//			var query = SELECT("*").FROM("invoice");
//
//			filter.patient().ifPresent(ref -> query.WHERE("adress", ref.id().value()));
//			filter.insurance().ifPresent(ref -> query.WHERE("name", ref.id().value()));
//
//			try (var resultSet = conn.createStatement().executeQuery(query.toString())) {
//				var tcs = new ArrayList<Invoice>();
//				while (resultSet.next()) {
//					tcs.add(readInvoice(resultSet));
//				}
//				return tcs;
//			} catch (SQLException e) {
//				throw new RuntimeException(e);
//			}
//		}
		
		@Override
		public List<Patient> getPatient(Patient.Filter filter) {

			var query = SELECT("*").FROM("patient");

			filter.adress().ifPresent(ref -> query.WHERE("adress", ref.id().value()));
			filter.lastName().ifPresent(ref -> query.WHERE("lastName", ref));
			filter.firstName().ifPresent(ref -> query.WHERE("firstName", ref));
			filter.birthDate().ifPresent(ref -> query.WHERE("birthDate", ref));
			filter.insuranceData().ifPresent(ref -> query.WHERE("insuranceData", ref));

			try (var resultSet = conn.createStatement().executeQuery(query.toString())) {
				var tcs = new ArrayList<Patient>();
				while (resultSet.next()) {
					tcs.add(readPatient(resultSet));
				}
				return tcs;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
//		@Override
//		public List<Protocol> getProtocol(Protocol.Filter filter) {
//
//			var query = SELECT("*").FROM("protocol");
//
//			filter.patient().ifPresent(ref -> query.WHERE("adress", ref.id().value()));
//			filter.insurance().ifPresent(ref -> query.WHERE("name", ref.id().value()));
//
//			try (var resultSet = conn.createStatement().executeQuery(query.toString())) {
//				var tcs = new ArrayList<Protocol>();
//				while (resultSet.next()) {
//					tcs.add(readProtocol(resultSet));
//				}
//				return tcs;
//			} catch (SQLException e) {
//				throw new RuntimeException(e);
//			}
//		}
		
		@Override
		public List<ServiceProvider> getServiceProvider(ServiceProvider.Filter filter) {

			var query = SELECT("*").FROM("serviceProvider");

			filter.adress().ifPresent(ref -> query.WHERE("adress", ref.id().value()));
			filter.type().ifPresent(ref -> query.WHERE("type", ref));
			filter.name().ifPresent(ref -> query.WHERE("name", ref));

			try (var resultSet = conn.createStatement().executeQuery(query.toString())) {
				var tcs = new ArrayList<ServiceProvider>();
				while (resultSet.next()) {
					tcs.add(readServiceProvider(resultSet));
				}
				return tcs;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public List<TransportDetails> getTransportDetails(TransportDetails.Filter filter) {

			var query = SELECT("*").FROM("transportDetails");

			filter.transportDocument().ifPresent(ref -> query.WHERE("transportDocument", ref.id().value()));
			filter.transportDate().ifPresent(ref -> query.WHERE("transportDate", ref));
			filter.adress().ifPresent(ref -> query.WHERE("startAdress", ref.id().value()));
			filter.adress().ifPresent(ref -> query.WHERE("endAdress", ref.id().value()));
			filter.direction().ifPresent(ref -> query.WHERE("direction", ref));
			filter.transportProvider().ifPresent(ref -> query.WHERE("transportProvider", ref.id().value()));

			try (var resultSet = conn.createStatement().executeQuery(query.toString())) {
				var tcs = new ArrayList<TransportDetails>();
				while (resultSet.next()) {
					tcs.add(readTransportDetails(resultSet));
				}
				return tcs;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public List<TransportDocument> getTransportDocument(TransportDocument.Filter filter) {

			var query = SELECT("*").FROM("transportDocument");

			filter.patient().ifPresent(ref -> query.WHERE("patient", ref.id().value()));
			filter.insuranceData().ifPresent(ref -> query.WHERE("insuranceData", ref.id().value()));
			filter.startDate().ifPresent(ref -> query.WHERE("startDate", ref));
			filter.endDate().ifPresent(ref -> query.WHERE("endDate", ref));
			filter.healthcareServiceProvider().ifPresent(
					ref -> query.WHERE("healthcareServiceProvider", ref.id().value()));
			filter.transportationType().ifPresent(ref -> query.WHERE("transportationType", ref));
			filter.signature().ifPresent(ref -> query.WHERE("signature", ref.id().value()));

			try (var resultSet = conn.createStatement().executeQuery(query.toString())) {
				var tcs = new ArrayList<TransportDocument>();
				while (resultSet.next()) {
					tcs.add(readTransportDocument(resultSet));
				}
				return tcs;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public List<User> getUser(User.Filter filter) {

			var query = SELECT("*").FROM("user");

			filter.lastName().ifPresent(ref -> query.WHERE("lastName", ref));
			filter.firstName().ifPresent(ref -> query.WHERE("firstName", ref));
			filter.adress().ifPresent(ref -> query.WHERE("adress", ref.id().value()));
			filter.userName().ifPresent(ref -> query.WHERE("userName", ref));
			filter.serviceProvider().ifPresent(ref -> query.WHERE("serviceProvider", ref.id().value()));
			filter.role().ifPresent(ref -> query.WHERE("role", ref));

			try (var resultSet = conn.createStatement().executeQuery(query.toString())) {
				var tcs = new ArrayList<User>();
				while (resultSet.next()) {
					tcs.add(readUser(resultSet));
				}
				return tcs;
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		
		
		
		
		@Override
		public Optional<Adress> getAdress(Id<Adress> id) {
			try (var stmt = conn.createStatement();
					var rs = stmt.executeQuery("SELECT * FROM adress WHERE id = '" + id.value() + "'")) {
				if(!rs.isClosed())
					if (rs.next()) 
						return Optional.of(readAdress(rs));
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			return Optional.empty();
		}

		@Override
		public Optional<Insurance> getInsurance(Id<Insurance> id) {
			try (var stmt = conn.createStatement();
					var rs = stmt.executeQuery("SELECT * FROM insurance WHERE id = '" + id.value() + "'")) {
				if(!rs.isClosed())
					if (rs.next()) 
						return Optional.of(readInsurance(rs));
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			return Optional.empty();
		}
		
		@Override
		public Optional<InsuranceData> getInsuranceData(Id<InsuranceData> id) {
			try (var stmt = conn.createStatement();
					var rs = stmt.executeQuery("SELECT * FROM insuranceData WHERE id = '" + id.value() + "'")) {
				if(!rs.isClosed())
					if (rs.next()) 
						return Optional.of(readInsuranceData(rs));
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			return Optional.empty();
		}
		
//		@Override
//		public Optional<Invoice> getInvoice(Id<Invoice> id) {
//			try (var stmt = conn.createStatement();
//					var rs = stmt.executeQuery("SELECT * FROM invoice WHERE id = '" + id.value() + "'")) {
//				if(!rs.isClosed())
//					if (rs.next()) 
//						return Optional.of(readInvoice(rs));
//			} catch (SQLException e) {
//				throw new RuntimeException(e);
//			}
//			return Optional.empty();
//		}
		
		@Override
		public Optional<Patient> getPatient(Id<Patient> id) {
			try (var stmt = conn.createStatement();
					var rs = stmt.executeQuery("SELECT * FROM patient WHERE id = '" + id.value() + "'")) {
				if(!rs.isClosed())
					if (rs.next()) 
						return Optional.of(readPatient(rs));
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			return Optional.empty();
		}
		
//		@Override
//		public Optional<Protocol> getProtocol(Id<Protocol> id) {
//			try (var stmt = conn.createStatement();
//					var rs = stmt.executeQuery("SELECT * FROM protocol WHERE id = '" + id.value() + "'")) {
//				if(!rs.isClosed())
//					if (rs.next()) 
//						return Optional.of(readProtocol(rs));
//			} catch (SQLException e) {
//				throw new RuntimeException(e);
//			}
//			return Optional.empty();
//		}
		
		@Override
		public Optional<ServiceProvider> getServiceProvider(Id<ServiceProvider> id) {
			try (var stmt = conn.createStatement();
					var rs = stmt.executeQuery("SELECT * FROM serviceProvider WHERE id = '" + id.value() + "'")) {
				if(!rs.isClosed())
					if (rs.next()) 
						return Optional.of(readServiceProvider(rs));
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			return Optional.empty();
		}
		
		@Override
		public Optional<TransportDetails> getTransportDetails(Id<TransportDetails> id) {
			try (var stmt = conn.createStatement();
					var rs = stmt.executeQuery("SELECT * FROM transportDetails WHERE id = '" + id.value() + "'")) {
				if(!rs.isClosed())
					if (rs.next()) 
						return Optional.of(readTransportDetails(rs));
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			return Optional.empty();
		}
		
		@Override
		public Optional<TransportDocument> getTransportDocument(Id<TransportDocument> id) {
			try (var stmt = conn.createStatement();
					var rs = stmt.executeQuery("SELECT * FROM transportDocument WHERE id = '" + id.value() + "'")) {
				if(!rs.isClosed())
					if (rs.next()) 
						return Optional.of(readTransportDocument(rs));
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			return Optional.empty();
		}
		
		@Override
		public Optional<User> getUser(Id<User> id) {
			try (var stmt = conn.createStatement();
					var rs = stmt.executeQuery("SELECT * FROM user WHERE id = '" + id.value() + "'")) {
				if(!rs.isClosed())
					if (rs.next()) 
						return Optional.of(readUser(rs));
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			return Optional.empty();
		}
}
