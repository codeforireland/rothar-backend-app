package eu.appbucket.rothar.core.domain.email;

import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.core.service.exception.ServiceException;

public interface EmailService {
	
	/**
	 * Sends email with the activation link to the user.
	 * 
	 * The email will contain activation link and only after recipient receives email 
	 * and click activation code then user is becoming activate in the system 
	 * and can login.
	 *  
	 * @param user to be send email to with activation code
	 * 
	 * @throws ServiceException if there was problem with sending email
	 */
	void sendUserActivationEmail(UserEntry userToSendActinactionLink) throws ServiceException;
}
