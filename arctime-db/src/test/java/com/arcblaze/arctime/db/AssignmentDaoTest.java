package com.arcblaze.arctime.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.arcblaze.arctime.db.dao.AssignmentDao;
import com.arcblaze.arctime.db.util.TestDatabase;
import com.arcblaze.arctime.model.Assignment;
import com.arcblaze.arctime.model.Company;
import com.arcblaze.arctime.model.Task;
import com.arcblaze.arctime.model.User;

/**
 * Perform database integration testing.
 */
public class AssignmentDaoTest {
	/**
	 * Perform test setup activities.
	 * 
	 * @throws Exception
	 *             if there is a problem performing test initialization
	 */
	@BeforeClass
	public static void setup() throws Exception {
		TestDatabase.initialize();
	}

	/**
	 * Perform test cleanup activities.
	 */
	@AfterClass
	public static void cleanup() {
		DaoFactory.reset();
	}

	/**
	 * @throws DatabaseException
	 *             if there is a problem with the database
	 */
	@Test
	public void dbIntegrationTests() throws DatabaseException {
		Company company = new Company().setName("Company").setActive(true);
		DaoFactory.getCompanyDao().add(company);

		Task task = new Task();
		task.setCompanyId(company.getId());
		task.setDescription("Task");
		task.setJobCode("job code");
		task.setAdministrative(false);
		task.setActive(true);
		DaoFactory.getTaskDao().add(company.getId(), task);

		User user = new User();
		user.setLogin("user");
		user.setHashedPass("hashed");
		user.setEmail("email");
		user.setFirstName("first");
		user.setLastName("last");
		DaoFactory.getUserDao().add(company.getId(), user);

		AssignmentDao dao = DaoFactory.getAssignmentDao();
		Set<Assignment> assignments = dao.getForUser(company.getId(),
				user.getId(), null);
		assertNotNull(assignments);
		assertEquals(0, assignments.size());

		Assignment assignment = new Assignment();
		assignment.setCompanyId(company.getId());
		assignment.setTaskId(task.getId());
		assignment.setUserId(user.getId());
		assignment.setLaborCat("labor cat");
		assignment.setItemName("item name");
		assignment.setBegin(DateUtils.truncate(
				DateUtils.addDays(new Date(), -10), Calendar.DATE));
		assignment.setEnd(DateUtils.truncate(DateUtils.addDays(new Date(), 10),
				Calendar.DATE));

		dao.add(company.getId(), assignment);
		assertNotNull(assignment.getId());
		assertEquals(company.getId(), assignment.getCompanyId());

		assignments = dao.getForUser(company.getId(), user.getId(), null);
		assertNotNull(assignments);
		assertEquals(1, assignments.size());
		assertTrue(assignments.contains(assignment));

		assignments = dao.getForUser(company.getId(), user.getId(), new Date());
		assertNotNull(assignments);
		assertEquals(1, assignments.size());
		assertTrue(assignments.contains(assignment));

		assignments = dao.getForUser(company.getId(), user.getId(),
				DateUtils.addDays(new Date(), -15));
		assertNotNull(assignments);
		assertEquals(0, assignments.size());

		assignments = dao.getForUser(company.getId(), user.getId(),
				DateUtils.addDays(new Date(), 15));
		assertNotNull(assignments);
		assertEquals(0, assignments.size());

		Assignment getAssignment = dao.get(company.getId(), assignment.getId());
		assertEquals(assignment, getAssignment);

		assignment.setItemName("New Item Name");
		dao.update(company.getId(), assignment);
		getAssignment = dao.get(company.getId(), assignment.getId());
		assertEquals(assignment, getAssignment);

		dao.delete(company.getId(), assignment.getId());
		getAssignment = dao.get(company.getId(), assignment.getId());
		assertNull(getAssignment);

		assignments = dao.getForUser(company.getId(), user.getId(), null);
		assertNotNull(assignments);
		assertEquals(0, assignments.size());
	}
}
