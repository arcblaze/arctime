package com.arcblaze.arctime.db.dao;

import java.util.Collection;
import java.util.Set;

import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.db.DatabaseUniqueConstraintException;
import com.arcblaze.arctime.model.Enrichment;
import com.arcblaze.arctime.model.User;

/**
 * Performs operations on users in the system.
 */
public interface UserDao {
	/**
	 * @param includeInactive
	 *            whether inactive user accounts should be included
	 * 
	 * @return the total number of user accounts in the system
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public int count(boolean includeInactive) throws DatabaseException;

	/**
	 * @param login
	 *            the login value provided by the user
	 * 
	 * @return the requested user, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameters are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public User getLogin(String login) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the user will be
	 *            retrieved
	 * @param userId
	 *            the unique id of the user to retrieve
	 * @param enrichments
	 *            the types of additional data to include in the returned users
	 * 
	 * @return the requested user, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public User get(Integer companyId, Integer userId,
			Enrichment... enrichments) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the user will be
	 *            retrieved
	 * @param userId
	 *            the unique id of the user to retrieve
	 * @param enrichments
	 *            the types of additional data to include in the returned users
	 * 
	 * @return the requested user, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public User get(Integer companyId, Integer userId,
			Set<Enrichment> enrichments) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which users will be retrieved
	 * @param enrichments
	 *            the types of additional data to include in the returned users
	 * 
	 * @return all available users, possibly empty but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Set<User> getAll(Integer companyId, Enrichment... enrichments)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which users will be retrieved
	 * @param enrichments
	 *            the types of additional data to include in the returned users
	 * 
	 * @return all available users, possibly empty but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Set<User> getAll(Integer companyId, Set<Enrichment> enrichments)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the users will be added
	 * @param users
	 *            the new users to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseUniqueConstraintException
	 *             if there is a problem adding the user due to a unique
	 *             constraint violation
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void add(Integer companyId, User... users)
			throws DatabaseUniqueConstraintException, DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the users will be added
	 * @param users
	 *            the new users to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseUniqueConstraintException
	 *             if there is a problem adding the user due to a unique
	 *             constraint violation
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void add(Integer companyId, Collection<User> users)
			throws DatabaseUniqueConstraintException, DatabaseException;

	/**
	 * Save property updates within the provided users to the database. Note
	 * that this does not save any password changes, the
	 * {@link #setPassword(Integer, String, String)} method is used for that.
	 * 
	 * @param companyId
	 *            the unique id of the company for which the users will be
	 *            updated
	 * @param users
	 *            the users to be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseUniqueConstraintException
	 *             if there is a problem adding the user due to a unique
	 *             constraint violation
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void update(Integer companyId, User... users)
			throws DatabaseUniqueConstraintException, DatabaseException;

	/**
	 * Save property updates within the provided users to the database. Note
	 * that this does not save any password changes, the
	 * {@link #setPassword(Integer, String, String)} method is used for that.
	 * 
	 * @param companyId
	 *            the unique id of the company for which the users will be
	 *            updated
	 * @param users
	 *            the users to be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseUniqueConstraintException
	 *             if there is a problem adding the user due to a unique
	 *             constraint violation
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void update(Integer companyId, Collection<User> users)
			throws DatabaseUniqueConstraintException, DatabaseException;

	/**
	 * Save property updates within the provided users to the database. Note
	 * that this does not save any password changes.
	 * 
	 * @param userId
	 *            the unique id of the user whose password is being reset
	 * @param hashedPass
	 *            the new hashed password value to set for the user
	 * @param salt
	 *            the salt value to use when hashing the user's password
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void setPassword(Integer userId, String hashedPass, String salt)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the users will be
	 *            deleted
	 * @param userIds
	 *            the unique ids of the users to be deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void delete(Integer companyId, Integer... userIds)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the users will be
	 *            deleted
	 * @param userIds
	 *            the unique ids of the users to be deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void delete(Integer companyId, Collection<Integer> userIds)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which users will be retrieved
	 * @param userId
	 *            the unique id of the user whose supervisors are to be
	 *            retrieved
	 * @param enrichments
	 *            the types of additional data to include in the returned users
	 * 
	 * @return all the supervisors for the specified user, possibly empty but
	 *         never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Set<User> getSupervisors(Integer companyId, Integer userId,
			Set<Enrichment> enrichments) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which supervisors are being
	 *            added
	 * @param userId
	 *            the unique id of the user who will gain the new supervisors
	 * @param primary
	 *            whether the supervisors are to be added as primary supervisors
	 * @param supervisorIds
	 *            the unique ids of the users that are to become supervisors
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void addSupervisors(Integer companyId, Integer userId,
			boolean primary, Integer... supervisorIds) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which supervisors are being
	 *            added
	 * @param userId
	 *            the unique id of the user who will gain the new supervisors
	 * @param primary
	 *            whether the supervisors are to be added as primary supervisors
	 * @param supervisorIds
	 *            the unique ids of the users that are to become supervisors
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void addSupervisors(Integer companyId, Integer userId,
			boolean primary, Collection<Integer> supervisorIds)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which supervisors are being
	 *            removed
	 * @param userId
	 *            the unique id of the user who will lose the specified
	 *            supervisors
	 * @param supervisorIds
	 *            the unique ids of the users that are to be removed as
	 *            supervisors
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void deleteSupervisors(Integer companyId, Integer userId,
			Integer... supervisorIds) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which supervisors are being
	 *            removed
	 * @param userId
	 *            the unique id of the user who will lose the specified
	 *            supervisors
	 * @param supervisorIds
	 *            the unique ids of the users that are to be removed as
	 *            supervisors
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void deleteSupervisors(Integer companyId, Integer userId,
			Collection<Integer> supervisorIds) throws DatabaseException;
}
