package com.arcblaze.arctime.db;

import com.arcblaze.arctime.config.Property;
import com.arcblaze.arctime.db.dao.AssignmentDao;
import com.arcblaze.arctime.db.dao.AuditLogDao;
import com.arcblaze.arctime.db.dao.BillDao;
import com.arcblaze.arctime.db.dao.CompanyDao;
import com.arcblaze.arctime.db.dao.HolidayDao;
import com.arcblaze.arctime.db.dao.RoleDao;
import com.arcblaze.arctime.db.dao.TaskDao;
import com.arcblaze.arctime.db.dao.TimesheetDao;
import com.arcblaze.arctime.db.dao.UserDao;
import com.arcblaze.arctime.db.dao.jdbc.JdbcAssignmentDao;
import com.arcblaze.arctime.db.dao.jdbc.JdbcAuditLogDao;
import com.arcblaze.arctime.db.dao.jdbc.JdbcBillDao;
import com.arcblaze.arctime.db.dao.jdbc.JdbcCompanyDao;
import com.arcblaze.arctime.db.dao.jdbc.JdbcHolidayDao;
import com.arcblaze.arctime.db.dao.jdbc.JdbcRoleDao;
import com.arcblaze.arctime.db.dao.jdbc.JdbcTaskDao;
import com.arcblaze.arctime.db.dao.jdbc.JdbcTimesheetDao;
import com.arcblaze.arctime.db.dao.jdbc.JdbcUserDao;

/**
 * Used to retrieve DAO instances to work with the configured back-end database.
 */
public class DaoFactory {
	/** Holds the type of back-end data store used in the cached DAOs. */
	private static DatabaseType cachedDatabaseType = null;

	private static CompanyDao cachedCompanyDao = null;
	private static UserDao cachedUserDao = null;
	private static RoleDao cachedRoleDao = null;
	private static TimesheetDao cachedTimesheetDao = null;
	private static TaskDao cachedTaskDao = null;
	private static AssignmentDao cachedAssignmentDao = null;
	private static HolidayDao cachedHolidayDao = null;
	private static BillDao cachedBillDao = null;
	private static AuditLogDao cachedAuditLogDao = null;

	/**
	 * @return an {@link CompanyDao} based on the currently configured database
	 */
	public static CompanyDao getCompanyDao() {
		DatabaseType type = DatabaseType.parse(Property.DB_TYPE.getString());
		if (type != cachedDatabaseType) {
			clearCachedDaos();
			cachedDatabaseType = type;
		}

		if (cachedCompanyDao == null) {
			if (DatabaseType.JDBC.equals(type))
				cachedCompanyDao = new JdbcCompanyDao();
			else
				throw new RuntimeException("Invalid database type: " + type);
			cachedDatabaseType = type;
		}

		return cachedCompanyDao;
	}

	/**
	 * @return an {@link UserDao} based on the currently configured database
	 */
	public static UserDao getUserDao() {
		DatabaseType type = DatabaseType.parse(Property.DB_TYPE.getString());
		if (type != cachedDatabaseType) {
			clearCachedDaos();
			cachedDatabaseType = type;
		}

		if (cachedUserDao == null) {
			if (DatabaseType.JDBC.equals(type))
				cachedUserDao = new JdbcUserDao();
			else
				throw new RuntimeException("Invalid database type: " + type);
			cachedDatabaseType = type;
		}

		return cachedUserDao;
	}

	/**
	 * @return an {@link RoleDao} based on the currently configured database
	 */
	public static RoleDao getRoleDao() {
		DatabaseType type = DatabaseType.parse(Property.DB_TYPE.getString());
		if (type != cachedDatabaseType) {
			clearCachedDaos();
			cachedDatabaseType = type;
		}

		if (cachedRoleDao == null) {
			if (DatabaseType.JDBC.equals(type))
				cachedRoleDao = new JdbcRoleDao();
			else
				throw new RuntimeException("Invalid database type: " + type);
			cachedDatabaseType = type;
		}

		return cachedRoleDao;
	}

