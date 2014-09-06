package eu.appbucket.rothar.core.persistence;

import java.util.Collection;

import eu.appbucket.rothar.core.domain.user.RoleEntry;
import eu.appbucket.rothar.core.persistence.exception.RoleDaoException;

public interface RoleDao {
	
	/**
	 * Finds roles for the user.
	 * 
	 * @param userId ID of the user to find the roles
	 * @return found collection of roles or new (empty) collection or roles user if non was found
	 * 
	 * @throws RoleDaoException if there was general DB problem while finding the asset
	 */
	Collection<RoleEntry> findRolesByUserId(int userId) throws RoleDaoException;	
}
