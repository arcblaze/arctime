package com.arcblaze.arctime.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

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
 * Represents a task.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Task implements Comparable<Task> {
	/**
	 * The unique id of the task.
	 */
	private Integer id;

	/**
	 * The unique id of the company in which this task resides.
	 */
	private Integer companyId;

	/**
	 * The description of the task as displayed to employees in the system on
	 * their timesheets.
	 */
	private String description;

	/**
	 * The task job code used by payroll personnel to track the task in finance
	 * systems.
	 */
	private String jobCode;

	/**
	 * Whether this task is administrative and available to all employees in the
	 * company.
	 */
	private Boolean administrative;

	/**
	 * Whether this task is active and available for employees to charge
	 * against.
	 */
	private Boolean active = true;

	/**
	 * Holds the assignments associated with this task.
	 */
	private Set<Assignment> assignments = new TreeSet<>();

	/**
	 * Default constructor.
	 */
	public Task() {
		// Nothing to do.
	}

	/**
	 * @return the unique id of the task
	 */
	@XmlElement
	public Integer getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            the new unique task id value
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id value is invalid
	 */
	public Task setId(Integer id) {
		if (id == null)
			throw new IllegalArgumentException("Invalid null id");
		if (id < 0)
			throw new IllegalArgumentException("Invalid negative id");

		this.id = id;
		return this;
	}

	/**
	 * @return the unique id of the company in which this task resides
	 */
	@XmlElement
	public Integer getCompanyId() {
		return this.companyId;
	}

	/**
	 * @param companyId
	 *            the new unique company id value
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided companyId value is invalid
	 */
	public Task setCompanyId(Integer companyId) {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (companyId < 0)
			throw new IllegalArgumentException("Invalid negative company id");

		this.companyId = companyId;
		return this;
	}

	/**
	 * @return the task description
	 */
	@XmlElement
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description
	 *            the new task description value
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided description value is invalid
	 */
	public Task setDescription(String description) {
		if (StringUtils.isBlank(description))
			throw new IllegalArgumentException("Invalid blank login");

		this.description = StringUtils.trim(description);
		return this;
	}

	/**
	 * @return the job code associated with this task
	 */
	@XmlElement
	public String getJobCode() {
		return this.jobCode;
	}

	/**
	 * @param jobCode
	 *            the new job code for the task
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided email value is invalid
	 */
	public Task setJobCode(String jobCode) {
		if (StringUtils.isBlank(jobCode))
			throw new IllegalArgumentException("Invalid blank job code");

		this.jobCode = StringUtils.trim(jobCode);
		return this;
	}

	/**
	 * @return whether this task is administrative and available to all
	 *         employees
	 */
	@XmlElement
	public Boolean isAdministrative() {
		return this.administrative;
	}

	/**
	 * @param administrative
	 *            the new value indicating whether this is an administrative
	 *            task
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided administrative value is invalid
	 */
	public Task setAdministrative(Boolean administrative) {
		if (administrative == null)
			throw new IllegalArgumentException(
					"Invalid null administrative value");

		this.administrative = administrative;
		return this;
	}

	/**
	 * @return whether this task is active and available for hours to be charged
	 *         against it
	 */
	@XmlElement
	public Boolean isActive() {
		return this.active;
	}

	/**
	 * @param active
	 *            the new value indicating whether this is an active task
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided active value is invalid
	 */
	public Task setActive(Boolean active) {
		if (active == null)
			throw new IllegalArgumentException("Invalid null active value");

		this.active = active;
		return this;
	}

	/**
	 * @return all of the assignments being assignments by this account
	 */
	@XmlElement
	public Set<Assignment> getAssignments() {
		return Collections.unmodifiableSet(this.assignments);
	}

	/**
	 * @return {@code this}
	 */
	public Task clearAssignments() {
		this.assignments.clear();
		return this;
	}

	/**
	 * @param newAssignments
	 *            the new assignments to be assigned to this account
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided assignments assignments value is invalid
	 */
	public Task setAssignments(Assignment... newAssignments) {
		if (newAssignments == null)
			throw new IllegalArgumentException("Invalid null assignments");

		return this.setAssignments(Arrays.asList(newAssignments));
	}

	/**
	 * @param newAssignments
	 *            the new assignments to be assigned to this account
	 * 
	 * @return {@code this}
	 */
	public Task setAssignments(Collection<Assignment> newAssignments) {
		synchronized (this.assignments) {
			this.assignments.clear();
			if (newAssignments != null)
				for (Assignment assignment : newAssignments)
					if (assignment != null)
						this.assignments.add(assignment);
		}
		return this;
	}

	/**
	 * @param newAssignments
	 *            the new assignments to be assigned to this account
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided assignments assignments value is invalid
	 */
	public Task addAssignments(Assignment... newAssignments) {
		if (newAssignments == null)
			throw new IllegalArgumentException("Invalid null assignments");

		return this.addAssignments(Arrays.asList(newAssignments));
	}

	/**
	 * @param newAssignments
	 *            the new assignments to be assigned to this account
	 * 
	 * @return {@code this}
	 */
	public Task addAssignments(Collection<Assignment> newAssignments) {
		synchronized (this.assignments) {
			if (newAssignments != null)
				for (Assignment assignment : newAssignments)
					if (assignment != null)
						this.assignments.add(assignment);
		}
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
		builder.append("description", getDescription());
		builder.append("jobCode", getJobCode());
		builder.append("administrative", isAdministrative());
		builder.append("active", isActive());
		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Task) {
			Task other = (Task) obj;
			EqualsBuilder builder = new EqualsBuilder();
			builder.append(getId(), other.getId());
			builder.append(getCompanyId(), other.getCompanyId());
			builder.append(getDescription(), other.getDescription());
			builder.append(getJobCode(), other.getJobCode());
			builder.append(isAdministrative(), other.isAdministrative());
			builder.append(isActive(), other.isActive());
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
		builder.append(getDescription());
		builder.append(getJobCode());
		builder.append(isAdministrative());
		builder.append(isActive());
		return builder.toHashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Task other) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(getCompanyId(), other.getCompanyId());
		builder.append(other.isActive(), isActive());
		builder.append(isAdministrative(), other.isAdministrative());
		builder.append(getDescription(), other.getDescription());
		builder.append(getJobCode(), other.getJobCode());
		builder.append(getId(), other.getId());
		return builder.toComparison();
	}
}
