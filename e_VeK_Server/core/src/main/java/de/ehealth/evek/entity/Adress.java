package de.ehealth.evek.entity;

import java.util.List;
import java.util.Optional;

import de.ehealth.evek.type.Id;

public record Adress(
		Id<Adress> id, 
		Optional<String> name,
		String streetName, 
		String houseNumber,
		String country,
		String postCode,
		String city) {

	public static sealed interface Command permits Create, Delete, Update{
	}

	public static record Create(Optional<String> name, String streetName, String  houseNumber, 
			String country, String postCode, String city) implements Command {
	}

	public static record Delete(Id<Adress> id) implements Command {
	}

	public static record Update(Id<Adress> id, Optional<String> name, String streetName, 
			String houseNumber, String postCode, String city) implements Command {
	}

	public static record Filter(Optional<String> streetName, 
			Optional<String> postCode, Optional<String> city, Optional<String> name) {
	}

	public static interface Operations {
		Adress process(Command cmd) throws Exception;

		List<Adress> getAdress(Filter filter);

		Adress getAdress(Id<Adress> id);
	}

	public Adress updateWith(Optional<String> newName, String newStreetName, 
			String newHouseNumber, String newPostCode, String newCity) {
		return new Adress(this.id, newName, newStreetName, newHouseNumber, this.country, newPostCode, newCity);
	}
	
	public String toString() {
		return String.format(
				"Adress[id=%s, name=%s, streetName=%s, houseNumber=%s, country=%s, postCode=%s, city=%s]", 
				id, name, streetName, houseNumber,country, postCode, city);
	}
}
