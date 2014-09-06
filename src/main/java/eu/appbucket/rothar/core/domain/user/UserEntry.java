package eu.appbucket.rothar.core.domain.user;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

import eu.appbucket.rothar.web.domain.user.UserData;


public class UserEntry {
	
	private Integer userId;
	private String email;
	private String password;
	private String name;
	private Date created;
	private boolean activated;
	private String activationCode;
	private Collection<RoleEntry> roles = new HashSet<RoleEntry>();
	
	public Collection<RoleEntry> getRoles() {
		Collection<RoleEntry> roles = new HashSet<RoleEntry>(this.roles);
		return roles;
	}
	
	public void addRole(RoleEntry role) {
		roles.add(role);
	}
	
	public void addRoles(Collection<RoleEntry> roles) {
		for(RoleEntry role: roles) {
			roles.add(role);
		}
	}
	
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