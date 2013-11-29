package com.arcblaze.arctime.db;

import org.apache.commons.lang.StringUtils;

import com.arcblaze.arctime.config.Property;
import com.arcblaze.arctime.db.dao.CompanyDao;
import com.arcblaze.arctime.db.dao.ContractDao;
import com.arcblaze.arctime.db.dao.EmployeeDao;
import com.arcblaze.arctime.db.dao.HolidayDao;
import com.arcblaze.arctime.db.mysql.MySqlCompanyDao;
import com.arcblaze.arctime.db.mysql.MySqlContractDao;
import com.arcblaze.arctime.db.mysql.MySqlEmployeeDao;
import com.arcblaze.arctime.db.mysql.MySqlHolidayDao;

/**
 * Used to retrieve DAO instances to work with the configured back-end database.
 */
public class DaoFactory {
	/** Holds the type of back-end data store used in the cached DAOs. */
	private static String cachedDaoType = null;

	private static CompanyDao cachedCompanyDao = null;
	private static EmployeeDao cachedEmployeeDao = null;
	private static ContractDao cachedContractDao = null;
	private static HolidayDao cachedHolidayDao = null;

	/**
	 * @return an {@link CompanyDao} based on the currently configured database
	 */
	public static CompanyDao getCompanyDao() {
		String type = Property.DB_TYPE.getString();
		if (!StringUtils.equals(cachedDaoType, type)) {
			clearCachedDaos();
			cachedDaoType = type;
		}

		if (cachedCompanyDao == null) {
			if ("mysql".equals(type))
				cachedCompanyDao = new MySqlCompanyDao();
			else
				throw new RuntimeException("Invalid database type: " + type);
			cachedDaoType = type;
		}

		return cachedCompanyDao;
	}

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

	/**
	 * @return an {@link ContractDao} based on the currently configured database
	 */
	public static ContractDao getContractDao() {
		String type = Property.DB_TYPE.getString();
		if (!StringUtils.equals(cachedDaoType, type)) {
			clearCachedDaos();
			cachedDaoType = type;
		}

		if (cachedContractDao == null) {
			if ("mysql".equals(type))
				cachedContractDao = new MySqlContractDao();
			else
				throw new RuntimeException("Invalid database type: " + type);
			cachedDaoType = type;
		}

		return cachedContractDao;
	}

	/**
	 * @return an {@link HolidayDao} based on the currently configured database
	 */
	public static HolidayDao getHolidayDao() {
		String type = Property.DB_TYPE.getString();
		if (!StringUtils.equals(cachedDaoType, type)) {
			clearCachedDaos();
			cachedDaoType = type;
		}

		if (cachedHolidayDao == null) {
			if ("mysql".equals(type))
				cachedHolidayDao = new MySqlHolidayDao();
			else
				throw new RuntimeException("Invalid database type: " + type);
			cachedDaoType = type;
		}

		return cachedHolidayDao;
	}

	private static synchronized void clearCachedDaos() {
		cachedCompanyDao = null;
		cachedEmployeeDao = null;
		cachedContractDao = null;
		cachedHolidayDao = null;
	}
}
