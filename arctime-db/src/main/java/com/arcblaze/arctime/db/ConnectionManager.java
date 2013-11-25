package com.arcblaze.arctime.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;

import com.arcblaze.arctime.config.Property;

/**
 * Used to manage database connections.
 */
public class ConnectionManager {
	/**
	 * Used to pool connections to the database.
	 */
	private static BasicDataSource dataSource;

	/**
	 * Keep a hash of the database configuration options so we can reload the
	 * data source when the configuration changes.
	 */
	private static int configurationHash = 0;

	/**
	 * @return a {@link Connection} to the database
	 * 
	 * @throws SQLException
	 *             if there is a database connection problem
	 */
	public static synchronized Connection getConnection() throws SQLException {
		buildDataSource();

		return dataSource.getConnection();
	}

	/**
	 * Build or reset the internal data source if necessary.
	 * 
	 * @throws SQLException
	 *             if there is a database connection problem
	 */
	private static synchronized void buildDataSource() throws SQLException {
		String driver = Property.DB_DRIVER.getString();
		String url = Property.DB_URL.getString();
		String user = Property.DB_USERNAME.getString();
		String pass = Property.DB_PASSWORD.getString();

		int newConfigurationHash = driver.hashCode() * url.hashCode()
				* user.hashCode() * pass.hashCode();

		if (dataSource == null || configurationHash != newConfigurationHash) {
			if (dataSource != null)
				dataSource.close();

			dataSource = new BasicDataSource();
			dataSource.setDriverClassName(driver);
			dataSource.setUrl(url);
			dataSource.setUsername(user);
			dataSource.setPassword(pass);
			dataSource.setDefaultAutoCommit(true);
		}
	}
}
