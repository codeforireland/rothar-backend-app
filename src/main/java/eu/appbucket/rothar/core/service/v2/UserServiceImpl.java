package eu.appbucket.rothar.core.service.v2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.core.persistence.RoleDao;
import eu.appbucket.rothar.core.persistence.UserDao;
import eu.appbucket.rothar.core.persistence.exception.UserDaoException;
import eu.appbucket.rothar.core.service.exception.ServiceException;

@Service(value="v2.userService")
public class UserServiceImpl implements UserService {
	
	private eu.appbucket.rothar.core.service.v1.UserService userService1;
	private UserDao userDao;
	
	@Autowired
	@Qualifier("v1.userService")
	public void setUserService(
			eu.appbucket.rothar.core.service.v1.UserService userService1) {
		this.userService1 = userService1;
	}
	
	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	@Transactional
	public UserEntry createUser() throws ServiceException {
		UserEntry createdUser = createNewUser();
		return createdUser;
	}

	private UserEntry createNewUser() {
		UserEntry newUser = new UserEntry();
		UserEntry emptyUser = new UserEntry();
		try {
			newUser = userDao.createNewUser(emptyUser);
		} catch (UserDaoException userDaoException) {
			throw new ServiceException("Can't create user", userDaoException);
		}
		return newUser;
	}
	
	public void deleteUser(UserEntry userToBeDeleted) {
		userService1.deleteUser(userToBeDeleted);
	}
}
