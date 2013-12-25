package com.arcblaze.arctime.rest.user;

import static com.arcblaze.arctime.model.Enrichment.BILLS;
import static com.arcblaze.arctime.model.Enrichment.PAY_PERIODS;
import static com.arcblaze.arctime.model.Enrichment.TASKS;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

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

import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arcblaze.arctime.db.DaoFactory;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.AuditLogDao;
import com.arcblaze.arctime.db.dao.BillDao;
import com.arcblaze.arctime.db.dao.TimesheetDao;
import com.arcblaze.arctime.model.Assignment;
import com.arcblaze.arctime.model.AuditLog;
import com.arcblaze.arctime.model.Bill;
import com.arcblaze.arctime.model.Enrichment;
import com.arcblaze.arctime.model.Task;
import com.arcblaze.arctime.model.Timesheet;
import com.arcblaze.arctime.model.User;
import com.arcblaze.arctime.model.util.HolidayConfigurationException;
import com.arcblaze.arctime.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for saving hours into a timesheet.
 */
@Path("/user/timesheet/{id}/save")
public class TimesheetSaveResource extends BaseResource {
	private final static Logger log = LoggerFactory
			.getLogger(TimesheetSaveResource.class);

	@Context
	private ServletContext servletContext;

	@XmlRootElement
	static class SaveResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String msg = "The timesheet information was saved successfully.";
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param id
	 *            the unique id of the timesheet into which this data will be
	 *            saved
	 * @param data
	 *            the updated timesheet data to save
	 * 
	 * @return the timesheet save response
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public SaveResponse save(@Context SecurityContext security,
			@PathParam("id") Integer id, @FormParam("data") String data) {
		log.debug("Timesheet save request");
		try (Timer.Context timer = getTimer(this.servletContext,
				"/user/timesheet/" + id + "/save")) {
			Set<Enrichment> timesheetEnrichments = new LinkedHashSet<>(
					Arrays.asList(PAY_PERIODS, TASKS, BILLS));

			log.debug("Retrieving current timesheet");
			User currentUser = (User) security.getUserPrincipal();
			TimesheetDao dao = DaoFactory.getTimesheetDao();
			Timesheet timesheet = dao.get(currentUser.getCompanyId(), id,
					timesheetEnrichments);

			if (timesheet == null)
				throw badRequest("The requested timesheet could not be found");
			if (timesheet.getUserId() != currentUser.getId())
				throw forbidden(currentUser, "Unable to save timesheet data "
						+ "into a timesheet you do not own.");

			log.debug("Saving timesheet data");
			saveTimesheet(timesheet, data);

			return new SaveResponse();
		} catch (DatabaseException dbException) {
			throw dbError(dbException);
		} catch (HolidayConfigurationException badHoliday) {
			throw holidayError(badHoliday);
		}
	}

	protected static void saveTimesheet(Timesheet timesheet, String data)
			throws DatabaseException {
		BillDao billDao = DaoFactory.getBillDao();
		AuditLogDao auditLogDao = DaoFactory.getAuditLogDao();

		// Keep track of the processed bills sent from the client.
		Set<String> processed = new TreeSet<String>();

		Set<Bill> bills = Bill.fromTimesheetData(data);
		for (Bill bill : bills) {
			processed.add(bill.getUniqueId());

			Task task = timesheet.getTask(bill.getTaskId());
			Assignment assignment = bill.hasAssignmentId() ? task
					.getAssignment(bill.getAssignmentId()) : null;
			Bill existing = assignment == null ? task.getBill(bill.getDay())
					: assignment.getBill(bill.getDay());

			if (existing != null
					&& !existing.getHours().equals(bill.getHours())) {
				bill.setId(existing.getId());
				bill.setUserId(existing.getUserId());
				billDao.update(bill);

				auditLogDao.add(
						timesheet.getCompanyId(),
						getUpdatedLog(timesheet, task, assignment, existing,
								bill));
			} else if (existing == null) {
				bill.setUserId(timesheet.getUserId());
				billDao.add(bill);

				auditLogDao.add(timesheet.getCompanyId(),
						getAddedLog(timesheet, task, assignment, bill));
			}
		}

		Set<Integer> toDelete = new TreeSet<>();
		for (Task task : timesheet.getTasks()) {
			for (Bill bill : task.getBills()) {
				if (processed.contains(bill.getUniqueId()))
					continue;

				toDelete.add(bill.getId());
				auditLogDao.add(timesheet.getCompanyId(),
						getDeletedLog(timesheet, task, null, bill));
			}
			for (Assignment assignment : task.getAssignments()) {
				for (Bill bill : assignment.getBills()) {
					if (processed.contains(bill.getUniqueId()))
						continue;

					toDelete.add(bill.getId());
					auditLogDao.add(timesheet.getCompanyId(),
							getDeletedLog(timesheet, task, assignment, bill));
				}
			}
		}

		if (!toDelete.isEmpty())
			billDao.delete(toDelete);
	}

