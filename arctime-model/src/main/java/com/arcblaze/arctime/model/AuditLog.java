package com.arcblaze.arctime.model;

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
 * Represents an audit log.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class AuditLog implements Comparable<AuditLog> {
	/**
	 * The unique id of the timesheet to which this audit log applies.
	 */
	private Integer timesheetId;

	/**
	 * The log message describing the action that occurred.
	 */
	private String log;

	/**
	 * The timestamp when the audited activity took place.
	 */
	private Date timestamp;

	/**
	 * Default constructor.
	 */
	public AuditLog() {
		// Nothing to do.
	}

	/**
	 * @return the unique id of the timesheet for which this audit log applies
	 */
	@XmlElement
	public Integer getTimesheetId() {
		return this.timesheetId;
	}

	/**
	 * @param timesheetId
	 *            the new unique id of the timesheet for which this audit log
	 *            applies
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id value is invalid
	 */
	public AuditLog setTimesheetId(Integer timesheetId) {
		if (timesheetId == null)
			throw new IllegalArgumentException("Invalid null timesheet id");
		if (timesheetId < 0)
			throw new IllegalArgumentException("Invalid negative timesheet id");

		this.timesheetId = timesheetId;
		return this;
	}

	/**
	 * @return the log message describing the activity that occurred
	 */
	@XmlElement
	public String getLog() {
		return this.log;
	}

	/**
	 * @param log
	 *            the new log message describing the activity that occurred
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided log value is invalid
	 */
	public AuditLog setLog(String log) {
		if (StringUtils.isBlank(log))
			throw new IllegalArgumentException("Invalid blank log");

		this.log = StringUtils.trim(log);
		return this;
	}

	/**
	 * @return the timestamp when the audited activity took place
	 */
	@XmlElement
	public Date getTimestamp() {
		return this.timestamp == null ? null : new Date(
				this.timestamp.getTime());
	}

	/**
	 * @param timestamp
	 *            the new value indicating when the audited activity occurred
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided timestamp value is invalid
	 */
	public AuditLog setTimestamp(Date timestamp) {
		if (timestamp == null)
			throw new IllegalArgumentException("Invalid null timestamp value");

		this.timestamp = timestamp;
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("timesheetId", getTimesheetId());
		builder.append("log", getLog());
		builder.append("timestamp", getTimestamp());
		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AuditLog) {
			AuditLog other = (AuditLog) obj;
			EqualsBuilder builder = new EqualsBuilder();
			builder.append(getTimesheetId(), other.getTimesheetId());
			builder.append(getLog(), other.getLog());
			builder.append(getTimestamp(), other.getTimestamp());
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
		builder.append(getTimesheetId());
		builder.append(getLog());
		builder.append(getTimestamp());
		return builder.toHashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(AuditLog other) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(other.getTimesheetId(), getTimesheetId());
		builder.append(getTimestamp(), other.getTimestamp());
		builder.append(getLog(), other.getLog());
		return builder.toComparison();
	}
}
