package eu.appbucket.rothar.core.persistence;

import java.util.Date;
import java.util.List;

import eu.appbucket.rothar.core.domain.report.ReportEntry;
import eu.appbucket.rothar.core.domain.report.ReportListFilter;
import eu.appbucket.rothar.core.persistence.exception.ReportDaoException;

public interface ReportDao {
	
	void createNewEntry(ReportEntry reportData) throws ReportDaoException;
	
	List<ReportEntry> findEntriesByFilter(ReportListFilter filter) throws ReportDaoException;
	
	List<ReportEntry> findEntriesByFilterAndDate(ReportListFilter filter, Date from, Date to) throws ReportDaoException;
}
