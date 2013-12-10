package com.arcblaze.arctime.rest.login;

import java.util.Random;

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
import com.arcblaze.arctime.db.dao.EmployeeDao;
import com.arcblaze.arctime.model.Employee;
import com.arcblaze.arctime.model.Password;
import com.arcblaze.arctime.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for performing password resets for a user.
 */
@Path("/login/forgot")
public class ForgotPasswordResource extends BaseResource {
	private final static Logger log = LoggerFactory
			.getLogger(ForgotPasswordResource.class);

	@Context
	private ServletContext servletContext;

	@XmlRootElement
	static class PasswordReset {
		@XmlElement
		public String title = "Password Reset";

		@XmlElement
		public String msg = "An email with a new random password was sent "
				+ "to the email address associated with your account. "
				+ "Please check your email for your updated login info. "
				+ "If you have any problems, please contact the web site "
				+ "administrator (" + Property.SYSTEM_ADMIN_EMAIL + ")";
	}

	/**
	 * @param login
	 *            the user login to use when resetting the password
	 * 
	 * @return the password reset response
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public PasswordReset reset(@FormParam("j_username") String login)
			throws DatabaseException {
		log.debug("Password reset request");
		try (Timer.Context timer = getTimer(this.servletContext,
				"/login/forgot")) {

			if (StringUtils.isBlank(login))
				throw badRequest("The j_username parameter must be specified.");

			EmployeeDao dao = DaoFactory.getEmployeeDao();
			Employee employee = dao.getLogin(login);
			log.debug("  Found employee: {}", employee);

			if (employee == null)
				throw notFound("A user with the specified login was not found.");

			String newPassword = generatePassword();
			String hashedPass = Password.hash(newPassword);
			log.debug("  New password will be: {}", newPassword);
			log.debug("  Hashed password will be: {}", hashedPass);

			return new PasswordReset();
		}
	}

	/**
	 * @return a randomly generated password
	 */
	protected String generatePassword() {
		final String vowels = "aeuAEU";
		final String numbers = "23456789";
		final String consonants = "bdghjmnpqrstvzBDGHJLMNPQRSTVWXZ";

		Random random = new Random();
		StringBuilder password = new StringBuilder();
		for (int i = 0; i < 14; i++) {
			int r = random.nextInt(3);
			if (r == 0)
				password.append(consonants.charAt(random.nextInt(consonants
						.length())));
			else if (r == 1)
				password.append(numbers.charAt(random.nextInt(numbers.length())));
			else
				password.append(vowels.charAt(random.nextInt(vowels.length())));
		}
		return password.toString();
	}
}
