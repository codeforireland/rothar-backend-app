package eu.appbucket.rothar.core.persistence;

import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.core.persistence.exception.UserDaoException;

public interface UserDao {
	
	UserEntry findUserById(int userId) throws UserDaoException;
}
