package com.arcblaze.arctime.rest.manager;

import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import com.arcblaze.arctime.db.DaoFactory;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.HolidayDao;
import com.arcblaze.arctime.model.Employee;
import com.arcblaze.arctime.model.Holiday;
import com.arcblaze.arctime.model.util.HolidayConfigurationException;
import com.arcblaze.arctime.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for managing company holidays.
 */
@Path("/manager/holiday")
public class HolidayResource extends BaseResource {
	@Context
	private ServletContext servletContext;

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param holidayId
	 *            the unique id of the holiday to retrieve
	 * 
	 * @return the requested holiday (if in the same company as the current
	 *         user)
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing the holiday configuration
	 *             information
	 */
	@GET
	@Path("{holidayId}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Holiday one(@Context SecurityContext security,
			@PathParam("holidayId") Integer holidayId)
			throws DatabaseException, HolidayConfigurationException {
		try (Timer.Context timer = getTimer(this.servletContext,
				"/manager/holiday/{holidayId}")) {
			Employee currentUser = (Employee) security.getUserPrincipal();
			HolidayDao dao = DaoFactory.getHolidayDao();
			return dao.get(currentUser.getCompanyId(), holidayId);
		}
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * @return all of the available holidays in the same company as the current
	 *         user
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing the holiday configuration
	 *             information
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Set<Holiday> all(@Context SecurityContext security)
			throws DatabaseException, HolidayConfigurationException {
		try (Timer.Context timer = getTimer(this.servletContext,
				"/manager/holiday")) {
			Employee currentUser = (Employee) security.getUserPrincipal();
			HolidayDao dao = DaoFactory.getHolidayDao();
			return dao.getAll(currentUser.getCompanyId());
		}
	}
}
