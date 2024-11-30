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
		Reference<ServiceProvider> serviceProvider,
		UserRole role
		) implements Serializable {

	public static sealed interface Command extends Serializable permits Create, CreateFull, Update, Delete, UpdateRole, UpdateCredentials, LoginUser, Get, GetList {		
	}
	
	
	public static record Create(
			String userName,
			String password,
			String lastName,
			String firstName,
			Reference<Address> address,
			Reference<ServiceProvider> serviceProvider,
			UserRole role) implements Command{	
	}
	
	public static record CreateFull(
			String userName,
			String password,
			String lastName,
			String firstName,
			Address.Create addressCmd,
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
			Reference<ServiceProvider> serviceProvider
			) implements Command {
	}
	
	public static record UpdateRole(
			Id<User> id, 
			UserRole role) implements Command {	
	}
	public static record UpdateCredentials(
			Id<User> oldUserName, 
			String newUserName, 
			String oldPassword, 
			String newPassword) implements Command {
	}
	
	public static record LoginUser(
			String userName, 
			String password) implements Command {	
	}
	
	public static record Get(Id<User> id) implements Command {
	}
	public static record GetList(Filter filter) implements Command {
	}
	
	public static record Filter(COptional<String> lastName, COptional<String> firstName, 
			COptional<Reference<Address>> address,
			COptional<Reference<ServiceProvider>> serviceProvider, COptional<UserRole> role) {
	}

	public static interface Operations {
		User process(Command cmd, Reference<User> processingUser) throws Throwable;

		List<User> getUser(Filter filter);

		User getUser(Id<User> id);
	}

	public User updateWith(
			String newLastName,
			String newFirstName,
			Reference<Address> newAddress,
			Reference<ServiceProvider> newServiceProvider) {
		return new User(this.id, newLastName, newFirstName, newAddress, 
				newServiceProvider, this.role);
	}
	
	
	
	public User updateWith(UserRole newRole) {
		return new User(this.id, this.lastName, this.firstName, this.address, 
				this.serviceProvider, newRole);
	}
	
	public User updateCredentials(Id<User> oldUserName, Id<User> newUserName) {
		if(oldUserName != this.id)
			throw new IllegalArgumentException();
		return new User(newUserName, this.lastName, this.firstName, this.address, 
				this.serviceProvider, this.role);
	}
	
	public String toString() {
		return String.format(
				"User[id=%s, lastName=%s, firstName=%s, "
				+ "address=%s, serviceProvider=%s, "
				+ "role=%s]", 
				id,
				lastName,
				firstName,
				address.toString(),
				serviceProvider.toString(),
				role.toString());
	}
}
