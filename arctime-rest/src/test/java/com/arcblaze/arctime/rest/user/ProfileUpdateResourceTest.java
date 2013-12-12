package com.arcblaze.arctime.rest.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collections;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.SecurityContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.arcblaze.arctime.db.DaoFactory;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.EmployeeDao;
import com.arcblaze.arctime.db.util.TestDatabase;
import com.arcblaze.arctime.model.Company;
import com.arcblaze.arctime.model.Employee;
import com.arcblaze.arctime.model.Password;
import com.arcblaze.arctime.model.PersonnelType;

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
		resource.update(null, " ", "a", "b", "c", "d", "e");
	}

	/**
	 * Test how the resource responds when the provided parameters are invalid.
	 */
	@Test(expected = BadRequestException.class)
	public void testInvalidParameters2() {
		ProfileUpdateResource resource = new ProfileUpdateResource();
		resource.update(null, "a", " ", "b", "c", "d", "e");
	}

	/**
	 * Test how the resource responds when the provided parameters are invalid.
	 */
	@Test(expected = BadRequestException.class)
	public void testInvalidParameters3() {
		ProfileUpdateResource resource = new ProfileUpdateResource();
		resource.update(null, "a", "b", "c", " ", "d", "e");
	}

	/**
	 * Test how the resource responds when the provided parameters are invalid.
	 */
	@Test(expected = BadRequestException.class)
	public void testInvalidParameters4() {
		ProfileUpdateResource resource = new ProfileUpdateResource();
		resource.update(null, "a", "b", "c", "d", " ", "e");
	}

	/**
	 * Test how the resource responds when the provided parameters are invalid.
	 */
	@Test(expected = BadRequestException.class)
	public void testInvalidParameters5() {
		SecurityContext security = Mockito.mock(SecurityContext.class);

		ProfileUpdateResource resource = new ProfileUpdateResource();
		resource.update(security, "a", "b", "c", null, "d", null);
	}

	/**
	 * Test how the resource responds with no updated password specified.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 */
	@Test
	public void testValidBlankPassword() throws DatabaseException {
		Company company = new Company();
		company.setName("company");
		company.setActive(true);

		DaoFactory.getCompanyDao().add(Collections.singleton(company));

		Employee employee = new Employee();
		employee.setLogin("employee");
		employee.setHashedPass("hashed");
		employee.setEmail("email@whatever.com");
		employee.setFirstName("first");
		employee.setLastName("last");
		employee.setSuffix("suffix");
		employee.setDivision("division");
		employee.setPersonnelType(PersonnelType.EMPLOYEE);
		employee.setActive(true);

		EmployeeDao employeeDao = DaoFactory.getEmployeeDao();
		employeeDao.add(company.getId(), Collections.singleton(employee));

		SecurityContext security = Mockito.mock(SecurityContext.class);
		Mockito.when(security.getUserPrincipal()).thenReturn(employee);

		ProfileUpdateResource resource = new ProfileUpdateResource();
		resource.update(security, "a", "b", "c", "d", "e", null);

		Employee updated = employeeDao.getLogin("d");
		assertEquals(employee.getId(), updated.getId());
		assertEquals(company.getId(), updated.getCompanyId());
		assertEquals("d", updated.getLogin());
		assertEquals("e", updated.getEmail());
		assertEquals("a", updated.getFirstName());
		assertEquals("b", updated.getLastName());
		assertEquals(employee.getDivision(), updated.getDivision());
		assertEquals(employee.getPersonnelType(), updated.getPersonnelType());

		assertEquals("c", updated.getSuffix());
		assertEquals(employee.getHashedPass(), updated.getHashedPass());
	}

	/**
	 * Test how the resource responds with a blank suffix value.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 */
	@Test
	public void testValidNoSuffix() throws DatabaseException {
		Company company = new Company();
		company.setName("company");
		company.setActive(true);

		DaoFactory.getCompanyDao().add(Collections.singleton(company));

		Employee employee = new Employee();
		employee.setLogin("employee");
		employee.setHashedPass("hashed");
		employee.setEmail("email@whatever.com");
		employee.setFirstName("first");
		employee.setLastName("last");
		employee.setSuffix("suffix");
		employee.setDivision("division");
		employee.setPersonnelType(PersonnelType.EMPLOYEE);
		employee.setActive(true);

		EmployeeDao employeeDao = DaoFactory.getEmployeeDao();
		employeeDao.add(company.getId(), Collections.singleton(employee));

		SecurityContext security = Mockito.mock(SecurityContext.class);
		Mockito.when(security.getUserPrincipal()).thenReturn(employee);

		ProfileUpdateResource resource = new ProfileUpdateResource();
		resource.update(security, "a", "b", "", "d", "e", "password");

		Employee updated = employeeDao.getLogin("d");
		assertEquals(employee.getId(), updated.getId());
		assertEquals(company.getId(), updated.getCompanyId());
		assertEquals("d", updated.getLogin());
		assertEquals("e", updated.getEmail());
		assertEquals("a", updated.getFirstName());
		assertEquals("b", updated.getLastName());
		assertEquals(employee.getDivision(), updated.getDivision());
		assertEquals(employee.getPersonnelType(), updated.getPersonnelType());

		// Suffix gets set to null.
		assertNull(updated.getSuffix());

		// Password should be an updated value.
		assertEquals(new Password().hash("password"), updated.getHashedPass());
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
		Company company = new Company();
		company.setName("company");
		company.setActive(true);

		DaoFactory.getCompanyDao().add(Collections.singleton(company));

		Employee existing = new Employee();
		existing.setLogin("existing");
		existing.setHashedPass("hashed");
		existing.setEmail("existing@whatever.com");
		existing.setFirstName("first");
		existing.setLastName("last");
		existing.setSuffix("suffix");
		existing.setDivision("division");
		existing.setPersonnelType(PersonnelType.EMPLOYEE);
		existing.setActive(true);

		Employee employee = new Employee();
		employee.setLogin("employee");
		employee.setHashedPass("hashed");
		employee.setEmail("email@whatever.com");
		employee.setFirstName("first");
		employee.setLastName("last");
		employee.setSuffix("suffix");
		employee.setDivision("division");
		employee.setPersonnelType(PersonnelType.EMPLOYEE);
		employee.setActive(true);

		EmployeeDao employeeDao = DaoFactory.getEmployeeDao();
		employeeDao.add(company.getId(), Arrays.asList(existing, employee));

		SecurityContext security = Mockito.mock(SecurityContext.class);
		Mockito.when(security.getUserPrincipal()).thenReturn(employee);

		ProfileUpdateResource resource = new ProfileUpdateResource();
		resource.update(security, "a", "b", "", "existing", "e", "password");
	}
}
