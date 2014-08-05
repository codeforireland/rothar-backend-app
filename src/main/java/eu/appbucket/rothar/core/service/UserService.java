package eu.appbucket.rothar.core.service;

import org.springframework.stereotype.Service;

import eu.appbucket.rothar.core.domain.user.UserEntry;

@Service
public interface UserService {
	
	UserEntry findUser(int userId);
}