	/**
	 * @return an {@link TimesheetDao} based on the currently configured
	 *         database
	 */
	public static TimesheetDao getTimesheetDao() {
		DatabaseType type = DatabaseType.parse(Property.DB_TYPE.getString());
		if (type != cachedDatabaseType) {
			clearCachedDaos();
			cachedDatabaseType = type;
		}

		if (cachedTimesheetDao == null) {
			if (DatabaseType.JDBC.equals(type))
				cachedTimesheetDao = new JdbcTimesheetDao();
			else
				throw new RuntimeException("Invalid database type: " + type);
			cachedDatabaseType = type;
		}

		return cachedTimesheetDao;
	}

	/**
	 * @return an {@link TaskDao} based on the currently configured database
	 */
	public static TaskDao getTaskDao() {
		DatabaseType type = DatabaseType.parse(Property.DB_TYPE.getString());
		if (type != cachedDatabaseType) {
			clearCachedDaos();
			cachedDatabaseType = type;
		}

		if (cachedTaskDao == null) {
			if (DatabaseType.JDBC.equals(type))
				cachedTaskDao = new JdbcTaskDao();
			else
				throw new RuntimeException("Invalid database type: " + type);
			cachedDatabaseType = type;
		}

		return cachedTaskDao;
	}

	/**
	 * @return an {@link AssignmentDao} based on the currently configured
	 *         database
	 */
	public static AssignmentDao getAssignmentDao() {
		DatabaseType type = DatabaseType.parse(Property.DB_TYPE.getString());
		if (type != cachedDatabaseType) {
			clearCachedDaos();
			cachedDatabaseType = type;
		}

		if (cachedAssignmentDao == null) {
			if (DatabaseType.JDBC.equals(type))
				cachedAssignmentDao = new JdbcAssignmentDao();
			else
				throw new RuntimeException("Invalid database type: " + type);
			cachedDatabaseType = type;
		}

		return cachedAssignmentDao;
	}

	/**
	 * @return an {@link HolidayDao} based on the currently configured database
	 */
	public static HolidayDao getHolidayDao() {
		DatabaseType type = DatabaseType.parse(Property.DB_TYPE.getString());
		if (type != cachedDatabaseType) {
			clearCachedDaos();
			cachedDatabaseType = type;
		}

		if (cachedHolidayDao == null) {
			if (DatabaseType.JDBC.equals(type))
				cachedHolidayDao = new JdbcHolidayDao();
			else
				throw new RuntimeException("Invalid database type: " + type);
			cachedDatabaseType = type;
		}

		return cachedHolidayDao;
	}

	/**
	 * @return an {@link BillDao} based on the currently configured database
	 */
	public static BillDao getBillDao() {
		DatabaseType type = DatabaseType.parse(Property.DB_TYPE.getString());
		if (type != cachedDatabaseType) {
			clearCachedDaos();
			cachedDatabaseType = type;
		}

		if (cachedBillDao == null) {
			if (DatabaseType.JDBC.equals(type))
				cachedBillDao = new JdbcBillDao();
			else
				throw new RuntimeException("Invalid database type: " + type);
			cachedDatabaseType = type;
		}

		return cachedBillDao;
	}

	/**
	 * @return an {@link AuditLogDao} based on the currently configured database
	 */
	public static AuditLogDao getAuditLogDao() {
		DatabaseType type = DatabaseType.parse(Property.DB_TYPE.getString());
		if (type != cachedDatabaseType) {
			clearCachedDaos();
			cachedDatabaseType = type;
		}

		if (cachedAuditLogDao == null) {
			if (DatabaseType.JDBC.equals(type))
				cachedAuditLogDao = new JdbcAuditLogDao();
			else
				throw new RuntimeException("Invalid database type: " + type);
			cachedDatabaseType = type;
		}

		return cachedAuditLogDao;
	}

	private static synchronized void clearCachedDaos() {
		cachedCompanyDao = null;
		cachedUserDao = null;
		cachedRoleDao = null;
		cachedTaskDao = null;
		cachedAssignmentDao = null;
		cachedHolidayDao = null;
		cachedBillDao = null;
		cachedAuditLogDao = null;
	}

	/**
	 * Reset the internal DAOs and connections to be recreated when next needed.
	 */
	public static synchronized void reset() {
		clearCachedDaos();
		ConnectionManager.reset();
	}
}
