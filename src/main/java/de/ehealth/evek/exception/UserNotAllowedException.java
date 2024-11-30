package de.ehealth.evek.exception;

import de.ehealth.evek.entity.User;
import de.ehealth.evek.type.Id;
import de.ehealth.evek.type.UserRole;

public class UserNotAllowedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8275336912813657500L;

	private final Id<User> user;
	private final UserRole userRole;
	private String message;
	
	public UserNotAllowedException(Id<User> user, UserRole userRole) {
		this(String.format("User %s as %s is not allowed to perform this Command!", 
				user.value().toString(), 
				userRole.toString()),
					user,
					userRole);
	}
	
	public UserNotAllowedException(String message, Id<User> user, UserRole userRole) {
		this.user = user;
		this.userRole = userRole;
		this.message = message;
	}

	public String message() {
			return message;
	}
	
	public Id<User> getProcessingUser(){
		return user;
	}
	
	public UserRole getProcessingUserRole() {
		return userRole;
	}
	
}
