package eu.appbucket.rothar.web.domain.user;

import eu.appbucket.rothar.core.domain.user.UserEntry;

public class UserData {
	
	private String name;
	private String email;
	private String password;
	private Integer userId;
	
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String passwrod) {
		this.password = passwrod;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public static UserData fromUserEntry(UserEntry entry) {
		UserData data = new UserData();
		data.setEmail(entry.getEmail());
		data.setName(entry.getName());
		data.setUserId(entry.getUserId());
		return data;
	}
}
