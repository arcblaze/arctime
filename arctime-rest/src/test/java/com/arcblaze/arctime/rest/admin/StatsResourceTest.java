package com.arcblaze.arctime.rest.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.arcblaze.arctime.db.DaoFactory;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.UserDao;
import com.arcblaze.arctime.db.util.TestDatabase;
import com.arcblaze.arctime.model.Company;
import com.arcblaze.arctime.model.Transaction;
import com.arcblaze.arctime.model.TransactionType;
import com.arcblaze.arctime.model.User;
import com.arcblaze.arctime.rest.admin.SystemStatsResource.Stats;
import com.arcblaze.arctime.rest.admin.SystemStatsResource.SystemStat;

/**
 * Perform testing of the system statistics.
 */
public class StatsResourceTest {
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
	 * Test how the resource responds with no updated password specified.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 */
	@Test
	public void testNoData() throws DatabaseException {
		SystemStatsResource resource = new SystemStatsResource();
		Stats stats = resource.getStats();

		assertNotNull(stats);
		assertEquals(4, stats.statList.size());

		Iterator<SystemStat> iter = stats.statList.iterator();

		SystemStat stat = iter.next();
		assertEquals("Revenue YTD", stat.name);
		assertEquals("$0.00", stat.value);

		stat = iter.next();
		assertEquals("Revenue Year", stat.name);
		assertEquals("$0.00", stat.value);

		stat = iter.next();
		assertEquals("Active Users", stat.name);
		assertEquals("0", stat.value);

		stat = iter.next();
		assertEquals("Active Companies", stat.name);
		assertEquals("0", stat.value);
	}

	/**
	 * Test how the resource responds with a blank suffix value.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 */
	@Test
	public void testStatsAvailable() throws DatabaseException {
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

		UserDao userDao = DaoFactory.getUserDao();
		userDao.add(company.getId(), user);

		Transaction tx1 = new Transaction();
		tx1.setCompanyId(company.getId());
		tx1.setUserId(user.getId());
		tx1.setTimestamp(new Date());
		tx1.setTransactionType(TransactionType.PAYMENT);
		tx1.setDescription("payment");
		tx1.setAmount("50.00");
		tx1.setNotes("payment notes");

		Transaction tx2 = new Transaction();
		tx2.setCompanyId(company.getId());
		tx2.setUserId(user.getId());
		tx2.setTimestamp(new Date());
		tx2.setTransactionType(TransactionType.REFUND);
		tx2.setDescription("refund");
		tx2.setAmount("-10.00");
		tx2.setNotes("refund notes");

		DaoFactory.getTransactionDao().add(tx1, tx2);

		SystemStatsResource resource = new SystemStatsResource();
		Stats stats = resource.getStats();

		assertNotNull(stats);
		assertEquals(4, stats.statList.size());

		Iterator<SystemStat> iter = stats.statList.iterator();

		SystemStat stat = iter.next();
		assertEquals("Revenue YTD", stat.name);
		assertEquals("$40.00", stat.value);

		stat = iter.next();
		assertEquals("Revenue Year", stat.name);
		assertEquals("$40.00", stat.value);

		stat = iter.next();
		assertEquals("Active Users", stat.name);
		assertEquals("1", stat.value);

		stat = iter.next();
		assertEquals("Active Companies", stat.name);
		assertEquals("1", stat.value);
	}
}
