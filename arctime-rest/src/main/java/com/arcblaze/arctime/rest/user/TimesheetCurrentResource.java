package com.arcblaze.arctime.rest.user;

import java.util.Arrays;
import java.util.LinkedHashSet;
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
import com.arcblaze.arctime.db.DatabaseUniqueConstraintException;
import com.arcblaze.arctime.db.dao.TimesheetDao;
import com.arcblaze.arctime.model.Enrichment;
import com.arcblaze.arctime.model.PayPeriod;
import com.arcblaze.arctime.model.Timesheet;
import com.arcblaze.arctime.model.User;
import com.arcblaze.arctime.model.util.HolidayConfigurationException;
import com.arcblaze.arctime.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for retrieving the user's current timesheet.
 */
@Path("/user/timesheet/current")
public class TimesheetCurrentResource extends BaseResource {
	private final static Logger log = LoggerFactory
			.getLogger(TimesheetCurrentResource.class);

	@Context
	private ServletContext servletContext;

	@XmlRootElement
	static class TimesheetResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String msg = "Your timesheet was retrieved successfully.";

		@XmlElement
		public Timesheet timesheet = null;
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * 
	 * @return the current timesheet response
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public TimesheetResponse current(@Context SecurityContext security) {
		log.debug("Current timesheet request");
		try (Timer.Context timer = getTimer(this.servletContext,
				"/user/timesheet/current")) {
			Set<Enrichment> timesheetEnrichments = new LinkedHashSet<>(
					Arrays.asList(Enrichment.PAY_PERIODS,
							Enrichment.AUDIT_LOGS, Enrichment.HOLIDAYS,
							Enrichment.USERS, Enrichment.TASKS));

			User currentUser = (User) security.getUserPrincipal();
			TimesheetDao dao = DaoFactory.getTimesheetDao();
			Timesheet timesheet = dao.getLatestForUser(
					currentUser.getCompanyId(), currentUser.getId(),
					timesheetEnrichments);
			log.debug("  Found timesheet: {}", timesheet);

			if (timesheet == null) {
				log.debug("  Timesheet not found, creating it...");
				PayPeriod payPeriod = DaoFactory.getPayPeriodDao().getCurrent(
						currentUser.getCompanyId());
				if (payPeriod == null)
					throw notFound("A pay period could not be found.");

				timesheet = new Timesheet();
				timesheet.setCompanyId(currentUser.getCompanyId());
				timesheet.setUserId(currentUser.getId());
				timesheet.setBegin(payPeriod.getBegin());
				dao.add(currentUser.getCompanyId(), timesheet);
				log.debug("  Created timesheet: {}", timesheet);

				// Retrieve an enriched version of the newly created timesheet.
				timesheet = dao.getLatestForUser(currentUser.getCompanyId(),
						currentUser.getId(), timesheetEnrichments);
			}

			TimesheetResponse response = new TimesheetResponse();
			response.timesheet = timesheet;
			return response;
		} catch (DatabaseUniqueConstraintException alreadyExists) {
			throw badRequest("The specified login or email already exists.");
		} catch (DatabaseException dbException) {
			throw dbError(dbException);
		} catch (HolidayConfigurationException badHoliday) {
			throw holidayError(badHoliday);
		}
	}
}
