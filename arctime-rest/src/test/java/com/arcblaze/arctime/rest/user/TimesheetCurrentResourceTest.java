package com.arcblaze.arctime.rest.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Date;
import java.util.Set;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.arcblaze.arctime.db.DaoFactory;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.TimesheetDao;
import com.arcblaze.arctime.db.util.TestDatabase;
import com.arcblaze.arctime.model.Assignment;
import com.arcblaze.arctime.model.AuditLog;
import com.arcblaze.arctime.model.Bill;
import com.arcblaze.arctime.model.Company;
import com.arcblaze.arctime.model.Holiday;
import com.arcblaze.arctime.model.PayPeriod;
import com.arcblaze.arctime.model.PayPeriodType;
import com.arcblaze.arctime.model.Task;
import com.arcblaze.arctime.model.Timesheet;
import com.arcblaze.arctime.model.User;
import com.arcblaze.arctime.model.util.HolidayConfigurationException;
import com.arcblaze.arctime.rest.user.TimesheetCurrentResource.TimesheetResponse;

/**
 * Perform testing of the current timesheet retrieval capabilities.
 */
public class TimesheetCurrentResourceTest {
	private final static String[] FMT = { "yyyyMMdd HHmmss", "yyyyMMdd" };

	/**
	 * Perform test setup activities.
	 * 
	 * @throws Exception
	 *             if there is a problem performing test initialization
	 */
	@Before
	public void setup() throws Exception {
		TestDatabase.initialize();
	}

	/**
	 * Perform test cleanup activities.
	 */
	@After
	public void cleanup() {
		DaoFactory.reset();
	}

	/**
	 * Test how the resource responds with no pay periods available.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 */
	@Test(expected = NotFoundException.class)
	public void testNoPayPeriodsAvailable() throws DatabaseException {
		Company company = new Company().setName("company").setActive(true);
		DaoFactory.getCompanyDao().add(company);

		User user = new User();
		user.setLogin("user");
		user.setHashedPass("hashed");
		user.setSalt("salt");
		user.setEmail("email@whatever.com");
		user.setFirstName("first");
		user.setLastName("last");
		user.setActive(true);
		DaoFactory.getUserDao().add(company.getId(), user);

		SecurityContext security = Mockito.mock(SecurityContext.class);
		Mockito.when(security.getUserPrincipal()).thenReturn(user);

		TimesheetCurrentResource resource = new TimesheetCurrentResource();
		resource.current(security);
	}

	/**
	 * Test how the resource responds with no timesheet available, but a pay
	 * period available for automatic creation.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 * @throws ParseException
	 *             if there is a problem parsing dates
	 */
	@Test
	public void testNoTimesheetsAvailable() throws DatabaseException,
			ParseException {
		Company company = new Company().setName("company").setActive(true);
		DaoFactory.getCompanyDao().add(company);

		PayPeriod payPeriod = new PayPeriod().setCompanyId(company.getId())
				.setType(PayPeriodType.WEEKLY)
				.setBegin(DateUtils.parseDate("20140101", FMT))
				.setEnd(DateUtils.parseDate("20140107", FMT));
		DaoFactory.getPayPeriodDao().add(company.getId(), payPeriod);

		User user = new User();
		user.setLogin("user");
		user.setHashedPass("hashed");
		user.setSalt("salt");
		user.setEmail("email@whatever.com");
		user.setFirstName("first");
		user.setLastName("last");
		user.setActive(true);
		DaoFactory.getUserDao().add(company.getId(), user);

		SecurityContext security = Mockito.mock(SecurityContext.class);
		Mockito.when(security.getUserPrincipal()).thenReturn(user);

		TimesheetCurrentResource resource = new TimesheetCurrentResource();
		TimesheetResponse response = resource.current(security);

		Timesheet ts = response.timesheet;
		assertNotNull(ts);
		assertEquals(company.getId(), ts.getCompanyId());
		assertEquals(user, ts.getUser());
		assertEquals(user.getId(), ts.getUserId());
		assertNotNull(ts.getPayPeriod());
		assertTrue(ts.getPayPeriod().contains(new Date()));
		assertNotNull(ts.getBegin());
		assertEquals(ts.getBegin(), ts.getPayPeriod().getBegin());
		assertFalse(ts.isCompleted());
		assertFalse(ts.isApproved());
		assertFalse(ts.isVerified());
		assertFalse(ts.isExported());
		assertNull(ts.getApprover());
		assertNull(ts.getApproverId());
		assertNull(ts.getVerifier());
		assertNull(ts.getVerifierId());
		assertNull(ts.getExporter());
		assertNull(ts.getExporterId());
		assertNotNull(ts.getAuditLogs());
		assertTrue(ts.getAuditLogs().isEmpty());
		assertNotNull(ts.getHolidays());
		assertTrue(ts.getHolidays().isEmpty());
		assertNotNull(ts.getTasks());
		assertTrue(ts.getTasks().isEmpty());
	}

