package eu.appbucket.rothar.core.service;

import java.util.List;

import eu.appbucket.rothar.core.domain.report.ReportEntry;
import eu.appbucket.rothar.core.domain.report.ReportEntryFilter;

public interface ReportService {
	void saveReportEntry(ReportEntry reportData);
	List<ReportEntry> findReportEntries(ReportEntryFilter filter);
}
