package com.arcblaze.arctime.db.dao;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.model.Transaction;

/**
 * Performs operations on transactions in the system.
 */
public interface TransactionDao {
	/**
	 * @param companyId
	 *            the unique id of the company for which the sum of amounts will
	 *            be retrieved
	 * @param begin
	 *            the beginning boundary of the time frame where transactions
	 *            should be retrieved (inclusive)
	 * @param end
	 *            the ending boundary of the time frame where transactions
	 *            should be retrieved (exclusive)
	 * 
	 * @return the sum of amounts for all transactions within the specified date
	 *         range, possibly zero if no transactions were available during
	 *         that time period
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public BigDecimal amountBetween(Integer companyId, Date begin, Date end)
			throws DatabaseException;

	/**
	 * @param begin
	 *            the beginning boundary of the time frame where transactions
	 *            should be retrieved (inclusive)
	 * @param end
	 *            the ending boundary of the time frame where transactions
	 *            should be retrieved (exclusive)
	 * 
	 * @return the sum of amounts for all transactions within the specified date
	 *         range, possibly zero if no transactions were available during
	 *         that time period
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public BigDecimal amountBetween(Date begin, Date end)
			throws DatabaseException;

	/**
	 * @param id
	 *            the unique id of the transaction to be retrieved
	 * 
	 * @return the requested transaction, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Transaction get(Integer id) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the transactions will
	 *            be retrieved
	 * 
	 * @return all available transactions associated with the specified company,
	 *         possibly empty but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Set<Transaction> getForCompany(Integer companyId)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the transactions will
	 *            be retrieved
	 * @param begin
	 *            the beginning boundary of the time frame where transactions
	 *            should be retrieved (inclusive)
	 * @param end
	 *            the ending boundary of the time frame where transactions
	 *            should be retrieved (exclusive)
	 * 
	 * @return all available transactions associated with the specified company,
	 *         possibly empty but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id or date range is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Set<Transaction> getForCompany(Integer companyId, Date begin,
			Date end) throws DatabaseException;

	/**
	 * @param transactions
	 *            the new transactions to be added
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void add(Transaction... transactions) throws DatabaseException;

	/**
	 * @param transactions
	 *            the new transactions to be added
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void add(Collection<Transaction> transactions)
			throws DatabaseException;

	/**
	 * @param transactions
	 *            the transactions to be updated
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void update(Transaction... transactions) throws DatabaseException;

	/**
	 * @param transactions
	 *            the transactions to be updated
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void update(Collection<Transaction> transactions)
			throws DatabaseException;

	/**
	 * @param transactionIds
	 *            the unique ids of the transactions to be deleted
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void delete(Integer... transactionIds) throws DatabaseException;

	/**
	 * @param transactionIds
	 *            the unique ids of the transactions to be deleted
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void delete(Collection<Integer> transactionIds)
			throws DatabaseException;
}
