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
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
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
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
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
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
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
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
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
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Set<Role> getRoles(Integer employeeId) throws DatabaseException;

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
	public void addRoles(Integer employeeId, Collection<Role> roles)
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
	public void deleteRoles(Integer employeeId, Collection<Role> roles)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which employees will be
	 *            retrieved
	 * @param employeeId
	 *            the unique id of the employee whose supervisors are to be
	 *            retrieved
	 * @param enrichments
	 *            the types of additional data to include in the returned
	 *            employees
	 * 
	 * @return all the supervisors for the specified employee, possibly empty
	 *         but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Set<Employee> getSupervisors(Integer companyId, Integer employeeId,
			Set<Enrichment> enrichments) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which supervisors are being
	 *            added
	 * @param employeeId
	 *            the unique id of the employee who will gain the new
	 *            supervisors
	 * @param supervisorIds
	 *            the unique ids of the employees that are to become supervisors
	 * @param primary
	 *            whether the supervisors are to be added as primary supervisors
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void addSupervisors(Integer companyId, Integer employeeId,
			Collection<Integer> supervisorIds, boolean primary)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which supervisors are being
	 *            removed
	 * @param employeeId
	 *            the unique id of the employee who will lose the specified
	 *            supervisors
	 * @param supervisorIds
	 *            the unique ids of the employees that are to be removed as
	 *            supervisors
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void deleteSupervisors(Integer companyId, Integer employeeId,
			Collection<Integer> supervisorIds) throws DatabaseException;
}
