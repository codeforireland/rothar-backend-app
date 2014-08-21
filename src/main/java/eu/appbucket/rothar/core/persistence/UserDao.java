package eu.appbucket.rothar.core.persistence;

import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.core.persistence.exception.AssetDaoException;
import eu.appbucket.rothar.core.persistence.exception.UserDaoException;
import eu.appbucket.rothar.core.service.exception.ServiceException;

public interface UserDao {
	/**
	 * Checks if user exists.
	 * 
	 * @param userId ID of the user to check for existence
	 * @return TRUE if user exists, FALSE if not
	 * 
	 * @throws ServiceException if there was general DB problem while checking the existence of user
	 */
	boolean isUserExisting(int userId) throws UserDaoException;
	
	/**
	 * Finds user.
	 * 
	 * @param userId ID of the user to be found
	 * @return found user or new (empty) user if non was found
	 * 
	 * @throws AssetDaoException if there was general DB problem while finding the asset
	 */
	UserEntry findUserById(int userId) throws UserDaoException;
}
