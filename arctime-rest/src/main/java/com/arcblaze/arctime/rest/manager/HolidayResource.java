package com.arcblaze.arctime.rest.manager;

import java.util.Set;

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

/**
 * The REST end-point for managing company holidays.
 */
@Path("/manager/holiday")
public class HolidayResource {
	/** Security information associated with the web request. */
	@Context
	private SecurityContext security = null;

	/**
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
	public Holiday one(@PathParam("holidayId") Integer holidayId)
			throws DatabaseException, HolidayConfigurationException {
		Employee currentUser = (Employee) this.security.getUserPrincipal();
		HolidayDao dao = DaoFactory.getHolidayDao();
		return dao.get(currentUser.getCompanyId(), holidayId);
	}

	/**
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
	public Set<Holiday> all() throws DatabaseException,
			HolidayConfigurationException {
		Employee currentUser = (Employee) this.security.getUserPrincipal();
		HolidayDao dao = DaoFactory.getHolidayDao();
		return dao.getAll(currentUser.getCompanyId());
	}
}
