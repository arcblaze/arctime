package com.arcblaze.arctime.mail;

import java.util.Collections;

import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;

import com.arcblaze.arctime.model.User;

/**
 * Responsible for sending emails to user accounts when they request a password
 * reset.
 */
public class SendResetPasswordEmail {
	/**
	 * This method is responsible for sending the email when a user forgets
	 * their password.
	 * 
	 * @param user
	 *            the {@link User} to whom the email will be sent
	 * @param newPassword
	 *            the new password value to be sent to the user
	 * 
	 * @throws MessagingException
	 *             if there is a problem sending the email
	 */
	public void send(User user, String newPassword) throws MessagingException {
		if (user == null)
			throw new IllegalArgumentException("Invalid null user");
		if (StringUtils.isBlank(user.getEmail()))
			throw new IllegalArgumentException("Invalid blank user email");
		if (StringUtils.isBlank(newPassword))
			throw new IllegalArgumentException("Invalid blank password");

		String message = "\nNotice:\n\n"
				+ "The ARCTIME web site received a password change request\n"
				+ "for your account.  If you have not requested a password\n"
				+ "change from the ARCTIME web site by indicating that you\n"
				+ "forgot your password, please inform your security point of\n"
				+ "contact about the possible security breach attempt.\n\n"
				+ "Otherwise, here is your new password:\n    " + newPassword
				+ "\n\n"
				+ "If you have any problems or questions, contact your\n"
				+ "supervisor or ARCTIME support.\n\n";
		String subject = "Password Change Notification";

		MailSender.send(message, subject, Collections.singleton(user));
	}
}
