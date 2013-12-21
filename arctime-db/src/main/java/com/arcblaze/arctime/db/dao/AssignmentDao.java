package com.arcblaze.arctime.db.dao;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.model.Assignment;

/**
 * Performs operations on assignments in the system.
 */
public interface AssignmentDao {
	/**
	 * @param companyId
	 *            the unique id of the company for which the assignment will be
	 *            retrieved
	 * @param assignmentId
	 *            the unique id of the assignment to retrieve
	 * 
	 * @return the requested assignment, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Assignment get(Integer companyId, Integer assignmentId)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which assignments will be
	 *            retrieved
	 * @param userId
	 *            the unique id of the user for which assignments will be
	 *            retrieved
	 * @param day
	 *            the day for which assignment assignments must be valid, may be
	 *            {@code null} if date is not important
	 * 
	 * @return all available assignments for the user during the specified day,
	 *         possibly empty but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids or pay period are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Set<Assignment> getForUser(Integer companyId, Integer userId,
			Date day) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the assignments will be
	 *            added
	 * @param assignments
	 *            the new assignments to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void add(Integer companyId, Collection<Assignment> assignments)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the assignments will be
	 *            updated
	 * @param assignments
	 *            the assignments to be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void update(Integer companyId, Collection<Assignment> assignments)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the assignments will be
	 *            deleted
	 * @param assignmentIds
	 *            the unique ids of the assignments to be deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void delete(Integer companyId, Collection<Integer> assignmentIds)
			throws DatabaseException;
}
