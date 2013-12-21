package com.arcblaze.arctime.rest.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import javax.ws.rs.core.SecurityContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.arcblaze.arctime.db.DaoFactory;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.UserDao;
import com.arcblaze.arctime.db.util.TestDatabase;
import com.arcblaze.arctime.model.Company;
import com.arcblaze.arctime.model.User;
import com.arcblaze.arctime.rest.user.SupervisorsResource.UserSupervisors;

/**
 * Perform testing of the profile update capabilities.
 */
public class SupervisorsResourceTest {
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
	public void testNoSupervisors() throws DatabaseException {
		Company company = new Company().setName("company").setActive(true);
		DaoFactory.getCompanyDao().add(company);

		User user = new User();
		user.setLogin("user");
		user.setHashedPass("hashed");
		user.setEmail("email@whatever.com");
		user.setFirstName("first");
		user.setLastName("last");
		user.setActive(true);

		UserDao userDao = DaoFactory.getUserDao();
		userDao.add(company.getId(), user);

		SecurityContext security = Mockito.mock(SecurityContext.class);
		Mockito.when(security.getUserPrincipal()).thenReturn(user);

		SupervisorsResource resource = new SupervisorsResource();
		UserSupervisors supervisors = resource.get(security);

		assertNotNull(supervisors);
		assertNotNull(supervisors.supervisors);
		assertTrue(supervisors.supervisors.isEmpty());
	}

	/**
	 * Test how the resource responds with no updated password specified.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 */
	@Test
	public void testMultipleSupervisors() throws DatabaseException {
		Company company = new Company().setName("company").setActive(true);
		DaoFactory.getCompanyDao().add(company);

		User supervisor1 = new User();
		supervisor1.setLogin("s1");
		supervisor1.setHashedPass("hashed");
		supervisor1.setEmail("s1@whatever.com");
		supervisor1.setFirstName("first");
		supervisor1.setLastName("last");
		supervisor1.setActive(true);

		User supervisor2 = new User();
		supervisor2.setLogin("s2");
		supervisor2.setHashedPass("hashed");
		supervisor2.setEmail("s2@whatever.com");
		supervisor2.setFirstName("first");
		supervisor2.setLastName("last");
		supervisor2.setActive(true);

		User user = new User();
		user.setLogin("user");
		user.setHashedPass("hashed");
		user.setEmail("email@whatever.com");
		user.setFirstName("first");
		user.setLastName("last");
		user.setActive(true);

		UserDao userDao = DaoFactory.getUserDao();
		userDao.add(company.getId(),
				Arrays.asList(supervisor1, supervisor2, user));

		userDao.addSupervisors(company.getId(), user.getId(), true,
				supervisor1.getId());
		userDao.addSupervisors(company.getId(), user.getId(), false,
				supervisor2.getId());

		SecurityContext security = Mockito.mock(SecurityContext.class);
		Mockito.when(security.getUserPrincipal()).thenReturn(user);

		SupervisorsResource resource = new SupervisorsResource();
		UserSupervisors response = resource.get(security);

		assertNotNull(response);
		assertNotNull(response.supervisors);
		System.out.println(response.supervisors);
		assertEquals(2, response.supervisors.size());
		assertTrue(response.supervisors.contains(supervisor1));
		assertTrue(response.supervisors.contains(supervisor2));
	}
}
