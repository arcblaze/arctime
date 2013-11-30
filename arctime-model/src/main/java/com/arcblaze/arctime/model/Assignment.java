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
 * Represents an assignment of an employee to a task.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Assignment implements Comparable<Assignment> {
	/**
	 * The unique id of this assignment.
	 */
	private Integer id;

	/**
	 * The unique id of the company for which this assignment applies.
	 */
	private Integer companyId;

	/**
	 * The unique id of the task to which the employee is assigned.
	 */
	private Integer taskId;

	/**
	 * The unique id of the employee assigned to the task.
	 */
	private Integer employeeId;

	/**
	 * The labor category being used by the employee on the task.
	 */
	private String laborCat;

	/**
	 * The item name of this assignment used to match financial data.
	 */
	private String itemName;

	/**
	 * The first day in the assignment.
	 */
	private Date begin;

	/**
	 * The last day in the assignment.
	 */
	private Date end;

	/**
	 * Default constructor.
	 */
	public Assignment() {
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
	public Assignment setId(Integer id) {
		if (id == null)
			throw new IllegalArgumentException("Invalid null id");
		if (id < 0)
			throw new IllegalArgumentException("Invalid negative id");

		this.id = id;
		return this;
	}

	/**
	 * @return the unique id of the company for which this assignment applies
	 */
	@XmlElement
	public Integer getCompanyId() {
		return this.companyId;
	}

	/**
	 * @param companyId
	 *            the new unique id of the company for which this assignment
	 *            applies
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id value is invalid
	 */
	public Assignment setCompanyId(Integer companyId) {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (companyId < 0)
			throw new IllegalArgumentException("Invalid negative company id");

		this.companyId = companyId;
		return this;
	}

	/**
	 * @return the unique id of the task for which this assignment applies
	 */
	@XmlElement
	public Integer getTaskId() {
		return this.taskId;
	}

	/**
	 * @param taskId
	 *            the new unique id of the task for which this assignment
	 *            applies
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id value is invalid
	 */
	public Assignment setTaskId(Integer taskId) {
		if (taskId == null)
			throw new IllegalArgumentException("Invalid null task id");
		if (taskId < 0)
			throw new IllegalArgumentException("Invalid negative task id");

		this.taskId = taskId;
		return this;
	}

	/**
	 * @return the unique id of the employee for which this assignment applies
	 */
	@XmlElement
	public Integer getEmployeeId() {
		return this.employeeId;
	}

	/**
	 * @param employeeId
	 *            the new unique id of the employee for which this assignment
	 *            applies
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id value is invalid
	 */
	public Assignment setEmployeeId(Integer employeeId) {
		if (employeeId == null)
			throw new IllegalArgumentException("Invalid null employee id");
		if (employeeId < 0)
			throw new IllegalArgumentException("Invalid negative employee id");

		this.employeeId = employeeId;
		return this;
	}

	/**
	 * @return the labor category of this assignment
	 */
	@XmlElement
	public String getLaborCat() {
		return this.laborCat;
	}

	/**
	 * @param laborCat
	 *            the new labor category of this assignment
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided labor category value is invalid
	 */
	public Assignment setLaborCat(String laborCat) {
		if (StringUtils.isBlank(laborCat))
			throw new IllegalArgumentException("Invalid blank labor category");

		this.laborCat = StringUtils.trim(laborCat);
		return this;
	}

	/**
	 * @return the item name of this assignment
	 */
	@XmlElement
	public String getItemName() {
		return this.itemName;
	}

	/**
	 * @param itemName
	 *            the new item name of this assignment
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided item name value is invalid
	 */
	public Assignment setItemName(String itemName) {
		if (StringUtils.isBlank(itemName))
			throw new IllegalArgumentException("Invalid blank item name");

		this.itemName = StringUtils.trim(itemName);
		return this;
	}

	/**
	 * @return the first day in this assignment
	 */
	@XmlElement
	public Date getBegin() {
		return this.begin == null ? null : new Date(this.begin.getTime());
	}

	/**
	 * @param begin
	 *            the new value indicating the first day of this assignment
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided begin value is invalid
	 */
	public Assignment setBegin(Date begin) {
		if (begin == null)
			throw new IllegalArgumentException("Invalid null begin value");

		this.begin = begin;
		return this;
	}

	/**
	 * @return the last day in this assignment
	 */
	@XmlElement
	public Date getEnd() {
		return this.end == null ? null : new Date(this.end.getTime());
	}

	/**
	 * @param end
	 *            the new value indicating the last day of this assignment
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided end value is invalid
	 */
	public Assignment setEnd(Date end) {
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
		builder.append("id", getId());
		builder.append("companyId", getCompanyId());
		builder.append("taskId", getTaskId());
		builder.append("employeeId", getEmployeeId());
		builder.append("laborCat", getLaborCat());
		builder.append("itemName", getItemName());
		builder.append("begin", getBegin());
		builder.append("end", getEnd());
		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Assignment) {
			Assignment other = (Assignment) obj;
			EqualsBuilder builder = new EqualsBuilder();
			builder.append(getId(), other.getId());
			builder.append(getCompanyId(), other.getCompanyId());
			builder.append(getTaskId(), other.getTaskId());
			builder.append(getEmployeeId(), other.getEmployeeId());
			builder.append(getLaborCat(), other.getLaborCat());
			builder.append(getItemName(), other.getItemName());
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
		builder.append(getId());
		builder.append(getCompanyId());
		builder.append(getTaskId());
		builder.append(getEmployeeId());
		builder.append(getLaborCat());
		builder.append(getItemName());
		builder.append(getBegin());
		builder.append(getEnd());
		return builder.toHashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Assignment other) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(other.getCompanyId(), getCompanyId());
		builder.append(getEmployeeId(), other.getEmployeeId());
		builder.append(getTaskId(), other.getTaskId());
		builder.append(getLaborCat(), other.getLaborCat());
		builder.append(getItemName(), other.getItemName());
		builder.append(getBegin(), other.getBegin());
		builder.append(getEnd(), other.getEnd());
		return builder.toComparison();
	}
}
