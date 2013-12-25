package com.arcblaze.arctime.rest.user;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
import com.arcblaze.arctime.db.dao.TimesheetDao;
import com.arcblaze.arctime.model.AuditLog;
import com.arcblaze.arctime.model.Timesheet;
import com.arcblaze.arctime.model.User;
import com.arcblaze.arctime.model.util.HolidayConfigurationException;
import com.arcblaze.arctime.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for fixing a timesheet.
 */
@Path("/user/timesheet/{id}/fix")
public class TimesheetFixResource extends BaseResource {
	private final static Logger log = LoggerFactory
			.getLogger(TimesheetFixResource.class);

	@Context
	private ServletContext servletContext;

	@XmlRootElement
	static class FixResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String msg = "The timesheet was reopened successfully.";
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param id
	 *            the unique id of the timesheet to reopen
	 * 
	 * @return the fixed timesheet response
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public FixResponse fix(@Context SecurityContext security,
			@PathParam("id") Integer id) {
		log.debug("Timesheet fix request");
		try (Timer.Context timer = getTimer(this.servletContext,
				"/user/timesheet/" + id + "/fix")) {
			User currentUser = (User) security.getUserPrincipal();
			TimesheetDao dao = DaoFactory.getTimesheetDao();
			Timesheet timesheet = dao.get(currentUser.getCompanyId(), id);

			if (timesheet == null)
				throw badRequest("The requested timesheet could not be found");
			if (timesheet.getUserId() != currentUser.getId())
				throw forbidden(currentUser, "Unable to fix timesheet that "
						+ "you do not own.");

			dao.complete(timesheet.getCompanyId(), false, timesheet.getId());
			DaoFactory.getAuditLogDao().add(
					timesheet.getCompanyId(),
					new AuditLog().setTimesheetId(timesheet.getId()).setLog(
							"Timesheet reopened"));

			return new FixResponse();
		} catch (DatabaseException dbException) {
			throw dbError(dbException);
		} catch (HolidayConfigurationException badHoliday) {
			throw holidayError(badHoliday);
		}
	}
}
