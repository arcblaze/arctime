package com.arcblaze.arctime.db.dao;

import java.util.Collection;
import java.util.Set;

import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.model.Holiday;
import com.arcblaze.arctime.model.PayPeriod;
import com.arcblaze.arctime.model.util.HolidayConfigurationException;

/**
 * Performs operations on holidays in the system.
 */
public interface HolidayDao {
	/**
	 * @param companyId
	 *            the unique id of the company for which the holiday should be
	 *            retrieved
	 * @param id
	 *            the unique id of the holiday to be retrieved
	 * 
	 * @return the requested holiday, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing the holiday configuration
	 *             information
	 */
	public Holiday get(Integer companyId, Integer id) throws DatabaseException,
			HolidayConfigurationException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the holidays should be
	 *            retrieved
	 * 
	 * @return all available holidays, possibly empty but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing the holiday configuration
	 *             information
	 */
	public Set<Holiday> getAll(Integer companyId) throws DatabaseException,
			HolidayConfigurationException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the holidays should be
	 *            retrieved
	 * 
	 * @param payPeriod
	 *            the {@link PayPeriod} for which holidays should be retrieved
	 * 
	 * @return all available holidays that fall within the provided
	 *         {@link PayPeriod}, possibly empty but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing the holiday configuration
	 *             values
	 * 
	 */
	public Set<Holiday> getForPayPeriod(Integer companyId, PayPeriod payPeriod)
			throws DatabaseException, HolidayConfigurationException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the holidays should be
	 *            added
	 * @param holidays
	 *            the new holidays to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void add(Integer companyId, Holiday... holidays)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the holidays should be
	 *            added
	 * @param holidays
	 *            the new holidays to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void add(Integer companyId, Collection<Holiday> holidays)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the holidays should be
	 *            updated
	 * @param holidays
	 *            the holidays to be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void update(Integer companyId, Holiday... holidays)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the holidays should be
	 *            updated
	 * @param holidays
	 *            the holidays to be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void update(Integer companyId, Collection<Holiday> holidays)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the holidays should be
	 *            deleted
	 * @param holidayIds
	 *            the unique ids of the holidays to be deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void delete(Integer companyId, Integer... holidayIds)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the holidays should be
	 *            deleted
	 * @param holidayIds
	 *            the unique ids of the holidays to be deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void delete(Integer companyId, Collection<Integer> holidayIds)
			throws DatabaseException;
}
