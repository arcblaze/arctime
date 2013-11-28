package com.arcblaze.arctime.rest.manager;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	/** This will be used to log messages. */
	private final static Logger log = LoggerFactory
			.getLogger(EmployeeResource.class);

	/**
	 * @param employeeId
	 *            the unique id of the employee to retrieve
	 * @param enrichments
	 *            indicates the additional data to be included in the returned
	 *            employee
	 * 
	 * @return the requested employee
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Path("{employeeId}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Employee specific(@PathParam("employeeId") Integer employeeId,
			@QueryParam("enrichments") Set<Enrichment> enrichments)
			throws DatabaseException {
		Integer companyId = 1; // TODO: Pull this out of the current user.
		EmployeeDao dao = DaoFactory.getEmployeeDao();
		Employee employee = dao.get(companyId, employeeId, enrichments);
		log.info("{}", employee);
		return employee;
	}

	/**
	 * @param enrichments
	 *            indicates the additional data to be included in the returned
	 *            employee
	 * 
	 * @return all of the available employees
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Set<Employee> all(
			@QueryParam("enrichments") Set<Enrichment> enrichments)
			throws DatabaseException {
		Integer companyId = 1; // TODO: Pull this out of the current user.
		EmployeeDao dao = DaoFactory.getEmployeeDao();
		Set<Employee> employees = dao.getAll(companyId, enrichments);
		log.info("{} employees", employees.size());
		return employees;
	}
}
