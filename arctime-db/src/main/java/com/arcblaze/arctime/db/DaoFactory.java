package com.arcblaze.arctime.db;

import com.arcblaze.arctime.config.Property;
import com.arcblaze.arctime.db.dao.CompanyDao;
import com.arcblaze.arctime.db.dao.TaskDao;
import com.arcblaze.arctime.db.dao.EmployeeDao;
import com.arcblaze.arctime.db.dao.HolidayDao;
import com.arcblaze.arctime.db.dao.jdbc.JdbcCompanyDao;
import com.arcblaze.arctime.db.dao.jdbc.JdbcTaskDao;
import com.arcblaze.arctime.db.dao.jdbc.JdbcEmployeeDao;
import com.arcblaze.arctime.db.dao.jdbc.JdbcHolidayDao;

/**
 * Used to retrieve DAO instances to work with the configured back-end database.
 */
public class DaoFactory {
	/** Holds the type of back-end data store used in the cached DAOs. */
	private static DatabaseType cachedDatabaseType = null;

	private static CompanyDao cachedCompanyDao = null;
	private static EmployeeDao cachedEmployeeDao = null;
	private static TaskDao cachedTaskDao = null;
	private static HolidayDao cachedHolidayDao = null;

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
	 * @return an {@link EmployeeDao} based on the currently configured database
	 */
	public static EmployeeDao getEmployeeDao() {
		DatabaseType type = DatabaseType.parse(Property.DB_TYPE.getString());
		if (type != cachedDatabaseType) {
			clearCachedDaos();
			cachedDatabaseType = type;
		}

		if (cachedEmployeeDao == null) {
			if (DatabaseType.JDBC.equals(type))
				cachedEmployeeDao = new JdbcEmployeeDao();
			else
				throw new RuntimeException("Invalid database type: " + type);
			cachedDatabaseType = type;
		}

		return cachedEmployeeDao;
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

	private static synchronized void clearCachedDaos() {
		cachedCompanyDao = null;
		cachedEmployeeDao = null;
		cachedTaskDao = null;
		cachedHolidayDao = null;
	}

	/**
	 * Reset the internal DAOs and connections to be recreated when next needed.
	 */
	public static synchronized void reset() {
		clearCachedDaos();
		ConnectionManager.reset();
	}
}
