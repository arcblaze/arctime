package com.arcblaze.arctime.db.dao;

import java.util.Collection;
import java.util.Set;

import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.model.Contract;
import com.arcblaze.arctime.model.Enrichment;

/**
 * Performs operations on contracts in the system.
 */
public interface ContractDao {
	/**
	 * @param companyId
	 *            the unique id of the company for which the contract will be
	 *            retrieved
	 * @param contractId
	 *            the unique id of the contract to retrieve
	 * @param enrichments
	 *            the types of additional data to include in the returned
	 *            contracts
	 * 
	 * @return the requested contract, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Contract get(Integer companyId, Integer contractId,
			Set<Enrichment> enrichments) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which contracts will be
	 *            retrieved
	 * @param enrichments
	 *            the types of additional data to include in the returned
	 *            contracts
	 * 
	 * @return all available contracts, possibly empty but never {@code null}
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Set<Contract> getAll(Integer companyId, Set<Enrichment> enrichments)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the contracts will be
	 *            added
	 * @param contracts
	 *            the new contracts to be added
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void add(Integer companyId, Collection<Contract> contracts)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the contracts will be
	 *            updated
	 * @param contracts
	 *            the contracts to be updated
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void update(Integer companyId, Collection<Contract> contracts)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the contracts will be
	 *            deleted
	 * @param contractIds
	 *            the unique ids of the contracts to be deleted
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void delete(Integer companyId, Collection<Integer> contractIds)
			throws DatabaseException;
}
