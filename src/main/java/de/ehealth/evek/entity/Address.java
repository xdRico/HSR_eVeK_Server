package de.ehealth.evek.entity;

import java.io.Serializable;
import java.util.List;

import de.ehealth.evek.type.Id;
import de.ehealth.evek.util.COptional;

public record Address(
		Id<Address> id, 
		COptional<String> name,
		String streetName, 
		String houseNumber,
		String country,
		String postCode,
		String city
		) implements Serializable {

	public static sealed interface Command extends Serializable permits Create, Delete, Update{
	}

	public static record Create(COptional<String> name, String streetName, String  houseNumber, 
			String country, String postCode, String city) implements Command {
	}
	public static record Delete(Id<Address> id) implements Command {
	}

	public static record Update(Id<Address> id, COptional<String> name, String streetName, 
			String houseNumber, String postCode, String city) implements Command {
	}

	public static record Filter(COptional<String> streetName, 
			COptional<String> postCode, COptional<String> city, COptional<String> name) {
	}

	public static interface Operations {
		Address process(Command cmd) throws Exception;

		List<Address> getAddress(Filter filter);

		Address getAddress(Id<Address> id);
	}

	public Address updateWith(COptional<String> newName, String newStreetName, 
			String newHouseNumber, String newPostCode, String newCity) {
		return new Address(this.id, newName, newStreetName, newHouseNumber, this.country, newPostCode, newCity);
	}
	
	public String toString() {
		return String.format(
				"Address[id=%s, name=%s, streetName=%s, houseNumber=%s, country=%s, postCode=%s, city=%s]", 
				id, name, streetName, houseNumber,country, postCode, city);
	}
	
	
}