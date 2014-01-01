package com.arcblaze.arctime.rest.login;

import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arcblaze.arctime.config.Property;
import com.arcblaze.arctime.db.DaoFactory;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.UserDao;
import com.arcblaze.arctime.mail.SendResetPasswordEmail;
import com.arcblaze.arctime.model.Password;
import com.arcblaze.arctime.model.User;
import com.arcblaze.arctime.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for performing password resets for a user.
 */
@Path("/login/reset")
public class ResetPasswordResource extends BaseResource {
	private final static Logger log = LoggerFactory
			.getLogger(ResetPasswordResource.class);

	@Context
	private ServletContext servletContext;

	@XmlRootElement
	static class PasswordReset {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String title = "Password Reset";

		@XmlElement
		public final String msg = "An email with a new random password was "
				+ "sent to the email address associated with your account. "
				+ "Please check your email for your updated login info. "
				+ "If you have any problems, please contact the web site "
				+ "administrator (" + Property.EMAIL_SYSTEM_ADMIN.getString()
				+ ")";
	}

	/**
	 * Used to send password-reset emails to the user.
	 */
	private final SendResetPasswordEmail emailSender;

	/**
	 * Used to generate new random passwords for users.
	 */
	private final Password password;

	/**
	 * Default constructor.
	 */
	public ResetPasswordResource() {
		this.emailSender = new SendResetPasswordEmail();
		this.password = new Password();
	}

	/**
	 * @param emailSender
	 *            the object responsible for sending emails
	 * @param password
	 *            the object used to generate new random passwords for users
	 */
	public ResetPasswordResource(SendResetPasswordEmail emailSender,
			Password password) {
		if (emailSender == null)
			throw new IllegalArgumentException("Invalid null email sender");

		this.emailSender = emailSender;
		this.password = password;
	}

	/**
	 * @param login
	 *            the user login to use when resetting the password
	 * 
	 * @return the password reset response
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public PasswordReset reset(@FormParam("j_username") String login) {
		log.debug("Password reset request");
		try (Timer.Context timer = getTimer(this.servletContext, "/login/reset")) {

			if (StringUtils.isBlank(login))
				throw badRequest("The j_username parameter must be specified.");

			UserDao dao = DaoFactory.getUserDao();
			User user = dao.getLogin(login);
			log.debug("  Found user: {}", user);

			if (user == null)
				throw notFound("A user with the specified login was not found.");

			String salt = this.password.random(10);
			String newPassword = this.password.random();
			String hashedPass = this.password.hash(newPassword, salt);
			log.debug("  New password will be: {}", newPassword);
			log.debug("  Hashed password will be: {}", hashedPass);

			dao.setPassword(user.getId(), hashedPass, salt);
			log.debug("  Password updated successfully");

			try {
				this.emailSender.send(user, newPassword);
			} catch (MessagingException mailException) {
				log.debug("  Failed to send email, setting password back");
				dao.setPassword(user.getId(), user.getHashedPass(),
						user.getSalt());
				throw mailError(mailException);
			}

			return new PasswordReset();
		} catch (DatabaseException dbException) {
			throw dbError(dbException);
		}
	}
}
