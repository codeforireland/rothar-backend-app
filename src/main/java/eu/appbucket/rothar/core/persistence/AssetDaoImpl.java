package eu.appbucket.rothar.core.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import eu.appbucket.rothar.core.domain.asset.AssetEntry;
import eu.appbucket.rothar.core.domain.asset.AssetFilter;
import eu.appbucket.rothar.core.persistence.exception.AssetDaoException;

@Repository
public class AssetDaoImpl implements AssetDao {

	private JdbcTemplate jdbcTempalte;
	
	public static final String CREATE_ASSET_QUERY = "INSERT INTO assets "
			+ "(asset_id, user_id, status_id, description, created)";
	
	public static final String UPDATE_ASSET_QUERY = "UPDATE assets "
			+ " SET status_id = ?, description = ?)"
			+ " WHERE asset_id = ?";
	
	public static final String FIND_SINGLE_ASSET_QUERY = "SELECT * from assets "
			+ " WHERE user_id = ?, asset_id = ?";
	
	public static final String FIND_MULTIPLE_ASSETS_QUERY = "SELECT * from assets "
			+ " WHERE user_id = %s"
			+ " ORDER BY %s %s "
			+ " LIMIT %d, %d";
	
	@Autowired
	public void setJdbcTempalte(JdbcTemplate jdbcTempalte) {
		this.jdbcTempalte = jdbcTempalte;
	}
	
	public void createNewAsset(AssetEntry asset) throws AssetDaoException {
		int numberOfCreatedAssets = 0;
		try {
			numberOfCreatedAssets = 
				jdbcTempalte.update(CREATE_ASSET_QUERY, 
					asset.getAssetId(),
					asset.getUserId(),
					asset.getStatusId(),
					asset.getDescription(),
					new Date());	
		} catch (DataAccessException e) {
			throw new AssetDaoException("Can't create asset for user: " + asset.getUserId(), e);
		}
		if(numberOfCreatedAssets != 1) {
			throw new AssetDaoException(
					"Incorrect number of created assets: " + numberOfCreatedAssets + " while expected 0"
					+ " for user: " + asset.getUserId());
		}
	}

	public void updateExistingAsset(AssetEntry asset) throws AssetDaoException {
		int numberOfUpdatedAssets = 0;
		try {
			numberOfUpdatedAssets = jdbcTempalte.update(UPDATE_ASSET_QUERY,
					asset.getStatusId(),
					asset.getDescription(),
					asset.getAssetId());	
		} catch (DataAccessException e) {
			throw new AssetDaoException("Can't update asset " + asset.getAssetId(), e);
		}
		if(numberOfUpdatedAssets != 1) {
			throw new AssetDaoException(
					"Incorrect number of updates assets: " + numberOfUpdatedAssets + " while expected 0"
					+ " for asset: " + asset.getAssetId());
		}		
	}

	public AssetEntry findAssetByUserAndAssetId(Integer userId, Integer assetId) {
		AssetEntry asset = null;
		try {
			asset = jdbcTempalte.queryForObject(FIND_SINGLE_ASSET_QUERY, 
					new AssetEntryMapper(), userId, assetId);	
		} catch (DataAccessException e) {
			throw new AssetDaoException("Can't find asset " + assetId + " for user: " + userId, e);
		}
		if(asset == null) {
			asset = new AssetEntry();
			// throw new AssetDaoException("Asset: " + assetId + " for user: " + userId + " doesn't exists.");
		}
		return asset;
	}
	
	private final static class AssetEntryMapper implements RowMapper<AssetEntry> {
		public AssetEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
			AssetEntry asset = new AssetEntry();
			asset.setUserId(rs.getInt("user_id"));
			asset.setAssetId(rs.getInt("asset_id"));
			asset.setCreated(rs.getTimestamp("created"));
			asset.setDescription(rs.getString("description"));
			asset.setStatusId(rs.getInt(rs.getInt("status_id")));
			asset.setMajor(rs.getInt("major"));
			asset.setMinor(rs.getInt("minor"));
			asset.setUserId(rs.getInt("user_id"));
			asset.setUuid(rs.getString("uuid"));
			return asset;
		}
	}
	
	public List<AssetEntry> findAssets(AssetFilter filter)
			throws AssetDaoException {
		String query = String.format(FIND_MULTIPLE_ASSETS_QUERY, 
				filter.getUserId(),
				filter.getSort(), filter.getOrder(),
				filter.getOffset(), filter.getLimit());
		List<AssetEntry> assets = null;
		try {
			assets = jdbcTempalte.query(query, new AssetEntryMapper());
		} catch (DataAccessException e) {
			throw new AssetDaoException("Can't find assets for filter: " + filter, e);
		}
		return assets;
	}
}
