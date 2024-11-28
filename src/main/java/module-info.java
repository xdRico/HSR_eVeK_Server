module hsr_evek_server {
	exports de.ehealth.evek.data;
	exports de.ehealth.evek.db;
	exports de.ehealth.evek.util;
	exports de.ehealth.evek.test;
	exports de.ehealth.evek.network;
	exports de.ehealth.evek.entity;
	exports de.ehealth.evek.core;
	exports de.ehealth.evek.type;

	requires transitive java.sql;
	
	uses de.ehealth.evek.data.IRepository.IRepositoryProvider;
	
	provides de.ehealth.evek.data.IRepository.IRepositoryProvider with de.ehealth.evek.db.JDBCRepository.RepositoryProvider;
}