package com.arcblaze.arctime.model;

import org.apache.commons.lang.StringUtils;

/**
 * Describes the personnel category type of an account in this system.
 */
public enum Enrichment {
	/**
	 * Used to enrich employee data with the employee's roles.
	 */
	ROLE,

	/**
	 * Used to enrich employee data with the employees being supervised.
	 */
	SUPERVISED,

	;

	/**
	 * Attempt to convert the provided value back into a role with more
	 * flexibility than what the {@link #valueOf(String)} method provides.
	 * 
	 * @param value
	 *            the value to attempt conversion into a {@link Enrichment}
	 * 
	 * @return the identified {@link Enrichment}, or {@code null} if the
	 *         conversion fails
	 */
	public static Enrichment parse(String value) {
		for (Enrichment role : values())
			if (StringUtils.equalsIgnoreCase(role.name(), value))
				return role;

		try {
			return Enrichment.valueOf(value);
		} catch (IllegalArgumentException badValue) {
			return null;
		}
	}
}
