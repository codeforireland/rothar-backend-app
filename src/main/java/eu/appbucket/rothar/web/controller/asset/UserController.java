package eu.appbucket.rothar.web.controller.asset;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.appbucket.rothar.core.domain.email.EmailService;
import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.core.service.UserService;
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
@Controller
public class UserController {
	
	private static final Logger LOGGER = Logger.getLogger(UserController.class);	
	private UserService userService;
	
	
	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@RequestMapping(value = "v1/users", method = RequestMethod.POST)
	@ResponseBody
	public void registerUser(@RequestBody UserData userToBeCreated) {
		LOGGER.info("registerUser");
		UserEntry userToBeRegistered = UserEntry.fromUserData(userToBeCreated);
		userService.createUser(userToBeRegistered);
	}
	
	@RequestMapping(value = "v1/users/{userIdToActivate}/code/{activationCode}", method = RequestMethod.GET)
	@ResponseBody
	public void activateUser(
			@PathVariable Integer userIdToActivate,
			@PathVariable String activationCode) {
		LOGGER.info("activateUser");
		UserEntry userToActivate = new UserEntry();
		userToActivate.setActivationCode(activationCode);
		userToActivate.setUserId(userIdToActivate);
		userService.activateUser(userToActivate);
	}
	
	@RequestMapping(value = "v1/users/{userIdToFind}", method = RequestMethod.GET)
	@ResponseBody
	public UserData findUser(@PathVariable Integer userIdToFind) {
		LOGGER.info("findUser");
		UserEntry foundUserEntry = userService.findUserById(userIdToFind);
		UserData foundUserData = UserData.fromUserEntry(foundUserEntry);
		return foundUserData;
	}
	
	@RequestMapping(value = "v1/users/{userIdToUpdate}", method = RequestMethod.PUT)
	@ResponseBody
	public void updateUser(@RequestBody UserData userDataToBeUpdated) {
		LOGGER.info("updateUser");
		UserEntry userEntryToBeUpdated = UserEntry.fromUserData(userDataToBeUpdated);
		userService.updateUser(userEntryToBeUpdated);
	}
}