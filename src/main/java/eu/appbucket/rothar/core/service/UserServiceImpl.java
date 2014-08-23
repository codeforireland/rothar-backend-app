package eu.appbucket.rothar.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
			throw new ServiceException("Problem checking existence of the user with id: " + userId, userDaoException);
		}	
		return userExists;
	}
	
	public boolean isUserNotExisting(UserEntry userToCheckForNotExistence) throws ServiceException {
		boolean userDoesntExists = false;
		String email = userToCheckForNotExistence.getEmail();
		try {
			userDoesntExists = !userDao.isUserExisting(email);
		} catch (UserDaoException userDaoException) {
			throw new ServiceException("Problem checking existence of the user with eamil: " + email, userDaoException);
		}
		return userDoesntExists;
	}
	
	public UserEntry findUser(int userId) throws ServiceException {
		assertUserExistById(userId);
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

	private void assertUserExistById(Integer userId) throws ServiceException {
		UserEntry userToCheckForExistence = new UserEntry();
		userToCheckForExistence.setUserId(userId);
		boolean userExists = isUserExisting(userToCheckForExistence);
		if(!userExists) {
			throw new ServiceException("User: " + userId + " doesn't exists.");
		}
	}

	public UserEntry createUser(UserEntry userToBeCreated)
			throws ServiceException {
		assertUserDoesntExistsByEmail(userToBeCreated.getEmail());
		return createNewUser(userToBeCreated);
	}

	private void assertUserDoesntExistsByEmail(String email) {
		UserEntry userToCheckForNotExistence = new UserEntry();
		userToCheckForNotExistence.setEmail(email);
		boolean userDoesntExists = isUserNotExisting(userToCheckForNotExistence);
		if(!userDoesntExists) {
			throw new ServiceException("User with email: " + email + " already exists.");
		}
	}

	private UserEntry createNewUser(UserEntry userToBeCreated) {
		UserEntry newUser = new UserEntry();
		String activationCode = new String();
		try {
			newUser = userDao.createNewUser(userToBeCreated);
			activationCode = userDao.generateUserActicationCode(newUser.getUserId());
			newUser.setActivationCode(activationCode);
		} catch (UserDaoException userDaoException) {
			throw new ServiceException("Can't create user with email: " + userToBeCreated.getEmail(), userDaoException);
		}
		return newUser;
	}
	
	public UserEntry activateUser(UserEntry userToBeActivated) throws ServiceException {
		assertUserExistById(userToBeActivated.getUserId());
		assertActivationCodeMatch(userToBeActivated);
		activatExistingUser(userToBeActivated);
		String password = generatePasswordForUser(userToBeActivated);
		UserEntry activatedUser = userToBeActivated;
		activatedUser.setPassword(password);
		return activatedUser;
	}

	private void assertActivationCodeMatch(UserEntry userToCheckActivationCode) {
		UserEntry userWithActicationCode = userDao.findUserById(userToCheckActivationCode.getUserId());
		if(!userWithActicationCode.getActivationCode().equals(userToCheckActivationCode.getActivationCode())) {
			throw new ServiceException("Activation code doesn't match for user: " + userToCheckActivationCode.getUserId());
		}
	}
	
	private void activatExistingUser(UserEntry userToBeActivated) {
		try {
			userDao.activateExistingUser(userToBeActivated.getUserId());
		} catch (UserDaoException userDaoException) {
			throw new ServiceException("Can't activate user with id: " + userToBeActivated.getUserId(), userDaoException);
		}
	}
	
	private String generatePasswordForUser(UserEntry userToGeneratePassword) {
		try {
			return userDao.generateUserPassword(userToGeneratePassword.getUserId());
		} catch (UserDaoException userDaoException) {
			throw new ServiceException("Can't generate password for user with id: " + userToGeneratePassword.getUserId(), userDaoException);
		}
	}
	
	public void updateUser(UserEntry userToBeUpdated) throws ServiceException {
		assertUserExistById(userToBeUpdated.getUserId());
		updateExistingUser(userToBeUpdated);
	}
	
	private void updateExistingUser(UserEntry userToBeUpdated) {
		try {
			userDao.updateExistingUser(userToBeUpdated);
		} catch (UserDaoException userDaoException) {
			throw new ServiceException("Can't update user with id: " + userToBeUpdated.getUserId(), userDaoException);
		}
	}
}
