package com.arcblaze.arctime.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.time.DateUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.arcblaze.arctime.db.dao.HolidayDao;
import com.arcblaze.arctime.db.util.TestDatabase;
import com.arcblaze.arctime.model.Company;
import com.arcblaze.arctime.model.Holiday;
import com.arcblaze.arctime.model.PayPeriod;
import com.arcblaze.arctime.model.util.HolidayConfigurationException;

/**
 * Perform database integration testing.
 */
public class HolidayDaoTest {
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

	protected Set<Holiday> getFederalHolidays()
			throws HolidayConfigurationException {
		Map<String, String> map = new HashMap<>();
		map.put("New Years", "January 1st Observance");
		map.put("Martin Luther King Junior Day", "3rd Monday in January");
		map.put("President's Day", "3rd Monday in February");
		map.put("Memorial Day", "Last Monday in May");
		map.put("Independence Day", "July 4th Observance");
		map.put("Labor Day", "1st Monday in September");
		map.put("Columbus Day", "2nd Monday in October");
		map.put("Veterans Day", "November 11th Observance");
		map.put("Thanksgiving Day", "4th Thursday in November");
		map.put("Christmas Day", "December 25th Observance");

		Set<Holiday> holidays = new TreeSet<>();
		for (Map.Entry<String, String> entry : map.entrySet())
			holidays.add(new Holiday().setDescription(entry.getKey())
					.setConfig(entry.getValue()));
		return holidays;
	}

	/**
	 * @throws DatabaseException
	 *             if there is a problem with the database
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing holiday configurations
	 */
	@Test
	public void dbIntegrationTests() throws DatabaseException,
			HolidayConfigurationException {
		Company company = new Company().setName("Company").setActive(true);
		DaoFactory.getCompanyDao().add(company);

		HolidayDao dao = DaoFactory.getHolidayDao();
		Set<Holiday> holidays = dao.getAll(company.getId());
		assertNotNull(holidays);
		assertEquals(0, holidays.size());

		holidays = getFederalHolidays();
		dao.add(company.getId(), holidays);
		for (Holiday holiday : holidays) {
			assertNotNull(holiday.getId());
			assertEquals(company.getId(), holiday.getCompanyId());
		}

		Set<Holiday> getAllHolidays = dao.getAll(company.getId());
		assertNotNull(getAllHolidays);
		assertEquals(holidays.size(), getAllHolidays.size());

		Holiday first = holidays.iterator().next();
		Holiday getHoliday = dao.get(company.getId(), first.getId());
		assertEquals(first, getHoliday);

		first.setDescription("New Description");
		dao.update(company.getId(), first);
		getHoliday = dao.get(company.getId(), first.getId());
		assertEquals(first, getHoliday);

		dao.delete(company.getId(), first.getId());
		getHoliday = dao.get(company.getId(), first.getId());
		assertNull(getHoliday);

		getAllHolidays = dao.getAll(company.getId());
		assertNotNull(getAllHolidays);
		assertEquals(holidays.size() - 1, getAllHolidays.size());

		Holiday second = getAllHolidays.iterator().next();
		PayPeriod payPeriod = new PayPeriod().setBegin(
				DateUtils.addDays(second.getDay(), -3)).setEnd(
				DateUtils.addDays(second.getDay(), 3));
		Set<Holiday> forPayPeriod = dao.getForPayPeriod(company.getId(),
				payPeriod);
		assertNotNull(forPayPeriod);
		assertEquals(1, forPayPeriod.size());
		assertEquals(second, forPayPeriod.iterator().next());
	}
}
