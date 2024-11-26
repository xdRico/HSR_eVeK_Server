package de.ehealth.evek.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import de.ehealth.evek.type.Id;
import de.ehealth.evek.type.Reference;

public record Insurance(
		Id<Insurance> id,
		// ToDo type field?
		String name,
		Reference<Adress> adress
		) implements Serializable {
	
	public static sealed interface Command permits Create, Delete, Move, Update {
	}

	public static record Create(String insuranceId, String name, Reference<Adress> adress) implements Command {
	}

	public static record Delete(Id<Insurance> id) implements Command {
	}
	
	public static record Move(Id<Insurance> id, 
			Reference<Adress> adress) implements Command {
	}

	public static record Update(Id<Insurance> id,
			String name) implements Command {
	}

	public static record Filter(Optional<Reference<Adress>> adress, 
			Optional<String> name) {
	}

	public static interface Operations {
		Insurance process(Command cmd) throws Exception;

		List<Insurance> getInsurance(Filter filter);

		Insurance getInsurance(Id<Insurance> id);
	}

	public Insurance updateWith(String newName) {
		return new Insurance(this.id, newName, this.adress);
	}
	
	public Insurance updateWith(Reference<Adress> newAdress) {
		return new Insurance(this.id, this.name, newAdress);
	}
	
	public String toString() {
		return String.format(
				"Insurance[id=%s, name=%s, adress=%s]", 
				id, name, adress.toString());
	}
}
