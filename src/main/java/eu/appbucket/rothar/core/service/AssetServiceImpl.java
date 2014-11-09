package eu.appbucket.rothar.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.appbucket.rothar.core.domain.asset.AssetEntry;
import eu.appbucket.rothar.core.domain.asset.AssetFilter;
import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.core.persistence.AssetDao;
import eu.appbucket.rothar.core.persistence.exception.AssetDaoException;
import eu.appbucket.rothar.core.service.exception.ServiceException;
import eu.appbucket.rothar.core.service.v1.UserService;
import eu.appbucket.rothar.web.domain.asset.AssetStatus;

@Service
@Transactional(rollbackFor={Exception.class})
public class AssetServiceImpl implements AssetService {

	private AssetDao assetDao;
	private UserService userService;
	
	@Autowired
	public void setAssetDao(AssetDao assetDao) {
		this.assetDao = assetDao;
	}
	
	@Autowired
	@Qualifier("v1.userService")
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public boolean isAssetExisting(AssetEntry assetToCheckForExistence) throws ServiceException {
		boolean assetExists = false;
		int assetId = assetToCheckForExistence.getAssetId();
		try {
			assetExists = assetDao.isAssetExisting(assetId);
		} catch (AssetDaoException assetDaoException) {
			throw new ServiceException("Problem checking existence of the asset: " + assetId, assetDaoException);
		}
		return assetExists;
	}

	public boolean isAssetNotExisting(AssetEntry assetToCheckForExistence) throws ServiceException {
		boolean assetDoesntExists = true;
		try {
			assetDoesntExists = !assetDao.isAssetExisting(
					assetToCheckForExistence.getUuid(), 
					assetToCheckForExistence.getMajor(), 
					assetToCheckForExistence.getMinor());
		} catch (AssetDaoException assetDaoException) {
			throw new ServiceException(
					"Problem checking existence of the asset with"
					+ " uuid: " + assetToCheckForExistence.getUuid()
					+ ", major id: " + assetToCheckForExistence.getMajor()
					+ ", minor id: " + assetToCheckForExistence.getMinor()
					, assetDaoException);
		}
		return assetDoesntExists;
	}
	
	public boolean isAssetOwnedByUser(AssetEntry assetToCheckForOwnership) throws ServiceException {
		boolean assetOwnedByTheUser = false;
		int assetId = assetToCheckForOwnership.getAssetId();
		int userId = assetToCheckForOwnership.getUserId();
		try {
			assetOwnedByTheUser = assetDao.isAssetOwnedByUser(assetId, userId);
		} catch (AssetDaoException assetDaoException) {
			throw new ServiceException("Problem checking existence of the asset: " + assetId + " for the user: " + userId, assetDaoException);
		}
		return assetOwnedByTheUser;
	}
	
	@Transactional
	public AssetEntry createAsset(AssetEntry assetToBeCreated) throws ServiceException {
		assertAssetDoesntExist(assetToBeCreated);
		assertUserExist(assetToBeCreated.getUserId());
		return createNewAsset(assetToBeCreated);
	}

	private void assertUserExist(int userId) throws ServiceException {
		UserEntry userToCheckForExistence = new UserEntry();
		userToCheckForExistence.setUserId(userId);
		boolean userExists = userService.isUserExistingById(userToCheckForExistence);
		if(!userExists) {
			throw new ServiceException("User: " + userId + " doesn't exists.");
		}
	}
	
	private AssetEntry createNewAsset(AssetEntry asset) throws ServiceException {
		AssetEntry newAsset = new AssetEntry();
		asset.setStatusId(AssetStatus.WITH_OWNER.getStatusId());
		try {
			newAsset = assetDao.createNewAsset(asset);
		} catch (AssetDaoException assetDaoException) {
			throw new ServiceException("Can't create asset with uuid: " + asset.getUuid() + " for the user with id: " + asset.getUserId(), assetDaoException);
		}
		return newAsset;
	}
	
	@Transactional
	public void updateAsset(AssetEntry assetToBeUpdates) {
		assertAssetExistByAssetId(assetToBeUpdates.getAssetId());
		assertUserExist(assetToBeUpdates.getUserId());
		assertAssetIsOwnerByTheUser(assetToBeUpdates.getAssetId(), assetToBeUpdates.getUserId());
		updateExistingAsset(assetToBeUpdates);
	}
	
	private void assertAssetDoesntExist(AssetEntry assetToCheckForExistence) throws ServiceException {		
		boolean assetExists = isAssetNotExisting(assetToCheckForExistence);
		if(!assetExists) {
			throw new ServiceException("Asset with "
					+ "uuid: " + assetToCheckForExistence.getUuid()
					+ ", major id: " + assetToCheckForExistence.getMajor()
					+ ", minor id: " + assetToCheckForExistence.getMinor()
					+ " already exists.");
		}
	}
	
	private void assertAssetExistByAssetId(Integer assetId) throws ServiceException {
		AssetEntry assetToCheckForExistence = new AssetEntry();
		assetToCheckForExistence.setAssetId(assetId);
		boolean assetExists = isAssetExisting(assetToCheckForExistence);
		if(!assetExists) {
			throw new ServiceException("Asset with id: " + assetId + " doesn't exists.");
		}
	}
	
	private void assertAssetIsOwnerByTheUser(Integer assetId, Integer userId) throws ServiceException {
		AssetEntry assetToCheckForOwnership = new AssetEntry();
		assetToCheckForOwnership.setAssetId(assetId);
		assetToCheckForOwnership.setUserId(userId);
		boolean assetIsOwnedByUser = this.isAssetOwnedByUser(assetToCheckForOwnership);
		if(!assetIsOwnedByUser) {
			throw new ServiceException("Asset: " + assetId + " is not owned by user: " + userId);
		}
	}
	
	private void updateExistingAsset(AssetEntry updatedAsset) throws ServiceException {
		try {
			assetDao.updateExistingAsset(updatedAsset);	
		} catch (AssetDaoException assetDaoException) {
			throw new ServiceException("Can't update asset: " + updatedAsset.getAssetId(), assetDaoException);
		}
	}

	public AssetEntry findAsset(Integer userId, Integer assetId) throws ServiceException {
		assertAssetExistByAssetId(assetId);
		assertUserExist(userId);
		assertAssetIsOwnerByTheUser(assetId, userId);
		AssetEntry foundAsset = null;
		try {
			foundAsset = assetDao.findAssetByUserAndAssetId(userId, assetId);	
		} catch (AssetDaoException assetDaoException) {
			throw new ServiceException("Can't find asset: " + assetId + " for user: " + userId, assetDaoException);
		}
		return foundAsset;
	}
	
	public List<AssetEntry> findAssets(AssetFilter filter) {
		List<AssetEntry> assets = null;
		try {
			assets = assetDao.findAssets(filter);
		} catch (AssetDaoException e) {
			throw new ServiceException("Can't find find asset for user : " + filter.getUserId());
		}
		return assets;
	}
	
	public AssetEntry createSystemSpecificAsset(AssetEntry assetToBeCreated)
			throws ServiceException {
		String beaconUUID = (String) System.getProperty("IBEACON_UUID");
		assetToBeCreated.setUuid(beaconUUID);
		int beaconMajorId = Integer.valueOf(System.getProperty("IBEACON_UUID"));
		assetToBeCreated.setMajor(beaconMajorId);
		assertUserExist(assetToBeCreated.getUserId());
		return createNewAsset(assetToBeCreated);		
	}
}
