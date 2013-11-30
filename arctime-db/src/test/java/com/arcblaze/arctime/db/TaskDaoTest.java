package com.arcblaze.arctime.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.arcblaze.arctime.db.dao.TaskDao;
import com.arcblaze.arctime.db.util.TestDatabase;
import com.arcblaze.arctime.model.Company;
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
	}
}
