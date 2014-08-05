package eu.appbucket.rothar.core.persistence.exception;

@SuppressWarnings("serial")
public class UserDaoException extends RuntimeException {
	
	public UserDaoException(String s) {
		super(s);
	}

	public UserDaoException(String s, Throwable throwable) {
		super(s, throwable);
	}
}
