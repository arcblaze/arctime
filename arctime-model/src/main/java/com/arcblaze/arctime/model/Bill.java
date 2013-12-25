package com.arcblaze.arctime.model;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

/**
 * Represents a bill of hours by an user to a task assignment.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Bill implements Comparable<Bill> {
	/**
	 * The unique id of this bill.
	 */
	private Integer id;

	/**
	 * The unique id of the task assignment for which this bill applies.
	 */
	private Integer assignmentId;

	/**
	 * The unique id of the task to which the user is billing hours.
	 */
	private Integer taskId;

	/**
	 * The unique id of the user assigned to the task.
	 */
	private Integer userId;

	/**
	 * The day in which the hours are to be applied.
	 */
	private Date day;

	/**
	 * The number of hours being applied to the task assignment.
	 */
	private BigDecimal hours;

	/**
	 * The time stamp when this bill was created.
	 */
	private Date timestamp;

	/**
	 * The reason for changing hours from one value to the other.
	 */
	private String reason;

	/**
	 * Default constructor.
	 */
	public Bill() {
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
	public Bill setId(Integer id) {
		if (id == null)
			throw new IllegalArgumentException("Invalid null id");
		if (id < 0)
			throw new IllegalArgumentException("Invalid negative id");

		this.id = id;
		return this;
	}

	/**
	 * @return whether this bill includes an assignment id
	 */
	public boolean hasAssignmentId() {
		return this.assignmentId != null;
	}

	/**
	 * @return the unique id of the task assignment for which this bill applies
	 */
	@XmlElement
	public Integer getAssignmentId() {
		return this.assignmentId;
	}

	/**
	 * @param assignmentId
	 *            the new unique id of the task assignment for which this bill
	 *            applies
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id value is invalid
	 */
	public Bill setAssignmentId(Integer assignmentId) {
		if (assignmentId == null)
			throw new IllegalArgumentException("Invalid null assignment id");
		if (assignmentId < 0)
			throw new IllegalArgumentException("Invalid negative assignment id");

		this.assignmentId = assignmentId;
		return this;
	}

	/**
	 * @return the unique id of the task for which this bill applies
	 */
	@XmlElement
	public Integer getTaskId() {
		return this.taskId;
	}

	/**
	 * @param taskId
	 *            the new unique id of the task for which this bill applies
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id value is invalid
	 */
	public Bill setTaskId(Integer taskId) {
		if (taskId == null)
			throw new IllegalArgumentException("Invalid null task id");
		if (taskId < 0)
			throw new IllegalArgumentException("Invalid negative task id");

		this.taskId = taskId;
		return this;
	}

	/**
	 * @return a unique id capable of distinguishing this bill in a timesheet
	 */
	@XmlTransient
	public String getUniqueId() {
		StringBuilder uid = new StringBuilder();
		uid.append(getTaskId());
		uid.append(":");
		uid.append(getAssignmentId());
		uid.append(":");
		uid.append(DateFormatUtils.format(getDay(), "yyyyMMdd"));
		return uid.toString();
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
	public Bill setUserId(Integer userId) {
		if (userId == null)
			throw new IllegalArgumentException("Invalid null user id");
		if (userId < 0)
			throw new IllegalArgumentException("Invalid negative user id");

		this.userId = userId;
		return this;
	}

	/**
	 * @return the day in which the hours are being billed
	 */
	@XmlElement
	public Date getDay() {
		return this.day == null ? null : new Date(this.day.getTime());
	}

	/**
	 * @param day
	 *            the new day when the hours were billed
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided day value is invalid
	 */
	public Bill setDay(Date day) {
		if (day == null)
			throw new IllegalArgumentException("Invalid null day");

		this.day = day;
		return this;
	}

	/**
	 * @return the hours being billed to the task assignment
	 */
	@XmlElement
	public BigDecimal getHours() {
		return this.hours;
	}

	/**
	 * @param hours
	 *            the new value specifying the hours being billed to the task
	 *            assignment
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided hours value is invalid
	 */
	public Bill setHours(Float hours) {
		if (hours == null)
			throw new IllegalArgumentException("Invalid null hours");

		this.hours = new BigDecimal(hours).setScale(2);
		return this;
	}

	/**
	 * @param hours
	 *            the new value specifying the hours being billed to the task
	 *            assignment
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided hours value is invalid
	 */
	public Bill setHours(String hours) {
		if (hours == null)
			throw new IllegalArgumentException("Invalid null hours");

		try {
			this.hours = new BigDecimal(hours).setScale(2);
		} catch (NumberFormatException badNumber) {
			throw new IllegalArgumentException("Invalid number: " + hours,
					badNumber);
		}
		return this;
	}

	/**
	 * @param hours
	 *            the new value specifying the hours being billed to the task
	 *            assignment
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided hours value is invalid
	 */
	public Bill setHours(BigDecimal hours) {
		if (hours == null)
			throw new IllegalArgumentException("Invalid null hours");

		this.hours = new BigDecimal(hours.toPlainString()).setScale(2);
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
	public Bill setTimestamp(Date timestamp) {
		if (timestamp == null)
			throw new IllegalArgumentException("Invalid null timestamp value");

		this.timestamp = timestamp;
		return this;
	}

	/**
	 * @return whether this bill contains a reason why the hours were modified.
	 */
	public boolean hasReason() {
		return this.reason != null;
	}

	/**
	 * @return the reason indicating why hours were changed from an old value to
	 *         a new value
	 */
	@XmlElement
	public String getReason() {
		return this.reason;
	}

	/**
	 * @param reason
	 *            the new reason value indicating why hours were changed from an
	 *            old value to a new value
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided reason value is invalid
	 */
	public Bill setReason(String reason) {
		if (StringUtils.isBlank(reason))
			throw new IllegalArgumentException("Invalid blank reason value");

		this.reason = reason;
		return this;
	}

	/**
	 * @param data
	 *            the raw encoded data sent from the time sheet user interface
	 *            to be parsed into the individual bills
	 * 
	 * @return the parsed data in the form of bills
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided data value could not be parsed successfully
	 */
	public static Set<Bill> fromTimesheetData(String data) {
		Set<Bill> bills = new TreeSet<Bill>();

		if (StringUtils.isBlank(data))
			return bills;

		// data looks like: "8_57:20100602:5.00:reason;8_56:20100605:8.00"
		String[] dataParts = data.split(";");
		for (String dataPart : dataParts) {
			if (StringUtils.isBlank(dataPart))
				continue;

			String[] pieces = dataPart.split(":", 4);

			try {
				Bill bill = new Bill();

				String[] taskAssignmentId = pieces[0].split("_", 2);
				bill.setTaskId(Integer.parseInt(taskAssignmentId[0]));

				Integer assignmentId = (taskAssignmentId.length == 2 && StringUtils
						.isNotBlank(taskAssignmentId[1])) ? Integer
						.parseInt(taskAssignmentId[1]) : null;
				if (assignmentId != null)
					bill.setAssignmentId(assignmentId);

				bill.setDay(DateUtils.parseDate(pieces[1],
						new String[] { "yyyyMMdd" }));
				bill.setHours(new BigDecimal(pieces[2]));

				String reason = pieces.length == 4 ? pieces[3] : null;
				if (StringUtils.isNotBlank(reason))
					bill.setReason(reason);

				bill.setTimestamp(new Date());
				bills.add(bill);
			} catch (NumberFormatException badNumber) {
				throw new IllegalArgumentException("Invalid numeric value.",
						badNumber);
			} catch (ParseException badDate) {
				throw new IllegalArgumentException("Invalid date value.",
						badDate);
			}
		}

		return bills;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("id", getId());
		builder.append("assignmentId", getAssignmentId());
		builder.append("taskId", getTaskId());
		builder.append("userId", getUserId());
		builder.append("day", getDay());
		builder.append("hours", getHours());
		builder.append("reason", getReason());
		builder.append("timestamp", getTimestamp());
		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Bill) {
			Bill other = (Bill) obj;
			EqualsBuilder builder = new EqualsBuilder();
			builder.append(getId(), other.getId());
			builder.append(getAssignmentId(), other.getAssignmentId());
			builder.append(getTaskId(), other.getTaskId());
			builder.append(getUserId(), other.getUserId());
			builder.append(getDay(), other.getDay());
			builder.append(getHours(), other.getHours());
			builder.append(getTimestamp(), other.getTimestamp());
			builder.append(getReason(), other.getReason());
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
		builder.append(getAssignmentId());
		builder.append(getTaskId());
		builder.append(getUserId());
		builder.append(getDay());
		builder.append(getHours());
		builder.append(getTimestamp());
		builder.append(getReason());
		return builder.toHashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Bill other) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(other.getAssignmentId(), getAssignmentId());
		builder.append(getUserId(), other.getUserId());
		builder.append(getTaskId(), other.getTaskId());
		builder.append(getDay(), other.getDay());
		builder.append(other.getTimestamp(), getTimestamp());
		builder.append(getHours(), other.getHours());
		builder.append(getReason(), other.getReason());
		return builder.toComparison();
	}
}
