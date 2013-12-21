package com.arcblaze.arctime.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.arcblaze.arctime.db.dao.CompanyDao;
import com.arcblaze.arctime.db.util.TestDatabase;
import com.arcblaze.arctime.model.Company;

/**
 * Perform database integration testing.
 */
public class CompanyDaoTest {
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
		CompanyDao dao = DaoFactory.getCompanyDao();
		Set<Company> companies = dao.getAll();
		assertNotNull(companies);
		assertEquals(0, companies.size());

		Company company = new Company();
		company.setName("Company");
		company.setActive(true);

		dao.add(company);
		assertNotNull(company.getId());

		companies = dao.getAll();
		assertNotNull(companies);
		assertEquals(1, companies.size());
		Company getAllCompany = companies.iterator().next();
		assertEquals(company, getAllCompany);

		Company getCompany = dao.get(company.getId());
		assertEquals(company, getCompany);

		company.setName("New Name");
		dao.update(company);
		getCompany = dao.get(company.getId());
		assertEquals(company, getCompany);

		dao.delete(company.getId());
		getCompany = dao.get(company.getId());
		assertNull(getCompany);

		companies = dao.getAll();
		assertNotNull(companies);
		assertEquals(0, companies.size());
	}
}
