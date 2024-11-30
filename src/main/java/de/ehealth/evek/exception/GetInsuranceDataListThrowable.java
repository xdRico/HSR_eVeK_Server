package de.ehealth.evek.exception;

import java.util.List;

import de.ehealth.evek.entity.InsuranceData;

public class GetInsuranceDataListThrowable extends Throwable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3271973190530539711L;
	
	private final List<InsuranceData> list;
	
	public GetInsuranceDataListThrowable(List<InsuranceData> insuranceData) {
		this.list = insuranceData;
	}
	
	public List<InsuranceData> getList() {
		return list;
	}

}
