package eu.appbucket.rothar.web.controller.asset;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

@Controller
public class AssetController {

	private static final Logger LOGGER = Logger.getLogger(AssetController.class);	
	private AssetService assetService;
	
	@Autowired
	public void setAssetService(AssetService assetService) {
		this.assetService = assetService;
	}
	
	@RequestMapping(value = "v1/users/{$ownerId}/assets", 
			method = RequestMethod.POST)
	@ResponseBody
	public void createOwnerAsset(
			@PathVariable String ownerId, 
			@RequestBody AssetData assetData) {
  		LOGGER.info("createAsset");
		AssetEntry assetEntry = AssetEntry.fromAssetData(assetData);
		assetService.createAsset(assetEntry);
	}
	
	@RequestMapping(value = "v1/users/{$ownerId}/assets/{$assetId}", 
			method = RequestMethod.PUT)
	@ResponseBody
	public void updateOwnerAsset(
			@PathVariable Integer ownerId,
			@PathVariable Integer assetId,
			@RequestBody AssetData assetData) {
		LOGGER.info("updateAsset");
		AssetEntry assetEntry = AssetEntry.fromAssetData(assetData);
		assetEntry.setAssetId(assetId);
		assetEntry.setUserId(ownerId);
		assetService.updateAsset(assetEntry);
	}
	
	@RequestMapping(value = "v1/user/{$ownerId}/asset", method = RequestMethod.GET)
	@ResponseBody
	public List<AssetData> getOwnerAssets(
			@PathVariable String ownerId,
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
		assetEntries = assetService.findAssets(assetFilter);
		for(AssetEntry assetEntry: assetEntries) {
			assetData = AssetData.fromAssetEntry(assetEntry);
			assetsData.add(assetData);
		}
		return assetsData;
	}
	
	private static final class InputSanitizer {	
		private static final Set<String> VALID_SORT = new HashSet<String>();
		private static final Set<String> VALID_ORDER = new HashSet<String>();
		
		static {
			VALID_SORT.add("created");
			VALID_SORT.add("description");
			VALID_SORT.add("status");
			VALID_ORDER.add("asc");
			VALID_ORDER.add("desc");
		}
	
		public static String resoleveSort(String inputSort) {
			String defaultSort = "status";
			if(StringUtils.isEmpty(inputSort)) {
				return defaultSort;
			}
			if(!VALID_SORT.contains(inputSort)) {
				return defaultSort;
			}
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
			int maxLimit = 20;
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
	}
}
