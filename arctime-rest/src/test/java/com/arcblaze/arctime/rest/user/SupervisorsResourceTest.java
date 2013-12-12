package com.arcblaze.arctime.rest.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

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
import com.arcblaze.arctime.model.PersonnelType;
import com.arcblaze.arctime.rest.user.SupervisorsResource.EmployeeSupervisors;

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

		SupervisorsResource resource = new SupervisorsResource();
		EmployeeSupervisors supervisors = resource.get(security);

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
		Company company = new Company();
		company.setName("company");
		company.setActive(true);

		DaoFactory.getCompanyDao().add(Collections.singleton(company));

		Employee supervisor1 = new Employee();
		supervisor1.setLogin("s1");
		supervisor1.setHashedPass("hashed");
		supervisor1.setEmail("s1@whatever.com");
		supervisor1.setFirstName("first");
		supervisor1.setLastName("last");
		supervisor1.setSuffix("suffix");
		supervisor1.setDivision("division");
		supervisor1.setPersonnelType(PersonnelType.EMPLOYEE);
		supervisor1.setActive(true);

		Employee supervisor2 = new Employee();
		supervisor2.setLogin("s2");
		supervisor2.setHashedPass("hashed");
		supervisor2.setEmail("s2@whatever.com");
		supervisor2.setFirstName("first");
		supervisor2.setLastName("last");
		supervisor2.setSuffix("suffix");
		supervisor2.setDivision("division");
		supervisor2.setPersonnelType(PersonnelType.EMPLOYEE);
		supervisor2.setActive(true);

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
		employeeDao.add(company.getId(),
				Arrays.asList(supervisor1, supervisor2, employee));

		employeeDao.addSupervisors(company.getId(), employee.getId(),
				Collections.singleton(supervisor1.getId()), true);
		employeeDao.addSupervisors(company.getId(), employee.getId(),
				Collections.singleton(supervisor2.getId()), false);

		SecurityContext security = Mockito.mock(SecurityContext.class);
		Mockito.when(security.getUserPrincipal()).thenReturn(employee);

		SupervisorsResource resource = new SupervisorsResource();
		EmployeeSupervisors response = resource.get(security);

		assertNotNull(response);
		assertNotNull(response.supervisors);
		System.out.println(response.supervisors);
		assertEquals(2, response.supervisors.size());
		assertTrue(response.supervisors.contains(supervisor1));
		assertTrue(response.supervisors.contains(supervisor2));
	}
}
