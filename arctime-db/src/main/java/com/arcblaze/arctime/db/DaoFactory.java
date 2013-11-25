package com.arcblaze.arctime.db;

import org.apache.commons.lang.StringUtils;

import com.arcblaze.arctime.config.Property;
import com.arcblaze.arctime.db.dao.EmployeeDao;
import com.arcblaze.arctime.db.mysql.MySqlEmployeeDao;

/**
 * Used to retrieve DAO instances to work with the configured back-end database.
 */
public class DaoFactory {
	private static String cachedDaoType = null;

	private static EmployeeDao cachedEmployeeDao = null;

	/**
	 * @return an {@link EmployeeDao} based on the currently configured database
	 */
	public static EmployeeDao getEmployeeDao() {
		String type = Property.DB_TYPE.getString();
		if (cachedEmployeeDao == null
				|| !StringUtils.equals(cachedDaoType, type)) {
			if ("mysql".equals(type))
				cachedEmployeeDao = new MySqlEmployeeDao();
			else
				throw new RuntimeException("Invalid database type: " + type);
			cachedDaoType = type;
		}

		return cachedEmployeeDao;
	}
}
