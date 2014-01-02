package com.arcblaze.arctime.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.lang.time.DateUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.arcblaze.arctime.db.dao.CompanyDao;
import com.arcblaze.arctime.db.util.TestDatabase;
import com.arcblaze.arctime.model.Company;

/**
 * Perform database integration testing.
 */
public class CompanyDaoTest {
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
		CompanyDao dao = DaoFactory.getCompanyDao();
		Set<Company> companies = dao.getAll();
		assertNotNull(companies);
		assertEquals(0, companies.size());

		Company company = new Company();
		company.setName("Company");
		company.setActive(true);

		dao.add(company);
		assertNotNull(company.getId());

		companies = dao.getAll();
		assertNotNull(companies);
		assertEquals(1, companies.size());
		Company getAllCompany = companies.iterator().next();
		assertEquals(company, getAllCompany);

		Company getCompany = dao.get(company.getId());
		assertEquals(company, getCompany);

		company.setName("New Name");
		dao.update(company);
		getCompany = dao.get(company.getId());
		assertEquals(company, getCompany);

		dao.delete(company.getId());
		getCompany = dao.get(company.getId());
		assertNull(getCompany);

		companies = dao.getAll();
		assertNotNull(companies);
		assertEquals(0, companies.size());

		Date begin = DateUtils.addMonths(
				DateUtils.truncate(new Date(), Calendar.MONTH), -3);
		Date end = DateUtils.addDays(new Date(), 1);
		SortedMap<Date, Integer> map = dao.getActiveByMonth(begin, end);
		assertEquals(4, map.size());
		for (Integer value : map.values())
			assertEquals(new Integer(0), value);

		dao.setActiveCompanies(begin, 5);
		dao.setActiveCompanies(DateUtils.addDays(begin, 3), 6);
		dao.setActiveCompanies(DateUtils.addDays(begin, 32), 8);
		dao.setActiveCompanies(DateUtils.addDays(begin, 33), 9);
		dao.setActiveCompanies(DateUtils.addDays(begin, 66), 7);
		dao.setActiveCompanies(DateUtils.addDays(begin, 67), 6);

		map = dao.getActiveByMonth(begin, end);
		assertEquals(4, map.size());

		Iterator<Entry<Date, Integer>> iter = map.entrySet().iterator();
		Entry<Date, Integer> entry = iter.next();
		assertEquals(begin, entry.getKey());
		assertEquals(new Integer(6), entry.getValue());

		entry = iter.next();
		assertEquals(DateUtils.addMonths(begin, 1), entry.getKey());
		assertEquals(new Integer(9), entry.getValue());

		entry = iter.next();
		assertEquals(DateUtils.addMonths(begin, 2), entry.getKey());
		assertEquals(new Integer(7), entry.getValue());

		entry = iter.next();
		assertEquals(DateUtils.addMonths(begin, 3), entry.getKey());
		assertEquals(new Integer(0), entry.getValue());
	}
}
