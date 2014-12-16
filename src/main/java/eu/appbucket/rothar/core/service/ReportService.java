package eu.appbucket.rothar.core.service;

import java.util.List;

import eu.appbucket.rothar.core.domain.report.ReportEntry;
import eu.appbucket.rothar.core.domain.report.ReportListFilter;

public interface ReportService {
	void saveReportEntry(ReportEntry reportData);
	void saveSystemReportEntry(ReportEntry reportData);
	List<ReportEntry> findReportEntries(ReportListFilter filter);
}
