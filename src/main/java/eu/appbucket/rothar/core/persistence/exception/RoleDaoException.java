package eu.appbucket.rothar.core.persistence.exception;

@SuppressWarnings("serial")
public class RoleDaoException extends RuntimeException {
	
	public RoleDaoException(String s) {
		super(s);
	}

	public RoleDaoException(String s, Throwable throwable) {
		super(s, throwable);
	}
}
