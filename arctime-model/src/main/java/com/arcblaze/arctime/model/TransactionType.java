package com.arcblaze.arctime.model;

import org.apache.commons.lang.StringUtils;

/**
 * Describes the types of financial transactions that happen in this system.
 */
public enum TransactionType {
	/**
	 * Represents a payment received from a customer.
	 */
	PAYMENT,

	/**
	 * Represents money being refunded back to a customer.
	 */
	REFUND,

	/**
	 * A catch-all for other forms of transactions.
	 */
	OTHER,

	;

	/**
	 * Attempt to convert the provided value into an {@link TransactionType}
	 * with more flexibility than what the {@link #valueOf(String)} method
	 * provides.
	 * 
	 * @param value
	 *            the value to attempt conversion into a {@link TransactionType}
	 * 
	 * @return the identified {@link TransactionType}, or {@code null} if the
	 *         conversion fails
	 */
	public static TransactionType parse(String value) {
		for (TransactionType transactionType : values())
			if (StringUtils.equalsIgnoreCase(transactionType.name(), value))
				return transactionType;

		try {
			return TransactionType.valueOf(value);
		} catch (IllegalArgumentException badValue) {
			return null;
		}
	}
}
