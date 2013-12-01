package com.arcblaze.arctime.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.arcblaze.arctime.db.dao.TaskDao;
import com.arcblaze.arctime.db.util.TestDatabase;
import com.arcblaze.arctime.model.Assignment;
import com.arcblaze.arctime.model.Company;
import com.arcblaze.arctime.model.Employee;
import com.arcblaze.arctime.model.PersonnelType;
import com.arcblaze.arctime.model.Task;

/**
 * Perform database integration testing.
 */
public class TaskDaoTest {
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
	 */
	@Test
	public void dbIntegrationTests() throws DatabaseException {
		Company company = new Company().setName("Company").setActive(true);
		DaoFactory.getCompanyDao().add(Collections.singleton(company));

		TaskDao dao = DaoFactory.getTaskDao();
		Set<Task> tasks = dao.getAll(company.getId());
		assertNotNull(tasks);
		assertEquals(0, tasks.size());

		Task task = new Task();
		task.setDescription("My Task");
		task.setJobCode("job code");
		task.setAdministrative(false);
		task.setActive(true);

		dao.add(company.getId(), Collections.singleton(task));
		assertNotNull(task.getId());
		assertEquals(company.getId(), task.getCompanyId());

		Set<Task> getAllTasks = dao.getAll(company.getId());
		assertNotNull(getAllTasks);
		assertEquals(1, getAllTasks.size());

		Task getTask = dao.get(company.getId(), task.getId());
		assertEquals(task, getTask);

		task.setDescription("New Description");
		dao.update(company.getId(), Collections.singleton(task));
		getTask = dao.get(company.getId(), task.getId());
		assertEquals(task, getTask);

		dao.delete(company.getId(), Collections.singleton(task.getId()));
		getTask = dao.get(company.getId(), task.getId());
		assertNull(getTask);

		getAllTasks = dao.getAll(company.getId());
		assertNotNull(getAllTasks);
		assertEquals(0, getAllTasks.size());

		// Test assignments

		Task adminTask = new Task();
		adminTask.setDescription("Admin Task");
		adminTask.setJobCode("admin");
		adminTask.setAdministrative(true);
		adminTask.setActive(true);

		dao.add(company.getId(), Arrays.asList(adminTask, task));

		Employee employee = new Employee();
		employee.setLogin("employee");
		employee.setHashedPass("hashed");
		employee.setEmail("email");
		employee.setFirstName("first");
		employee.setLastName("last");
		employee.setSuffix("suffix");
		employee.setDivision("division");
		employee.setPersonnelType(PersonnelType.EMPLOYEE);
		DaoFactory.getEmployeeDao().add(company.getId(),
				Collections.singleton(employee));

		Assignment assignment = new Assignment();
		assignment.setCompanyId(company.getId());
		assignment.setTaskId(task.getId());
		assignment.setEmployeeId(employee.getId());
		assignment.setLaborCat("labor cat");
		assignment.setItemName("item name");
		assignment.setBegin(DateUtils.truncate(
				DateUtils.addDays(new Date(), -10), Calendar.DATE));
		assignment.setEnd(DateUtils.truncate(DateUtils.addDays(new Date(), 10),
				Calendar.DATE));
		DaoFactory.getAssignmentDao().add(company.getId(),
				Collections.singleton(assignment));

		Set<Task> assignedTasks = dao.getForEmployee(company.getId(),
				employee.getId(), null, true);
		assertNotNull(assignedTasks);
		assertEquals(2, assignedTasks.size());
		assertTrue(assignedTasks.contains(task));
		assertTrue(assignedTasks.contains(adminTask));

		assignedTasks = dao.getForEmployee(company.getId(), employee.getId(),
				null, false);
		assertNotNull(assignedTasks);
		assertEquals(1, assignedTasks.size());
		assertTrue(assignedTasks.contains(task));

		assignedTasks = dao.getForEmployee(company.getId(), employee.getId(),
				new Date(), true);
		assertNotNull(assignedTasks);
		assertEquals(2, assignedTasks.size());
		assertTrue(assignedTasks.contains(task));
		assertTrue(assignedTasks.contains(adminTask));

		assignedTasks = dao.getForEmployee(company.getId(), employee.getId(),
				new Date(), false);
		assertNotNull(assignedTasks);
		assertEquals(1, assignedTasks.size());
		assertTrue(assignedTasks.contains(task));

		assignedTasks = dao.getForEmployee(company.getId(), employee.getId(),
				DateUtils.addDays(new Date(), -15), true);
		assertNotNull(assignedTasks);
		assertEquals(1, assignedTasks.size());
		assertTrue(assignedTasks.contains(adminTask));

		assignedTasks = dao.getForEmployee(company.getId(), employee.getId(),
				DateUtils.addDays(new Date(), -15), false);
		assertNotNull(assignedTasks);
		assertEquals(0, assignedTasks.size());

		assignedTasks = dao.getForEmployee(company.getId(), employee.getId(),
				DateUtils.addDays(new Date(), 15), true);
		assertNotNull(assignedTasks);
		assertEquals(1, assignedTasks.size());
		assertTrue(assignedTasks.contains(adminTask));

		assignedTasks = dao.getForEmployee(company.getId(), employee.getId(),
				DateUtils.addDays(new Date(), 15), false);
		assertNotNull(assignedTasks);
		assertEquals(0, assignedTasks.size());
	}
}
