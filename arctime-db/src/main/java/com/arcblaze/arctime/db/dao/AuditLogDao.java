package com.arcblaze.arctime.db.dao;

import java.util.Collection;
import java.util.Set;

import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.model.AuditLog;

/**
 * Performs operations on audit logs in the system.
 */
public interface AuditLogDao {
	/**
	 * @param companyId
	 *            the unique id of the company for which audit logs will be
	 *            retrieved
	 * @param timesheetId
	 *            the unique id of the timesheet for which audit logs will be
	 *            retrieved
	 * 
	 * @return all available audit logs for the provided timesheet, possibly
	 *         empty but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public Set<AuditLog> getForTimesheet(Integer companyId, Integer timesheetId)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which audit logs will be
	 *            added
	 * @param auditLogs
	 *            the new audit logs to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void add(Integer companyId, AuditLog... auditLogs)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which audit logs will be
	 *            added
	 * @param auditLogs
	 *            the new audit logs to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void add(Integer companyId, Collection<AuditLog> auditLogs)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which audit logs will be
	 *            deleted
	 * @param timesheetIds
	 *            the unique ids of the timesheets for which audit logs will be
	 *            deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void delete(Integer companyId, Integer... timesheetIds)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which audit logs will be
	 *            deleted
	 * @param timesheetIds
	 *            the unique ids of the timesheets for which audit logs will be
	 *            deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void delete(Integer companyId, Collection<Integer> timesheetIds)
			throws DatabaseException;
}
