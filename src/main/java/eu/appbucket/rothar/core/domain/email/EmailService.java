package eu.appbucket.rothar.core.domain.email;

import eu.appbucket.rothar.core.domain.user.UserEntry;

public interface EmailService {
	
	/**
	 * Sends email with the activation link to the user.
	 * 
	 * The email will contain activation link and only after recipient receives email 
	 * and click activation code then user is becoming activate in the system 
	 * and can login.
	 *  
	 * @param user to be send email to with activation code
	 */
	void sendUserActivationEmail(UserEntry userToSendActinactionLink);
	
	
	/** 
	 * Sends email with password to the user.
	 * 
	 * The email will contain password which will be used by the user to login to the system.
	 * 
	 * @param user
	 */
	void sendUserPasswordEmail(UserEntry userToSendPassword);
}
