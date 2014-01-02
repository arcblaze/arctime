package com.arcblaze.arctime.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.lang.time.DateUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.arcblaze.arctime.db.dao.RoleDao;
import com.arcblaze.arctime.db.dao.UserDao;
import com.arcblaze.arctime.db.util.TestDatabase;
import com.arcblaze.arctime.model.Company;
import com.arcblaze.arctime.model.Enrichment;
import com.arcblaze.arctime.model.Role;
import com.arcblaze.arctime.model.User;

/**
 * Perform database integration testing.
 */
public class UserDaoTest {
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
		Set<Enrichment> enrichments = new HashSet<>(
				Arrays.asList(Enrichment.ROLES, Enrichment.SUPERVISERS,
						Enrichment.SUPERVISED));

		Company company = new Company().setName("Company").setActive(true);
		DaoFactory.getCompanyDao().add(company);

		UserDao userDao = DaoFactory.getUserDao();
		Set<User> users = userDao.getAll(company.getId());
		assertNotNull(users);
		assertEquals(0, users.size());

		User user = new User();
		user.setLogin("user");
		user.setHashedPass("hashed");
		user.setSalt("salt");
		user.setEmail("email");
		user.setFirstName("first");
		user.setLastName("last");

		userDao.add(company.getId(), user);
		assertNotNull(user.getId());
		assertEquals(company.getId(), user.getCompanyId());

		RoleDao roleDao = DaoFactory.getRoleDao();
		user.addRoles(Role.MANAGER, Role.PAYROLL);
		roleDao.add(user.getId(), user.getRoles());

		User supervisor1 = new User();
		supervisor1.setLogin("supervisor1");
		supervisor1.setHashedPass("hashed");
		supervisor1.setSalt("salt");
		supervisor1.setEmail("supervisor1");
		supervisor1.setFirstName("first");
		supervisor1.setLastName("last");
		userDao.add(company.getId(), supervisor1);
		userDao.addSupervisors(company.getId(), user.getId(), true,
				supervisor1.getId());

		User supervisor2 = new User();
		supervisor2.setLogin("supervisor2");
		supervisor2.setHashedPass("hashed");
		supervisor2.setSalt("salt");
		supervisor2.setEmail("supervisor2");
		supervisor2.setFirstName("first");
		supervisor2.setLastName("last");
		userDao.add(company.getId(), supervisor2);
		userDao.addSupervisors(company.getId(), user.getId(), false,
				supervisor2.getId());

		try {
			User user2 = new User();
			user2.setLogin("user"); // same as other user
			user2.setHashedPass("hashed");
			user2.setSalt("salt");
			user2.setEmail("email2");
			user2.setFirstName("first");
			user2.setLastName("last");
			userDao.add(company.getId(), user2);
			throw new RuntimeException("No unique constraint was thrown");
		} catch (DatabaseUniqueConstraintException uniqueConstraint) {
			// Expected
		}

		try {
			User user2 = new User();
			user2.setLogin("user2");
			user2.setHashedPass("hashed");
			user2.setSalt("salt");
			user2.setEmail("EMAIL"); // same as other user
			user2.setFirstName("first");
			user2.setLastName("last");
			userDao.add(company.getId(), user2);
			throw new RuntimeException("No unique constraint was thrown");
		} catch (DatabaseUniqueConstraintException uniqueConstraint) {
			// Expected
		}

		users = userDao.getAll(company.getId());
		assertNotNull(users);
		assertEquals(3, users.size());
		assertTrue(users.contains(user));
		assertTrue(users.contains(supervisor1));
		assertTrue(users.contains(supervisor2));

