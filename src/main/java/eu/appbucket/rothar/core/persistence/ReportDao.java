package eu.appbucket.rothar.core.persistence;

import java.util.List;

import eu.appbucket.rothar.core.domain.report.ReportEntry;
import eu.appbucket.rothar.core.domain.report.ReportListFilter;
import eu.appbucket.rothar.core.persistence.exception.ReportDaoException;

public interface ReportDao {
	
	void createNewEntry(ReportEntry reportData) throws ReportDaoException;
	List<ReportEntry> findEntries(ReportListFilter filter) throws ReportDaoException;
}
