package com.arcblaze.arctime.rest.manager;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import com.arcblaze.arctime.db.DaoFactory;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.EmployeeDao;
import com.arcblaze.arctime.model.Employee;
import com.arcblaze.arctime.model.Enrichment;

/**
 * The REST end-point for managing employees.
 */
@Path("/manager/employee")
public class EmployeeResource {
	/**
	 * @param security
	 *            the security information associated with the request
	 * @param employeeId
	 *            the unique id of the employee to retrieve
	 * @param enrichments
	 *            indicates the additional data to be included in the returned
	 *            employee
	 * 
	 * @return the requested employee (if in the same company as the current
	 *         user)
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Path("{employeeId}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Employee one(@Context SecurityContext security,
			@PathParam("employeeId") Integer employeeId,
			@QueryParam("enrichments") Set<Enrichment> enrichments)
			throws DatabaseException {
		Employee currentUser = (Employee) security.getUserPrincipal();
		EmployeeDao dao = DaoFactory.getEmployeeDao();
		return dao.get(currentUser.getCompanyId(), employeeId, enrichments);
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param enrichments
	 *            indicates the additional data to be included in the returned
	 *            employees
	 * 
	 * @return all of the available employees in the same company as the current
	 *         user
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Set<Employee> all(@Context SecurityContext security,
			@QueryParam("enrichments") Set<Enrichment> enrichments)
			throws DatabaseException {
		Employee currentUser = (Employee) security.getUserPrincipal();
		EmployeeDao dao = DaoFactory.getEmployeeDao();
		return dao.getAll(currentUser.getCompanyId(), enrichments);
	}
}
