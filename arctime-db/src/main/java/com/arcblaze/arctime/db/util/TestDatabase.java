package com.arcblaze.arctime.db.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.arcblaze.arctime.config.Property;
import com.arcblaze.arctime.db.ConnectionManager;

/**
 * Used to initialize a simple in-memory database for testing purposes.
 */
public class TestDatabase {
	/**
	 * Loads the table schema into the testing database for testing.
	 * 
	 * @throws FileNotFoundException
	 *             if the schema configuration SQL file is not found
	 * @throws IOException
	 *             if there is a problem reading the schema configuration SQL
	 *             file
	 * @throws SQLException
	 *             if there is a problem building the schema
	 */
	public static void initialize() throws SQLException, FileNotFoundException,
			IOException {
		System.setProperty("arctime.configurationFile",
				"../conf/arctime-config.properties");

		Property.DB_DRIVER.set("org.hsqldb.jdbc.JDBCDriver");
		Property.DB_URL.set("jdbc:hsqldb:mem:testdb");
		Property.DB_USERNAME.set("SA");
		Property.DB_PASSWORD.set("");

		File schema = new File("src/main/resources/hsqldb/db.sql");
		if (!schema.exists()) {
			URL resource = TestDatabase.class.getClassLoader().getResource(
					"hsqldb/db.sql");
			if (resource != null)
				schema = new File(resource.getFile());
		}

		StringBuilder sql = new StringBuilder();
		try (FileReader fr = new FileReader(schema);
				BufferedReader br = new BufferedReader(fr)) {
			String line = null;
			while ((line = br.readLine()) != null) {
				sql.append(line);
				sql.append("\n");
			}
		}

		try (Connection conn = ConnectionManager.getConnection();
				Statement stmt = conn.createStatement()) {
			stmt.execute(sql.toString());
		}
	}
}