	/**
	 * Test how the resource responds when enriching a timesheet with holiday
	 * information.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 * @throws ParseException
	 *             if there is a problem parsing dates
	 * @throws HolidayConfigurationException
	 *             if there is a holiday configuration issue
	 */
	@Test
	public void testTimesheetWithHolidays() throws DatabaseException,
			ParseException, HolidayConfigurationException {
		Company company = new Company().setName("company").setActive(true);
		DaoFactory.getCompanyDao().add(company);

		PayPeriod payPeriod = new PayPeriod().setCompanyId(company.getId())
				.setType(PayPeriodType.WEEKLY).setBegin(new Date());
		payPeriod.setEnd(DateUtils.addDays(payPeriod.getBegin(), 7));
		DaoFactory.getPayPeriodDao().add(company.getId(), payPeriod);

		User user = new User();
		user.setLogin("user");
		user.setHashedPass("hashed");
		user.setSalt("salt");
		user.setEmail("email@whatever.com");
		user.setFirstName("first");
		user.setLastName("last");
		user.setActive(true);
		DaoFactory.getUserDao().add(company.getId(), user);

		SecurityContext security = Mockito.mock(SecurityContext.class);
		Mockito.when(security.getUserPrincipal()).thenReturn(user);

		Holiday holiday = new Holiday()
				.setCompanyId(company.getId())
				.setDescription("holiday")
				.setConfig(
						DateFormatUtils.format(payPeriod.getBegin(), "MMM d"));
		DaoFactory.getHolidayDao().add(company.getId(), holiday);

		TimesheetCurrentResource resource = new TimesheetCurrentResource();
		TimesheetResponse response = resource.current(security);

		Timesheet ts = response.timesheet;
		assertNotNull(ts);
		assertEquals(company.getId(), ts.getCompanyId());
		assertEquals(user, ts.getUser());
		assertEquals(user.getId(), ts.getUserId());
		assertNotNull(ts.getPayPeriod());
		assertTrue(ts.getPayPeriod().contains(new Date()));
		assertNotNull(ts.getBegin());
		assertEquals(ts.getBegin(), ts.getPayPeriod().getBegin());
		assertFalse(ts.isCompleted());
		assertFalse(ts.isApproved());
		assertFalse(ts.isVerified());
		assertFalse(ts.isExported());
		assertNull(ts.getApprover());
		assertNull(ts.getApproverId());
		assertNull(ts.getVerifier());
		assertNull(ts.getVerifierId());
		assertNull(ts.getExporter());
		assertNull(ts.getExporterId());
		assertNotNull(ts.getAuditLogs());
		assertTrue(ts.getAuditLogs().isEmpty());
		assertNotNull(ts.getHolidays());
		assertEquals(1, ts.getHolidays().size());
		assertEquals(holiday, ts.getHolidays().iterator().next());
		assertNotNull(ts.getTasks());
		assertTrue(ts.getTasks().isEmpty());
	}

