package com.arcblaze.arctime.db.dao;

import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.SortedMap;

import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.model.Company;

/**
 * Performs operations on companies in the system.
 */
public interface CompanyDao {
	/**
	 * @param includeInactive
	 *            whether inactive companies should be included
	 * 
	 * @return the total number of companies in the system
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public int count(boolean includeInactive) throws DatabaseException;

	/**
	 * @param begin
	 *            the beginning of the date range for which active company
	 *            information should be calculated per month (inclusive)
	 * @param end
	 *            the ending of the date range for which active company
	 *            information should be calculated per month (exclusive)
	 * 
	 * @return a map containing the first day of each month along with the max
	 *         active company count during that month, for every month between
	 *         the begin and end dates, whether data is available for that month
	 *         or not
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided begin or end dates are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public SortedMap<Date, Integer> getActiveByMonth(Date begin, Date end)
			throws DatabaseException;

	/**
	 * @param day
	 *            the day for which the active company count applies
	 * @param count
	 *            the count value indicating the number of active companies for
	 *            the specified day
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided day or count values are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void setActiveCompanies(Date day, Integer count)
			throws DatabaseException;

	/**
	 * @param id
	 *            the unique id of the company to be retrieved
	 * 
	 * @return the requested company, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Company get(Integer id) throws DatabaseException;

	/**
	 * @return all available companies, possibly empty but never {@code null}
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Set<Company> getAll() throws DatabaseException;

	/**
	 * @param companies
	 *            the new companies to be added
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void add(Company... companies) throws DatabaseException;

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
	public void update(Company... companies) throws DatabaseException;

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
	public void delete(Integer... companyIds) throws DatabaseException;

	/**
	 * @param companyIds
	 *            the unique ids of the companies to be deleted
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void delete(Collection<Integer> companyIds) throws DatabaseException;
}
