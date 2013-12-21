package com.arcblaze.arctime.mail;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.arcblaze.arctime.config.Property;
import com.arcblaze.arctime.model.User;

/**
 * Responsible for sending emails based on the system configuration.
 */
class MailSender {
	protected static Properties getConfiguration() {
		Properties props = System.getProperties();
		props.setProperty("mail.smtp.host", Property.EMAIL_SERVER.getString());
		props.setProperty("mail.smtp.port",
				Property.EMAIL_SERVER_PORT.getString());

		if (Property.EMAIL_USE_SSL.getBoolean()) {
			props.setProperty("mail.smtp.ssl.enable", "true");
			props.setProperty("mail.smtp.starttls.enable", "true");
			props.setProperty("mail.smtp.ssl.protocols", "SSLv3 TLSv1");
		}

		if (Property.EMAIL_AUTHENTICATE_FIRST.getBoolean())
			props.setProperty("mail.smtp.auth", "true");

		return props;
	}

	/**
	 * @param msg
	 *            the content within the body of the email
	 * @param subject
	 *            the subject to include in the email
	 * @param users
	 *            the users to which the message will be sent
	 * 
	 * @throws MessagingException
	 *             if there is a problem sending the messages
	 */
	public static void send(String msg, String subject, User... users)
			throws MessagingException {
		if (users != null)
			send(msg, subject, new LinkedHashSet<>(Arrays.asList(users)));
	}

	/**
	 * @param msg
	 *            the content within the body of the email
	 * @param subject
	 *            the subject to include in the email
	 * @param users
	 *            the users to which the message will be sent
	 * 
	 * @throws MessagingException
	 *             if there is a problem sending the messages
	 */
	public static void send(String msg, String subject, Set<User> users)
			throws MessagingException {
		Properties props = getConfiguration();

		Session session = Session.getDefaultInstance(props, null);
		MimeMessage message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(
					Property.EMAIL_AUTHENTICATE_USER.getString(), "ARCTIME"));
			for (User user : users)
				message.addRecipient(
						Message.RecipientType.TO,
						new InternetAddress(user.getEmail(), user.getFullName()));
			message.setSubject(subject);
			message.setText(msg);
		} catch (UnsupportedEncodingException badEncoding) {
			throw new MessagingException(
					"Invalid encoding when sending message.", badEncoding);
		}

		if (Property.EMAIL_AUTHENTICATE_FIRST.getBoolean()) {
			Transport transport = session.getTransport("smtp");
			transport.connect(Property.EMAIL_SERVER.getString(),
					Property.EMAIL_AUTHENTICATE_USER.getString(),
					Property.EMAIL_AUTHENTICATE_PASSWORD.getString());
			message.saveChanges();
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} else
			Transport.send(message);
	}
}
