package eu.appbucket.rothar.web.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import eu.appbucket.rothar.core.domain.user.RoleEntry;
import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.core.service.v1.UserService;

public class CustomAuthenticationProvider implements AuthenticationProvider {

	private UserService userService;
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		String login = authentication.getName();
		String password = authentication.getCredentials().toString();
		UserEntry user = findUserByEmail(login);
		if(userIsActivated(user) && passwordIsCorrect(user, password)) {
			List<GrantedAuthority> grantedAuths = convertUserRolesToGrantedAuthorities(user);
	        Authentication auth = new UsernamePasswordAuthenticationToken(login, password, grantedAuths);
			return auth;	
		} else {
			throw new BadCredentialsException("Password for the login: " + login + " is incorrect.");
		}
	}
	
	private UserEntry findUserByEmail(String email) {
		return userService.findUserByEmail(email);
	}
	
	private boolean userIsActivated(UserEntry user) {
		return user.isActivated();
	}
	
	private boolean passwordIsCorrect(UserEntry user, String password) {
		if(user.getPassword().equals(password)) {
			return true;
		}
		return false;
	}
	
	private List<GrantedAuthority> convertUserRolesToGrantedAuthorities(UserEntry user) {
		List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
		for(RoleEntry role: user.getRoles()) {
			grantedAuths.add(
					new SimpleGrantedAuthority(role.getName()));
		}
		return grantedAuths;
	}
	
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}
