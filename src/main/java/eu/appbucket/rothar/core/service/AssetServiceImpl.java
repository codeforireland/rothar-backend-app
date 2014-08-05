package eu.appbucket.rothar.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import eu.appbucket.rothar.core.domain.asset.AssetEntry;
import eu.appbucket.rothar.core.domain.asset.AssetFilter;
import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.core.persistence.AssetDao;
import eu.appbucket.rothar.core.persistence.exception.AssetDaoException;
import eu.appbucket.rothar.core.persistence.exception.UserDaoException;
import eu.appbucket.rothar.core.service.exception.ServiceException;
import eu.appbucket.rothar.web.domain.asset.AssetStatus;

@Service
public class AssetServiceImpl implements AssetService {

	private AssetDao assetDao;
	private UserService userService;
	
	@Autowired
	public void setAssetDao(AssetDao assetDao) {
		this.assetDao = assetDao;
	}
	
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	public void createAsset(AssetEntry asset) {
		assertUserExist(asset.getUserId());
		assertAssetDoesntExist(asset.getUserId(), asset.getAssetId());
		createAssetEntry(asset);
	}

	private void assertUserExist(int userId) {
		UserEntry user = null;
		try {
			user = userService.findUser(userId);
		} catch (UserDaoException e) {
			throw new ServiceException("Can't find user: " + userId);
		}
		if(user.getUserId() == null) {
			throw new ServiceException("User: " + user.getUserId() + " doesn't exists.");
		}
	}
	
	private void assertAssetDoesntExist(Integer userId, Integer assetId) {
		AssetEntry existingAsset = null;
		try {
			existingAsset = assetDao.findAssetByUserAndAssetId(userId, assetId);
		} catch (UserDaoException e) {
			throw new ServiceException("Can't find asset: " + assetId);
		}
		if(existingAsset.getAssetId() != null) {
			throw new ServiceException("Asset: " + assetId + " already exists.");
		}
	}
	
	private void assertAssetExist(Integer userId, Integer assetId) {
		AssetEntry existingAsset = null;
		try {
			existingAsset = assetDao.findAssetByUserAndAssetId(userId, assetId);
		} catch (UserDaoException e) {
			throw new ServiceException("Can't find asset: " + assetId);
		}
		if(existingAsset.getAssetId() == null) {
			throw new ServiceException("Asset: " + assetId + " doesn't exists.");
		}
	}
	
	private void createAssetEntry(AssetEntry assetToCreate) {
		assetToCreate.setStatusId(AssetStatus.WITH_OWNER.getStatusId());
		try {
			assetDao.createNewAsset(assetToCreate);
		} catch (AssetDaoException e) {
			throw new ServiceException("Can't create asset for user: " + assetToCreate.getUserId());
		}
	}
	
	public void updateAsset(AssetEntry updatedAsset) {
		assertUserExist(updatedAsset.getUserId());
		assertAssetExist(updatedAsset.getUserId(), updatedAsset.getAssetId());
		fillMissingInformation(updatedAsset);
		updateExistingAsset(updatedAsset);
	}
	
	private AssetEntry fillMissingInformation(AssetEntry assetToFill) throws ServiceException {
		AssetEntry fullyPopulatedAsset = null;
		try {
			fullyPopulatedAsset = assetDao.findAssetByUserAndAssetId(assetToFill.getUserId(), assetToFill.getAssetId());
		} catch (UserDaoException e) {
			throw new ServiceException("Can't find asset: " + assetToFill.getAssetId());
		}
		if(fullyPopulatedAsset.getAssetId() == null) {
			throw new ServiceException("Asset: " + assetToFill.getAssetId() + " doesn't exists.");
		}
		AssetStatus status = AssetStatus.getStatusEnumById(assetToFill.getStatusId());
		if(status == null) {
			assetToFill.setStatusId(fullyPopulatedAsset.getStatusId());
		}
		if(StringUtils.isEmpty(assetToFill.getDescription())) {
			assetToFill.setDescription(fullyPopulatedAsset.getDescription());
		}
		return assetToFill;
	}
	
	private void updateExistingAsset(AssetEntry updatedAsset) throws ServiceException {
		try {
			assetDao.updateExistingAsset(updatedAsset);	
		} catch (AssetDaoException e) {
			throw new ServiceException("Can't update asset: " + updatedAsset.getAssetId());
		}
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
	
	public AssetEntry findAsset(Integer userId, Integer assetId) {
		AssetEntry foundAsset = assetDao.findAssetByUserAndAssetId(userId, assetId);
		return foundAsset;
	}
}
