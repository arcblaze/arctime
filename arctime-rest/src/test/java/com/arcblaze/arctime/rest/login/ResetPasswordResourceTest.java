package com.arcblaze.arctime.rest.login;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import javax.mail.MessagingException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.arcblaze.arctime.db.DaoFactory;
import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.dao.EmployeeDao;
import com.arcblaze.arctime.db.util.TestDatabase;
import com.arcblaze.arctime.mail.SendResetPasswordEmail;
import com.arcblaze.arctime.model.Company;
import com.arcblaze.arctime.model.Employee;
import com.arcblaze.arctime.model.Password;
import com.arcblaze.arctime.model.PersonnelType;

/**
 * Perform testing of the password reset capabilities.
 */
public class ResetPasswordResourceTest {
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
	 * Test how the resource responds when the provided login value is null.
	 */
	@Test(expected = BadRequestException.class)
	public void testNullUser() {
		ResetPasswordResource resource = new ResetPasswordResource();
		resource.reset(null);
	}

	/**
	 * Test how the resource responds when the provided login value is blank.
	 */
	@Test(expected = BadRequestException.class)
	public void testBlankUser() {
		ResetPasswordResource resource = new ResetPasswordResource();
		resource.reset("  ");
	}

	/**
	 * Test how the resource responds when the user doesn't exist.
	 */
	@Test(expected = NotFoundException.class)
	public void testNonExistentUser() {
		ResetPasswordResource resource = new ResetPasswordResource();
		resource.reset("nonexistent-user");
	}

	/**
	 * Test how the resource responds when an existing login is provided as
	 * input.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testExistingUserByLogin() throws DatabaseException {
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

		SendResetPasswordEmail mockEmailSender = Mockito
				.mock(SendResetPasswordEmail.class);
		Password mockPassword = Mockito.mock(Password.class);
		Mockito.when(mockPassword.random()).thenReturn("new-password");
		Mockito.when(mockPassword.hash("new-password")).thenReturn(
				"hashed-password");

		ResetPasswordResource resource = new ResetPasswordResource(
				mockEmailSender, mockPassword);
		resource.reset("employee");

		// Make sure the password was updated.
		Employee updated = employeeDao.getLogin(employee.getLogin());
		assertEquals("hashed-password", updated.getHashedPass());
	}

	/**
	 * Test how the resource responds when an existing email address is provided
	 * as input.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testExistingUserByEmail() throws DatabaseException {
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

		SendResetPasswordEmail mockEmailSender = Mockito
				.mock(SendResetPasswordEmail.class);
		Password mockPassword = Mockito.mock(Password.class);
		Mockito.when(mockPassword.random()).thenReturn("new-password");
		Mockito.when(mockPassword.hash("new-password")).thenReturn(
				"hashed-password");

		ResetPasswordResource resource = new ResetPasswordResource(
				mockEmailSender, mockPassword);
		resource.reset("email@whatever.com");

		// Make sure the password was updated.
		Employee updated = employeeDao.getLogin(employee.getLogin());
		assertEquals("hashed-password", updated.getHashedPass());
	}

	/**
	 * Test how the resource responds when there is a problem sending the email.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 * @throws MessagingException
	 *             if there is an email-sending problem
	 */
	@Test
	public void testExistingUserEmailError() throws DatabaseException,
			MessagingException {
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

		SendResetPasswordEmail emailSender = new SendResetPasswordEmail();
		SendResetPasswordEmail mockEmailSender = Mockito.spy(emailSender);
		Mockito.doThrow(MessagingException.class).when(mockEmailSender)
				.send(employee, "new-password");

		Password mockPassword = Mockito.mock(Password.class);
		Mockito.when(mockPassword.random()).thenReturn("new-password");
		Mockito.when(mockPassword.hash("new-password")).thenReturn(
				"hashed-password");

		try {
			ResetPasswordResource resource = new ResetPasswordResource(
					mockEmailSender, mockPassword);
			resource.reset("employee");
			Assert.fail("Expected an email-sending error");
		} catch (InternalServerErrorException expected) {
			// This is expected.
		}

		// Make sure the password was reverted back to the original.
		Employee updated = employeeDao.getLogin(employee.getLogin());
		assertEquals("hashed", updated.getHashedPass());
	}
}
