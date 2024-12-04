module evek.server {
	exports de.ehealth.evek.server.data;
	exports de.ehealth.evek.server.db;
	exports de.ehealth.evek.server.network;
	exports de.ehealth.evek.server.core;

	requires transitive java.sql;
	requires transitive evek.api;
	
	uses de.ehealth.evek.server.data.IRepository.IRepositoryProvider;
	
	provides de.ehealth.evek.server.data.IRepository.IRepositoryProvider with de.ehealth.evek.server.db.JDBCRepository.RepositoryProvider;
}