	/**
	 * Test how the resource responds when enriching a timesheet with user
	 * information.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 * @throws ParseException
	 *             if there is a problem parsing dates
	 * @throws HolidayConfigurationException
	 *             if there is a holiday parsing problem
	 */
	@Test
	public void testTimesheetWithUsers() throws DatabaseException,
			ParseException, HolidayConfigurationException {
		Company company = new Company().setName("company").setActive(true);
		DaoFactory.getCompanyDao().add(company);

		PayPeriod payPeriod = new PayPeriod().setCompanyId(company.getId())
				.setType(PayPeriodType.WEEKLY).setBegin(new Date());
		payPeriod.setEnd(DateUtils.addDays(payPeriod.getBegin(), 7));
		DaoFactory.getPayPeriodDao().add(company.getId(), payPeriod);

		User user = new User();
		user.setLogin("user");
		user.setHashedPass("hashed");
		user.setSalt("salt");
		user.setEmail("email@whatever.com");
		user.setFirstName("first");
		user.setLastName("last");
		user.setActive(true);

		User approver = new User();
		approver.setLogin("approver");
		approver.setHashedPass("hashed");
		approver.setSalt("salt");
		approver.setEmail("approver@whatever.com");
		approver.setFirstName("first");
		approver.setLastName("last");
		approver.setActive(true);

		User verifier = new User();
		verifier.setLogin("verifier");
		verifier.setHashedPass("hashed");
		verifier.setSalt("salt");
		verifier.setEmail("verifier@whatever.com");
		verifier.setFirstName("first");
		verifier.setLastName("last");
		verifier.setActive(true);

		User exporter = new User();
		exporter.setLogin("exporter");
		exporter.setHashedPass("hashed");
		exporter.setSalt("salt");
		exporter.setEmail("exporter@whatever.com");
		exporter.setFirstName("first");
		exporter.setLastName("last");
		exporter.setActive(true);

		DaoFactory.getUserDao().add(company.getId(), user, approver, verifier,
				exporter);

		SecurityContext security = Mockito.mock(SecurityContext.class);
		Mockito.when(security.getUserPrincipal()).thenReturn(user);

		Timesheet timesheet = new Timesheet();
		timesheet.setCompanyId(company.getId());
		timesheet.setUserId(user.getId());
		timesheet.setBegin(payPeriod.getBegin());
		TimesheetDao dao = DaoFactory.getTimesheetDao();
		dao.add(company.getId(), timesheet);

		dao.complete(company.getId(), true, timesheet.getId());
		dao.approve(company.getId(), true, approver, timesheet.getId());
		dao.verify(company.getId(), true, verifier, timesheet.getId());
		dao.export(company.getId(), true, exporter, timesheet.getId());

		TimesheetCurrentResource resource = new TimesheetCurrentResource();
		TimesheetResponse response = resource.current(security);

		Timesheet ts = response.timesheet;
		assertNotNull(ts);
		assertEquals(company.getId(), ts.getCompanyId());
		assertEquals(user, ts.getUser());
		assertEquals(user.getId(), ts.getUserId());
		assertNotNull(ts.getPayPeriod());
		assertTrue(ts.getPayPeriod().contains(new Date()));
		assertNotNull(ts.getBegin());
		assertEquals(ts.getBegin(), ts.getPayPeriod().getBegin());
		assertTrue(ts.isCompleted());
		assertTrue(ts.isApproved());
		assertTrue(ts.isVerified());
		assertTrue(ts.isExported());
		assertEquals(approver, ts.getApprover());
		assertEquals(verifier, ts.getVerifier());
		assertEquals(exporter, ts.getExporter());
		assertNotNull(ts.getAuditLogs());
		assertTrue(ts.getAuditLogs().isEmpty());
		assertNotNull(ts.getHolidays());
		assertTrue(ts.getHolidays().isEmpty());
		assertNotNull(ts.getTasks());
		assertTrue(ts.getTasks().isEmpty());
	}

