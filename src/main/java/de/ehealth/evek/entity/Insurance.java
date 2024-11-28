package de.ehealth.evek.entity;

import java.io.Serializable;
import java.util.List;
import de.ehealth.evek.util.COptional;

import de.ehealth.evek.type.Id;
import de.ehealth.evek.type.Reference;

public record Insurance(
		Id<Insurance> id,
		// ToDo type field?
		String name,
		Reference<Address> address
		) implements Serializable {
	
	public static sealed interface Command extends Serializable permits Create, Delete, Move, Update {
	}

	public static record Create(String insuranceId, String name, Reference<Address> address) implements Command {
	}
	public static record Delete(Id<Insurance> id) implements Command {
	}
	
	public static record Move(Id<Insurance> id, 
			Reference<Address> address) implements Command {
	}

	public static record Update(Id<Insurance> id,
			String name) implements Command {
	}

	public static record Filter(COptional<Reference<Address>> address, 
			COptional<String> name) {
	}

	public static interface Operations {
		Insurance process(Command cmd) throws Exception;

		List<Insurance> getInsurance(Filter filter);

		Insurance getInsurance(Id<Insurance> id);
	}

	public Insurance updateWith(String newName) {
		return new Insurance(this.id, newName, this.address);
	}
	
	public Insurance updateWith(Reference<Address> newaddress) {
		return new Insurance(this.id, this.name, newaddress);
	}
	
	public String toString() {
		return String.format(
				"Insurance[id=%s, name=%s, address=%s]", 
				id, name, address.toString());
	}
}
