package com.arcblaze.arctime.rest.user;

import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arcblaze.arctime.db.DaoFactory;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.EmployeeDao;
import com.arcblaze.arctime.model.Employee;
import com.arcblaze.arctime.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for retrieving the supervisors for the current user.
 */
@Path("/user/supervisors")
public class SupervisorsResource extends BaseResource {
	private final static Logger log = LoggerFactory
			.getLogger(SupervisorsResource.class);

	@Context
	private ServletContext servletContext;

	@XmlRootElement
	static class EmployeeSupervisors {
		@XmlElement
		public Set<Employee> supervisors;
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * 
	 * @return the employee supervisors response
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public EmployeeSupervisors get(@Context SecurityContext security) {
		log.debug("User supervisor request");
		try (Timer.Context timer = getTimer(this.servletContext,
				"/user/supervisors")) {

			Employee currentUser = (Employee) security.getUserPrincipal();
			EmployeeDao dao = DaoFactory.getEmployeeDao();
			Set<Employee> supervisors = dao.getSupervisors(
					currentUser.getCompanyId(), currentUser.getId(), null);
			log.debug("  Found supervisors: {}", supervisors.size());

			EmployeeSupervisors response = new EmployeeSupervisors();
			response.supervisors = supervisors;
			return response;
		} catch (DatabaseException dbException) {
			throw dbError(dbException);
		}
	}
}
