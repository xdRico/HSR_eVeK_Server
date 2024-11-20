package de.ehealth.evek.type;


public record Reference<T>(Id<T> id) {

	public static <T> Reference<T> to(String id){
		return new Reference<>(new Id<>(id));
	}
	
	@Override
	public String toString() {
		return id.value().toString();
	}
}
