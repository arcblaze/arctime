package com.arcblaze.arctime.db.dao;

import java.util.Collection;
import java.util.Set;

import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.model.Company;
import com.arcblaze.arctime.model.Enrichment;

/**
 * Performs operations on companies in the system.
 */
public interface CompanyDao {
	/**
	 * @param id
	 *            the unique id of the company to be retrieved
	 * @param enrichments
	 *            the types of additional data to include in the returned
	 *            company
	 * 
	 * @return the requested company, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Company get(Integer id, Set<Enrichment> enrichments)
			throws DatabaseException;

	/**
	 * @param enrichments
	 *            the types of additional data to include in the returned
	 *            companies
	 * 
	 * @return all available companies, possibly empty but never {@code null}
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Set<Company> getAll(Set<Enrichment> enrichments)
			throws DatabaseException;

	/**
	 * @param companies
	 *            the new companies to be added
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void add(Collection<Company> companies) throws DatabaseException;

	/**
	 * @param companies
	 *            the companies to be updated
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void update(Collection<Company> companies) throws DatabaseException;

	/**
	 * @param companyIds
	 *            the unique ids of the companies to be deleted
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void delete(Collection<Integer> companyIds) throws DatabaseException;
}
