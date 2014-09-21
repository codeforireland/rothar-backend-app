package eu.appbucket.rothar.core.service.v1;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.appbucket.rothar.core.domain.user.RoleEntry;
import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.core.persistence.RoleDao;
import eu.appbucket.rothar.core.persistence.UserDao;
import eu.appbucket.rothar.core.persistence.exception.RoleDaoException;
import eu.appbucket.rothar.core.persistence.exception.UserDaoException;
import eu.appbucket.rothar.core.service.EmailService;
import eu.appbucket.rothar.core.service.exception.ServiceException;

@Service(value="v1.userService")
public class UserServiceImpl implements UserService {
	
	private UserDao userDao;
	private RoleDao roleDao;
	private EmailService emailService;
	
	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	@Autowired
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}
	
	@Autowired
	public void setRoleDao(RoleDao roleDao) {
		this.roleDao = roleDao;
	}
	
	public boolean isUserExistingById(UserEntry userToCheckForExistence) throws ServiceException {
		boolean userExists = false;
		int userId = userToCheckForExistence.getUserId();
		try {
			userExists = userDao.isUserExistingById(userId);
		} catch (UserDaoException userDaoException) {
			throw new ServiceException("Problem checking existence of the user with id: " + userId, userDaoException);
		}	
		return userExists;
	}
	
	public boolean isUserExistingByEmail(UserEntry userToCheckForExistence) throws ServiceException {
		boolean userExists = false;
		String email = userToCheckForExistence.getEmail();
		try {
			userExists = userDao.isUserExistingByEmail(email);
		} catch (UserDaoException userDaoException) {
			throw new ServiceException("Problem checking existence of the user with email: " + email, userDaoException);
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
	
	public UserEntry findUserById(int userId) throws ServiceException {
		assertUserExistsById(userId);
		UserEntry foundUser = findUserDataById(userId);
		foundUser.addRoles(findRolesForUser(foundUser.getUserId()));
		return foundUser;
	}

	private UserEntry findUserDataById(int userId) throws ServiceException {
		UserEntry foundUser = null;
		try {
			foundUser = userDao.findUserById(userId);
		} catch (UserDaoException userDaoException) {
			throw new ServiceException("Can't find user with id: " + userId, userDaoException);
		}
		return foundUser;
	}
	
	private Collection<RoleEntry> findRolesForUser(int userId) throws ServiceException {
		Collection<RoleEntry> roles = null;
		try {
			roles = roleDao.findRolesByUserId(userId);
		} catch (RoleDaoException roleDaoException) {
			throw new ServiceException("Can't find roles for user with id: " + userId, roleDaoException);
		}
		return roles;
	}
	
	public UserEntry findUserByEmail(String email) throws ServiceException {
		assertUserExistsByEmail(email);
		UserEntry foundUser = findUserDataByEmail(email);
		foundUser.addRoles(findRolesForUser(foundUser.getUserId()));
		return foundUser;
	}
	
	private UserEntry findUserDataByEmail(String email) throws ServiceException {
		UserEntry foundUser = null;
		try {
			foundUser = userDao.findUserByEmail(email);
		} catch (UserDaoException userDaoException) {
			throw new ServiceException("Can't find user with email: " + email, userDaoException);
		}
		return foundUser;
	}
	private void assertUserExistsById(Integer userId) throws ServiceException {
		UserEntry userToCheckForExistence = new UserEntry();
		userToCheckForExistence.setUserId(userId);
		boolean userExists = isUserExistingById(userToCheckForExistence);
		if(!userExists) {
			throw new ServiceException("User with id: " + userId + " doesn't exists.");
		}
	}

	private void assertUserExistsByEmail(String email) throws ServiceException {
		UserEntry userToCheckForExistence = new UserEntry();
		userToCheckForExistence.setEmail(email);
		boolean userExists = isUserExistingByEmail(userToCheckForExistence);
		if(!userExists) {
			throw new ServiceException("User with email: " + email + " doesn't exists.");
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
		assertUserExistsById(userToBeUpdated.getUserId());
		assertUserIsActivated(userToBeUpdated.getUserId());
		assertRequiredInformationIsProvided(userToBeUpdated);
		updateExistingUser(userToBeUpdated);
	}
	
	private void assertUserIsActivated(Integer userIdToBeChecked) throws ServiceException {
		UserEntry user = findUserDataById(userIdToBeChecked);
		if(!user.isActivated()) {
			throw new ServiceException("User with id: " + userIdToBeChecked + " is not activated.");
		}
	}
	
	/**
	 * @param userToBeUpdated
	 * @throws ServiceException
	 */
	private void assertRequiredInformationIsProvided(UserEntry userToBeUpdated) throws ServiceException {
		if(StringUtils.isEmpty(userToBeUpdated.getEmail())) {
			throw new ServiceException("Email address is empty for user with id: " + userToBeUpdated.getUserId());
		}
		if(StringUtils.isEmpty(userToBeUpdated.getName())) {
			throw new ServiceException("Name is empty for user with id: " + userToBeUpdated.getUserId());
		}
	}
	
	private void updateExistingUser(UserEntry userToBeUpdated) {
		try {
			userDao.updateExistingUser(userToBeUpdated);
		} catch (UserDaoException userDaoException) {
			throw new ServiceException("Can't update user with id: " + userToBeUpdated.getUserId(), userDaoException);
		}
	}
	
	@Transactional
	public void deleteUser(UserEntry userToBeDeleted) {
		try {
			userDao.removeExistingUser(userToBeDeleted.getUserId());
		} catch (UserDaoException userDaoException) {
			throw new ServiceException("Can't delete user with id: " + userToBeDeleted.getUserId(), userDaoException);
		}
	}
}
