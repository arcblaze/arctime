package com.arcblaze.arctime.db.dao;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.model.PayPeriod;
import com.arcblaze.arctime.model.Task;

/**
 * Performs operations on tasks in the system.
 */
public interface TaskDao {
	/**
	 * @param companyId
	 *            the unique id of the company for which the task will be
	 *            retrieved
	 * @param taskId
	 *            the unique id of the task to retrieve
	 * 
	 * @return the requested task, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Task get(Integer companyId, Integer taskId) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which tasks will be retrieved
	 * 
	 * @return all available tasks, possibly empty but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Set<Task> getAll(Integer companyId) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which tasks will be retrieved
	 * @param payPeriod
	 *            the pay period for which to retrieve tasks
	 * @param userId
	 *            the unique id of the user for which tasks will be retrieved
	 * 
	 * @return all available tasks for the user during the provided pay period,
	 *         possibly empty but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids or pay period are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Set<Task> getForPayPeriod(Integer companyId, PayPeriod payPeriod,
			Integer userId) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which tasks will be retrieved
	 * @param userId
	 *            the unique id of the user for which tasks will be retrieved
	 * @param day
	 *            the day for which task assignments must be valid, may be
	 *            {@code null} if date is not important
	 * @param includeAdmin
	 *            whether administrative tasks should be included
	 * 
	 * @return all available tasks for the user on the given day, possibly empty
	 *         but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids or pay period are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Set<Task> getForUser(Integer companyId, Integer userId, Date day,
			boolean includeAdmin) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the tasks will be added
	 * @param tasks
	 *            the new tasks to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void add(Integer companyId, Task... tasks) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the tasks will be added
	 * @param tasks
	 *            the new tasks to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void add(Integer companyId, Collection<Task> tasks)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the tasks will be
	 *            updated
	 * @param tasks
	 *            the tasks to be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void update(Integer companyId, Task... tasks)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the tasks will be
	 *            updated
	 * @param tasks
	 *            the tasks to be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void update(Integer companyId, Collection<Task> tasks)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the tasks will be
	 *            deleted
	 * @param taskIds
	 *            the unique ids of the tasks to be deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void delete(Integer companyId, Integer... taskIds)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the tasks will be
	 *            deleted
	 * @param taskIds
	 *            the unique ids of the tasks to be deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void delete(Integer companyId, Collection<Integer> taskIds)
			throws DatabaseException;
}
