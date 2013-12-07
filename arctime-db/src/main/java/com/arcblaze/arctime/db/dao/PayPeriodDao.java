package com.arcblaze.arctime.db.dao;

import java.util.Collection;
import java.util.Date;

import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.model.PayPeriod;

/**
 * Performs operations on pay periods in the system.
 */
public interface PayPeriodDao {
	/**
	 * @param companyId
	 *            the unique id of the company for which pay period information
	 *            should be retrieved
	 * @param begin
	 *            the begin date of the pay period to be retrieved
	 * 
	 * @return the requested pay period, potentially {@code null} if the
	 *         requested pay period does not exist
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided company or begin date is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public PayPeriod get(Integer companyId, Date begin)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which pay period information
	 *            will be added
	 * @param payPeriods
	 *            the new pay periods to be inserted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void add(Integer companyId, Collection<PayPeriod> payPeriods)
			throws DatabaseException;
}
