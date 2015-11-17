package eu.appbucket.rothar.core.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class SystemServiceImpl implements SystemService {

	public int getSystemUserId() {
		String userId = System.getenv("USER_ID");
        return Integer.valueOf(userId);
	}
}
