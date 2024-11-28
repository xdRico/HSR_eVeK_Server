package de.ehealth.evek.type;

import java.io.Serializable;

public record Reference<T>(Id<T> id) implements Serializable{

	public static <T> Reference<T> to(String id){
		return new Reference<>(new Id<>(id));
	}
	
	@Override
	public String toString() {
		return id.value().toString();
	}
}
