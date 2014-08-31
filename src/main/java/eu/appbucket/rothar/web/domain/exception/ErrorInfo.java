package eu.appbucket.rothar.web.domain.exception;

public class ErrorInfo {
	
	private String url;
	private String clientMessage;
	private String developerMessage;
	
	public void setDeveloperMessage(String developerMessage) {
		this.developerMessage = developerMessage;
	}
	
	public String getDeveloperMessage() {
		return developerMessage;
	}

	public void setClientMessage(String clientMessage) {
		this.clientMessage = clientMessage;
	}
	
	public String getClientMessage() {
		return clientMessage;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
