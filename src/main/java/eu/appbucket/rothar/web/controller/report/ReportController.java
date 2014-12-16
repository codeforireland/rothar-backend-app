package eu.appbucket.rothar.web.controller.report;

import java.util.ArrayList;
import java.util.Date;
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

import eu.appbucket.rothar.core.domain.report.ReportEntry;
import eu.appbucket.rothar.core.domain.report.ReportListFilter;
import eu.appbucket.rothar.core.service.ReportService;
import eu.appbucket.rothar.core.service.SystemService;
import eu.appbucket.rothar.web.domain.report.ReportData;

@Controller
public class ReportController {

	private static final Logger LOGGER = Logger.getLogger(ReportController.class);	
	private ReportService reportService;
	private SystemService systemService;
	
	@Autowired
	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}
	
	@Autowired
	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
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
	
	@RequestMapping(value = {"v3/assets/{assetId}/reports"}, method = RequestMethod.POST)
	@ResponseBody
	public void createAnonymousReportForAsset( 
			@PathVariable Integer assetId, 
			@RequestBody ReportData reportEntryData) {
  		LOGGER.info("createAnonymousReportForAsset: " + assetId);
  		ReportEntry reportEntry = ReportEntry.fromReportEntry(reportEntryData);
		reportEntry.setCreated(new Date());
		reportEntry.setAssetId(assetId);
		reportService.saveSystemReportEntry(reportEntry);
	}
	
	/*@RequestMapping(value = {"v4/assets/{assetId}/reports/summary"}, method = RequestMethod.GET)
	@ResponseBody	
	public List<ReportData> getAnonymousReportsSummaryForAsset(
			@PathVariable Integer assetId,
			@RequestParam(value = "offset", required = false) Integer offset, 
			@RequestParam(value = "limit", required = false) Integer limit, 
			@RequestParam(value = "sort", required = false) String sort, 
			@RequestParam(value = "order", required = false) String order) {
		int systemOwnerId = systemService.getSystemUserId();
		ParameterSanitizer summaryParametersSanitizer = new ParameterSanitizer()
			.addValidOrder("asc").addValidOrder("desc").addValidSort("created").addValidSort("count");
		offset = summaryParametersSanitizer.sanitizeOffset(offset);
		limit = summaryParametersSanitizer.sanitizeLimit(limit);
		sort = summaryParametersSanitizer.sanitizeSort(sort);
		order = summaryParametersSanitizer.sanitizeOrder(order);
		ReportListFilter reportFilter =  new ReportListFilter.Builder()
			.forAsset(assetId)
			.ownerBy(systemOwnerId)
			.fromOffset(offset).withLimit(limit)
			.sortBy(sort).orderBy(order).build();
		return getReportsForAsset(systemOwnerId, assetId, offset, limit, sort, order);
	}*/
	
	@RequestMapping(value = {"v4/assets/{assetId}/reports/{fromDate}/{toDate}"}, method = RequestMethod.GET)
	@ResponseBody	
	public List<ReportData> getAnonymousReportsForAssetInDateBoundaries(
			@PathVariable Integer assetId,
			@PathVariable Date fromDate,
			@PathVariable Date toDate,
			@RequestParam(value = "offset", required = false) Integer offset, 
			@RequestParam(value = "limit", required = false) Integer limit, 
			@RequestParam(value = "sort", required = false) String sort, 
			@RequestParam(value = "order", required = false) String order) {
		LOGGER.info("getAnonymousReportsForAssetInDateBoundaries: " + assetId);
		int systemOwnerId = systemService.getSystemUserId();
		ReportListFilter reportFilter = sanitizeParametersAndBuildReportFilter(systemOwnerId, assetId, offset, limit, sort, order);
		List<ReportEntry> reportsEntries = this.reportService.findReportEntriesForDate(reportFilter, fromDate, toDate);
		List<ReportData> reportsData = convertReportEntriesToReportDate(reportsEntries);
		return reportsData;
	}
	
	private ReportListFilter sanitizeParametersAndBuildReportFilter (
			Integer ownerId,
			Integer assetId,
			Integer offset,
			Integer limit,
			String sort,
			String order) {
		ParameterSanitizer reportsParameterSanitizer = new ParameterSanitizer()
			.addValidOrder("asc").addValidOrder("desc").addValidSort("created");
		offset = reportsParameterSanitizer.sanitizeOffset(offset);
		limit = reportsParameterSanitizer.sanitizeLimit(limit);
		sort = reportsParameterSanitizer.sanitizeSort(sort);
		order = reportsParameterSanitizer.sanitizeOrder(order);
		ReportListFilter reportFilter =  new ReportListFilter.Builder()
				.forAsset(assetId)
				.ownerBy(ownerId)
				.fromOffset(offset).withLimit(limit)
				.sortBy(sort).orderBy(order).build();
		return reportFilter;
	}
	
	private List<ReportData> convertReportEntriesToReportDate(List<ReportEntry> reportsEntries) {
		ReportData reportData = null;
		List<ReportData> reportsData = new ArrayList<ReportData>();
		for(ReportEntry reportEntry: reportsEntries) {
			reportData = ReportEntry.fromReportData(reportEntry);
			reportsData.add(reportData);
		}
		return reportsData;
	}
	
	@RequestMapping(value = {"v4/assets/{assetId}/reports"}, method = RequestMethod.GET)
	@ResponseBody	
	public List<ReportData> getAnonymousReportsForAsset(
			@PathVariable Integer assetId,
			@RequestParam(value = "offset", required = false) Integer offset, 
			@RequestParam(value = "limit", required = false) Integer limit, 
			@RequestParam(value = "sort", required = false) String sort, 
			@RequestParam(value = "order", required = false) String order) {
		int systemOwnerId = systemService.getSystemUserId();
		return getReportsForAsset(systemOwnerId, assetId, offset, limit, sort, order);
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
		ReportListFilter reportFilter = sanitizeParametersAndBuildReportFilter(ownerId, assetId, offset, limit, sort, order);
		List<ReportEntry> reportsEntries = this.reportService.findReportEntries(reportFilter);
		List<ReportData> reportsData = convertReportEntriesToReportDate(reportsEntries);
		return reportsData;
	}
	
	private static final class ParameterSanitizer {	
		private Set<String> validSort = new HashSet<String>();
		private Set<String> validOrder = new HashSet<String>();
		
		public ParameterSanitizer addValidSort(String sort) {
			validSort.add(sort);
			return this;
		}
		
		public ParameterSanitizer addValidOrder(String order) {
			validOrder.add(order);
			return this;
		}
	
		public String sanitizeSort(String inputSort) {
			String defaultSort = "created";
			if(StringUtils.isEmpty(inputSort)) {
				return defaultSort;
			}
			if(!validSort.contains(inputSort)) {
				return defaultSort;
			}
			return inputSort;
		}
		
		public String sanitizeOrder(String inputOrder) {
			String defaultOrder = "desc";
			if(StringUtils.isEmpty(inputOrder)) {
				return defaultOrder;
			}
			if(!validOrder.contains(inputOrder)) {
				return defaultOrder;
			}
			return inputOrder;
		}
		
		public int sanitizeLimit(Integer limit) {
			int defaultLimit = 10;
			int minLimit = 1;
			int maxLimit = 20;
			if(limit == null || limit < minLimit || limit > maxLimit) {
				return defaultLimit;
			}
			return limit;
		}
		
		public int sanitizeOffset(Integer inputOffset) {
			int defaultOffset = 0;
			int minOffset = 0;
			if(inputOffset == null || inputOffset < minOffset) {
				return defaultOffset;
			}
			return inputOffset;
		}
	}
}
