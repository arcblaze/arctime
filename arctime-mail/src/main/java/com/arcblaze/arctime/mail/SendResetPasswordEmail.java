package com.arcblaze.arctime.mail;

import java.util.Collections;

import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;

import com.arcblaze.arctime.model.Employee;

/**
 * Responsible for sending emails to user accounts when they request a password
 * reset.
 */
public class SendResetPasswordEmail {
	/**
	 * This method is responsible for sending the email when a user forgets
	 * their password.
	 * 
	 * @param employee
	 *            the {@link Employee} to whom the email will be sent
	 * 
	 * @param newPassword
	 *            the new password value to be sent to the employee
	 * 
	 * @throws MessagingException
	 *             if there is a problem sending the email
	 */
	public static void send(Employee employee, String newPassword)
			throws MessagingException {
		if (employee == null)
			throw new IllegalArgumentException("Invalid null employee");
		if (StringUtils.isBlank(employee.getEmail()))
			throw new IllegalArgumentException("Invalid blank employee email");
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

		MailSender.send(message, subject, Collections.singleton(employee));
	}
}
