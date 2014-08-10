package eu.appbucket.rothar.core.service;

import org.springframework.stereotype.Service;

import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.core.persistence.exception.AssetDaoException;
import eu.appbucket.rothar.core.service.exception.ServiceException;

@Service
public interface UserService {
	
	/**
	 * Checks if user exists.
	 * 
	 * @param userToCheckForExistence to check for existence
	 * @return TRUE if user exists, FALSE if not
	 * 
	 * @throws ServiceException if there was general DB problem while checking the existence of user
	 */
	boolean isUserExisting(UserEntry userToCheckForExistence) throws ServiceException;
	
	
	boolean isUserOwnerOfAsset(int ownerId, int assetId) throws ServiceException;
	UserEntry findUser(int userId) throws ServiceException;
}
