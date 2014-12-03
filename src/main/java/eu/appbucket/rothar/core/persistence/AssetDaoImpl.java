package eu.appbucket.rothar.core.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import eu.appbucket.rothar.core.domain.asset.AssetEntry;
import eu.appbucket.rothar.core.domain.asset.AssetFilter;
import eu.appbucket.rothar.core.persistence.exception.AssetDaoException;

@Repository
public class AssetDaoImpl implements AssetDao {

	private JdbcTemplate jdbcTempalte;
	
	public static final String IS_ASSET_EXISTING_BY_ASSET_ID_QUERY = "SELECT count(*) from assets "
			+ " WHERE asset_id = ?";
	
	public static final String IS_ASSET_EXISTING_BY_ASSET_BEACON_ID_QUERY = "SELECT count(*) from assets "
			+ " WHERE uuid = ? and major = ? and minor = ?";
	
	public static final String IS_ASSET_OWNED_BY_USER_QUERY = "SELECT count(*) from assets "
			+ " WHERE asset_id = ? AND user_id = ?";
	
	public static final String CREATE_ASSET_QUERY = "INSERT INTO assets "
			+ "(user_id, status_id, description, uuid, major, minor, created) "
			+ "values (?, ?, ?, ?, ?, ?, ?)";
	
	public static final String CREATE_ASSET_WITH_NEXT_MINOR_ID_QUERY = "INSERT INTO assets "
			+ "(user_id, status_id, description, uuid, major, minor, created) "
			+ "values (?, ?, ?, ?, ?, (select (max(minor) + 1) from assets x), ?)";
	
	public static final String UPDATE_ASSET_QUERY = "UPDATE assets "
			+ " SET status_id = ?, description = ? where asset_id = ?";
	
	public static final String FIND_ASSET_BY_ASSET_ID_AND_USER_ID_QUERY = "SELECT * from assets "
			+ " WHERE user_id = ? and asset_id = ?";
	
	public static final String FIND_ASSET_BY_TAG_CODE_AND_USER_ID_QUERY = "SELECT * from assets "
			+ " WHERE user_id = ? and tag_code = ?";
	
	public static final String FIND_MULTIPLE_ASSETS_OWNED_BY_USER_QUERY = "SELECT * from assets "
			+ " WHERE user_id = %s"
			+ " ORDER BY %s %s "
			+ " LIMIT %d, %d";
	
	public static final String FIND_MULTIPLE_ASSETS_QUERY = "SELECT * from assets "
			+ " WHERE status_id = %d"			
			+ " LIMIT %d, %d";
	
	@Autowired
	public void setJdbcTempalte(JdbcTemplate jdbcTempalte) {
		this.jdbcTempalte = jdbcTempalte;
	}
	
	public boolean isAssetExisting(int assetId) {
		try {
			int count = jdbcTempalte.queryForInt(IS_ASSET_EXISTING_BY_ASSET_ID_QUERY, assetId);
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
	
	public boolean isAssetExisting(String uuid, int majorId, int minorId) {
		try {
			int count = jdbcTempalte.queryForInt(IS_ASSET_EXISTING_BY_ASSET_BEACON_ID_QUERY, uuid, majorId, minorId);
			if(count > 0) {
				return true;
			}
		} catch(EmptyResultDataAccessException emptyResultDataAccessException) {
			return false;
		} catch (DataAccessException dataAccessException) {
			throw new AssetDaoException("Can't check if asset: " + uuid + " exists.", dataAccessException);
		}
		return false;
	}
	
	public boolean isAssetOwnedByUser(int assetId, int userId) {
		try {
			int count = jdbcTempalte.queryForInt(IS_ASSET_OWNED_BY_USER_QUERY, assetId, userId);
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
	
	public AssetEntry createNewAsset(final AssetEntry asset) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		final Date createdOn = new Date();
		try {
			jdbcTempalte.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con)
						throws SQLException {
					PreparedStatement preparedStatement = con.prepareStatement(CREATE_ASSET_QUERY,
							new String[] { "asset_id" });
					preparedStatement.setInt(1, asset.getUserId());
					preparedStatement.setInt(2, asset.getStatusId());
					preparedStatement.setString(3, asset.getDescription());
					preparedStatement.setString(4, asset.getUuid());					
					preparedStatement.setInt(5, asset.getMajor());
					preparedStatement.setInt(6, asset.getMinor());
					preparedStatement.setTimestamp(7, new Timestamp(createdOn.getTime()));
					return preparedStatement;
				}
			}, keyHolder);
		} catch (DataAccessException dataAccessException) {
			throw new AssetDaoException(
					"Can't create asset : " + asset.getUuid() + " for user: " + asset.getUserId(), 
					dataAccessException);
		}
		int assetId = keyHolder.getKey().intValue();
		return findAssetByUserAndAssetId(asset.getUserId(), assetId);
	}
	
