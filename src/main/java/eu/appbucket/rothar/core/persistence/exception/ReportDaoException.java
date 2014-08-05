package eu.appbucket.rothar.core.persistence.exception;

@SuppressWarnings("serial")
public class ReportDaoException extends RuntimeException {
	
	public ReportDaoException(String s) {
		super(s);
	}

	public ReportDaoException(String s, Throwable throwable) {
		super(s, throwable);
	}
}
