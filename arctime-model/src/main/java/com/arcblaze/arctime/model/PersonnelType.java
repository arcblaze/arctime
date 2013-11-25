package com.arcblaze.arctime.model;

import org.apache.commons.lang.StringUtils;

/**
 * Describes the personnel category type of an account in this system.
 */
public enum PersonnelType {
	/**
	 * A direct employee.
	 */
	EMPLOYEE,

	/**
	 * A consultant.
	 */
	CONSULTANT,

	;

	/**
	 * Attempt to convert the provided value back into a personnel type with
	 * more flexibility than what the {@link #valueOf(String)} method provides.
	 * 
	 * @param value
	 *            the value to attempt conversion into a {@link PersonnelType}
	 * 
	 * @return the identified {@link PersonnelType}, or {@code null} if the
	 *         conversion fails
	 */
	public static PersonnelType parse(String value) {
		for (PersonnelType personnelType : values())
			if (StringUtils.equalsIgnoreCase(personnelType.name(), value))
				return personnelType;

		try {
			return PersonnelType.valueOf(value);
		} catch (IllegalArgumentException badValue) {
			return null;
		}
	}
}
