package eu.appbucket.rothar.web.controller.asset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.appbucket.rothar.core.domain.asset.AssetEntry;
import eu.appbucket.rothar.core.domain.asset.AssetFilter;
import eu.appbucket.rothar.core.service.AssetService;
import eu.appbucket.rothar.web.domain.asset.AssetData;
import eu.appbucket.rothar.web.domain.asset.AssetStatus;

@Controller
public class AssetController {

	private static final Logger LOGGER = Logger.getLogger(AssetController.class);	
	private AssetService assetService;
	
	@Autowired
	public void setAssetService(AssetService assetService) {
		this.assetService = assetService;
	}
	
	@RequestMapping(value = {"v1/users/{ownerId}/assets", "v2/users/{ownerId}/assets"}, method = RequestMethod.POST)
	@ResponseBody
	public AssetData createOwnerAsset(
			@PathVariable Integer ownerId, 
			@RequestBody AssetData assetData) {
  		LOGGER.info("createAsset");
		AssetEntry assetEntry = AssetEntry.fromAssetData(assetData);
		assetEntry.setUserId(ownerId);
		AssetEntry newAsset = assetService.createAsset(assetEntry);
		return AssetEntry.fromAssetEntry(newAsset);
	}

	@RequestMapping(value = {"v3/users/{ownerId}/assets"}, method = RequestMethod.POST)
	@ResponseBody
	public AssetData createSystemSpecificOwnerAsset(
			@PathVariable Integer ownerId, 
			@RequestBody AssetData assetData) {
  		LOGGER.info("createSystemSpecificOwnerAsset");
		AssetEntry assetEntry = AssetEntry.fromAssetData(assetData);
		assetEntry.setUserId(ownerId);
		AssetEntry newAsset = assetService.createSystemAsset(assetEntry);
		return AssetEntry.fromAssetEntry(newAsset);
	}
	
	@RequestMapping(value = {"v1/users/{ownerId}/assets/{assetId}", "v2/users/{ownerId}/assets/{assetId}"}, 
			method = RequestMethod.PUT)
	@ResponseBody
	public AssetData updateOwnerAsset(
			@PathVariable Integer ownerId,
			@PathVariable Integer assetId,
			@RequestBody AssetData assetData) {
		LOGGER.info("updateOwnerAsset");
		AssetEntry assetEntry = AssetEntry.fromAssetData(assetData);
		assetEntry.setAssetId(assetId);
		assetEntry.setUserId(ownerId);
		assetService.updateAsset(assetEntry);
		return AssetEntry.fromAssetEntry(assetEntry);
	}
	
	@RequestMapping(value = {"v4/assets/{assetId}"}, 
			method = RequestMethod.PUT)
	@ResponseBody
	public AssetData updateSystemAsset(
			@PathVariable Integer assetId,
			@RequestBody AssetData assetData) {
		LOGGER.info("updateOwnerAsset");
		AssetEntry assetEntry = AssetEntry.fromAssetData(assetData);
		assetEntry.setAssetId(assetId);
		assetService.updateSystemAsset(assetEntry);
		return AssetEntry.fromAssetEntry(assetEntry);
	}
	
	@RequestMapping(value = {"v1/users/{ownerId}/assets/{assetId}", "v2/users/{ownerId}/assets/{assetId}"}, 
			method = RequestMethod.GET)
	@ResponseBody
	public AssetData getOwnerAsset(
			@PathVariable Integer ownerId,
			@PathVariable Integer assetId) {
		LOGGER.info("getOwnerAsset");		
		AssetEntry assetEntry = assetService.findAsset(ownerId, assetId);
		AssetData assetData = AssetEntry.fromAssetEntry(assetEntry);
		return assetData;
	}
	
	@RequestMapping(value = {"v1/users/{ownerId}/assets", "v2/users/{ownerId}/assets"}, method = RequestMethod.GET)
	@ResponseBody
	public List<AssetData> getOwnerAssets(
			@PathVariable Integer ownerId,
			@RequestParam(value = "offset", required = false) Integer offset, 
			@RequestParam(value = "limit", required = false) Integer limit, 
			@RequestParam(value = "sort", required = false) String sort, 
			@RequestParam(value = "order", required = false) String order) {
		LOGGER.info("Retrieving asset(s) for user: " + ownerId);
		offset = InputSanitizer.resolveOffset(offset);
		limit = InputSanitizer.resolveLimit(limit);
		sort = InputSanitizer.resoleveSort(sort);
		order = InputSanitizer.resoleveOrder(order);
		AssetFilter assetFilter =  new AssetFilter.Builder()
			.starFrom(offset)
			.limitTo(limit)
			.sortBy(sort)
			.orderBy(order)
			.buildFilterForUser(ownerId);
		List<AssetEntry> assetEntries = null;
		AssetData assetData = null;
		List<AssetData> assetsData = new ArrayList<AssetData>();
		assetEntries = assetService.findUserAssets(assetFilter);
		for(AssetEntry assetEntry: assetEntries) {
			assetData = AssetEntry.fromAssetEntry(assetEntry);
			assetsData.add(assetData);
		}
		return assetsData;
	}
	
