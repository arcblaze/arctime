package com.arcblaze.arctime.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.arcblaze.arctime.db.dao.PayPeriodDao;
import com.arcblaze.arctime.db.util.TestDatabase;
import com.arcblaze.arctime.model.Company;
import com.arcblaze.arctime.model.PayPeriod;
import com.arcblaze.arctime.model.PayPeriodType;
import com.arcblaze.arctime.model.util.HolidayConfigurationException;

/**
 * Perform database integration testing.
 */
public class PayPeriodDaoTest {
	private final static String[] FMT = { "yyyyMMdd HHmmss", "yyyyMMdd" };

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
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing holiday configurations
	 * @throws ParseException
	 *             if there is a date-parsing issue
	 */
	@Test
	public void dbIntegrationTests() throws DatabaseException,
			HolidayConfigurationException, ParseException {
		Company company = new Company().setName("Company").setActive(true);
		DaoFactory.getCompanyDao().add(company);

		PayPeriodDao dao = DaoFactory.getPayPeriodDao();

		// No pay periods exist yet, so these will all be null
		assertNull(dao.getLatest(company.getId()));
		assertNull(dao.getCurrent(company.getId()));
		assertNull(dao.getContaining(company.getId(), new Date()));

		PayPeriod first = new PayPeriod().setCompanyId(company.getId())
				.setType(PayPeriodType.WEEKLY)
				.setBegin(DateUtils.parseDate("20140101", FMT))
				.setEnd(DateUtils.parseDate("20140107", FMT));
		dao.add(company.getId(), first);

		PayPeriod get = dao.get(company.getId(), first.getBegin());
		assertNotNull(get);
		assertEquals(first, get);

		PayPeriod latest = dao.getLatest(company.getId());
		assertNotNull(latest);
		assertEquals(first, latest);

		for (String d : Arrays.asList("20140101", "20140102", "20140107")) {
			PayPeriod containing = dao.getContaining(company.getId(),
					DateUtils.parseDate(d, FMT));
			assertEquals(first, containing);
		}

		PayPeriod prev = first.getPrevious();
		PayPeriod before = dao.get(company.getId(), prev.getBegin());
		assertNull(before); // Not automatically created.
		before = dao.getContaining(company.getId(),
				DateUtils.parseDate("20131230", FMT));
		assertNotNull(before); // Automatically created.
		assertEquals(prev, before);

		PayPeriod next = first.getNext();
		PayPeriod after = dao.get(company.getId(), next.getBegin());
		assertNull(after); // Not automatically created.
		after = dao.getContaining(company.getId(),
				DateUtils.parseDate("20140109", FMT));
		assertNotNull(after); // Automatically created.
		assertEquals(next, after);

		latest = dao.getLatest(company.getId());
		assertNotNull(latest);
		assertEquals(next, latest);

		Date early = DateUtils.parseDate("20130901", FMT);
		before = dao.get(company.getId(), early);
		assertNull(before); // Not automatically created.
		before = dao.getContaining(company.getId(), early);
		assertNotNull(before); // Automatically created.
		assertTrue(before.contains(early));

		Date late = DateUtils.parseDate("20140301", FMT);
		after = dao.get(company.getId(), late);
		assertNull(after); // Not automatically created.
		after = dao.getContaining(company.getId(), late);
		assertNotNull(after); // Automatically created.
		assertTrue(after.contains(late));

		PayPeriod current = dao.getCurrent(company.getId());
		assertNotNull(current);
		assertTrue(current.contains(new Date()));
	}
}
