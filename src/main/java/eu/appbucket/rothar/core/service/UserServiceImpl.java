package eu.appbucket.rothar.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.appbucket.rothar.core.domain.asset.AssetEntry;
import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.core.persistence.UserDao;
import eu.appbucket.rothar.core.persistence.exception.UserDaoException;
import eu.appbucket.rothar.core.service.exception.ServiceException;

@Service
public class UserServiceImpl implements UserService {
	
	private UserDao userDao;
	
	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public boolean isUserExisting(UserEntry userToCheckForExistence) throws ServiceException {
		boolean userExists = false;
		int userId = userToCheckForExistence.getUserId();
		try {
			userExists = userDao.isUserExisting(userId);
		} catch (UserDaoException userDaoException) {
			throw new ServiceException("Problem checking existence of the userser: " + userId, userDaoException);
		}	
		return userExists;
	}
	
	public UserEntry findUser(int userId) throws ServiceException {
		assertUserExist(userId);
		UserEntry foundUser = null;
		try {
			foundUser = userDao.findUserById(userId);
		} catch (UserDaoException userDaoException) {
			throw new ServiceException("Can't find user: " + userId, userDaoException);
		}
		if(foundUser.getUserId() == null) {
			throw new ServiceException("User: " + userId + " doesn't exists.");
		}
		return foundUser;
	}

	private void assertUserExist(Integer userId) throws ServiceException {
		UserEntry userToCheckForExistence = new UserEntry();
		userToCheckForExistence.setUserId(userId);
		boolean userExists = isUserExisting(userToCheckForExistence);
		if(!userExists) {
			throw new ServiceException("User: " + userId + " doesn't exists.");
		}
	}
}
