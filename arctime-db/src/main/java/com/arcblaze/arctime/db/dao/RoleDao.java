package com.arcblaze.arctime.db.dao;

import java.util.Collection;
import java.util.Set;

import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.model.Role;

/**
 * Performs operations on user roles in the system.
 */
public interface RoleDao {
	/**
	 * @param userId
	 *            the unique id of the user for which roles will be identified
	 * 
	 * @return the identified roles for the provided user, possibly empty but
	 *         never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Set<Role> get(Integer userId) throws DatabaseException;

	/**
	 * @param userId
	 *            the unique id of the user for which roles will be added
	 * @param roles
	 *            the new roles to be given to the specified user
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void add(Integer userId, Role... roles) throws DatabaseException;

	/**
	 * @param userId
	 *            the unique id of the user for which roles will be added
	 * @param roles
	 *            the new roles to be given to the specified user
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void add(Integer userId, Collection<Role> roles)
			throws DatabaseException;

	/**
	 * @param userId
	 *            the unique id of the user for which roles will be removed
	 * @param roles
	 *            the roles to be removed from the specified user
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void delete(Integer userId, Role... roles) throws DatabaseException;

	/**
	 * @param userId
	 *            the unique id of the user for which roles will be removed
	 * @param roles
	 *            the roles to be removed from the specified user
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void delete(Integer userId, Collection<Role> roles)
			throws DatabaseException;
}
