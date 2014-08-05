package eu.appbucket.rothar.core.persistence.exception;

@SuppressWarnings("serial")
public class AssetDaoException extends RuntimeException {
	
	public AssetDaoException(String s) {
		super(s);
	}

	public AssetDaoException(String s, Throwable throwable) {
		super(s, throwable);
	}
}
