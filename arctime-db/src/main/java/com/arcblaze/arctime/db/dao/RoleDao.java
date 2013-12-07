package com.arcblaze.arctime.db.dao;

import java.util.Collection;
import java.util.Set;

import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.model.Role;

/**
 * Performs operations on employee roles in the system.
 */
public interface RoleDao {
	/**
	 * @param employeeId
	 *            the unique id of the employee for which roles will be
	 *            identified
	 * 
	 * @return the identified roles for the provided employee, possibly empty
	 *         but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Set<Role> get(Integer employeeId) throws DatabaseException;

	/**
	 * @param employeeId
	 *            the unique id of the employee for which roles will be added
	 * @param roles
	 *            the new roles to be given to the specified employee
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void add(Integer employeeId, Collection<Role> roles)
			throws DatabaseException;

	/**
	 * @param employeeId
	 *            the unique id of the employee for which roles will be removed
	 * @param roles
	 *            the roles to be removed from the specified employee
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void delete(Integer employeeId, Collection<Role> roles)
			throws DatabaseException;
}
