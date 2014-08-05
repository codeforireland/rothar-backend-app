package eu.appbucket.rothar.core.service;

import java.util.List;

import eu.appbucket.rothar.core.domain.asset.AssetEntry;
import eu.appbucket.rothar.core.domain.asset.AssetFilter;

public interface AssetService {
	
	void createAsset(AssetEntry assetData);
	void updateAsset(AssetEntry assetData);
	AssetEntry findAsset(Integer userId, Integer assetId);
	List<AssetEntry> findAssets(AssetFilter filter);
}
