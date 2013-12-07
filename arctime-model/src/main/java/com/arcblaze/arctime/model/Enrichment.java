package com.arcblaze.arctime.model;

import org.apache.commons.lang.StringUtils;

/**
 * Describes the personnel category type of an account in this system.
 */
public enum Enrichment {
	/**
	 * Used to enrich employee data with the employee's roles.
	 */
	ROLES,

	/**
	 * Used to enrich employee data with the employees being supervised.
	 */
	SUPERVISED,

	/**
	 * Used to enrich employee data with the employee's supervisors.
	 */
	SUPERVISERS,

	/**
	 * Used to enrich timesheet data with the employee that owns each timesheet.
	 */
	EMPLOYEES,

	/**
	 * Used to enrich timesheet data with the pay period in which the timesheet
	 * falls.
	 */
	PAY_PERIODS,

	/**
	 * Used to enrich timesheet data with audit log information.
	 */
	AUDIT_LOGS,

	/**
	 * Used to enrich timesheet data with the tasks for which the employee is
	 * assigned during the pay period.
	 */
	TASKS,

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
