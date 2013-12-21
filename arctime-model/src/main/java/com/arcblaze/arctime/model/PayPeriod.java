package com.arcblaze.arctime.model;

import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import com.arcblaze.arctime.model.util.HolidayConfigurationException;

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
	 * @param time
	 *            the {@link Date} to check to determine if it falls within this
	 *            pay period
	 * 
	 * @return whether the provided date falls into this pay period
	 */
	public boolean contains(Date time) {
		if (time == null)
			return false;

		Date b = getBegin();
		Date e = getEnd();

		if (b == null || e == null)
			return false;

		Date day = DateUtils.truncate(time, Calendar.DATE);

		return day.getTime() >= b.getTime() && day.getTime() <= e.getTime();
	}

	/**
	 * @param holiday
	 *            the {@link Holiday} to check to determine if it falls within
	 *            this pay period
	 * 
	 * @return whether the provided date falls into this pay period
	 * 
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing the holiday configuration
	 *             information
	 */
	public boolean contains(Holiday holiday)
			throws HolidayConfigurationException {
		if (holiday == null)
			return false;

		Date b = getBegin();
		Date e = getEnd();

		if (b == null || e == null)
			return false;

		int yb = Integer.parseInt(DateFormatUtils.format(b, "yyyy"));
		int ye = Integer.parseInt(DateFormatUtils.format(e, "yyyy"));

		return yb == ye ? contains(holiday.getDateForYear(yb))
				: contains(holiday.getDateForYear(yb))
						|| contains(holiday.getDateForYear(ye));
	}

	/**
	 * @param date
	 *            the date to check
	 * 
	 * @return true if the end date in this pay period (truncated to the day)
	 *         comes before the provided date (also truncated to the day)
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameter is {@code null}
	 */
	public boolean isBefore(Date date) {
		if (date == null)
			throw new IllegalArgumentException("Invalid null date");

		Date truncated = DateUtils.truncate(date, Calendar.DATE);
		Date endTruncated = DateUtils.truncate(getEnd(), Calendar.DATE);

		return endTruncated.before(truncated);
	}

	/**
	 * @param date
	 *            the date to check
	 * 
	 * @return true if the begin date in this pay period (truncated to the day)
	 *         comes after the provided date (also truncated to the day)
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameter is {@code null}
	 */
	public boolean isAfter(Date date) {
		if (date == null)
			throw new IllegalArgumentException("Invalid null date");

		Date truncated = DateUtils.truncate(date, Calendar.DATE);
		Date beginTruncated = DateUtils.truncate(getBegin(), Calendar.DATE);

		return beginTruncated.after(truncated);
	}

	/**
	 * @return a calculation of what the previous pay period is based on the
	 *         configuration of this pay period
	 */
	public PayPeriod getPrevious() {
		PayPeriod payPeriod = new PayPeriod();
		payPeriod.setCompanyId(getCompanyId());
		payPeriod.setType(getType());

		Date b = getBegin();
		Date e = getEnd();

		switch (payPeriod.getType()) {
		case WEEKLY:
			// Subtract 7 days from the start and end.
			b = DateUtils.addDays(b, -7);
			e = DateUtils.addDays(e, -7);
			break;
		case BI_WEEKLY:
			// Subtract 14 days from the start and end.
			b = DateUtils.addDays(b, -14);
			e = DateUtils.addDays(e, -14);
			break;
		case MONTHLY:
			// The end of the previous pay period is the day before the
			// beginning of this pay period.
			e = DateUtils.addDays(b, -1);
			b = DateUtils.addMonths(b, -1);
			break;
		case SEMI_MONTHLY:
			// The end of the previous pay period is the day before the
			// beginning of this pay period.
			e = DateUtils.addDays(b, -1);

			// Use the original end date to calculate the previous begin date.
			b = DateUtils.addMonths(DateUtils.addDays(getEnd(), 1), -1);
			break;
		}

		payPeriod.setBegin(b);
		payPeriod.setEnd(e);
		return payPeriod;
	}

	/**
	 * @return a calculation of what the next pay period is based on the
	 *         configuration of this pay period
	 */
	public PayPeriod getNext() {
		PayPeriod payPeriod = new PayPeriod();
		payPeriod.setCompanyId(getCompanyId());
		payPeriod.setType(getType());

		Date b = getBegin();
		Date e = getEnd();

		switch (payPeriod.getType()) {
		case WEEKLY:
			// Add 7 days to the start and end.
			b = DateUtils.addDays(b, 7);
			e = DateUtils.addDays(e, 7);
			break;
		case BI_WEEKLY:
			// Add 14 days to the start and end.
			b = DateUtils.addDays(b, 14);
			e = DateUtils.addDays(e, 14);
			break;
		case MONTHLY:
			// The beginning of the next pay period is the day after the
			// end of this pay period.
			b = DateUtils.addDays(e, 1);

			// Is the begin date the first day of the month?
			if ("1".equals(DateFormatUtils.format(b, "d"))) {
				// Use the last day of the month.
				Date d = DateUtils.addDays(b, 27);
				while (!"1".equals(DateFormatUtils.format(d, "d")))
					d = DateUtils.addDays(d, 1);
				e = DateUtils.addDays(d, -1);
			} else
				e = DateUtils.addMonths(getEnd(), 1);
			break;
		case SEMI_MONTHLY:
			// The beginning of the next pay period is the day after the
			// end of this pay period.
			b = DateUtils.addDays(e, 1);

			// Use the original begin date to calculate the end end date.
			Date prevEnd = DateUtils.addDays(getBegin(), -1);
			// Was the previous end date the last day of the month?
			if ("1".equals(DateFormatUtils.format(
					DateUtils.addDays(prevEnd, 1), "d"))) {
				// Use the last day of the month.
				Date d = DateUtils.addDays(b, 12);
				while (!"1".equals(DateFormatUtils.format(d, "d")))
					d = DateUtils.addDays(d, 1);
				e = DateUtils.addDays(d, -1);
			} else
				e = DateUtils.addMonths(prevEnd, 1);
			break;
		}

		payPeriod.setBegin(b);
		payPeriod.setEnd(e);
		return payPeriod;
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
