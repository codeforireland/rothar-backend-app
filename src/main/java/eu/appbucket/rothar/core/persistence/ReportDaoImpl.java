package eu.appbucket.rothar.core.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import eu.appbucket.rothar.core.domain.report.ReportEntry;
import eu.appbucket.rothar.core.domain.report.ReportListFilter;
import eu.appbucket.rothar.core.persistence.exception.ReportDaoException;

@Repository
public class ReportDaoImpl implements ReportDao {
	
	private JdbcTemplate jdbcTempalte;
	
	private final static String SQL_INSERT_REPORT_ENTRY = 
			"INSERT INTO reports(`asset_id`, `reporter_id`, `latitude`, `longitude`, `created`, `reporter_uuid`) "
			+ "VALUES (?, ?, ?, ?, ?, ?)";
	
	private final static String SQL_SELECT_REPORT_ENTRIES =
			"SELECT * FROM reports, assets "
			+ "WHERE "
			+ "reports.asset_id = ? "
			+ "AND assets.user_id = ? "
			+ "AND assets.asset_id = reports.asset_id "
			+ "ORDER BY reports.%s %s "
			+ "LIMIT ? "
			+ "OFFSET ?";
	
	private final static String SQL_SELECT_REPORT_ENTRIES_WITH_DATE_BOUNDARIES =
			"SELECT * FROM reports, assets "
			+ "WHERE "
			+ "reports.asset_id = ? "
			+ "AND assets.user_id = ? "
			+ "AND reports.created >= ? "
			+ "AND reports.created <= ? "
			+ "AND assets.asset_id = reports.asset_id "
			+ "ORDER BY reports.%s %s "
			+ "LIMIT ? "
			+ "OFFSET ?";
	
	@Autowired
	public void setJdbcTempalte(JdbcTemplate jdbcTempalte) {
		this.jdbcTempalte = jdbcTempalte;
	}

	public void createNewEntry(ReportEntry entry) {
		jdbcTempalte.update(SQL_INSERT_REPORT_ENTRY, 
				entry.getAssetId(),
				entry.getReporterId(),
				entry.getLatitude(),
				entry.getLongitude(),
				entry.getCreated(),
				entry.getReporterUuid());
	}

	public List<ReportEntry> findEntriesByFilter(ReportListFilter filter) {
		String query = String.format(SQL_SELECT_REPORT_ENTRIES, filter.getSort(), filter.getOrder());
		List<ReportEntry> reports = jdbcTempalte.query(
				query, 
				new Object[]{filter.getAssetId(), filter.getUserId(), filter.getLimit(), filter.getOffset()},
				new ReportMapper());
 		return reports;
	} 
	
	private static final class ReportMapper implements RowMapper<ReportEntry> {
		public ReportEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
			ReportEntry report = new ReportEntry();
			report.setAssetId(rs.getInt("asset_id"));
			report.setLongitude(rs.getDouble("longitude"));
			report.setLatitude(rs.getDouble("latitude"));
			report.setCreated(rs.getTimestamp("created"));
			return report;
		}
	}
	
	public List<ReportEntry> findEntriesByFilterAndDate(ReportListFilter filter, Date from, Date to)
			throws ReportDaoException {
		String query = String.format(SQL_SELECT_REPORT_ENTRIES_WITH_DATE_BOUNDARIES, filter.getSort(), filter.getOrder());
		List<ReportEntry> reports = jdbcTempalte.query(
				query, 
				new Object[]{
						filter.getAssetId(), 
						filter.getUserId(), 
						from, to,
						filter.getLimit(), filter.getOffset(), 
						},
				new ReportMapper());
 		return reports;
	}
}
