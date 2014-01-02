package com.arcblaze.arctime.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.lang.time.DateUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.arcblaze.arctime.db.dao.TransactionDao;
import com.arcblaze.arctime.db.util.TestDatabase;
import com.arcblaze.arctime.model.Company;
import com.arcblaze.arctime.model.Transaction;
import com.arcblaze.arctime.model.TransactionType;
import com.arcblaze.arctime.model.User;

/**
 * Perform database integration testing.
 */
public class TransactionDaoTest {
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
		Company company = new Company().setName("company");
		DaoFactory.getCompanyDao().add(company);

		User user = new User();
		user.setLogin("user");
		user.setHashedPass("hashed");
		user.setSalt("salt");
		user.setEmail("email");
		user.setFirstName("first");
		user.setLastName("last");
		DaoFactory.getUserDao().add(company.getId(), user);

		TransactionDao dao = DaoFactory.getTransactionDao();
		Set<Transaction> transactions = dao.getForCompany(company.getId());
		assertNotNull(transactions);
		assertEquals(0, transactions.size());

		Transaction t1 = new Transaction();
		t1.setCompanyId(company.getId());
		t1.setUserId(user.getId());
		t1.setTimestamp(new Date());
		t1.setTransactionType(TransactionType.PAYMENT);
		t1.setDescription("Received payment");
		t1.setAmount("30.00");

		Transaction t2 = new Transaction();
		t2.setCompanyId(company.getId());
		t2.setUserId(user.getId());
		t2.setTimestamp(new Date());
		t2.setTransactionType(TransactionType.REFUND);
		t2.setDescription("Refunded some money");
		t2.setAmount("-10.00");
		t2.setNotes("Some notes");

		dao.add(t1, t2);
		assertNotNull(t1.getId());
		assertNotNull(t2.getId());

		transactions = dao.getForCompany(company.getId());
		assertNotNull(transactions);
		assertEquals(2, transactions.size());
		assertTrue(transactions.contains(t1));
		assertTrue(transactions.contains(t2));

		Date beginOfMonth = DateUtils.truncate(new Date(), Calendar.MONTH);
		Date tomorrow = DateUtils.addDays(new Date(), 1);
		BigDecimal amount = dao.amountBetween(beginOfMonth, tomorrow);
		assertEquals("20.00", amount.toPlainString());
		amount = dao.amountBetween(company.getId(), beginOfMonth, tomorrow);
		assertEquals("20.00", amount.toPlainString());
		SortedMap<Date, BigDecimal> map = dao.getSumByMonth(beginOfMonth,
				tomorrow);
		assertEquals(1, map.size());
		BigDecimal sum = map.get(beginOfMonth);
		assertEquals("20.00", sum.toPlainString());

		Transaction getTransaction = dao.get(t1.getId());
		assertEquals(t1, getTransaction);

		t1.setDescription("New Description");
		t1.setAmount("40");
		t1.setNotes("New Notes");
		dao.update(t1);
		getTransaction = dao.get(t1.getId());
		assertEquals(t1, getTransaction);

		dao.delete(t1.getId(), t2.getId());
		getTransaction = dao.get(t1.getId());
		assertNull(getTransaction);

		transactions = dao.getForCompany(company.getId());
		assertNotNull(transactions);
		assertEquals(0, transactions.size());
	}
}