	@RequestMapping(value = {"v4/assets/code/{tagCode}"}, method = RequestMethod.GET)
	@ResponseBody
	public AssetData getSystemAssetByTagCode(
			@PathVariable String tagCode) {
		AssetEntry asset = assetService.findSystemAssetByTagCode(tagCode);
		return AssetEntry.fromAssetEntry(asset);
	}
	
	private static final class InputSanitizer {	
		private static final Map<String, String> VALID_SORT = new HashMap<String, String>();
		private static final Set<String> VALID_ORDER = new HashSet<String>();
		
		static {
			VALID_SORT.put("created", "created");
			VALID_SORT.put("description", "description");
			VALID_SORT.put("status", "status_id");
			VALID_ORDER.add("asc");
			VALID_ORDER.add("desc");
		}
	
		public static String resoleveSort(String inputSort) {
			String defaultSort = VALID_SORT.get("status");
			if(StringUtils.isEmpty(inputSort)) {
				return defaultSort;
			}
			if(!VALID_SORT.keySet().contains(inputSort)) {
				return defaultSort;
			}
			inputSort = VALID_SORT.get(inputSort);
			return inputSort;
		}
		
		public static String resoleveOrder(String inputOrder) {
			String defaultOrder = "desc";
			if(StringUtils.isEmpty(inputOrder)) {
				return defaultOrder;
			}
			if(!VALID_ORDER.contains(inputOrder)) {
				return defaultOrder;
			}
			return inputOrder;
		}
		
		public static int resolveLimit(Integer limit) {
			int defaultLimit = 10;
			int minLimit = 1;
			int maxLimit = 100;
			if(limit == null || limit < minLimit || limit > maxLimit) {
				return defaultLimit;
			}
			return limit;
		}
		
		public static int resolveOffset(Integer inputOffset) {
			int defaultOffset = 0;
			int minOffset = 0;
			if(inputOffset == null || inputOffset < minOffset) {
				return defaultOffset;
			}
			return inputOffset;
		}
		
		public static AssetStatus resolveStatus(String inputStatus) {			
			AssetStatus defaultStatus = AssetStatus.STOLEN;
			if(StringUtils.isEmpty(inputStatus)) {
				return defaultStatus;
			}
			AssetStatus outputStatus= AssetStatus.valueOf(inputStatus);
			if(outputStatus == null) {
				return defaultStatus;
			}
			return outputStatus;
		}
	}
	
	@RequestMapping(value = {"v3/assets"}, method = RequestMethod.GET)
	@ResponseBody
	public List<AssetData> getAssets(
			@RequestParam(value = "offset", required = false) Integer offset, 
			@RequestParam(value = "limit", required = false) Integer limit,
			@RequestParam(value = "status", required = false) String statusName) {
		LOGGER.info("Retrieving asset(s)");
		offset = InputSanitizer.resolveOffset(offset);
		limit = InputSanitizer.resolveLimit(limit);
		AssetStatus status = InputSanitizer.resolveStatus(statusName);
		AssetFilter assetFilter =  new AssetFilter.Builder()
			.starFrom(offset)
			.limitTo(limit)
			.withStatus(status)
			.buildFilterForAssets();
		List<AssetEntry> assetEntries = null;
		AssetData assetData = null;
		List<AssetData> assetsData = new ArrayList<AssetData>();
		assetEntries = assetService.findAssets(assetFilter);
		for(AssetEntry assetEntry: assetEntries) {
			assetData = AssetEntry.fromAssetEntry(assetEntry);
			assetData = anonymizeAssetDate(assetData);
			assetsData.add(assetData);
		}
		return assetsData;
	}
	
	private AssetData anonymizeAssetDate(AssetData dataToBeAnonymized) {		
		dataToBeAnonymized.setUserId(null);
		dataToBeAnonymized.setDescription(null);
		dataToBeAnonymized.setCreated(null);
		return dataToBeAnonymized;
	}
}
