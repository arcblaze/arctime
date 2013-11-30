package com.arcblaze.arctime.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.arcblaze.arctime.db.dao.EmployeeDao;
import com.arcblaze.arctime.model.Company;
import com.arcblaze.arctime.model.Employee;
import com.arcblaze.arctime.model.PersonnelType;

/**
 * Perform database integration testing.
 */
public class EmployeeDaoTest {
	/**
	 * Perform test setup activities.
	 * 
	 * @throws Exception
	 *             if there is a problem performing test initialization
	 */
	@BeforeClass
	public static void setup() throws Exception {
		DatabaseInitialization.initializeDatabase();
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
	 */
	@Test
	public void dbIntegrationTests() throws DatabaseException {
		Company company = new Company().setName("Company").setActive(true);
		DaoFactory.getCompanyDao().add(Collections.singleton(company));

		EmployeeDao dao = DaoFactory.getEmployeeDao();
		Set<Employee> employees = dao.getAll(company.getId(), null);
		assertNotNull(employees);
		assertEquals(0, employees.size());

		Employee employee = new Employee();
		employee.setLogin("employee");
		employee.setHashedPass("hashed");
		employee.setEmail("email");
		employee.setFirstName("first");
		employee.setLastName("last");
		employee.setSuffix("suffix");
		employee.setDivision("division");
		employee.setPersonnelType(PersonnelType.EMPLOYEE);

		dao.add(company.getId(), Collections.singleton(employee));
		assertNotNull(employee.getId());
		assertEquals(company.getId(), employee.getCompanyId());

		try {
			Employee employee2 = new Employee();
			employee2.setLogin("employee2");
			employee2.setHashedPass("hashed");
			employee2.setEmail("EMAIL");
			employee2.setFirstName("first");
			employee2.setLastName("last");
			employee2.setSuffix("suffix");
			employee2.setDivision("division");
			employee2.setPersonnelType(PersonnelType.EMPLOYEE);
			dao.add(company.getId(), Collections.singleton(employee2));
			throw new RuntimeException("No unique constraint was thrown");
		} catch (DatabaseException uniqueConstraint) {
			// Expected
		}

		employees = dao.getAll(company.getId(), null);
		assertNotNull(employees);
		assertEquals(1, employees.size());
		assertEquals(employee, employees.iterator().next());

		Employee getEmployee = dao.get(company.getId(), employee.getId(), null);
		assertEquals(employee, getEmployee);

		Employee loginEmployee = dao.getLogin(company.getId(),
				employee.getLogin());
		assertEquals(employee, loginEmployee);

		loginEmployee = dao.getLogin(company.getId(), employee.getEmail());
		assertEquals(employee, loginEmployee);

		loginEmployee = dao.getLogin(company.getId(), employee.getEmail()
				.toUpperCase());
		assertEquals(employee, loginEmployee);

		employee.setEmail("New Email");
		dao.update(company.getId(), Collections.singleton(employee));
		getEmployee = dao.get(company.getId(), employee.getId(), null);
		assertEquals(employee, getEmployee);

		dao.delete(company.getId(), Collections.singleton(employee.getId()));
		getEmployee = dao.get(company.getId(), employee.getId(), null);
		assertNull(getEmployee);

		employees = dao.getAll(company.getId(), null);
		assertNotNull(employees);
		assertEquals(0, employees.size());
	}
}
