package eu.appbucket.rothar.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.appbucket.rothar.core.domain.asset.AssetEntry;
import eu.appbucket.rothar.core.domain.report.ReportEntry;
import eu.appbucket.rothar.core.domain.report.ReportListFilter;
import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.core.persistence.ReportDao;
import eu.appbucket.rothar.core.persistence.UserDao;
import eu.appbucket.rothar.core.service.v1.UserService;

@Service
public class ReportServiceImpl implements ReportService {

	private ReportDao reportDao;
	private UserService userService;
	
	@Autowired
	public void setReportDao(ReportDao reportDao) {
		this.reportDao = reportDao;
	}
	
	@Autowired
	@Qualifier("v1.userService")
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@Transactional
	public void saveReportEntry(ReportEntry report) {
		assertUserExists(report.getReporterId());
		reportDao.createNewEntry(report);
	}
	
	private void assertUserExists(Integer userId) {
		userService.findUserById(userId);
	}
	
	public List<ReportEntry> findReportEntries(ReportListFilter filter) {
		assertUserExists(filter.getUserId());
		return reportDao.findEntries(filter);
	}

	public void saveSystemReportEntry(ReportEntry report) {
		report = applySystemSettings(report);
		reportDao.createNewEntry(report);
	}
	
	private ReportEntry applySystemSettings(ReportEntry report) {
		int userId = Integer.valueOf(System.getProperty("USER_ID"));
		report.setReporterId(userId);
		return report;
	}
}	
