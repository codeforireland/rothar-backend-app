package eu.appbucket.rothar.core.persistence;

import java.util.List;

import eu.appbucket.rothar.core.domain.report.ReportEntry;
import eu.appbucket.rothar.core.domain.report.ReportEntryFilter;

public interface ReportDao {
	
	void createNewEntry(ReportEntry reportData);
	List<ReportEntry> findEntries(ReportEntryFilter filter);
}
