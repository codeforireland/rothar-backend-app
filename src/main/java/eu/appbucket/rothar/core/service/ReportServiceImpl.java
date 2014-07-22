package eu.appbucket.rothar.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.appbucket.rothar.core.domain.report.ReportEntry;
import eu.appbucket.rothar.core.domain.report.ReportEntryFilter;
import eu.appbucket.rothar.core.persistence.ReportDao;

@Service
public class ReportServiceImpl implements ReportService {

	private ReportDao reportDao;
	
	@Autowired
	public void setReportDao(ReportDao reportDao) {
		this.reportDao = reportDao;
	}
	
	public void saveReportEntry(ReportEntry reportData) {
		reportDao.createNewEntry(reportData);
	}

	public List<ReportEntry> findReportEntries(ReportEntryFilter filter) {
		return reportDao.findEntries(filter);
	}

}	
