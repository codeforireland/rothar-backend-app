package eu.appbucket.rothar.core.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import eu.appbucket.rothar.core.domain.user.UserEntry;
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
	
	private static final String REMOVE_USER_QUERY = "DELETE FROM users "
			+ "where user_id = ?";
	
	private static final String LAST_INSERTER_ID_QUERY = "SELECT last_insert_id()";
	
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
			throw new UserDaoException("Can't check if user with id: " + userId + " exists.", dataAccessException);
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
			throw new UserDaoException("Can't check if user with email: " + email + " exists.", dataAccessException);
		}
		return false;
	}

	public UserEntry createNewUser(final UserEntry userToBeCreated)
			throws UserDaoException {
		final int isActivated = 0;
		final Date createdOn = new Date();
		KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			jdbcTempalte.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con)
						throws SQLException {
					PreparedStatement preparedStatement = con.prepareStatement(CREATE_USER_QUERY,
							new String[] { "user_id" });
					preparedStatement.setString(1, userToBeCreated.getEmail());
					preparedStatement.setString(2, userToBeCreated.getName());
					preparedStatement.setString(3, userToBeCreated.getPassword());
					preparedStatement.setInt(4, isActivated);
					preparedStatement.setTimestamp(5, new Timestamp(createdOn.getTime()));
					return preparedStatement;
				}
			}, keyHolder);
		} catch (DataAccessException dataAccessException) {
			throw new UserDaoException(
					"Can't create user with email : " + userToBeCreated.getEmail(), dataAccessException);
		}
		int userId = keyHolder.getKey().intValue();
		return findUserById(userId);
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
			throw new UserDaoException(
					"Can't store activation code for user : " + userId, dataAccessException);
		}
	}
	
	public void activateExistingUser(int userId) throws UserDaoException {
		try {
			jdbcTempalte.update(ACTIVATE_USER_QUERY,
				userId);
		} catch (DataAccessException dataAccessException) {
			throw new UserDaoException(
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
			throw new UserDaoException(
					"Can't update user with id: " + userToBeUpdate.getUserId(), dataAccessException);
		}
	}
	
	public void removeExistingUser(int userIdToBeRemoved) throws UserDaoException {
		try {
			jdbcTempalte.update(REMOVE_USER_QUERY,
					userIdToBeRemoved);
		} catch (DataAccessException dataAccessException) {
			throw new UserDaoException(
					"Can't remove user with id: " + userIdToBeRemoved, dataAccessException);
		}
	}
	
	public int findLastInsertedId() throws UserDaoException {
		int lastInsertedId = 0;
		try {
			lastInsertedId = jdbcTempalte.queryForInt(LAST_INSERTER_ID_QUERY);
		} catch (DataAccessException dataAccessException) {
			throw new UserDaoException(
					"Can't retrieved last inserted id", dataAccessException);
		}
		return lastInsertedId;
	}
	
}