	/**
	 * Test how the resource responds when enriching a timesheet with audit log
	 * information.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 * @throws ParseException
	 *             if there is a problem parsing dates
	 * @throws HolidayConfigurationException
	 *             if there is a holiday parsing problem
	 */
	@Test
	public void testTimesheetWithAuditLogs() throws DatabaseException,
			ParseException, HolidayConfigurationException {
		Company company = new Company().setName("company").setActive(true);
		DaoFactory.getCompanyDao().add(company);

		PayPeriod payPeriod = new PayPeriod().setCompanyId(company.getId())
				.setType(PayPeriodType.WEEKLY).setBegin(new Date());
		payPeriod.setEnd(DateUtils.addDays(payPeriod.getBegin(), 7));
		DaoFactory.getPayPeriodDao().add(company.getId(), payPeriod);

		User user = new User();
		user.setLogin("user");
		user.setHashedPass("hashed");
		user.setSalt("salt");
		user.setEmail("email@whatever.com");
		user.setFirstName("first");
		user.setLastName("last");
		user.setActive(true);
		DaoFactory.getUserDao().add(company.getId(), user);

		SecurityContext security = Mockito.mock(SecurityContext.class);
		Mockito.when(security.getUserPrincipal()).thenReturn(user);

		Timesheet timesheet = new Timesheet();
		timesheet.setCompanyId(company.getId());
		timesheet.setUserId(user.getId());
		timesheet.setBegin(payPeriod.getBegin());
		TimesheetDao dao = DaoFactory.getTimesheetDao();
		dao.add(company.getId(), timesheet);

		AuditLog al1 = new AuditLog().setCompanyId(company.getId())
				.setTimesheetId(timesheet.getId()).setLog("Log message 1");
		AuditLog al2 = new AuditLog().setCompanyId(company.getId())
				.setTimesheetId(timesheet.getId()).setLog("Log message 2");
		DaoFactory.getAuditLogDao().add(company.getId(), al1, al2);

		TimesheetCurrentResource resource = new TimesheetCurrentResource();
		TimesheetResponse response = resource.current(security);

		Timesheet ts = response.timesheet;
		assertNotNull(ts);
		assertEquals(company.getId(), ts.getCompanyId());
		assertEquals(user, ts.getUser());
		assertEquals(user.getId(), ts.getUserId());
		assertNotNull(ts.getPayPeriod());
		assertTrue(ts.getPayPeriod().contains(new Date()));
		assertNotNull(ts.getBegin());
		assertEquals(ts.getBegin(), ts.getPayPeriod().getBegin());
		assertFalse(ts.isCompleted());
		assertFalse(ts.isApproved());
		assertFalse(ts.isVerified());
		assertFalse(ts.isExported());
		assertNotNull(ts.getAuditLogs());
		assertEquals(2, ts.getAuditLogs().size());
		assertTrue(ts.getAuditLogs().contains(al1));
		assertTrue(ts.getAuditLogs().contains(al2));
		assertNotNull(ts.getHolidays());
		assertTrue(ts.getHolidays().isEmpty());
		assertNotNull(ts.getTasks());
		assertTrue(ts.getTasks().isEmpty());
	}

