package com.arcblaze.arctime.config;

import java.io.File;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Describes the individual properties that are recognized in this system.
 */
public enum Property {
	/**
	 * The configuration file used to load the system configuration properties.
	 */
	CONFIG_FILE("./conf/arctime.properties") {
		@Override
		String getDefaultValue() {
			String configFile = System.getProperty("config.file");
			if (!StringUtils.isBlank(configFile))
				return configFile;
			return super.getDefaultValue();
		}
	},

	/**
	 * The type of back-end database being used.
	 */
	DB_TYPE("mysql"),

	/**
	 * The driver class name to use when creating JDBC connections to the
	 * database.
	 */
	DB_DRIVER("com.mysql.jdbc.Driver"),

	/**
	 * The JDBC connection URL to use when accessing the database.
	 */
	DB_URL("jdbc:mysql://localhost/arctime"),

	/**
	 * The name of the user to use when authenticating with the database.
	 */
	DB_USERNAME("arctime"),

	/**
	 * The password of the user to use when authenticating with the database.
	 */
	DB_PASSWORD,

	;

	/** This will be used to log messages. */
	private final static Logger log = LoggerFactory.getLogger(Property.class);

	/** Holds all of the loaded configuration properties. */
	private static Configuration config = load();

	/**
	 * The default value to be returned when the requested configuration
	 * property is not available.
	 */
	private final String defaultValue;

	/**
	 * The default constructor is used to create properties that do not have a
	 * default value.
	 */
	private Property() {
		this.defaultValue = null;
	}

	/**
	 * @param defaultValue
	 *            the default value to use for the configuration property
	 */
	private Property(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the configuration property key name used when accessing this
	 *         property from a property source
	 */
	private String getKey() {
		return this.name().toLowerCase().replaceAll("_", ".");
	}

	/**
	 * @return the default value for this configuration property
	 */
	String getDefaultValue() {
		return this.defaultValue;
	}

	/**
	 * @return the value for this property as a string, potentially the default
	 *         value if no specific value was configured
	 */
	public String getString() {
		return config.getString(getKey(), getDefaultValue());
	}

	/**
	 * @return the value for this property as an int, potentially the default
	 *         value if no specific value was configured
	 * 
	 * @throws ConversionException
	 *             if the configured value or the default value for the property
	 *             is not an integer
	 */
	public int getInt() {
		try {
			return config.getInt(getKey(), Integer.parseInt(getDefaultValue()));
		} catch (NumberFormatException badNumber) {
			throw new ConversionException("Not an integer value.", badNumber);
		}
	}

	/**
	 * @return the value for this property as a long, potentially the default
	 *         value if no specific value was configured
	 * 
	 * @throws ConversionException
	 *             if the configured value or the default value for the property
	 *             is not a long
	 */
	public long getLong() {
		try {
			return config.getLong(getKey(), Long.parseLong(getDefaultValue()));
		} catch (NumberFormatException badNumber) {
			throw new ConversionException("Not a long value.", badNumber);
		}
	}

	/**
	 * @return the value for this property as a boolean, potentially the default
	 *         value if no specific value was configured
	 * 
	 * @throws ConversionException
	 *             if the configured value or the default value for the property
	 *             is not a boolean
	 */
	public boolean getBoolean() {
		return config.getBoolean(getKey(),
				Boolean.parseBoolean(getDefaultValue()));
	}

	/**
	 * @param fileName
	 *            the file path containing the configuration information
	 * 
	 * @return the {@link Configuration} for the specified configuration file
	 */
	private static Configuration load() {
		File configFile = new File(CONFIG_FILE.getDefaultValue());

		if (configFile.exists()) {
			log.info("Loading configuration from "
					+ configFile.getAbsolutePath());
			try {
				PropertiesConfiguration conf = new PropertiesConfiguration(
						configFile);
				conf.setDelimiterParsingDisabled(true);
				conf.setReloadingStrategy(new FileChangedReloadingStrategy());
				return conf;
			} catch (ConfigurationException badConfig) {
				throw new RuntimeException(
						"Failed to load system configuration from "
								+ configFile.getAbsolutePath(), badConfig);
			}
		}

		throw new RuntimeException(
				"Failed to load system configuration from non-existent file: "
						+ configFile.getAbsolutePath());
	}
}
