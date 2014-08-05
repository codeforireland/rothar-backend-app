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
	
	public UserEntry findUser(int userId) {
		UserEntry user = null;
		try {
			userDao.findUserById(userId);
		} catch (UserDaoException e) {
			throw new ServiceException("Can't retrieve user: " + userId, e);
		}
		return user;
	}
}
