package com.arcblaze.arctime.rest.user;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import javax.ws.rs.BadRequestException;
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
import com.arcblaze.arctime.model.Password;
import com.arcblaze.arctime.model.User;

/**
 * Perform testing of the profile update capabilities.
 */
public class ProfileUpdateResourceTest {
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
	 * Test how the resource responds when the provided parameters are invalid.
	 */
	@Test(expected = BadRequestException.class)
	public void testInvalidParameters1() {
		ProfileUpdateResource resource = new ProfileUpdateResource();
		resource.update(null, " ", "a", "b", "c", "d");
	}

	/**
	 * Test how the resource responds when the provided parameters are invalid.
	 */
	@Test(expected = BadRequestException.class)
	public void testInvalidParameters2() {
		ProfileUpdateResource resource = new ProfileUpdateResource();
		resource.update(null, "a", " ", "b", "c", "d");
	}

	/**
	 * Test how the resource responds when the provided parameters are invalid.
	 */
	@Test(expected = BadRequestException.class)
	public void testInvalidParameters3() {
		ProfileUpdateResource resource = new ProfileUpdateResource();
		resource.update(null, "a", "b", " ", "c", "d");
	}

	/**
	 * Test how the resource responds when the provided parameters are invalid.
	 */
	@Test(expected = BadRequestException.class)
	public void testInvalidParameters4() {
		ProfileUpdateResource resource = new ProfileUpdateResource();
		resource.update(null, "a", "b", "c", " ", "d");
	}

	/**
	 * Test how the resource responds when the provided parameters are invalid.
	 */
	@Test(expected = BadRequestException.class)
	public void testInvalidParameters5() {
		SecurityContext security = Mockito.mock(SecurityContext.class);

		ProfileUpdateResource resource = new ProfileUpdateResource();
		resource.update(security, "a", "b", null, "c", null);
	}

	/**
	 * Test how the resource responds with no updated password specified.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 */
	@Test
	public void testValidBlankPassword() throws DatabaseException {
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

		SecurityContext security = Mockito.mock(SecurityContext.class);
		Mockito.when(security.getUserPrincipal()).thenReturn(user);

		ProfileUpdateResource resource = new ProfileUpdateResource();
		resource.update(security, "a", "b", "c", "d", null);

		User updated = userDao.getLogin("d");
		assertEquals(user.getId(), updated.getId());
		assertEquals(company.getId(), updated.getCompanyId());
		assertEquals("c", updated.getLogin());
		assertEquals("d", updated.getEmail());
		assertEquals("a", updated.getFirstName());
		assertEquals("b", updated.getLastName());
		assertEquals(user.getHashedPass(), updated.getHashedPass());
	}

	/**
	 * Test how the resource responds with a blank suffix value.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 */
	@Test
	public void testValidWithPassword() throws DatabaseException {
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

		SecurityContext security = Mockito.mock(SecurityContext.class);
		Mockito.when(security.getUserPrincipal()).thenReturn(user);

		Password mockPassword = Mockito.mock(Password.class);
		Mockito.when(mockPassword.random(10)).thenReturn("new-salt");
		Mockito.when(mockPassword.hash("password", "new-salt")).thenReturn(
				"hashed-password");

		ProfileUpdateResource resource = new ProfileUpdateResource(mockPassword);
		resource.update(security, "a", "b", "c", "d", "password");

		User updated = userDao.getLogin("c");
		assertEquals(user.getId(), updated.getId());
		assertEquals(company.getId(), updated.getCompanyId());
		assertEquals("c", updated.getLogin());
		assertEquals("d", updated.getEmail());
		assertEquals("a", updated.getFirstName());
		assertEquals("b", updated.getLastName());

		// Password should be an updated value.
		assertEquals("hashed-password", updated.getHashedPass());
	}

	/**
	 * Test how the resource responds when the updated user login value already
	 * exists.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 */
	@Test(expected = BadRequestException.class)
	public void testLoginAlreadyExists() throws DatabaseException {
		Company company = new Company().setName("company").setActive(true);
		DaoFactory.getCompanyDao().add(company);

		User existing = new User();
		existing.setLogin("existing");
		existing.setHashedPass("hashed");
		existing.setSalt("salt");
		existing.setEmail("existing@whatever.com");
		existing.setFirstName("first");
		existing.setLastName("last");
		existing.setActive(true);

		User user = new User();
		user.setLogin("user");
		user.setHashedPass("hashed");
		user.setSalt("salt");
		user.setEmail("email@whatever.com");
		user.setFirstName("first");
		user.setLastName("last");
		user.setActive(true);

		UserDao userDao = DaoFactory.getUserDao();
		userDao.add(company.getId(), Arrays.asList(existing, user));

		SecurityContext security = Mockito.mock(SecurityContext.class);
		Mockito.when(security.getUserPrincipal()).thenReturn(user);

		ProfileUpdateResource resource = new ProfileUpdateResource();
		resource.update(security, "a", "b", "existing", "e", "password");
	}
}
