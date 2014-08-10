package eu.appbucket.rothar.core.persistence;

import java.util.List;

import eu.appbucket.rothar.core.domain.asset.AssetEntry;
import eu.appbucket.rothar.core.domain.asset.AssetFilter;
import eu.appbucket.rothar.core.persistence.exception.AssetDaoException;
import eu.appbucket.rothar.core.service.exception.ServiceException;

public interface AssetDao {
	
	/**
	 * Checks if asset exists.
	 * 
	 * @param assetId ID of the asset to check
	 * @return TRUE if asset exists, FALSE if not
	 * 
	 * @throws AssetDaoException if there was general DB problem while checking the existence of asset
	 */
	boolean isAssetExisting(int assetId) throws AssetDaoException;
	
	/**
	 * Checks if asset is owned by user.
	 * 
	 * @param assetId ID of the asset to check
	 * @param userId ID of the user to check if he is the owner of the assets
	 * @return TRUE if asset is owned by user, FALSE if not
	 * 
	 * @throws AssetDaoException if there was general DB problem while checking the ownership of asset
	 */
	boolean isAssetOwnedByUser(int assetId, int userId) throws AssetDaoException;
	
	/**
	 * Creates new asset.
	 * 
	 * @param assetToBeCreated data for the new asset to be created
	 * 
	 * @throws AssetDaoException if there was general DB problem while creating the new asset
	 */
	void createNewAsset(AssetEntry assetToBeCreated) throws AssetDaoException;
	
	/**
	 * Updates existing asset.
	 * 
	 * @param assetToBeUpdate data which needs to be updated in the asset
	 * 
	 * @throws AssetDaoException if there was general DB problem while updating the existing asset
	 */
	void updateExistingAsset(AssetEntry assetToBeUpdate) throws AssetDaoException;
	
	/**
	 * Finds user asset.
	 * 
	 * @param userId ID of the asset owner
	 * @param assetId ID of the asset to be found
	 * @return found asset or new (empty) asset if non was found
	 * 
	 * @throws AssetDaoException if there was general DB problem while finding the asset
	 */
	AssetEntry findAssetByUserAndAssetId(Integer userId, Integer assetId) throws AssetDaoException;
	
	/**
	 * Finds user assets using the give filter.
	 * 
	 * @param filter contains multiple constraints used for finding the assets @see {@link AssetFilter}
	 * @return list of assets owned by the user give in the filter or empty list if there was no assets found
	 * 
	 * @throws ServiceException if there was general DB problem with retrieving the assets
	 */
	List<AssetEntry> findAssets(AssetFilter filter) throws AssetDaoException;
}
