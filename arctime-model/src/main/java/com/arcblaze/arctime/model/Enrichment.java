package com.arcblaze.arctime.model;

import org.apache.commons.lang.StringUtils;

/**
 * Describes the types of enrichment to support when retrieving model objects in
 * this system.
 */
public enum Enrichment {
	/**
	 * Used to enrich user data with the user's roles.
	 */
	ROLES,

	/**
	 * Used to enrich user data with the users being supervised.
	 */
	SUPERVISED,

	/**
	 * Used to enrich user data with the user's supervisors.
	 */
	SUPERVISERS,

	/**
	 * Used to enrich timesheet data with the user that owns each timesheet.
	 */
	USERS,

	/**
	 * Used to enrich timesheet data with the pay period in which the timesheet
	 * falls.
	 */
	PAY_PERIODS,

	/**
	 * Used to enrich timesheet data with the holidays that fall within the
	 * timesheet pay period.
	 */
	HOLIDAYS,

	/**
	 * Used to enrich timesheet data with audit log information.
	 */
	AUDIT_LOGS,

	/**
	 * Used to enrich timesheet data with the tasks for which the user is
	 * assigned during the pay period.
	 */
	TASKS,

	/**
	 * Used to enrich timesheet data with the hours billed to the timesheet
	 * during the pay period.
	 */
	BILLS,

	;

	/**
	 * Attempt to convert the provided value into an {@link Enrichment} with
	 * more flexibility than what the {@link #valueOf(String)} method provides.
	 * 
	 * @param value
	 *            the value to attempt conversion into a {@link Enrichment}
	 * 
	 * @return the identified {@link Enrichment}, or {@code null} if the
	 *         conversion fails
	 */
	public static Enrichment parse(String value) {
		for (Enrichment enrichment : values())
			if (StringUtils.equalsIgnoreCase(enrichment.name(), value))
				return enrichment;

		try {
			return Enrichment.valueOf(value);
		} catch (IllegalArgumentException badValue) {
			return null;
		}
	}
}
