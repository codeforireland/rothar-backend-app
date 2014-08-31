package eu.appbucket.rothar.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.appbucket.rothar.core.domain.email.EmailService;
import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.core.persistence.UserDao;
import eu.appbucket.rothar.core.persistence.exception.UserDaoException;
import eu.appbucket.rothar.core.service.exception.ServiceException;

@Service
public class UserServiceImpl implements UserService {
	
	private UserDao userDao;
	private EmailService emailService;
	
	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	@Autowired
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}
	
	public boolean isUserExisting(UserEntry userToCheckForExistence) throws ServiceException {
		boolean userExists = false;
		int userId = userToCheckForExistence.getUserId();
		try {
			userExists = userDao.isUserExistingById(userId);
		} catch (UserDaoException userDaoException) {
			throw new ServiceException("Problem checking existence of the user with id: " + userId, userDaoException);
		}	
		return userExists;
	}
	
	public boolean isUserNotExisting(UserEntry userToCheckForNotExistence) throws ServiceException {
		boolean userDoesntExists = false;
		String email = userToCheckForNotExistence.getEmail();
		try {
			userDoesntExists = !userDao.isUserExistingByEmail(email);
		} catch (UserDaoException userDaoException) {
			throw new ServiceException("Problem checking existence of the user with eamil: " + email, userDaoException);
		}
		return userDoesntExists;
	}
	
	public UserEntry findUser(int userId) throws ServiceException {
		assertUserExistsById(userId);
		UserEntry foundUser = null;
		try {
			foundUser = userDao.findUserById(userId);
		} catch (UserDaoException userDaoException) {
			throw new ServiceException("Can't find user: " + userId, userDaoException);
		}
		return foundUser;
	}

	private void assertUserExistsById(Integer userId) throws ServiceException {
		UserEntry userToCheckForExistence = new UserEntry();
		userToCheckForExistence.setUserId(userId);
		boolean userExists = isUserExisting(userToCheckForExistence);
		if(!userExists) {
			throw new ServiceException("User: " + userId + " doesn't exists.");
		}
	}

	@Transactional
	public UserEntry createUser(UserEntry userToBeCreated)
			throws ServiceException {
		assertUserDoesntExistsByEmail(userToBeCreated.getEmail());
		UserEntry createdUser = createNewUser(userToBeCreated);
		emailService.sendUserActivationEmail(createdUser);
		return createdUser;
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
			activationCode = userDao.setupUserActivationCode(newUser.getUserId());
			newUser.setActivationCode(activationCode);
		} catch (UserDaoException userDaoException) {
			throw new ServiceException("Can't create user with email: " + userToBeCreated.getEmail(), userDaoException);
		}
		return newUser;
	}
	
	@Transactional
	public UserEntry activateUser(UserEntry userToBeActivated) throws ServiceException {
		assertUserExistsById(userToBeActivated.getUserId());
		assertActivationCodeMatch(userToBeActivated);
		activatExistingUser(userToBeActivated);
		UserEntry activatedUser = userToBeActivated;
		return activatedUser;
	}

	private void assertActivationCodeMatch(UserEntry userToCheckActivationCode) {
		UserEntry userWithActicationCode = userDao.findUserById(userToCheckActivationCode.getUserId());
		if(!userWithActicationCode.getActivationCode().equals(userToCheckActivationCode.getActivationCode())) {
			throw new ServiceException("Activation code doesn't match for user with id: " + userToCheckActivationCode.getUserId());
		}
	}
	
	private void activatExistingUser(UserEntry userToBeActivated) {
		try {
			userDao.activateExistingUser(userToBeActivated.getUserId());
		} catch (UserDaoException userDaoException) {
			throw new ServiceException("Can't activate user with id: " + userToBeActivated.getUserId(), userDaoException);
		}
	}
	
	@Transactional
	public void updateUser(UserEntry userToBeUpdated) throws ServiceException {
		assertUserIsActivated(userToBeUpdated.getUserId());
		updateExistingUser(userToBeUpdated);
	}
	
	private void assertUserIsActivated(Integer userIdToBeChecked) throws ServiceException {
		UserEntry user = findUser(userIdToBeChecked);
		if(!user.isActivated()) {
			throw new ServiceException("User with id: " + userIdToBeChecked + " is not activated.");
		}
	}
	
	private void updateExistingUser(UserEntry userToBeUpdated) {
		try {
			userDao.updateExistingUser(userToBeUpdated);
		} catch (UserDaoException userDaoException) {
			throw new ServiceException("Can't update user with id: " + userToBeUpdated.getUserId(), userDaoException);
		}
	}
}
