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
	 *             if there is a problem with the database
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

		Employee supervisor1 = new Employee();
		supervisor1.setLogin("supervisor1");
		supervisor1.setHashedPass("hashed");
		supervisor1.setEmail("supervisor1");
		supervisor1.setFirstName("first");
		supervisor1.setLastName("last");
		supervisor1.setSuffix("suffix");
		supervisor1.setDivision("division");
		supervisor1.setPersonnelType(PersonnelType.EMPLOYEE);
		employeeDao.add(company.getId(), Collections.singleton(supervisor1));
		employeeDao.addSupervisors(company.getId(), employee.getId(),
				Collections.singleton(supervisor1.getId()), true);

		Employee supervisor2 = new Employee();
		supervisor2.setLogin("supervisor2");
		supervisor2.setHashedPass("hashed");
		supervisor2.setEmail("supervisor2");
		supervisor2.setFirstName("first");
		supervisor2.setLastName("last");
		supervisor2.setSuffix("suffix");
		supervisor2.setDivision("division");
		supervisor2.setPersonnelType(PersonnelType.EMPLOYEE);
		employeeDao.add(company.getId(), Collections.singleton(supervisor2));
		employeeDao.addSupervisors(company.getId(), employee.getId(),
				Collections.singleton(supervisor2.getId()), false);

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
		} catch (DatabaseUniqueConstraintException uniqueConstraint) {
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
		} catch (DatabaseUniqueConstraintException uniqueConstraint) {
			// Expected
		}

		employees = employeeDao.getAll(company.getId(), null);
		assertNotNull(employees);
		assertEquals(3, employees.size());
		assertTrue(employees.contains(employee));
		assertTrue(employees.contains(supervisor1));
		assertTrue(employees.contains(supervisor2));

		employees = employeeDao.getAll(company.getId(), enrichments);
		assertNotNull(employees);
		assertEquals(3, employees.size());
		assertTrue(employees.contains(employee));
		assertTrue(employees.contains(supervisor1));
		assertTrue(employees.contains(supervisor2));
		Employee getAllEmployee = null;
		Employee getAllSupervisor1 = null;
		Employee getAllSupervisor2 = null;
		for (Employee e : employees)
			if (e.getId() == employee.getId())
				getAllEmployee = e;
			else if (e.getId() == supervisor1.getId())
				getAllSupervisor1 = e;
			else if (e.getId() == supervisor2.getId())
				getAllSupervisor2 = e;
		assertNotNull(getAllEmployee);
		assertNotNull(getAllSupervisor1);
		assertNotNull(getAllSupervisor2);
		assertEquals(employee, getAllEmployee);
		assertEquals(supervisor1, getAllSupervisor1);
		assertEquals(supervisor2, getAllSupervisor2);
		assertEquals(2, getAllEmployee.getRoles().size());
		assertTrue(getAllEmployee.isManager());
		assertTrue(getAllEmployee.isPayroll());
		assertEquals(2, getAllEmployee.getSupervisors().size());
		assertTrue(getAllEmployee.getSupervisors().contains(supervisor1));
		assertTrue(getAllEmployee.getSupervisors().contains(supervisor2));
		assertEquals(1, getAllSupervisor1.getSupervised().size());
		assertEquals(employee, getAllSupervisor1.getSupervised().iterator()
				.next());
		assertEquals(1, getAllSupervisor2.getSupervised().size());
		assertEquals(employee, getAllSupervisor2.getSupervised().iterator()
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

		Employee loginEmployee = employeeDao.getLogin(employee.getLogin());
		assertEquals(employee, loginEmployee);
		assertEquals(0, loginEmployee.getRoles().size());

		loginEmployee = employeeDao.getLogin(employee.getEmail());
		assertEquals(employee, loginEmployee);
		assertEquals(0, loginEmployee.getRoles().size());

		loginEmployee = employeeDao.getLogin(employee.getEmail().toUpperCase());
		assertEquals(employee, loginEmployee);
		assertEquals(0, loginEmployee.getRoles().size());

		employee.setEmail("New Email");
		employeeDao.update(company.getId(), Collections.singleton(employee));
		getEmployee = employeeDao.get(company.getId(), employee.getId(), null);
		assertEquals(employee, getEmployee);
		assertEquals(0, getEmployee.getRoles().size());

		roleDao.delete(employee.getId(), employee.getRoles());
		employeeDao.deleteSupervisors(company.getId(), employee.getId(),
				Arrays.asList(supervisor1.getId(), supervisor2.getId()));
		getEmployee = employeeDao.get(company.getId(), employee.getId(),
				enrichments);
		assertEquals(employee, getEmployee);
		assertEquals(0, getEmployee.getRoles().size());
		assertEquals(0, getEmployee.getSupervisors().size());

		employeeDao.delete(company.getId(), Arrays.asList(employee.getId(),
				supervisor1.getId(), supervisor2.getId()));
		getEmployee = employeeDao.get(company.getId(), employee.getId(), null);
		assertNull(getEmployee);

		employees = employeeDao.getAll(company.getId(), null);
		assertNotNull(employees);
		assertEquals(0, employees.size());
	}
}
