package eu.appbucket.rothar.core.persistence;

import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.core.persistence.exception.AssetDaoException;
import eu.appbucket.rothar.core.persistence.exception.UserDaoException;
import eu.appbucket.rothar.core.service.exception.ServiceException;

public interface UserDao {
	
	/**
	 * Checks if user exists by id.
	 * 
	 * @param userId ID of the user to check for existence
	 * @return TRUE if user exists, FALSE if not
	 * 
	 * @throws UserDaoException if there was general DB problem while checking the existence of user
	 */
	boolean isUserExistingById(int userId) throws UserDaoException;
	
	/**
	 * Checks if user exists by email.
	 * 
	 * @param email address of the user to check for existence
	 * @return TRUE if user exists, FALSE if not
	 * 
	 * @throws UserDaoException if there was general DB problem while checking the existence of user
	 */
	boolean isUserExistingByEmail(String email) throws UserDaoException;
	
	/**
	 * Finds user.
	 * 
	 * @param userId ID of the user to be found
	 * @return found user or new (empty) user if non was found
	 * 
	 * @throws UserDaoException if there was general DB problem while finding the asset
	 */
	UserEntry findUserById(int userId) throws UserDaoException;
	
	/**
	 * Finds user.
	 * 
	 * @param email of the user to be found
	 * @return found user or new (empty) user if non was found
	 * 
	 * @throws UserDaoException if there was general DB problem while finding the asset
	 */
	UserEntry findUserByEmail(String email) throws UserDaoException;
	
	/**
	 * Creates new user.
	 * 
	 * @param userToBeCreated data of the user to be created
	 * 
	 * @return newly created user
	 * 
	 * @throws UserDaoException if there was general DB problem while persisting the new user
	 */
	UserEntry createNewUser(UserEntry userToBeCreated) throws UserDaoException;
	
	/**
	 * Generates access code for the user.
	 * 
	 * @param userToGenerateAccessCode user for which access code will be generated
	 * 
	 * @return user access code generated
	 */
	String setupUserActivationCode(int userId);
	
	/**
	 * Activate user.
	 * 
	 * @param userId id of the user to be activated
	 * 
	 * @throws UserDaoException if there was general DB problem with activating existing user
	 */
	void activateExistingUser(int userId) throws UserDaoException;
	
	/**
	 * Updates existing user.
	 * Only selected information can be updated.
	 * 
	 * @param userToBeUpdate data which needs to be updated in the user
	 * 
	 * @throws UserDaoException if there was general DB problem while updating the existing user
	 */
	void updateExistingUser(UserEntry userToBeUpdate) throws UserDaoException;
	
	/**
	 * Removes user from DB.
	 * 
	 * @param userIdToBeDeleted user id to be removed
	 * 
	 * @throws UserDaoException if there was general DB problem with removing existing user
	 */
	void removeExistingUser(int userIdToBeDeleted) throws UserDaoException;
	
	/**
	 * Retrieves last inserted id (for example user id)
	 * 
	 * This method must be run in this same transaction as the method creating user to guarantee that correct value is returned.
	 * 
	 * @return last inserted id
	 * 
	 * @throws UserDaoException if there was general DB problem with retrieving last inserted id
	 */
	int findLastInsertedId() throws UserDaoException;
}
