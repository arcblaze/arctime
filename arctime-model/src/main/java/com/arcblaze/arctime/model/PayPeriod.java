package com.arcblaze.arctime.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Represents a pay period.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class PayPeriod implements Comparable<PayPeriod> {
	/**
	 * The unique id of the company for which this pay period applies.
	 */
	private Integer companyId;

	/**
	 * The type of pay period configured.
	 */
	private PayPeriodType type;

	/**
	 * The first day in the pay period.
	 */
	private Date begin;

	/**
	 * The last day in the pay period.
	 */
	private Date end;

	/**
	 * Default constructor.
	 */
	public PayPeriod() {
		// Nothing to do.
	}

	/**
	 * @return the unique id of the company for which this pay period applies
	 */
	@XmlElement
	public Integer getCompanyId() {
		return this.companyId;
	}

	/**
	 * @param companyId
	 *            the new unique id of the company for which this pay period
	 *            applies
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id value is invalid
	 */
	public PayPeriod setCompanyId(Integer companyId) {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (companyId < 0)
			throw new IllegalArgumentException("Invalid negative company id");

		this.companyId = companyId;
		return this;
	}

	/**
	 * @return the type of this pay period
	 */
	@XmlElement
	public PayPeriodType getType() {
		return this.type;
	}

	/**
	 * @param type
	 *            the new type of pay period
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided type value is invalid
	 */
	public PayPeriod setType(PayPeriodType type) {
		if (type == null)
			throw new IllegalArgumentException("Invalid null type");

		this.type = type;
		return this;
	}

	/**
	 * @return the first day in this pay period
	 */
	@XmlElement
	public Date getBegin() {
		return this.begin == null ? null : new Date(this.begin.getTime());
	}

	/**
	 * @param begin
	 *            the new value indicating the first day of this pay period
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided begin value is invalid
	 */
	public PayPeriod setBegin(Date begin) {
		if (begin == null)
			throw new IllegalArgumentException("Invalid null begin value");

		this.begin = begin;
		return this;
	}

	/**
	 * @return the last day in this pay period
	 */
	@XmlElement
	public Date getEnd() {
		return this.end == null ? null : new Date(this.end.getTime());
	}

	/**
	 * @param end
	 *            the new value indicating the last day of this pay period
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided end value is invalid
	 */
	public PayPeriod setEnd(Date end) {
		if (end == null)
			throw new IllegalArgumentException("Invalid null end value");

		this.end = end;
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("companyId", getCompanyId());
		builder.append("type", getType());
		builder.append("begin", getBegin());
		builder.append("end", getBegin());
		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PayPeriod) {
			PayPeriod other = (PayPeriod) obj;
			EqualsBuilder builder = new EqualsBuilder();
			builder.append(getCompanyId(), other.getCompanyId());
			builder.append(getType(), other.getType());
			builder.append(getBegin(), other.getBegin());
			builder.append(getEnd(), other.getEnd());
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
		builder.append(getCompanyId());
		builder.append(getType());
		builder.append(getBegin());
		builder.append(getEnd());
		return builder.toHashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(PayPeriod other) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(other.getCompanyId(), getCompanyId());
		builder.append(getType(), other.getType());
		builder.append(other.getBegin(), getBegin());
		builder.append(other.getEnd(), getEnd());
		return builder.toComparison();
	}
}
