package eu.appbucket.rothar.core.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.core.persistence.exception.AssetDaoException;
import eu.appbucket.rothar.core.persistence.exception.UserDaoException;
import eu.appbucket.rothar.core.service.exception.ServiceException;

@Repository
public class UserDaoImpl implements UserDao {
	
	private static final String FIND_USER_QUERY = "SELECT * FROM users where user_id = ?";
	
	public static final String IS_USER_EXISTING_QUERY = "SELECT count(*) from users "
			+ " WHERE user_id = ?";
	
	private JdbcTemplate jdbcTempalte;
	
	@Autowired
	public void setJdbcTempalte(JdbcTemplate jdbcTempalte) {
		this.jdbcTempalte = jdbcTempalte;
	}
	
	public UserEntry findUserById(int userId) throws UserDaoException {
		UserEntry user = null;
		try {
			user = jdbcTempalte.queryForObject(FIND_USER_QUERY, new UserEntryMapper(), userId);	
		} catch(EmptyResultDataAccessException emptyResultDataAccessException) {
			user = new UserEntry();
		} catch (DataAccessException e) {
			throw new UserDaoException("Can't find user" + userId, e);
		}
		return user;
	}
	
	private final static class UserEntryMapper implements RowMapper<UserEntry> {
		public UserEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserEntry user = new UserEntry();
			user.setUserId(rs.getInt("user_id"));
			user.setName(rs.getString("name"));
			user.setEmail(rs.getString("email"));
			user.setCreated(rs.getTimestamp("created"));
			return user;
		}
	}

	public boolean isUserExisting(int userId) throws UserDaoException {
		try {
			int count = jdbcTempalte.queryForInt(IS_USER_EXISTING_QUERY, userId);
			if(count > 0) {
				return true;
			}
		} catch(EmptyResultDataAccessException emptyResultDataAccessException) {
			return false;
		} catch (DataAccessException dataAccessException) {
			throw new AssetDaoException("Can't check if user: " + userId + " exists.", dataAccessException);
		}
		return false;
	}

	public boolean isUserExisting(String email) throws UserDaoException {
		// TODO Auto-generated method stub
		return false;
	}

	public UserEntry createNewUser(UserEntry userToBeCreated)
			throws UserDaoException {
		// TODO Auto-generated method stub
		return null;
	}

	public String generateUserActicationCode(int userId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void activateExistingUser(int userId) throws UserDaoException {
		// TODO Auto-generated method stub
		
	}

	public String generateUserPassword(int userId) throws UserDaoException {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateExistingUser(UserEntry userToBeUpdate)
			throws UserDaoException {
		// TODO Auto-generated method stub
		
	}
}
