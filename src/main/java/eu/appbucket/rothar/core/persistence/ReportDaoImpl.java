package eu.appbucket.rothar.core.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import eu.appbucket.rothar.core.domain.report.ReportEntry;
import eu.appbucket.rothar.core.domain.report.ReportEntryFilter;

@Repository
public class ReportDaoImpl implements ReportDao {
	
	private JdbcTemplate jdbcTempalte;
	
	private final static String SQL_INSERT_REPORT_ENTRY = 
			"INSERT INTO reports(`asset_id`, `latitude`, `longitude`, `created`) "
			+ "VALUES (?, ?, ?, ?)";
	
	@Autowired
	public void setJdbcTempalte(JdbcTemplate jdbcTempalte) {
		this.jdbcTempalte = jdbcTempalte;
	}

	public void createNewEntry(ReportEntry entry) {
		jdbcTempalte.update(SQL_INSERT_REPORT_ENTRY, 
				entry.getAssetId(),
				entry.getLatitude(),
				entry.getLongitude(),
				entry.getCreated());
	}

	public List<ReportEntry> findEntries(ReportEntryFilter filter) {
		String query = new ReportEntrySelectQueryBuilder()
			.forAssetId(filter.getAssetId())
			.startingFrom(filter.getOffset())
			.withTotal(filter.getLimit())
			.sortBy(filter.getSort())
			.orderBy(filter.getOrder())
			.buildQuery();
		List<ReportEntry> reports = jdbcTempalte.query(query, new ReportMapper());
		return reports;
	} 
	
	private static final class ReportMapper implements RowMapper<ReportEntry> {
		public ReportEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
			ReportEntry report = new ReportEntry();
			report.setAssetId(rs.getString("asset_id"));
			report.setLongitude(rs.getDouble("longitude"));
			report.setLatitude(rs.getDouble("latitude"));
			report.setCreated(rs.getTimestamp("created"));
			return report;
		}
	}
	
	private static final class ReportEntrySelectQueryBuilder {
		
		private String assetId;
		private int offset;
		private int limit;
		private String order;
		private String sort;
		
		private final static String SQL_SELECT_REPORT_ENTRIES =
				"SELECT * FROM reports "
				+ "WHERE asset_id = '%s' "
				+ "ORDER BY %s %s "
				+ "LIMIT %d "
				+ "OFFSET %d";
		
		public ReportEntrySelectQueryBuilder forAssetId(String assetId) {
			this.assetId = assetId;
			return this;
		}
		
		public ReportEntrySelectQueryBuilder sortBy(String sort) {
			this.sort = sort;
			return this;
		}
		
		public ReportEntrySelectQueryBuilder orderBy(String order) {
			this.order = order;
			return this;
		}
		
		public ReportEntrySelectQueryBuilder startingFrom(int offset) {
			this.offset= offset;
			return this;
		}
		
		public ReportEntrySelectQueryBuilder withTotal(int limit) {
			this.limit = limit;
			return this;
		}
		
		public String buildQuery() {
			return String.format(
					SQL_SELECT_REPORT_ENTRIES, 
					this.assetId,
					this.sort, this.order,
					this.limit, this.offset);
		}
	}
}
