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
 * Represents a bill of hours by an employee to a task assignment.
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
	 * The unique id of the task to which the employee is billing hours.
	 */
	private Integer taskId;

	/**
	 * The unique id of the employee assigned to the task.
	 */
	private Integer employeeId;

	/**
	 * The day in which the hours are to be applied.
	 */
	private Date day;

	/**
	 * The number of hours being applied to the task assignment.
	 */
	private Float hours;

	/**
	 * The timestamp when this bill was created.
	 */
	private Date timestamp;

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
	 * @return the unique id of the employee billing the hours
	 */
	@XmlElement
	public Integer getEmployeeId() {
		return this.employeeId;
	}

	/**
	 * @param employeeId
	 *            the new unique id of the employee billing the hours
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id value is invalid
	 */
	public Bill setEmployeeId(Integer employeeId) {
		if (employeeId == null)
			throw new IllegalArgumentException("Invalid null employee id");
		if (employeeId < 0)
			throw new IllegalArgumentException("Invalid negative employee id");

		this.employeeId = employeeId;
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
	public Float getHours() {
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

		this.hours = hours;
		return this;
	}

	/**
	 * @return the timestamp indicating when this bill was created
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
	 *             if the provided timestamp value is invalid
	 */
	public Bill setTimestamp(Date timestamp) {
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
		builder.append("id", getId());
		builder.append("assignmentId", getAssignmentId());
		builder.append("taskId", getTaskId());
		builder.append("employeeId", getEmployeeId());
		builder.append("day", getDay());
		builder.append("hours", getHours());
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
			builder.append(getEmployeeId(), other.getEmployeeId());
			builder.append(getDay(), other.getDay());
			builder.append(getHours(), other.getHours());
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
		builder.append(getId());
		builder.append(getAssignmentId());
		builder.append(getTaskId());
		builder.append(getEmployeeId());
		builder.append(getDay());
		builder.append(getHours());
		builder.append(getTimestamp());
		return builder.toHashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Bill other) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(other.getAssignmentId(), getAssignmentId());
		builder.append(getEmployeeId(), other.getEmployeeId());
		builder.append(getTaskId(), other.getTaskId());
		builder.append(getDay(), other.getDay());
		builder.append(other.getTimestamp(), getTimestamp());
		builder.append(getHours(), other.getHours());
		return builder.toComparison();
	}
}
