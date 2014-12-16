package eu.appbucket.rothar.core.service;

import java.util.Date;
import java.util.List;

import eu.appbucket.rothar.core.domain.report.ReportEntry;
import eu.appbucket.rothar.core.domain.report.ReportListFilter;

public interface ReportService {
	void saveReportEntry(ReportEntry reportData);
	void saveSystemReportEntry(ReportEntry reportData);
	List<ReportEntry> findReportEntries(ReportListFilter filter);

	/**
	 * 
	 * Retrieves reports for given date and filter parameters.
	 * 
	 * @param filter report filter
	 * @param from start date for the first report
	 * @param to end date for the last report
	 * 
	 * @return list of the reports matching dates and filter
	 */
	List<ReportEntry> findReportEntriesForDate(ReportListFilter filter, Date from, Date to);
}
