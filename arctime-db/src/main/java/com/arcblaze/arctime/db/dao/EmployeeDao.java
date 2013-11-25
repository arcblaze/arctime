package com.arcblaze.arctime.db.dao;

import java.util.Collection;
import java.util.Set;

import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.model.Employee;
import com.arcblaze.arctime.model.Enrichment;

/**
 * Performs operations on employees in the system.
 */
public interface EmployeeDao {
	/**
	 * @param employee
	 *            the employee for which existence will be checked
	 * 
	 * @return whether the specified employee exists
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided employee is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public boolean exists(Employee employee) throws DatabaseException;

	/**
	 * @param id
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
	public Employee get(Integer id, Set<Enrichment> enrichments)
			throws DatabaseException;

	/**
	 * @param enrichments
	 *            the types of additional data to include in the returned
	 *            employees
	 * 
	 * @return all available employees, possibly empty but never {@code null}
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Set<Employee> getAll(Set<Enrichment> enrichments)
			throws DatabaseException;

	/**
	 * @param employees
	 *            the new employees to be added
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void add(Collection<Employee> employees) throws DatabaseException;

	/**
	 * @param employees
	 *            the new employees to be added
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void update(Collection<Employee> employees) throws DatabaseException;

	/**
	 * @param ids
	 *            the unique ids of the employees to be deleted
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void delete(Collection<Integer> ids) throws DatabaseException;
}
