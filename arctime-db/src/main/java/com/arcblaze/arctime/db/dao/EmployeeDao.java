package com.arcblaze.arctime.db.dao;

import java.util.Collection;
import java.util.Set;

import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.model.Employee;
import com.arcblaze.arctime.model.Enrichment;
import com.arcblaze.arctime.model.Role;

/**
 * Performs operations on employees in the system.
 */
public interface EmployeeDao {
	/**
	 * @param companyId
	 *            the unique id of the company for which the employee will be
	 *            retrieved
	 * @param login
	 *            the login value provided by the user
	 * 
	 * @return the requested employee, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameters are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Employee getLogin(Integer companyId, String login)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the employee will be
	 *            retrieved
	 * @param employeeId
	 *            the unique id of the employee to retrieve
	 * @param enrichments
	 *            the types of additional data to include in the returned
	 *            employees
	 * 
	 * @return the requested employee, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Employee get(Integer companyId, Integer employeeId,
			Set<Enrichment> enrichments) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which employees will be
	 *            retrieved
	 * @param enrichments
	 *            the types of additional data to include in the returned
	 *            employees
	 * 
	 * @return all available employees, possibly empty but never {@code null}
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Set<Employee> getAll(Integer companyId, Set<Enrichment> enrichments)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the employees will be
	 *            added
	 * @param employees
	 *            the new employees to be added
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void add(Integer companyId, Collection<Employee> employees)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the employees will be
	 *            updated
	 * @param employees
	 *            the employees to be updated
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void update(Integer companyId, Collection<Employee> employees)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the employees will be
	 *            deleted
	 * @param employeeIds
	 *            the unique ids of the employees to be deleted
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void delete(Integer companyId, Collection<Integer> employeeIds)
			throws DatabaseException;

	/**
	 * @param employeeId
	 *            the unique id of the employee for which roles will be
	 *            identified
	 * 
	 * @return the identified roles for the provided employee, possibly empty
	 *         but never {@code null}
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Set<Role> getRoles(Integer employeeId) throws DatabaseException;
}
