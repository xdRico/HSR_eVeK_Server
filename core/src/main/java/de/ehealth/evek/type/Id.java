package de.ehealth.evek.type;

public record Id<T>(String value) {

	@Override
	public String toString() {
		return value;
	}
}