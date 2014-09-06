package eu.appbucket.rothar.core.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import eu.appbucket.rothar.core.domain.user.RoleEntry;
import eu.appbucket.rothar.core.persistence.exception.RoleDaoException;

@Repository
public class RoleDaoImpl implements RoleDao {
	
	private JdbcTemplate jdbcTempalte;
	
	private static final String FIND_ROLES_BY_USER_ID_QUERY = 
			"SELECT roles.* "
			+ "FROM roles, user_roles "
			+ "WHERE user_roles.user_id = ? "
			+ "AND roles.role_id = user_roles.role_id;";
	
	@Autowired
	public void setJdbcTempalte(JdbcTemplate jdbcTempalte) {
		this.jdbcTempalte = jdbcTempalte;
	}
	public Collection<RoleEntry> findRolesByUserId(int userId)
			throws RoleDaoException {
		List<RoleEntry> roles = null;
		try {
			roles = jdbcTempalte.query(FIND_ROLES_BY_USER_ID_QUERY, new RoleEntryMapper(), userId);	
		} catch(EmptyResultDataAccessException emptyResultDataAccessException) {
			roles = new ArrayList<RoleEntry>();
		} catch (DataAccessException e) {
			throw new RoleDaoException("Can't find roles for user with id" + userId, e);
		}
		return roles;
	}

	private final static class RoleEntryMapper implements RowMapper<RoleEntry> {
		public RoleEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
			RoleEntry role = new RoleEntry();
			role.setName(rs.getString("name"));
			return role;
		}
	}
}
