package eu.appbucket.rothar.core.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.core.persistence.exception.AssetDaoException;
import eu.appbucket.rothar.core.persistence.exception.UserDaoException;

@Repository
public class UserDaoImpl implements UserDao {
	
	private JdbcTemplate jdbcTempalte;
	
	private static final String FIND_USER_BY_ID_QUERY = "SELECT * FROM users where user_id = ?";
	
	private static final String FIND_USER_BY_EMAIL_QUERY = "SELECT * FROM users where email = ?";
	
	private static final String IS_USER_EXISTING_BY_ID_QUERY = "SELECT count(*) from users "
			+ " WHERE user_id = ?";
	
	private static final String IS_USER_EXISTING_BY_EMAIL_QUERY = "SELECT count(*) from users "
			+ " WHERE email like ?";
	
	private static final String CREATE_USER_QUERY = "INSERT INTO users "
			+ "(email, name, password, activated, created) "
			+ "values (?, ?, ?, ?, ?) ";
	
	private static final String STORE_USER_ACTIVATION_CODE_QUERY = "UPDATE users "
			+ "set activation_code = ? "
			+ "where user_id = ?";
	
	private static final String ACTIVATE_USER_QUERY = "UPDATE users "
			+ "set activated = 1 "
			+ "where user_id = ?";
	
	private static final String UPDATE_USER_QUERY = "UPDATE users "
			+ "set name = ?, email = ? "
			+ "where user_id = ?";
	
	@Autowired
	public void setJdbcTempalte(JdbcTemplate jdbcTempalte) {
		this.jdbcTempalte = jdbcTempalte;
	}
	
	public UserEntry findUserById(int userId) throws UserDaoException {
		UserEntry user = null;
		try {
			user = jdbcTempalte.queryForObject(FIND_USER_BY_ID_QUERY, new UserEntryMapper(), userId);	
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
			user.setActivated(rs.getInt("activated") == 1 ? true : false);
			user.setActivationCode(rs.getString("activation_code"));
			user.setPassword(rs.getString("password"));
			return user;
		}
	}

	public UserEntry findUserByEmail(String email) throws UserDaoException {
		UserEntry user = null;
		try {
			user = jdbcTempalte.queryForObject(FIND_USER_BY_EMAIL_QUERY, new UserEntryMapper(), email);	
		} catch(EmptyResultDataAccessException emptyResultDataAccessException) {
			user = new UserEntry();
		} catch (DataAccessException dataAccessException) {
			throw new UserDaoException("Can't find user by email: " + email, dataAccessException);
		}
		return user;
	}
	
	public boolean isUserExistingById(int userId) throws UserDaoException {
		try {
			int count = jdbcTempalte.queryForInt(IS_USER_EXISTING_BY_ID_QUERY, userId);
			if(count > 0) {
				return true;
			}
		} catch(EmptyResultDataAccessException emptyResultDataAccessException) {
			return false;
		} catch (DataAccessException dataAccessException) {
			throw new AssetDaoException("Can't check if user with id: " + userId + " exists.", dataAccessException);
		}
		return false;
	}

	public boolean isUserExistingByEmail(String email) throws UserDaoException {
		try {
			int count = jdbcTempalte.queryForInt(IS_USER_EXISTING_BY_EMAIL_QUERY, email);
			if(count > 0) {
				return true;
			}
		} catch(EmptyResultDataAccessException emptyResultDataAccessException) {
			return false;
		} catch (DataAccessException dataAccessException) {
			throw new AssetDaoException("Can't check if user with email: " + email + " exists.", dataAccessException);
		}
		return false;
	}

	public UserEntry createNewUser(UserEntry userToBeCreated)
			throws UserDaoException {
		int isActivated = 0;
		Date createdOn = new Date();
		try {
			jdbcTempalte.update(CREATE_USER_QUERY,
				userToBeCreated.getEmail(),
				userToBeCreated.getName(),
				userToBeCreated.getPassword(),
				isActivated,
				createdOn);
		} catch (DataAccessException dataAccessException) {
			throw new AssetDaoException(
					"Can't create user with email : " + userToBeCreated.getEmail(), dataAccessException);
		}		
		return findUserByEmail(userToBeCreated.getEmail());
	}

	public String setupUserActivationCode(int userId) {
		String activationCode = generateActivationCode();
		storeUserActivationCodeForUser(activationCode, userId);
		return activationCode;
	}
	
	private String generateActivationCode() {
		return UUID.randomUUID().toString();
	}
	
	private void storeUserActivationCodeForUser(String activationCode, int userId){
		try {
			jdbcTempalte.update(STORE_USER_ACTIVATION_CODE_QUERY,
				activationCode,
				userId);
		} catch (DataAccessException dataAccessException) {
			throw new AssetDaoException(
					"Can't store activation code for user : " + userId, dataAccessException);
		}
	}
	
	public void activateExistingUser(int userId) throws UserDaoException {
		try {
			jdbcTempalte.update(ACTIVATE_USER_QUERY,
				userId);
		} catch (DataAccessException dataAccessException) {
			throw new AssetDaoException(
					"Can't activate user with id: " + userId, dataAccessException);
		}
	}

	public void updateExistingUser(UserEntry userToBeUpdate) throws UserDaoException {
		try {
			jdbcTempalte.update(UPDATE_USER_QUERY,
					userToBeUpdate.getName(),
					userToBeUpdate.getEmail(),
					userToBeUpdate.getUserId());
		} catch (DataAccessException dataAccessException) {
			throw new AssetDaoException(
					"Can't update user with id: " + userToBeUpdate.getUserId(), dataAccessException);
		}
	}
}
