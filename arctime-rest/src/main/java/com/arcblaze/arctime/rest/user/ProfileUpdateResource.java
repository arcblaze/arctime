package com.arcblaze.arctime.rest.user;

import java.util.Collections;

import javax.servlet.ServletContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arcblaze.arctime.db.DaoFactory;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.DatabaseUniqueConstraintException;
import com.arcblaze.arctime.db.dao.EmployeeDao;
import com.arcblaze.arctime.model.Employee;
import com.arcblaze.arctime.model.Password;
import com.arcblaze.arctime.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for saving profile changes on behalf of a user.
 */
@Path("/user/profile")
public class ProfileUpdateResource extends BaseResource {
	private final static Logger log = LoggerFactory
			.getLogger(ProfileUpdateResource.class);

	@Context
	private ServletContext servletContext;

	@XmlRootElement
	static class ProfileUpdate {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String title = "Profile Updated";

		@XmlElement
		public final String msg = "Your profile information has been updated "
				+ "successfully.";
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param firstName
	 *            the updated first name for the user
	 * @param lastName
	 *            the updated last name for the user
	 * @param suffix
	 *            the updated name suffix for the user
	 * @param login
	 *            the updated user login value
	 * @param email
	 *            the updated email address for the user
	 * @param password
	 *            the updated password for the user
	 */
	protected void validateParams(String firstName, String lastName,
			String suffix, String login, String email, String password) {
		if (StringUtils.isBlank(firstName))
			throw badRequest("The firstName parameter must be specified.");
		if (StringUtils.isBlank(lastName))
			throw badRequest("The lastName parameter must be specified.");
		if (StringUtils.isBlank(login))
			throw badRequest("The login parameter must be specified.");
		if (StringUtils.isBlank(email))
			throw badRequest("The email parameter must be specified.");

		// NOTE: The suffix and password values can be blank.

		// NOTE: No attempt at validating the email address is intentional.
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param firstName
	 *            the updated first name for the user
	 * @param lastName
	 *            the updated last name for the user
	 * @param suffix
	 *            the updated name suffix for the user
	 * @param login
	 *            the updated user login value
	 * @param email
	 *            the updated email address for the user
	 * @param password
	 *            the updated password for the user
	 * 
	 * @return the profile update response
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public ProfileUpdate update(@Context SecurityContext security,
			@FormParam("firstName") String firstName,
			@FormParam("lastName") String lastName,
			@FormParam("suffix") String suffix,
			@FormParam("login") String login, @FormParam("email") String email,
			@FormParam("password") String password) {
		log.debug("Profile update request");
		try (Timer.Context timer = getTimer(this.servletContext,
				"/user/profile")) {

			validateParams(firstName, lastName, suffix, login, email, password);

			Employee currentUser = (Employee) security.getUserPrincipal();
			EmployeeDao dao = DaoFactory.getEmployeeDao();
			Employee employee = dao.get(currentUser.getCompanyId(),
					currentUser.getId());
			log.debug("  Found employee: {}", employee);

			if (employee == null)
				throw notFound("The current employee was not found.");

			employee.setFirstName(firstName);
			employee.setLastName(lastName);
			employee.setSuffix(suffix);
			employee.setLogin(login);
			employee.setEmail(email);
			employee.setDivision(currentUser.getDivision());
			employee.setPersonnelType(currentUser.getPersonnelType());
			employee.setActive(currentUser.isActive());
			log.debug("  Modified employee: {}", employee);

			dao.update(currentUser.getCompanyId(),
					Collections.singleton(employee));

			if (StringUtils.isNotBlank(password)) {
				log.debug("  Updating user password");
				String hashedPass = new Password().hash(password);
				dao.setPassword(currentUser.getId(), hashedPass);
				log.debug("  Password updated successfully");
			}

			return new ProfileUpdate();
		} catch (DatabaseUniqueConstraintException alreadyExists) {
			throw badRequest("The specified login or email already exists.");
		} catch (DatabaseException dbException) {
			throw dbError(dbException);
		}
	}
}
