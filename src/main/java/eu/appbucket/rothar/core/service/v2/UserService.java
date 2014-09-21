package eu.appbucket.rothar.core.service.v2;

import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.core.service.exception.ServiceException;

public interface UserService {
	
	/**
	 * Create active user new user.
	 * 
	 * By default user should be created as active and has user role, can fully access the system.
	 * 
	 * @return user with newly generated id
	 * 
	 * @throws ServiceException if there was general DB problem during creating the user
	 */
	UserEntry createUser() throws ServiceException;
	
	/**
	 * Removes user from DB along with related tables in which user is referenced
	 * 
	 * @param userToBeDeleted user which should be removed
	 */
	void deleteUser(UserEntry userToBeDeleted);
}
