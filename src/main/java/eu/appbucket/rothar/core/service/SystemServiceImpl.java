package eu.appbucket.rothar.core.service;

import org.springframework.stereotype.Service;

@Service
public class SystemServiceImpl implements SystemService {

	public int getSystemUserId() {
		return Integer.valueOf(System.getProperty("USER_ID"));
	}
}
