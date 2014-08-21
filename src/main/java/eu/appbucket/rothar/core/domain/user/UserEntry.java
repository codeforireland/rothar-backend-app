package eu.appbucket.rothar.core.domain.user;

import java.util.Date;

import eu.appbucket.rothar.web.domain.user.UserData;


public class UserEntry {
	
	private Integer userId;
	private String email;
	private String login;
	private String password;
	private String name;
	private Date created;
	private boolean activated;
	private String activationCode;
	
	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public static UserEntry fromUserData(UserData data) {
		UserEntry entry = new UserEntry();
		entry.setEmail(data.getEmail());
		entry.setName(data.getName());
		return entry;
	}
}