	/**
	 * A utility method for generating the audit log associated with changing
	 * hours associated with a bill.
	 * 
	 * @param timesheet
	 *            the timesheet being modified
	 * @param task
	 *            the task to which the bills apply
	 * @param assignment
	 *            the assignment to which the bills apply
	 * @param existing
	 *            the existing bill
	 * @param updated
	 *            the updated bill
	 * 
	 * @return an appropriate log message describing the changes
	 */
	private static AuditLog getUpdatedLog(Timesheet timesheet, Task task,
			Assignment assignment, Bill existing, Bill updated) {
		StringBuilder log = new StringBuilder();
		log.append("Hours for task ");
		log.append(task.getDescription());
		log.append(" ");
		if (assignment != null) {
			log.append("(LCAT: ");
			log.append(assignment.getLaborCat());
			log.append(") ");
		}
		log.append("on ");
		log.append(DateFormatUtils.format(updated.getDay(), "yyyy-MM-dd"));
		log.append(" changed from ");
		log.append(existing.getHours());
		log.append(" to ");
		log.append(updated.getHours());
		log.append(".");
		if (updated.hasReason()) {
			log.append(" The user-specified reason: ");
			log.append(updated.getReason());
		}

		return new AuditLog().setLog(log.toString())
				.setCompanyId(task.getCompanyId())
				.setTimesheetId(timesheet.getId());
	}

	/**
	 * A utility method for generating the audit log associated with adding
	 * hours to a timesheet.
	 * 
	 * @param timesheet
	 *            the timesheet being modified
	 * @param task
	 *            the task to which the bills apply
	 * @param assignment
	 *            the assignment to which the bills apply
	 * @param bill
	 *            the newly added bill
	 * 
	 * @return an appropriate log message describing the changes
	 */
	private static AuditLog getAddedLog(Timesheet timesheet, Task task,
			Assignment assignment, Bill bill) {
		StringBuilder log = new StringBuilder();
		log.append("Added ");
		log.append(bill.getHours());
		log.append(" hours for task ");
		log.append(task.getDescription());
		log.append(" ");
		if (assignment != null) {
			log.append("(LCAT: ");
			log.append(assignment.getLaborCat());
			log.append(") ");
		}
		log.append("on ");
		log.append(DateFormatUtils.format(bill.getDay(), "yyyy-MM-dd"));

		return new AuditLog().setLog(log.toString())
				.setCompanyId(task.getCompanyId())
				.setTimesheetId(timesheet.getId());
	}

	/**
	 * A utility method for generating the audit log associated with deleting
	 * hours associated with a bill.
	 * 
	 * @param timesheet
	 *            the timesheet being modified
	 * @param task
	 *            the task to which the bills apply
	 * @param assignment
	 *            the assignment to which the bills apply
	 * @param existing
	 *            the existing bill
	 * 
	 * @return an appropriate log message describing the changes
	 */
	private static AuditLog getDeletedLog(Timesheet timesheet, Task task,
			Assignment assignment, Bill existing) {
		StringBuilder log = new StringBuilder();
		log.append("Hours for task ");
		log.append(task.getDescription());
		log.append(" ");
		if (assignment != null) {
			log.append("(LCAT: ");
			log.append(assignment.getLaborCat());
			log.append(") ");
		}
		log.append("on ");
		log.append(DateFormatUtils.format(existing.getDay(), "yyyy-MM-dd"));
		log.append(" changed from ");
		log.append(existing.getHours());
		log.append(" to 0.00.");

		return new AuditLog().setLog(log.toString())
				.setCompanyId(task.getCompanyId())
				.setTimesheetId(timesheet.getId());
	}
}
