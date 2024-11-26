package de.ehealth.evek.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import de.ehealth.evek.type.Id;
import de.ehealth.evek.type.Reference;
import de.ehealth.evek.type.UserRole;

public record User(
		Id<User> id,
		String lastName,
		String firstName,
		Reference<Adress> adress,
		String userName,
		Reference<ServiceProvider> serviceProvider,
		UserRole role
		) implements Serializable {

	public static sealed interface Command permits Create, Update, Delete, UpdateRole {		
	}
	
	public static record Create(
			String lastName,
			String firstName,
			Reference<Adress> adress,
			String userName,
			Reference<ServiceProvider> serviceProvider,
			UserRole role) implements Command{	
	}
	
	public static record Delete(Id<User> id) implements Command{	
	}
	
	public static record Update(
			Id<User> id, 
			String lastName,
			String firstName,
			Reference<Adress> adress,
			String userName,
			Reference<ServiceProvider> serviceProvider
			) implements Command{
	}
	
	public static record UpdateRole(Id<User> id, UserRole role) implements Command{	
	}
	
	public static record Filter(Optional<String> lastName, Optional<String> firstName, 
			Optional<Reference<Adress>> adress, Optional<String> userName,
			Optional<Reference<ServiceProvider>> serviceProvider, Optional<UserRole> role) {
	}

	public static interface Operations {
		User process(Command cmd) throws Exception;

		List<User> getUser(Filter filter);

		User getUser(Id<User> id);
	}

	public User updateWith(
			String newLastName,
			String newFirstName,
			Reference<Adress> newAdress,
			String newUserName,
			Reference<ServiceProvider> newServiceProvider) {
		return new User(this.id, newLastName, newFirstName, newAdress, 
				newUserName, newServiceProvider, this.role);
	}
	
	
	
	public User updateWith(UserRole newRole) {
		return new User(this.id, this.lastName, this.firstName, this.adress, 
				this.userName, this.serviceProvider, newRole);
	}
	
	
	public String toString() {
		return String.format(
				"User[id=%s, lastName=%s, firstName=%s, "
				+ "adress=%s, userName=%s, serviceProvider=%s, "
				+ "role=%s]", 
				id,
				lastName,
				firstName,
				adress.toString(),
				userName,
				serviceProvider.toString(),
				role.toString());
	}
}
