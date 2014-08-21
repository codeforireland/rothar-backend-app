package eu.appbucket.rothar.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.appbucket.rothar.core.domain.report.ReportEntry;
import eu.appbucket.rothar.core.domain.report.ReportEntryFilter;
import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.core.persistence.ReportDao;
import eu.appbucket.rothar.core.persistence.UserDao;

@Service
public class ReportServiceImpl implements ReportService {

	private ReportDao reportDao;
	private UserService userService;
	
	@Autowired
	public void setReportDao(ReportDao reportDao) {
		this.reportDao = reportDao;
	}
	
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	public void saveReportEntry(ReportEntry reportData) {
		assertUserExists(reportData.getReporterId());
		reportDao.createNewEntry(reportData);
	}

	private void assertUserExists(Integer userId) {
		userService.findUser(userId);
	}
	
	public List<ReportEntry> findReportEntries(ReportEntryFilter filter) {
		assertUserExists(filter.getUserId());
		return reportDao.findEntries(filter);
	}

}	
