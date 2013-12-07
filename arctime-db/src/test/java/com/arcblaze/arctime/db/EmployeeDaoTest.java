package com.arcblaze.arctime.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.arcblaze.arctime.db.dao.EmployeeDao;
import com.arcblaze.arctime.db.dao.RoleDao;
import com.arcblaze.arctime.db.util.TestDatabase;
import com.arcblaze.arctime.model.Company;
import com.arcblaze.arctime.model.Employee;
import com.arcblaze.arctime.model.Enrichment;
import com.arcblaze.arctime.model.PersonnelType;
import com.arcblaze.arctime.model.Role;

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
	 */
	@Test
	public void dbIntegrationTests() throws DatabaseException {
		Set<Enrichment> enrichments = new HashSet<>(
				Arrays.asList(Enrichment.ROLES, Enrichment.SUPERVISERS,
						Enrichment.SUPERVISED));

		Company company = new Company().setName("Company").setActive(true);
		DaoFactory.getCompanyDao().add(Collections.singleton(company));

		EmployeeDao employeeDao = DaoFactory.getEmployeeDao();
		Set<Employee> employees = employeeDao.getAll(company.getId(), null);
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

		employeeDao.add(company.getId(), Collections.singleton(employee));
		assertNotNull(employee.getId());
		assertEquals(company.getId(), employee.getCompanyId());

		RoleDao roleDao = DaoFactory.getRoleDao();
		employee.addRoles(Role.MANAGER, Role.PAYROLL);
		roleDao.add(employee.getId(), employee.getRoles());

		Employee supervisor = new Employee();
		supervisor.setLogin("supervisor");
		supervisor.setHashedPass("hashed");
		supervisor.setEmail("supervisor");
		supervisor.setFirstName("first");
		supervisor.setLastName("last");
		supervisor.setSuffix("suffix");
		supervisor.setDivision("division");
		supervisor.setPersonnelType(PersonnelType.EMPLOYEE);
		employeeDao.add(company.getId(), Collections.singleton(supervisor));
		employeeDao.addSupervisors(company.getId(), employee.getId(),
				Collections.singleton(supervisor.getId()), true);

		try {
			Employee employee2 = new Employee();
			employee2.setLogin("employee"); // same as other employee
			employee2.setHashedPass("hashed");
			employee2.setEmail("email2");
			employee2.setFirstName("first");
			employee2.setLastName("last");
			employee2.setSuffix("suffix");
			employee2.setDivision("division");
			employee2.setPersonnelType(PersonnelType.EMPLOYEE);
			employeeDao.add(company.getId(), Collections.singleton(employee2));
			throw new RuntimeException("No unique constraint was thrown");
		} catch (DatabaseException uniqueConstraint) {
			// Expected
		}

		try {
			Employee employee2 = new Employee();
			employee2.setLogin("employee2");
			employee2.setHashedPass("hashed");
			employee2.setEmail("EMAIL"); // same as other employee
			employee2.setFirstName("first");
			employee2.setLastName("last");
			employee2.setSuffix("suffix");
			employee2.setDivision("division");
			employee2.setPersonnelType(PersonnelType.EMPLOYEE);
			employeeDao.add(company.getId(), Collections.singleton(employee2));
			throw new RuntimeException("No unique constraint was thrown");
		} catch (DatabaseException uniqueConstraint) {
			// Expected
		}

		employees = employeeDao.getAll(company.getId(), null);
		assertNotNull(employees);
		assertEquals(2, employees.size());
		assertTrue(employees.contains(employee));
		assertTrue(employees.contains(supervisor));

		employees = employeeDao.getAll(company.getId(), enrichments);
		assertNotNull(employees);
		assertEquals(2, employees.size());
		assertTrue(employees.contains(employee));
		assertTrue(employees.contains(supervisor));
		Employee getAllEmployee = null;
		Employee getAllSupervisor = null;
		for (Employee e : employees)
			if (e.getId() == employee.getId())
				getAllEmployee = e;
			else if (e.getId() == supervisor.getId())
				getAllSupervisor = e;
		assertNotNull(getAllEmployee);
		assertNotNull(getAllSupervisor);
		assertEquals(employee, getAllEmployee);
		assertEquals(supervisor, getAllSupervisor);
		assertEquals(2, getAllEmployee.getRoles().size());
		assertTrue(getAllEmployee.isManager());
		assertTrue(getAllEmployee.isPayroll());
		assertEquals(1, getAllEmployee.getSupervisors().size());
		assertEquals(supervisor, getAllEmployee.getSupervisors().iterator()
				.next());
		assertEquals(1, getAllSupervisor.getSupervised().size());
		assertEquals(employee, getAllSupervisor.getSupervised().iterator()
				.next());

		Employee getEmployee = employeeDao.get(company.getId(),
				employee.getId(), null);
		assertEquals(employee, getEmployee);
		assertEquals(0, getEmployee.getRoles().size());

		getEmployee = employeeDao.get(company.getId(), employee.getId(),
				enrichments);
		assertEquals(employee, getEmployee);
		assertEquals(2, getEmployee.getRoles().size());
		assertTrue(getEmployee.isManager());
		assertTrue(getEmployee.isPayroll());

		Employee loginEmployee = employeeDao.getLogin(company.getId(),
				employee.getLogin());
		assertEquals(employee, loginEmployee);
		assertEquals(0, loginEmployee.getRoles().size());

		loginEmployee = employeeDao.getLogin(company.getId(),
				employee.getEmail());
		assertEquals(employee, loginEmployee);
		assertEquals(0, loginEmployee.getRoles().size());

		loginEmployee = employeeDao.getLogin(company.getId(), employee
				.getEmail().toUpperCase());
		assertEquals(employee, loginEmployee);
		assertEquals(0, loginEmployee.getRoles().size());

		employee.setEmail("New Email");
		employeeDao.update(company.getId(), Collections.singleton(employee));
		getEmployee = employeeDao.get(company.getId(), employee.getId(), null);
		assertEquals(employee, getEmployee);
		assertEquals(0, getEmployee.getRoles().size());

		roleDao.delete(employee.getId(), employee.getRoles());
		employeeDao.deleteSupervisors(company.getId(), employee.getId(),
				Collections.singleton(supervisor.getId()));
		getEmployee = employeeDao.get(company.getId(), employee.getId(),
				enrichments);
		assertEquals(employee, getEmployee);
		assertEquals(0, getEmployee.getRoles().size());
		assertEquals(0, getEmployee.getSupervisors().size());

		employeeDao.delete(company.getId(),
				Arrays.asList(employee.getId(), supervisor.getId()));
		getEmployee = employeeDao.get(company.getId(), employee.getId(), null);
		assertNull(getEmployee);

		employees = employeeDao.getAll(company.getId(), null);
		assertNotNull(employees);
		assertEquals(0, employees.size());
	}
}
