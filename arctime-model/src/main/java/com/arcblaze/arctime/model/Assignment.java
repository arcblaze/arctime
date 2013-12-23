package com.arcblaze.arctime.model;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.time.DateUtils;

/**
 * Represents an assignment of a user to a task.
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
	 * The unique id of the task to which the user is assigned.
	 */
	private Integer taskId;

	/**
	 * The unique id of the user assigned to the task.
	 */
	private Integer userId;

	/**
	 * The labor category being used by the user on the task.
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
	 * The bills applied to this assignment during a pay period.
	 */
	private final Set<Bill> bills = new TreeSet<>();

	/**
	 * Default constructor.
	 */
	public Assignment() {
		// Nothing to do.
	}

	/**
	 * @param time
	 *            the {@link Date} to check to determine if it falls within this
	 *            assignment
	 * 
	 * @return whether the provided date falls into this assignment
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
	 * @return the unique id of the user for which this assignment applies
	 */
	@XmlElement
	public Integer getUserId() {
		return this.userId;
	}

	/**
	 * @param userId
	 *            the new unique id of the user for which this assignment
	 *            applies
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id value is invalid
	 */
	public Assignment setUserId(Integer userId) {
		if (userId == null)
			throw new IllegalArgumentException("Invalid null user id");
		if (userId < 0)
			throw new IllegalArgumentException("Invalid negative user id");

		this.userId = userId;
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

		this.begin = DateUtils.truncate(begin, Calendar.DATE);
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

		this.end = DateUtils.truncate(end, Calendar.DATE);
		return this;
	}

	/**
	 * @return all of the bills authorized for this account
	 */
	@XmlElementWrapper
	@XmlElement(name = "bill")
	public Set<Bill> getBills() {
		return Collections.unmodifiableSet(this.bills);
	}

	/**
	 * @param newBills
	 *            the new bill values to be assigned to this account
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided bills value is invalid
	 */
	public Assignment setBills(Bill... newBills) {
		if (newBills == null)
			throw new IllegalArgumentException("Invalid null bills");

		return this.setBills(Arrays.asList(newBills));
	}

	/**
	 * @param newBills
	 *            the new bill values to be assigned to this account
	 * 
	 * @return {@code this}
	 */
	public Assignment setBills(Collection<Bill> newBills) {
		synchronized (this.bills) {
			this.bills.clear();
			if (newBills != null)
				for (Bill bill : newBills)
					if (bill != null)
						this.bills.add(bill);
		}
		return this;
	}

	/**
	 * @param newBills
	 *            the new bill values to be assigned to this account
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided bills value is invalid
	 */
	public Assignment addBills(Bill... newBills) {
		if (newBills == null)
			throw new IllegalArgumentException("Invalid null bills");

		return this.addBills(Arrays.asList(newBills));
	}

	/**
	 * @param newBills
	 *            the new bill values to be assigned to this account
	 * 
	 * @return {@code this}
	 */
	public Assignment addBills(Collection<Bill> newBills) {
		synchronized (this.bills) {
			if (newBills != null)
				for (Bill bill : newBills)
					if (bill != null)
						this.bills.add(bill);
		}
		return this;
	}

	/**
	 * @return {@code this}
	 */
	public Assignment clearBills() {
		this.bills.clear();
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
		builder.append("userId", getUserId());
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
			builder.append(getUserId(), other.getUserId());
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
		builder.append(getUserId());
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
		builder.append(getUserId(), other.getUserId());
		builder.append(getTaskId(), other.getTaskId());
		builder.append(getLaborCat(), other.getLaborCat());
		builder.append(getItemName(), other.getItemName());
		builder.append(getBegin(), other.getBegin());
		builder.append(getEnd(), other.getEnd());
		return builder.toComparison();
	}
}
