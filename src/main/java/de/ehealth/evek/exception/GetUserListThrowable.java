package de.ehealth.evek.exception;

import java.util.List;

import de.ehealth.evek.entity.Patient;

public class GetUserListThrowable extends Throwable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3271973190530539711L;
	
	private final List<Patient> list;
	
	public GetUserListThrowable(List<Patient> patients) {
		this.list = patients;
	}
	
	public List<Patient> getList() {
		return list;
	}

}
