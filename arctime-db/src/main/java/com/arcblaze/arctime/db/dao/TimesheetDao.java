package com.arcblaze.arctime.db.dao;

import java.util.Collection;
import java.util.Set;

import com.arcblaze.arctime.db.DatabaseException;
import com.arcblaze.arctime.model.Enrichment;
import com.arcblaze.arctime.model.PayPeriod;
import com.arcblaze.arctime.model.Timesheet;
import com.arcblaze.arctime.model.User;
import com.arcblaze.arctime.model.util.HolidayConfigurationException;

/**
 * Performs operations on timesheets in the system.
 */
public interface TimesheetDao {
	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be retrieved
	 * @param timesheetId
	 *            the unique id of the timesheet to be retrieved
	 * @param enrichments
	 *            the types of additional data to include in the returned
	 *            timesheets
	 * 
	 * @return the requested timesheet, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing holiday configurations during
	 *             enrichment
	 */
	public Timesheet get(Integer companyId, Integer timesheetId,
			Enrichment... enrichments) throws DatabaseException,
			HolidayConfigurationException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be retrieved
	 * @param timesheetId
	 *            the unique id of the timesheet to be retrieved
	 * @param enrichments
	 *            the types of additional data to include in the returned
	 *            timesheets
	 * 
	 * @return the requested timesheet, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing holiday configurations during
	 *             enrichment
	 */
	public Timesheet get(Integer companyId, Integer timesheetId,
			Set<Enrichment> enrichments) throws DatabaseException,
			HolidayConfigurationException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be retrieved
	 * @param timesheetIds
	 *            the unique ids of the timesheets to be retrieved
	 * @param enrichments
	 *            the types of additional data to include in the returned
	 *            timesheets
	 * 
	 * @return the requested timesheet, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing holiday configurations during
	 *             enrichment
	 */
	public Set<Timesheet> getGroup(Integer companyId,
			Set<Integer> timesheetIds, Enrichment... enrichments)
			throws DatabaseException, HolidayConfigurationException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be retrieved
	 * @param timesheetIds
	 *            the unique ids of the timesheets to be retrieved
	 * @param enrichments
	 *            the types of additional data to include in the returned
	 *            timesheets
	 * 
	 * @return the requested timesheet, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing holiday configurations during
	 *             enrichment
	 */
	public Set<Timesheet> getGroup(Integer companyId,
			Set<Integer> timesheetIds, Set<Enrichment> enrichments)
			throws DatabaseException, HolidayConfigurationException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be retrieved
	 * @param userId
	 *            the unique id of the user that owns the timesheet to be
	 *            retrieved
	 * @param payPeriod
	 *            the pay period for which timesheet data will be returned
	 * @param enrichments
	 *            the types of additional data to include in the returned
	 *            timesheets
	 * 
	 * @return the requested timesheet, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing holiday configurations during
	 *             enrichment
	 */
	public Timesheet getForUser(Integer companyId, Integer userId,
			PayPeriod payPeriod, Enrichment... enrichments)
			throws DatabaseException, HolidayConfigurationException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be retrieved
	 * @param userId
	 *            the unique id of the user that owns the timesheet to be
	 *            retrieved
	 * @param payPeriod
	 *            the pay period for which timesheet data will be returned
	 * @param enrichments
	 *            the types of additional data to include in the returned
	 *            timesheets
	 * 
	 * @return the requested timesheet, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing holiday configurations during
	 *             enrichment
	 */
	public Timesheet getForUser(Integer companyId, Integer userId,
			PayPeriod payPeriod, Set<Enrichment> enrichments)
			throws DatabaseException, HolidayConfigurationException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be retrieved
	 * @param userId
	 *            the unique id of the user that owns the timesheet to be
	 *            retrieved
	 * @param enrichments
	 *            the types of additional data to include in the returned
	 *            timesheets
	 * 
	 * @return the requested timesheet, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing holiday configurations during
	 *             enrichment
	 */
	public Timesheet getLatestForUser(Integer companyId, Integer userId,
			Enrichment... enrichments) throws DatabaseException,
			HolidayConfigurationException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be retrieved
	 * @param userId
	 *            the unique id of the user that owns the timesheet to be
	 *            retrieved
	 * @param enrichments
	 *            the types of additional data to include in the returned
	 *            timesheets
	 * 
	 * @return the requested timesheet, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing holiday configurations during
	 *             enrichment
	 */
	public Timesheet getLatestForUser(Integer companyId, Integer userId,
			Set<Enrichment> enrichments) throws DatabaseException,
			HolidayConfigurationException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be added
	 * @param timesheets
	 *            the new timesheets to be added
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void add(Integer companyId, Timesheet... timesheets)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be added
	 * @param timesheets
	 *            the new timesheets to be added
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void add(Integer companyId, Collection<Timesheet> timesheets)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be updated
	 * @param completed
	 *            the new value for the completed status
	 * @param timesheetIds
	 *            the unique ids of the timesheets for which completion status
	 *            will be updated
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void complete(Integer companyId, boolean completed,
			Integer... timesheetIds) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be updated
	 * @param completed
	 *            the new value for the completed status
	 * @param timesheetIds
	 *            the unique ids of the timesheets for which completion status
	 *            will be updated
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void complete(Integer companyId, boolean completed,
			Collection<Integer> timesheetIds) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be updated
	 * @param approved
	 *            the new value for the approved status
	 * @param approver
	 *            the supervisor user that approved the timesheets
	 * @param timesheetIds
	 *            the unique ids of the timesheets for which approval status
	 *            will be updated
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void approve(Integer companyId, boolean approved, User approver,
			Integer... timesheetIds) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be updated
	 * @param approved
	 *            the new value for the approved status
	 * @param approver
	 *            the supervisor user that approved the timesheets
	 * @param timesheetIds
	 *            the unique ids of the timesheets for which approval status
	 *            will be updated
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void approve(Integer companyId, boolean approved, User approver,
			Collection<Integer> timesheetIds) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be updated
	 * @param verified
	 *            the new value for the verified status
	 * @param verifier
	 *            the payroll user that verified the timesheets
	 * @param timesheetIds
	 *            the unique ids of the timesheets for which verification status
	 *            will be updated
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void verify(Integer companyId, boolean verified, User verifier,
			Integer... timesheetIds) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be updated
	 * @param verified
	 *            the new value for the verified status
	 * @param verifier
	 *            the payroll user that verified the timesheets
	 * @param timesheetIds
	 *            the unique ids of the timesheets for which verification status
	 *            will be updated
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void verify(Integer companyId, boolean verified, User verifier,
			Collection<Integer> timesheetIds) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be updated
	 * @param exported
	 *            the new value for the export status
	 * @param exporter
	 *            the payroll user that exported the timesheets
	 * @param timesheetIds
	 *            the unique ids of the timesheets for which export status will
	 *            be updated
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void export(Integer companyId, boolean exported, User exporter,
			Integer... timesheetIds) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be updated
	 * @param exported
	 *            the new value for the export status
	 * @param exporter
	 *            the payroll user that exported the timesheets
	 * @param timesheetIds
	 *            the unique ids of the timesheets for which export status will
	 *            be updated
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void export(Integer companyId, boolean exported, User exporter,
			Collection<Integer> timesheetIds) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be updated
	 * @param timesheetIds
	 *            the unique ids of the timesheets to be deleted
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void delete(Integer companyId, Integer... timesheetIds)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be updated
	 * @param timesheetIds
	 *            the unique ids of the timesheets to be deleted
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	public void delete(Integer companyId, Collection<Integer> timesheetIds)
			throws DatabaseException;
}
