package com.arcblaze.arctime.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Represents a financial transaction that took place in this system.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Transaction implements Comparable<Transaction> {
	/**
	 * The unique id of this transaction.
	 */
	private Integer id;

	/**
	 * The unique id of the company for which this transaction applies.
	 */
	private Integer companyId;

	/**
	 * The unique id of the user that invoked this transaction.
	 */
	private Integer userId;

	/**
	 * The time stamp when this transaction took place.
	 */
	private Date timestamp;

	/**
	 * The type of transaction that occurred.
	 */
	private TransactionType transactionType;

	/**
	 * A brief description of the transaction that took place.
	 */
	private String description;

	/**
	 * The amount associated with this transaction.
	 */
	private BigDecimal amount;

	/**
	 * Any additional notes associated with the transaction.
	 */
	private String notes;

	/**
	 * Default constructor.
	 */
	public Transaction() {
		// Nothing to do.
	}

	/**
	 * @return the unique id of this assignment
	 */
	@XmlElement
	public Integer getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            the new unique id of this assignment
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id value is invalid
	 */
	public Transaction setId(Integer id) {
		if (id == null)
			throw new IllegalArgumentException("Invalid null id");
		if (id < 0)
			throw new IllegalArgumentException("Invalid negative id");

		this.id = id;
		return this;
	}

	/**
	 * @return the unique id of the company involved in this transaction
	 */
	@XmlElement
	public Integer getCompanyId() {
		return this.companyId;
	}

	/**
	 * @param companyId
	 *            the new unique id of the company involved in this transaction
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id value is invalid
	 */
	public Transaction setCompanyId(Integer companyId) {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (companyId < 0)
			throw new IllegalArgumentException("Invalid negative company id");

		this.companyId = companyId;
		return this;
	}

	/**
	 * @return the unique id of the user billing the hours
	 */
	@XmlElement
	public Integer getUserId() {
		return this.userId;
	}

	/**
	 * @param userId
	 *            the new unique id of the user billing the hours
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id value is invalid
	 */
	public Transaction setUserId(Integer userId) {
		if (userId == null)
			throw new IllegalArgumentException("Invalid null user id");
		if (userId < 0)
			throw new IllegalArgumentException("Invalid negative user id");

		this.userId = userId;
		return this;
	}

	/**
	 * @return the time stamp indicating when this bill was created
	 */
	@XmlElement
	public Date getTimestamp() {
		return this.timestamp == null ? null : new Date(
				this.timestamp.getTime());
	}

	/**
	 * @param timestamp
	 *            the new value indicating when this bill was created
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided time stamp value is invalid
	 */
	public Transaction setTimestamp(Date timestamp) {
		if (timestamp == null)
			throw new IllegalArgumentException("Invalid null timestamp value");

		this.timestamp = timestamp;
		return this;
	}

	/**
	 * @return the type of transaction this object represents
	 */
	@XmlElement
	public TransactionType getTransactionType() {
		return this.transactionType;
	}

	/**
	 * @param transactionType
	 *            the new value describing the type of this transaction
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided transaction type value is invalid
	 */
	public Transaction setTransactionType(TransactionType transactionType) {
		if (transactionType == null)
			throw new IllegalArgumentException(
					"Invalid null transaction type value");

		this.transactionType = transactionType;
		return this;
	}

	/**
	 * @return a description of the transaction
	 */
	@XmlElement
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description
	 *            the new value describing this transaction
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided description value is invalid
	 */
	public Transaction setDescription(String description) {
		if (StringUtils.isBlank(description))
			throw new IllegalArgumentException(
					"Invalid blank description value");

		this.description = description;
		return this;
	}

	/**
	 * @return the amount associated with this transaction
	 */
	@XmlElement
	public BigDecimal getAmount() {
		return this.amount;
	}

	/**
	 * @param amount
	 *            the new amount associated with this transaction
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided amount value is invalid
	 */
	public Transaction setAmount(Float amount) {
		if (amount == null)
			throw new IllegalArgumentException("Invalid null amount");

		this.amount = new BigDecimal(amount).setScale(2);
		return this;
	}

	/**
	 * @param amount
	 *            the new amount associated with this transaction
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided amount value is invalid
	 */
	public Transaction setAmount(String amount) {
		if (amount == null)
			throw new IllegalArgumentException("Invalid null amount");

		try {
			this.amount = new BigDecimal(amount).setScale(2);
		} catch (NumberFormatException badNumber) {
			throw new IllegalArgumentException("Invalid number: " + amount,
					badNumber);
		}
		return this;
	}

	/**
	 * @param amount
	 *            the new amount associated with this transaction
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided amount value is invalid
	 */
	public Transaction setAmount(BigDecimal amount) {
		if (amount == null)
			throw new IllegalArgumentException("Invalid null amount");

		this.amount = new BigDecimal(amount.toPlainString()).setScale(2);
		return this;
	}

	/**
	 * @return whether this transaction contains additional notes describing
	 *         what took place
	 */
	public boolean hasNotes() {
		return this.notes != null;
	}

	/**
	 * @return the notes providing additional information that describes this
	 *         transaction
	 */
	@XmlElement
	public String getNotes() {
		return this.notes;
	}

	/**
	 * @param notes
	 *            the new notes value associated with this transaction
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided notes value is invalid
	 */
	public Transaction setNotes(String notes) {
		if (StringUtils.isBlank(notes))
			throw new IllegalArgumentException("Invalid blank notes value");

		this.notes = notes;
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("id", getId());
		builder.append("companyId", getCompanyId());
		builder.append("userId", getUserId());
		builder.append("timestamp", getTimestamp());
		builder.append("transactionType", getTransactionType());
		builder.append("description", getDescription());
		builder.append("amount", getAmount());
		builder.append("notes", getNotes());
		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Transaction) {
			Transaction other = (Transaction) obj;
			EqualsBuilder builder = new EqualsBuilder();
			builder.append(getId(), other.getId());
			builder.append(getCompanyId(), other.getCompanyId());
			builder.append(getUserId(), other.getUserId());
			builder.append(getTimestamp(), other.getTimestamp());
			builder.append(getTransactionType(), other.getTransactionType());
			builder.append(getDescription(), other.getDescription());
			builder.append(getAmount(), other.getAmount());
			builder.append(getNotes(), other.getNotes());
			return builder.isEquals();
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(getId());
		builder.append(getCompanyId());
		builder.append(getUserId());
		builder.append(getTimestamp());
		builder.append(getTransactionType());
		builder.append(getDescription());
		builder.append(getAmount());
		builder.append(getNotes());
		return builder.toHashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Transaction other) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(other.getCompanyId(), getCompanyId());
		builder.append(getUserId(), other.getUserId());
		builder.append(other.getTimestamp(), getTimestamp());
		builder.append(getTransactionType(), other.getTransactionType());
		builder.append(getDescription(), other.getDescription());
		builder.append(getAmount(), other.getAmount());
		builder.append(getNotes(), other.getNotes());
		return builder.toComparison();
	}
}
