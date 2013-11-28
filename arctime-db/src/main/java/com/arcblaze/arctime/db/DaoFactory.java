package com.arcblaze.arctime.db;

import org.apache.commons.lang.StringUtils;

import com.arcblaze.arctime.config.Property;
import com.arcblaze.arctime.db.dao.EmployeeDao;
import com.arcblaze.arctime.db.mysql.MySqlEmployeeDao;

/**
 * Used to retrieve DAO instances to work with the configured back-end database.
 */
public class DaoFactory {
	/** Holds the type of back-end data store used in the cached DAOs. */
	private static String cachedDaoType = null;

	/** A cached instance of the employee DAO. */
	private static EmployeeDao cachedEmployeeDao = null;

	/**
	 * @return an {@link EmployeeDao} based on the currently configured database
	 */
	public static EmployeeDao getEmployeeDao() {
		String type = Property.DB_TYPE.getString();
		if (!StringUtils.equals(cachedDaoType, type)) {
			clearCachedDaos();
			cachedDaoType = type;
		}

		if (cachedEmployeeDao == null) {
			if ("mysql".equals(type))
				cachedEmployeeDao = new MySqlEmployeeDao();
			else
				throw new RuntimeException("Invalid database type: " + type);
			cachedDaoType = type;
		}

		return cachedEmployeeDao;
	}

	private static synchronized void clearCachedDaos() {
		cachedEmployeeDao = null;
	}
}
