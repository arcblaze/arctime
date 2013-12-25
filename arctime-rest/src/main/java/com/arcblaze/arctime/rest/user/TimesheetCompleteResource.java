package com.arcblaze.arctime.rest.user;

import static com.arcblaze.arctime.model.Enrichment.BILLS;
import static com.arcblaze.arctime.model.Enrichment.PAY_PERIODS;
import static com.arcblaze.arctime.model.Enrichment.TASKS;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
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
import com.arcblaze.arctime.db.dao.PayPeriodDao;
import com.arcblaze.arctime.db.dao.TimesheetDao;
import com.arcblaze.arctime.model.AuditLog;
import com.arcblaze.arctime.model.Enrichment;
import com.arcblaze.arctime.model.PayPeriod;
import com.arcblaze.arctime.model.Timesheet;
import com.arcblaze.arctime.model.User;
import com.arcblaze.arctime.model.util.HolidayConfigurationException;
import com.arcblaze.arctime.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for completing a timesheet.
 */
@Path("/user/timesheet/{id}/complete")
public class TimesheetCompleteResource extends BaseResource {
	private final static Logger log = LoggerFactory
			.getLogger(TimesheetCompleteResource.class);

	@Context
	private ServletContext servletContext;

	@XmlRootElement
	static class CompleteResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String msg = "The timesheet was completed successfully. "
				+ "Moved to the next pay period.";
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param id
	 *            the unique id of the timesheet to complete
	 * @param data
	 *            the updated timesheet data to save
	 * 
	 * @return the completed timesheet response
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public CompleteResponse complete(@Context SecurityContext security,
			@PathParam("id") Integer id, @FormParam("data") String data) {
		log.debug("Timesheet complete request");
		try (Timer.Context timer = getTimer(this.servletContext,
				"/user/timesheet/" + id + "/complete")) {
			Set<Enrichment> timesheetEnrichments = new LinkedHashSet<>(
					Arrays.asList(PAY_PERIODS, TASKS, BILLS));
			User currentUser = (User) security.getUserPrincipal();
			TimesheetDao dao = DaoFactory.getTimesheetDao();
			Timesheet timesheet = dao.get(currentUser.getCompanyId(), id,
					timesheetEnrichments);

			if (timesheet == null)
				throw badRequest("The requested timesheet could not be found");
			if (timesheet.getUserId() != currentUser.getId())
				throw forbidden(currentUser, "Unable to save timesheet data "
						+ "into a timesheet you do not own.");

			TimesheetSaveResource.saveTimesheet(timesheet, data);

			dao.complete(timesheet.getCompanyId(), true, timesheet.getId());
			DaoFactory.getAuditLogDao().add(
					timesheet.getCompanyId(),
					new AuditLog().setTimesheetId(timesheet.getId()).setLog(
							"Timesheet completed"));

			PayPeriod nextPayPeriod = timesheet.getPayPeriod().getNext();
			PayPeriodDao ppdao = DaoFactory.getPayPeriodDao();
			if (!ppdao.exists(timesheet.getCompanyId(),
					nextPayPeriod.getBegin())) {
				ppdao.add(timesheet.getCompanyId(), nextPayPeriod);
			}

			Timesheet next = new Timesheet();
			next.setCompanyId(currentUser.getCompanyId());
			next.setUserId(currentUser.getId());
			next.setBegin(nextPayPeriod.getBegin());
			dao.add(currentUser.getCompanyId(), next);
			log.debug("  Created next timesheet: {}", next);

			return new CompleteResponse();
		} catch (DatabaseException dbException) {
			throw dbError(dbException);
		} catch (HolidayConfigurationException badHoliday) {
			throw holidayError(badHoliday);
		}
	}
}
