package eu.appbucket.rothar.core.persistence;

import java.util.List;

import eu.appbucket.rothar.core.domain.asset.AssetEntry;
import eu.appbucket.rothar.core.domain.asset.AssetFilter;
import eu.appbucket.rothar.core.persistence.exception.AssetDaoException;

public interface AssetDao {
	
	void createNewAsset(AssetEntry asset) throws AssetDaoException;
	void updateExistingAsset(AssetEntry asset) throws AssetDaoException;
	AssetEntry findAssetByUserAndAssetId(Integer userId, Integer assetId);
	List<AssetEntry> findAssets(AssetFilter filter) throws AssetDaoException;
}
