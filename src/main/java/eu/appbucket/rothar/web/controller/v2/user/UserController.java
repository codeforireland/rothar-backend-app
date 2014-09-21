package eu.appbucket.rothar.web.controller.v2.user;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.core.service.v2.UserService;
import eu.appbucket.rothar.web.domain.user.UserData;

/*
 * User controller responsible for creating, activating, logging in and updating the user.
 * 
 * Workflow of creating and activating the user: 
 * 1. When a user registers in this app, he is automatically marked as inactive.
 * 2. A verification code is generated and stored in the user account.
 * 3. An email with verification link is sent to the email address specified for the user.
 * 4. When the user opens the email and clicks the link he is activated.
 * 5. If the request is successful (e.g. the verification code matches the one in the database), 
 * the user is marked as activated.
 * 
 * Workflow of logging in the user:
 * 1. User provide email address and password.
 * 2. If user is active and his email address is matching with the stored password user is 
 * allowed to login into the system.
*/
@Controller(value="v2.userController")
public class UserController {
	
	private static final Logger LOGGER = Logger.getLogger(UserController.class);	
	private UserService userService;
	
	
	@Autowired
	@Qualifier("v2.userService")
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@RequestMapping(value = "v2/users", method = RequestMethod.POST)
	@ResponseBody
	public UserData createUser() {
		LOGGER.info("registerUser");
		UserEntry createdUser = userService.createUser();
		return UserData.fromUserEntry(createdUser);
	}
}