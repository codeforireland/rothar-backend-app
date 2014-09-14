package eu.appbucket.rothar.core.service;

import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.core.service.exception.ServiceException;

public interface UserService {
	
	/**
	 * Checks if user exists.
	 * 
	 * @param userToCheckForExistence to check for existence
	 * @return TRUE if user exists, FALSE if not
	 * 
	 * @throws ServiceException if there was general DB problem while checking the existence of user
	 */
	boolean isUserExistingById(UserEntry userToCheckForExistence) throws ServiceException;
	
	/**
	 * Finds user.
	 * 
	 * @param userId ID of the user to be found
	 * @return found user
	 * 
	 * @throws ServiceException if:
	 * - user doesn't exists
	 * or
	 * - there was general DB problem during finding the user
	 */
	UserEntry findUserById(int userId) throws ServiceException;
	
	/**
	 * Finds user.
	 * 
	 * @param email of the user to be found
	 * @return found user
	 * 
	 * @throws ServiceException if:
	 * - user doesn't exists
	 * or
	 * - there was general DB problem during finding the user
	 */
	UserEntry findUserByEmail(String email) throws ServiceException;
	
	/**
	 * Create new user.
	 * 
	 * By default user should be created as inactive - is not allowed to login to the system
	 * 
	 * @param userToBeCreated basic information about the user to be created (name, email, ...)
	 * @return user with newly generated activation code
	 * 
	 * @throws ServiceException if:
	 * - user with this same email address already exists
	 * or
	 * - there was general DB problem during creating the user
	 */
	UserEntry createUser(UserEntry userToBeCreated) throws ServiceException;
	
	/**
	 * Activates the existing user.
	 * 
	 * The activation is validated by provided activation code.
	 * From that moment user is becoming active - can login to the system.
	 * 
	 * @param userToBeActivated information about the user which should be activate
	 * @return user with the newly generated password
	 * 
	 * @throws ServiceException if:
	 * - user doesn't exist
	 * or
	 * - provided activation code doesn't match the user email address
	 * or
	 * - there was general DB problem during activating the user
	 */
	UserEntry activateUser(UserEntry userToBeActivated) throws ServiceException;
	
	/**
	 * Updates the existing user.
	 * 
	 * Only certain information can be update like email address, name etc., other changes will be ignored.
	 * 
	 * @param userToBeUpdated details of the user to be updated
	 * 
	 * @throws ServiceException if:
	 * - user doesn't exists
	 * or
	 * - there was general DB problem during updating the user
	 */
	void updateUser(UserEntry userToBeUpdated) throws ServiceException;
}
