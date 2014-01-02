package com.arcblaze.arctime.rest.admin.stats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
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

/**
 * Perform testing of the system statistics.
 */
public class RevenueResourceTest {
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
	 * Test how the resource responds with no transaction data available.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 */
	@Test
	public void testNoData() throws DatabaseException {
		StringBuilder correct = new StringBuilder();
		correct.append("index\tamount\n");
		correct.append("0\t0.00\n");
		correct.append("1\t0.00\n");
		correct.append("2\t0.00\n");
		correct.append("3\t0.00\n");
		correct.append("4\t0.00\n");
		correct.append("5\t0.00\n");
		correct.append("6\t0.00\n");
		correct.append("7\t0.00\n");
		correct.append("8\t0.00\n");
		correct.append("9\t0.00\n");
		correct.append("10\t0.00\n");
		correct.append("11\t0.00\n");
		correct.append("12\t0.00\n");

		RevenueResource resource = new RevenueResource();
		String data = resource.getRevenue();

		assertNotNull(data);
		assertEquals(correct.toString(), data);
	}

	/**
	 * Test how the resource responds with some transactions available.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 */
	@Test
	public void testTransactionsAvailable() throws DatabaseException {
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
		tx1.setTimestamp(DateUtils.addMonths(new Date(), -1));
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

		StringBuilder correct = new StringBuilder();
		correct.append("index\tamount\n");
		correct.append("0\t0.00\n");
		correct.append("1\t0.00\n");
		correct.append("2\t0.00\n");
		correct.append("3\t0.00\n");
		correct.append("4\t0.00\n");
		correct.append("5\t0.00\n");
		correct.append("6\t0.00\n");
		correct.append("7\t0.00\n");
		correct.append("8\t0.00\n");
		correct.append("9\t0.00\n");
		correct.append("10\t0.00\n");
		correct.append("11\t50.00\n");
		correct.append("12\t-10.00\n");

		RevenueResource resource = new RevenueResource();
		String data = resource.getRevenue();

		assertNotNull(data);
		assertEquals(correct.toString(), data);
	}
}
