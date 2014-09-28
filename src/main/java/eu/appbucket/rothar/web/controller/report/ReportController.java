package eu.appbucket.rothar.web.controller.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.appbucket.rothar.core.domain.report.ReportEntry;
import eu.appbucket.rothar.core.domain.report.ReportEntryFilter;
import eu.appbucket.rothar.core.service.ReportService;
import eu.appbucket.rothar.web.domain.report.ReportData;

@Controller
public class ReportController {

	private static final Logger LOGGER = Logger.getLogger(ReportController.class);	
	private ReportService reportService;
	
	@Autowired
	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}
	
	@RequestMapping(value = {"v1/users/{reporterId}/assets/{assetId}/reports", "v2/users/{reporterId}/assets/{assetId}/reports"}, method = RequestMethod.POST)
	@ResponseBody
	public void createReportForAsset(
			@PathVariable Integer reporterId, 
			@PathVariable Integer assetId, 
			@RequestBody ReportData reportEntryData) {
  		LOGGER.info("createReportForAsset: " + assetId);
		ReportEntry reportEntry = ReportEntry.fromReportEntry(reportEntryData);
		reportEntry.setCreated(new Date());
		reportEntry.setAssetId(assetId);
		reportEntry.setReporterId(reporterId);
		reportService.saveReportEntry(reportEntry);
	}
	
	@RequestMapping(value = {"v1/users/{ownerId}/assets/{assetId}/reports", "v2/users/{ownerId}/assets/{assetId}/reports"}, method = RequestMethod.GET)
	@ResponseBody
	public List<ReportData> getReportsForAsset(
			@PathVariable Integer ownerId,
			@PathVariable Integer assetId,
			@RequestParam(value = "offset", required = false) Integer offset, 
			@RequestParam(value = "limit", required = false) Integer limit, 
			@RequestParam(value = "sort", required = false) String sort, 
			@RequestParam(value = "order", required = false) String order) {
		LOGGER.info("getReportsForAsset: " + assetId);
		offset = InputSanitizer.resolveOffset(offset);
		limit = InputSanitizer.resolveLimit(limit);
		sort = InputSanitizer.resoleveSort(sort);
		order = InputSanitizer.resoleveOrder(order);
		ReportEntryFilter reportFilter =  new ReportEntryFilter.Builder()
				.forAsset(assetId)
				.ownerBy(ownerId)
				.fromOffset(offset).withLimit(limit)
				.sortBy(sort).orderBy(order).build();
		ReportData reportData = null;
		List<ReportData> reportsData = new ArrayList<ReportData>();
		List<ReportEntry> reportsEntries = this.reportService.findReportEntries(reportFilter);
		for(ReportEntry reportEntry: reportsEntries) {
			reportData = ReportEntry.fromReportData(reportEntry);
			reportsData.add(reportData);
		}
		return reportsData;
	}
	
	private static final class InputSanitizer {	
		private static final Set<String> VALID_SORT = new HashSet<String>();
		private static final Set<String> VALID_ORDER = new HashSet<String>();
		
		static {
			VALID_SORT.add("created");
			VALID_ORDER.add("asc");
			VALID_ORDER.add("desc");
		}
	
		public static String resoleveSort(String inputSort) {
			String defaultSort = "created";
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