	/**
	 * Test how the resource responds when enriching a timesheet with task
	 * information.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 * @throws ParseException
	 *             if there is a problem parsing dates
	 * @throws HolidayConfigurationException
	 *             if there is a holiday parsing problem
	 */
	@Test
	public void testTimesheetWithTasks() throws DatabaseException,
			ParseException, HolidayConfigurationException {
		Company company = new Company().setName("company").setActive(true);
		DaoFactory.getCompanyDao().add(company);

		PayPeriod payPeriod = new PayPeriod().setCompanyId(company.getId())
				.setType(PayPeriodType.WEEKLY).setBegin(new Date());
		payPeriod.setEnd(DateUtils.addDays(payPeriod.getBegin(), 7));
		DaoFactory.getPayPeriodDao().add(company.getId(), payPeriod);

		User user = new User();
		user.setLogin("user");
		user.setHashedPass("hashed");
		user.setSalt("salt");
		user.setEmail("email@whatever.com");
		user.setFirstName("first");
		user.setLastName("last");
		user.setActive(true);
		DaoFactory.getUserDao().add(company.getId(), user);

		SecurityContext security = Mockito.mock(SecurityContext.class);
		Mockito.when(security.getUserPrincipal()).thenReturn(user);

		Timesheet timesheet = new Timesheet();
		timesheet.setCompanyId(company.getId());
		timesheet.setUserId(user.getId());
		timesheet.setBegin(payPeriod.getBegin());
		TimesheetDao dao = DaoFactory.getTimesheetDao();
		dao.add(company.getId(), timesheet);

		Task t1 = new Task().setCompanyId(company.getId())
				.setAdministrative(true).setDescription("Task 1")
				.setJobCode("Job Code 1").setActive(true);
		Task t2 = new Task().setCompanyId(company.getId())
				.setAdministrative(false).setDescription("Task 2")
				.setJobCode("Job Code 2").setActive(true);
		Task t3 = new Task().setCompanyId(company.getId())
				.setAdministrative(false).setDescription("Task 3")
				.setJobCode("Job Code 3").setActive(true);
		Task t4 = new Task().setCompanyId(company.getId())
				.setAdministrative(false).setDescription("Task 4")
				.setJobCode("Job Code 4").setActive(false);
		DaoFactory.getTaskDao().add(company.getId(), t1, t2, t3, t4);

		// This assignment includes the full pay period.
		Assignment a1 = new Assignment().setCompanyId(company.getId())
				.setUserId(user.getId()).setTaskId(t2.getId())
				.setItemName("Task 2:Item 1").setLaborCat("LCAT 1")
				.setBegin(DateUtils.addDays(payPeriod.getBegin(), -5))
				.setEnd(DateUtils.addDays(payPeriod.getEnd(), 5));

		// This assignment includes the beginning of the pay period.
		Assignment a2 = new Assignment().setCompanyId(company.getId())
				.setUserId(user.getId()).setTaskId(t2.getId())
				.setItemName("Task 2:Item 2").setLaborCat("LCAT 2")
				.setBegin(DateUtils.addDays(payPeriod.getBegin(), -5))
				.setEnd(DateUtils.addDays(payPeriod.getBegin(), 1));

		// This assignment includes the ending of the pay period.
		Assignment a3 = new Assignment().setCompanyId(company.getId())
				.setUserId(user.getId()).setTaskId(t2.getId())
				.setItemName("Task 2:Item 3").setLaborCat("LCAT 3")
				.setBegin(DateUtils.addDays(payPeriod.getEnd(), -1))
				.setEnd(DateUtils.addDays(payPeriod.getEnd(), 5));

		// This assignment is before the pay period.
		Assignment a4 = new Assignment().setCompanyId(company.getId())
				.setUserId(user.getId()).setTaskId(t3.getId())
				.setItemName("Task 3:Item 1").setLaborCat("LCAT 1")
				.setBegin(DateUtils.addDays(payPeriod.getBegin(), -10))
				.setEnd(DateUtils.addDays(payPeriod.getBegin(), -5));

		// This assignment is after the pay period.
		Assignment a5 = new Assignment().setCompanyId(company.getId())
				.setUserId(user.getId()).setTaskId(t3.getId())
				.setItemName("Task 3:Item 2").setLaborCat("LCAT 2")
				.setBegin(DateUtils.addDays(payPeriod.getEnd(), 5))
				.setEnd(DateUtils.addDays(payPeriod.getEnd(), 10));

		// This assignment includes the full pay period.
		Assignment a6 = new Assignment().setCompanyId(company.getId())
				.setUserId(user.getId()).setTaskId(t4.getId())
				.setItemName("Task 4:Item 1").setLaborCat("LCAT 1")
				.setBegin(DateUtils.addDays(payPeriod.getBegin(), -5))
				.setEnd(DateUtils.addDays(payPeriod.getEnd(), 5));

		DaoFactory.getAssignmentDao().add(company.getId(), a1, a2, a3, a4, a5,
				a6);

		// Bill against the admin task.
		Bill b1 = new Bill().setUserId(user.getId()).setTaskId(t1.getId())
				.setDay(payPeriod.getBegin()).setHours("8.0")
				.setTimestamp(new Date());

		// Bill against LCAT 1 on task 2
		Bill b2 = new Bill().setUserId(user.getId()).setTaskId(t2.getId())
				.setAssignmentId(a1.getId()).setDay(payPeriod.getBegin())
				.setHours("8.0").setTimestamp(new Date());

		// Bill against LCAT 2 on task 2
		Bill b3 = new Bill().setUserId(user.getId()).setTaskId(t2.getId())
				.setAssignmentId(a2.getId()).setDay(payPeriod.getBegin())
				.setHours("2.25").setTimestamp(new Date());

		// Bill against LCAT 3 on task 2, but outside the assignment dates
		Bill b4 = new Bill().setUserId(user.getId()).setTaskId(t2.getId())
				.setAssignmentId(a3.getId()).setDay(payPeriod.getBegin())
				.setHours("8.5").setTimestamp(new Date());

		// Bill against task 3, before the timesheet pay period
		Bill b5 = new Bill().setUserId(user.getId()).setTaskId(t3.getId())
				.setAssignmentId(a4.getId()).setDay(a4.getBegin())
				.setHours("3.5").setTimestamp(new Date());

		// Bill against task 3, after the timesheet pay period
		Bill b6 = new Bill().setUserId(user.getId()).setTaskId(t3.getId())
				.setAssignmentId(a5.getId()).setDay(a5.getBegin())
				.setHours("3.5").setTimestamp(new Date());

		// Bill against task 4, which is inactive
		Bill b7 = new Bill().setUserId(user.getId()).setTaskId(t4.getId())
				.setAssignmentId(a6.getId()).setDay(payPeriod.getEnd())
				.setHours("3.5").setTimestamp(new Date());

		DaoFactory.getBillDao().add(b1, b2, b3, b4, b5, b6, b7);

		TimesheetCurrentResource resource = new TimesheetCurrentResource();
		TimesheetResponse response = resource.current(security);

		Timesheet ts = response.timesheet;
		assertNotNull(ts);
		assertEquals(company.getId(), ts.getCompanyId());
		assertEquals(user, ts.getUser());
		assertEquals(user.getId(), ts.getUserId());
		assertNotNull(ts.getPayPeriod());
		assertTrue(ts.getPayPeriod().contains(new Date()));
		assertNotNull(ts.getBegin());
		assertEquals(ts.getBegin(), ts.getPayPeriod().getBegin());
		assertFalse(ts.isCompleted());
		assertFalse(ts.isApproved());
		assertFalse(ts.isVerified());
		assertFalse(ts.isExported());
		assertNotNull(ts.getAuditLogs());
		assertTrue(ts.getAuditLogs().isEmpty());
		assertNotNull(ts.getHolidays());
		assertTrue(ts.getHolidays().isEmpty());

		Set<Task> tasks = ts.getTasks();
		assertEquals(2, tasks.size());
		assertTrue(tasks.contains(t1));
		assertTrue(tasks.contains(t2));
		for (Task t : tasks) {
			if (t.getId() == 1) {
				assertTrue(t.getAssignments().isEmpty());
				assertEquals(1, t.getBills().size());
				assertEquals(b1, t.getBills().iterator().next());
			} else {
				assertEquals(3, t.getAssignments().size());
				assertTrue(t.getAssignments().contains(a1));
				assertTrue(t.getAssignments().contains(a2));
				assertTrue(t.getAssignments().contains(a3));

				for (Assignment a : t.getAssignments()) {
					if (a.getId() == a1.getId()) {
						assertEquals(1, a.getBills().size());
						assertEquals(b2, a.getBills().iterator().next());
					} else if (a.getId() == a2.getId()) {
						assertEquals(1, a.getBills().size());
						assertEquals(b3, a.getBills().iterator().next());
					} else if (a.getId() == a3.getId()) {
						assertTrue(a.getBills().isEmpty());
					}
				}
			}
		}
	}
}
