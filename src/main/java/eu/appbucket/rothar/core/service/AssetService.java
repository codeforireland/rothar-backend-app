package eu.appbucket.rothar.core.service;

import java.util.List;

import eu.appbucket.rothar.core.domain.asset.AssetEntry;
import eu.appbucket.rothar.core.domain.asset.AssetFilter;
import eu.appbucket.rothar.core.service.exception.ServiceException;

public interface AssetService {
	
	/**
	 * Checks if asset exists.
	 * 
	 * @param assetToCheckForExistence to check for the existence
	 * @return TRUE if asset exists, FALSE if not
	 * 
	 * @throws ServiceException if there was general DB problem while checking the existence of asset
	 */
	boolean isAssetExisting(AssetEntry assetToCheckForExistence) throws ServiceException;
	
	/**
	 * Checks if asset is owned by user.
	 * 
	 * @param assetToCheckForOwnership to be checked for the ownership
	 * @return TRUE if asset is owned by user, FALSE if not
	 * 
	 * @throws ServiceException if: 
	 * - asset to be checked doesn't exist
	 * or
	 * - there was general DB problem while checking the ownership of asset
	 */
	boolean isAssetOwnedByUser(AssetEntry assetToCheckForOwnership) throws ServiceException;
	
	/**
	 * Creates new asset for the user.
	 * 
	 * @param assetToBeCreated data of the asset to be created
	 * 
	 * @return newly create asset
	 * 
	 * @throws ServiceException if:
	 * - user for which asset should be created doesn't exits
	 * or
	 * - there was general problem with DB
	 */
	AssetEntry createAsset(AssetEntry assetToBeCreated) throws ServiceException;
	
	/**
	 * Creates new system specific asset for the user.
	 * 
	 * This asset will have UUID which is set for this application, major id which 
	 * is country specific (based on ISO 3166-1) and next available minor id. 
	 * 
	 * @param assetToBeCreated data of the asset to be created
	 * 
	 * @return newly create asset
	 * 
	 * @throws ServiceException if:
	 * - user for which asset should be created doesn't exits
	 * or
	 * - there was general problem with DB
	 */
	AssetEntry createSystemAsset(AssetEntry assetToBeCreated) throws ServiceException;
	
	/**
	 * Updates existing asset.
	 * 
	 * @param assetToBeUpdates data of the asset to be updated, only non-identification data can be updated like description, status etc.
	 * 
	 * @throws ServiceException if:
	 * - asset to be updated doesn't exist
	 * or  
	 * - asset is not owned by user
	 * or
	 * - there was general DB problem
	 */
	void updateAsset(AssetEntry assetToBeUpdates) throws ServiceException;
	
	/**
	 * Finds user asset.
	 * 
	 * @param userId ID of the asset owner
	 * @param assetId ID of the asset to be found
	 * @return found asset
	 * 
	 * @throws ServiceException if:
	 * - asset to be found doesn't exist
	 * or 
	 * - user for which asset should be found doesn't exits
	 * or
	 * - asset is not owned by user
	 * or
	 * - there was general DB problem during finding the asset
	 */
	AssetEntry findAsset(Integer userId, Integer assetId) throws ServiceException;
	
	
	/**
	 * Finds user assets using the give filter.
	 * 
	 * @param filter contains multiple constraints used for finding the assets @see {@link AssetFilter}
	 * @return list of assets owned by the user given by the filter or empty list if there was no assets found
	 * 
	 * @throws ServiceException if there was general DB problem with retrieving the assets
	 */
	List<AssetEntry> findUserAssets(AssetFilter filter) throws ServiceException;
	
	
	/**
	 * Finds single asset matching to the given code, the asset is owned by system user.
	 * 
	 * @param tagCode code of the asset to be retrieved
	 * @return asset matching the given code
	 */
	AssetEntry findSystemAssetByTagCode(String tagCode);
	
	/**
	 * Finds assets using the give filter.
	 * 
	 * @param filter contains multiple constraints used for finding the assets @see {@link AssetFilter}
	 * @return list of assets given by the filter or empty list if there was no assets found
	 * 
	 * @throws ServiceException if there was general DB problem with retrieving the assets
	 */
	List<AssetEntry> findAssets(AssetFilter filter) throws ServiceException;
}