		users = userDao.getAll(company.getId(), enrichments);
		assertNotNull(users);
		assertEquals(3, users.size());
		assertTrue(users.contains(user));
		assertTrue(users.contains(supervisor1));
		assertTrue(users.contains(supervisor2));
		User getAllUser = null;
		User getAllSupervisor1 = null;
		User getAllSupervisor2 = null;
		for (User e : users)
			if (e.getId() == user.getId())
				getAllUser = e;
			else if (e.getId() == supervisor1.getId())
				getAllSupervisor1 = e;
			else if (e.getId() == supervisor2.getId())
				getAllSupervisor2 = e;
		assertNotNull(getAllUser);
		assertNotNull(getAllSupervisor1);
		assertNotNull(getAllSupervisor2);
		assertEquals(user, getAllUser);
		assertEquals(supervisor1, getAllSupervisor1);
		assertEquals(supervisor2, getAllSupervisor2);
		assertEquals(2, getAllUser.getRoles().size());
		assertTrue(getAllUser.isManager());
		assertTrue(getAllUser.isPayroll());
		assertEquals(2, getAllUser.getSupervisors().size());
		assertTrue(getAllUser.getSupervisors().contains(supervisor1));
		assertTrue(getAllUser.getSupervisors().contains(supervisor2));
		assertEquals(1, getAllSupervisor1.getSupervised().size());
		assertEquals(user, getAllSupervisor1.getSupervised().iterator().next());
		assertEquals(1, getAllSupervisor2.getSupervised().size());
		assertEquals(user, getAllSupervisor2.getSupervised().iterator().next());

		User getUser = userDao.get(company.getId(), user.getId());
		assertEquals(user, getUser);
		assertEquals(0, getUser.getRoles().size());

		getUser = userDao.get(company.getId(), user.getId(), enrichments);
		assertEquals(user, getUser);
		assertEquals(2, getUser.getRoles().size());
		assertTrue(getUser.isManager());
		assertTrue(getUser.isPayroll());

		User loginUser = userDao.getLogin(user.getLogin());
		assertEquals(user, loginUser);
		assertEquals(0, loginUser.getRoles().size());

		loginUser = userDao.getLogin(user.getEmail());
		assertEquals(user, loginUser);
		assertEquals(0, loginUser.getRoles().size());

		loginUser = userDao.getLogin(user.getEmail().toUpperCase());
		assertEquals(user, loginUser);
		assertEquals(0, loginUser.getRoles().size());

		user.setEmail("New Email");
		userDao.update(company.getId(), user);
		getUser = userDao.get(company.getId(), user.getId());
		assertEquals(user, getUser);
		assertEquals(0, getUser.getRoles().size());

		roleDao.delete(user.getId(), user.getRoles());
		userDao.deleteSupervisors(company.getId(), user.getId(),
				Arrays.asList(supervisor1.getId(), supervisor2.getId()));
		getUser = userDao.get(company.getId(), user.getId(), enrichments);
		assertEquals(user, getUser);
		assertEquals(0, getUser.getRoles().size());
		assertEquals(0, getUser.getSupervisors().size());

		userDao.delete(
				company.getId(),
				Arrays.asList(user.getId(), supervisor1.getId(),
						supervisor2.getId()));
		getUser = userDao.get(company.getId(), user.getId());
		assertNull(getUser);

		users = userDao.getAll(company.getId());
		assertNotNull(users);
		assertEquals(0, users.size());

		Date begin = DateUtils.addMonths(
				DateUtils.truncate(new Date(), Calendar.MONTH), -3);
		Date end = DateUtils.addDays(new Date(), 1);
		SortedMap<Date, Integer> map = userDao.getActiveByMonth(begin, end);
		assertEquals(4, map.size());
		for (Integer value : map.values())
			assertEquals(new Integer(0), value);

		Map<Integer, Integer> values = new HashMap<>();
		values.put(company.getId(), 5);
		userDao.setActiveUsers(begin, values);
		values.put(company.getId(), 6);
		userDao.setActiveUsers(DateUtils.addDays(begin, 3), values);
		values.put(company.getId(), 8);
		userDao.setActiveUsers(DateUtils.addDays(begin, 32), values);
		values.put(company.getId(), 9);
		userDao.setActiveUsers(DateUtils.addDays(begin, 33), values);
		values.put(company.getId(), 7);
		userDao.setActiveUsers(DateUtils.addDays(begin, 66), values);
		values.put(company.getId(), 6);
		userDao.setActiveUsers(DateUtils.addDays(begin, 67), values);

		map = userDao.getActiveByMonth(begin, end);
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