	public AssetEntry createNewAssetWithNextMinorId(final AssetEntry asset) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		final Date createdOn = new Date();
		try {
			jdbcTempalte.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con)
						throws SQLException {
					PreparedStatement preparedStatement = con.prepareStatement(CREATE_ASSET_WITH_NEXT_MINOR_ID_QUERY,
							new String[] { "asset_id" });
					preparedStatement.setInt(1, asset.getUserId());
					preparedStatement.setInt(2, asset.getStatusId());
					preparedStatement.setString(3, asset.getDescription());
					preparedStatement.setString(4, asset.getUuid());					
					preparedStatement.setInt(5, asset.getMajor());
					preparedStatement.setTimestamp(6, new Timestamp(createdOn.getTime()));
					return preparedStatement;
				}
			}, keyHolder);
		} catch (DataAccessException dataAccessException) {
			throw new AssetDaoException(
					"Can't create asset : " + asset.getUuid() + " for user: " + asset.getUserId(), 
					dataAccessException);
		}
		int assetId = keyHolder.getKey().intValue();
		return findAssetByUserAndAssetId(asset.getUserId(), assetId);
	}
	
	public void updateExistingAsset(AssetEntry asset) {
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

	public AssetEntry findAssetByUserAndAssetId(Integer userId, Integer assetId) {
		AssetEntry asset = null;
		try {
			asset = jdbcTempalte.queryForObject(FIND_ASSET_BY_ASSET_ID_AND_USER_ID_QUERY, 
					new AssetEntryMapper(), userId, assetId);	
		} catch(EmptyResultDataAccessException emptyResultDataAccessException) {
			asset = new AssetEntry();
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
			asset.setStatusId(rs.getInt("status_id"));
			asset.setMajor(rs.getInt("major"));
			asset.setMinor(rs.getInt("minor"));
			asset.setUserId(rs.getInt("user_id"));
			asset.setUuid(rs.getString("uuid"));
			return asset;
		}
	}
	
	public List<AssetEntry> findUserAssets(AssetFilter filter) {
		String query = String.format(FIND_MULTIPLE_ASSETS_OWNED_BY_USER_QUERY, 
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
	
	public List<AssetEntry> findAssets(AssetFilter filter) {
		String query = String.format(FIND_MULTIPLE_ASSETS_QUERY,
				filter.getStatus().getStatusId(),
				filter.getOffset(), filter.getLimit());
		List<AssetEntry> assets = null;
		try {
			assets = jdbcTempalte.query(query, new AssetEntryMapper());
		} catch(EmptyResultDataAccessException emptyResultDataAccessException) {
			assets = new ArrayList<AssetEntry>();
		} catch (DataAccessException dataAccessException) {
			throw new AssetDaoException("Can't find assets using filter:"
					+ " status: " + filter.getStatus()
					+ " limit: " + filter.getLimit()
					+ " offset: " + filter.getOffset()					
					, dataAccessException);
		}
		if(assets == null) {
			assets = new ArrayList<AssetEntry>();
		}
		return assets;
	}
	
	public AssetEntry findAssetByUserAndTagCode(Integer userId, String tagCode) throws AssetDaoException {
		AssetEntry asset = null;
		try {
			asset = jdbcTempalte.queryForObject(FIND_ASSET_BY_TAG_CODE_AND_USER_ID_QUERY, 
					new AssetEntryMapper(), userId, tagCode);	
		} catch(EmptyResultDataAccessException emptyResultDataAccessException) {
			asset = new AssetEntry();
		} catch (DataAccessException dataAccessException) {
			throw new AssetDaoException("Can't find asset for tag code: " + tagCode, dataAccessException);
		}
		return asset;
	}
}
