package eu.appbucket.rothar.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import eu.appbucket.rothar.core.domain.user.UserEntry;
import eu.appbucket.rothar.core.service.exception.ServiceException;

@Service
public class EmailServiceImpl implements EmailService {

	private MailSender mailSender;

	@Autowired
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendUserActivationEmail(UserEntry userToSendActinactionLink) throws ServiceException {
		SimpleMailMessage activationEmail = new SimpleMailMessage();
		activationEmail.setSubject("Activation email from Rothar");
		activationEmail.setTo(userToSendActinactionLink.getEmail());
		activationEmail.setText(builActivationEmaildMessage(userToSendActinactionLink));
		try {
			mailSender.send(activationEmail);	
		} catch (MailException mailException) {
			throw new ServiceException("Can't send activation email for user with email address: " 
					+ userToSendActinactionLink.getEmail(), mailException);
		}
	}

	private String builActivationEmaildMessage(UserEntry userToSendActinactionLink) throws ServiceException {
		StringBuilder emailMessage = new StringBuilder();
		String applicationUrl = System.getProperty("APP_URL");
		emailMessage.append("Dear " + userToSendActinactionLink.getName() + ",\n\n");
		emailMessage.append("Thank you for registering to Rothar. \n\n");
		emailMessage.append("Please click link bellow to activate your account: \n");
		emailMessage.append(
				applicationUrl + "/v1/users/" + userToSendActinactionLink.getUserId() 
				+ "/code/" + userToSendActinactionLink.getActivationCode() + "\n\n");
		emailMessage.append("Regards, \n");
		emailMessage.append("Rothar team. \n");
		return emailMessage.toString();
	}
}
