package eu.appbucket.rothar.core.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import eu.appbucket.rothar.core.domain.asset.AssetEntry;
import eu.appbucket.rothar.core.domain.asset.AssetFilter;
import eu.appbucket.rothar.core.persistence.exception.AssetDaoException;
/**
 * 
 * TODO: IMPLEMENT ACCRODING TO THE INTERFACE !!!!!
 * 
 * 
 * @author abednarski
 *
 */

@Repository
public class AssetDaoImpl implements AssetDao {

	private JdbcTemplate jdbcTempalte;
	
	public static final String IS_ASSET_EXISTING_QUERY = "SELECT count(*) from assets "
			+ " WHERE asset_id = ?";
	
	public static final String IS_ASSET_OWNED_BY_USER_QUERY = "SELECT count(*) from assets "
			+ " WHERE asset_id = ? AND user_id = ?";
	
	public static final String CREATE_ASSET_QUERY = "INSERT INTO assets "
			+ "(asset_id, user_id, status_id, description, created)";
	
	public static final String UPDATE_ASSET_QUERY = "UPDATE assets "
			+ " SET status_id = ?, description = ?)"
			+ " WHERE asset_id = ?";
	
	public static final String FIND_ASSET_BY_ASSET_ID_AND_USER_ID_QUERY = "SELECT * from assets "
			+ " WHERE user_id = ?, asset_id = ?";
	
	public static final String FIND_MULTIPLE_ASSETS_QUERY = "SELECT * from assets "
			+ " WHERE user_id = %s"
			+ " ORDER BY %s %s "
			+ " LIMIT %d, %d";
	
	@Autowired
	public void setJdbcTempalte(JdbcTemplate jdbcTempalte) {
		this.jdbcTempalte = jdbcTempalte;
	}
	
	public boolean isAssetExisting(int assetId) throws AssetDaoException {
		try {
			int count = jdbcTempalte.queryForInt(IS_ASSET_EXISTING_QUERY, assetId);
			if(count > 0) {
				return true;
			}
		} catch(EmptyResultDataAccessException emptyResultDataAccessException) {
			return false;
		} catch (DataAccessException dataAccessException) {
			throw new AssetDaoException("Can't check if asset: " + assetId + " exists.", dataAccessException);
		}
		return false;
	}

	public boolean isAssetOwnedByUser(int assetId, int userId) throws AssetDaoException {
		try {
			int count = jdbcTempalte.queryForInt(IS_ASSET_OWNED_BY_USER_QUERY, assetId);
			if(count > 0) {
				return true;
			}
		} catch(EmptyResultDataAccessException emptyResultDataAccessException) {
			return false;
		} catch (DataAccessException dataAccessException) {
			throw new AssetDaoException("Can't check if asset: " + assetId + " is owned by user: " + userId, 
					dataAccessException);
		}
		return false;
	}
	
	public void createNewAsset(AssetEntry asset) throws AssetDaoException {
		try {
			jdbcTempalte.update(CREATE_ASSET_QUERY, 
				asset.getAssetId(),
				asset.getUserId(),
				asset.getStatusId(),
				asset.getDescription(),
				new Date());
		} catch (DataAccessException dataAccessException) {
			throw new AssetDaoException("Can't create asset : " + asset.getUuid() + " for user: " + asset.getUserId(), 
					dataAccessException);
		}
	}

	public void updateExistingAsset(AssetEntry asset) throws AssetDaoException {
		try {
			jdbcTempalte.update(UPDATE_ASSET_QUERY,
				asset.getStatusId(),
				asset.getDescription(),
				asset.getAssetId());
		} catch (DataAccessException dataAccessException) {
			throw new AssetDaoException("Can't update asset " + asset.getAssetId() + "for user: " + asset.getUserId(), 
					dataAccessException);
		}		
	}

	public AssetEntry findAssetByUserAndAssetId(Integer userId, Integer assetId) throws AssetDaoException {
		AssetEntry asset = null;
		try {
			asset = jdbcTempalte.queryForObject(FIND_ASSET_BY_ASSET_ID_AND_USER_ID_QUERY, 
					new AssetEntryMapper(), userId, assetId);	
		} catch (DataAccessException dataAccessException) {
			throw new AssetDaoException("Can't find asset " + assetId + " for user: " + userId, dataAccessException);
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
	
	public List<AssetEntry> findAssets(AssetFilter filter) throws AssetDaoException {
		String query = String.format(FIND_MULTIPLE_ASSETS_QUERY, 
				filter.getUserId(),
				filter.getSort(), filter.getOrder(),
				filter.getOffset(), filter.getLimit());
		List<AssetEntry> assets = null;
		try {
			assets = jdbcTempalte.query(query, new AssetEntryMapper());
		} catch(EmptyResultDataAccessException emptyResultDataAccessException) {
			assets = new ArrayList<AssetEntry>();
		} catch (DataAccessException dataAccessException) {
			throw new AssetDaoException("Can't find assets using filter:"
					+ " user: " + filter.getUserId()
					+ " limit: " + filter.getLimit()
					+ " offset: " + filter.getOffset()
					+ " sort: " + filter.getSort()
					+ " order: " + filter.getOrder()
					, dataAccessException);
		}
		if(assets == null) {
			assets = new ArrayList<AssetEntry>();
		}
		return assets;
	}
}
