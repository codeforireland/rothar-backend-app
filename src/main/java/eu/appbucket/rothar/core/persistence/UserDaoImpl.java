package eu.appbucket.rothar.core.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.core.persistence.exception.AssetDaoException;
import eu.appbucket.rothar.core.persistence.exception.UserDaoException;

@Repository
public class UserDaoImpl implements UserDao {
	
	private static final String FIND_USER_QUERY = "SELECT * FROM users where user_id = ?";
	
	private JdbcTemplate jdbcTempalte;
	
	@Autowired
	public void setJdbcTempalte(JdbcTemplate jdbcTempalte) {
		this.jdbcTempalte = jdbcTempalte;
	}
	
	public UserEntry findUserById(int userId) throws UserDaoException {
		UserEntry user = null;
		try {
			user = jdbcTempalte.queryForObject(FIND_USER_QUERY, new UserEntryMapper(), userId);	
		} catch (DataAccessException e) {
			throw new UserDaoException("Can't find user" + userId, e);
		}
		if(user == null) {
			// throw new UserDaoException("User: " + userId + " doesn't exists.");
			user = new UserEntry();
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
}
