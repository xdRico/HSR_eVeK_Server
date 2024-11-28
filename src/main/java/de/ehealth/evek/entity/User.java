package de.ehealth.evek.entity;

import java.io.Serializable;
import java.util.List;

import de.ehealth.evek.type.Id;
import de.ehealth.evek.type.Reference;
import de.ehealth.evek.type.UserRole;
import de.ehealth.evek.util.COptional;

public record User(
		Id<User> id,
		String lastName,
		String firstName,
		Reference<Address> address,
		String userName,
		Reference<ServiceProvider> serviceProvider,
		UserRole role
		) implements Serializable {

	public static sealed interface Command extends Serializable permits Create, CreateFull, Update, Delete, UpdateRole {		
	}
	
	
	public static record Create(
			String lastName,
			String firstName,
			Reference<Address> address,
			String userName,
			Reference<ServiceProvider> serviceProvider,
			UserRole role) implements Command{	
	}
	
	public static record CreateFull(
			String lastName,
			String firstName,
			Address.Create addressCmd,
			String userName,
			ServiceProvider.CreateFull serviceProviderCmd,
			UserRole role) implements Command{	
	}
	
	public static record Delete(Id<User> id) implements Command{	
	}
	
	public static record Update(
			Id<User> id, 
			String lastName,
			String firstName,
			Reference<Address> address,
			String userName,
			Reference<ServiceProvider> serviceProvider
			) implements Command{
	}
	
	public static record UpdateRole(Id<User> id, UserRole role) implements Command{	
	}
	
	public static record Filter(COptional<String> lastName, COptional<String> firstName, 
			COptional<Reference<Address>> address, COptional<String> userName,
			COptional<Reference<ServiceProvider>> serviceProvider, COptional<UserRole> role) {
	}

	public static interface Operations {
		User process(Command cmd) throws Exception;

		List<User> getUser(Filter filter);

		User getUser(Id<User> id);
	}

	public User updateWith(
			String newLastName,
			String newFirstName,
			Reference<Address> newAddress,
			String newUserName,
			Reference<ServiceProvider> newServiceProvider) {
		return new User(this.id, newLastName, newFirstName, newAddress, 
				newUserName, newServiceProvider, this.role);
	}
	
	
	
	public User updateWith(UserRole newRole) {
		return new User(this.id, this.lastName, this.firstName, this.address, 
				this.userName, this.serviceProvider, newRole);
	}
	
	
	public String toString() {
		return String.format(
				"User[id=%s, lastName=%s, firstName=%s, "
				+ "address=%s, userName=%s, serviceProvider=%s, "
				+ "role=%s]", 
				id,
				lastName,
				firstName,
				address.toString(),
				userName,
				serviceProvider.toString(),
				role.toString());
	}
}
