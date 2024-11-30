package de.ehealth.evek.exception;

import java.util.List;

import de.ehealth.evek.entity.Patient;

public class GetPatientListThrowable extends Throwable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3271973190530539711L;
	
	private final List<Patient> list;
	
	public GetPatientListThrowable(List<Patient> patients) {
		this.list = patients;
	}
	
	public List<Patient> getList() {
		return list;
	}

}
