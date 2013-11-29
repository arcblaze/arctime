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
 * Represents a timesheet for an employee during a pay period.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Timesheet implements Comparable<Timesheet> {
	/**
	 * The unique id of this timesheet.
	 */
	private Integer id;

	/**
	 * The unique id of the employee that owns this timesheet.
	 */
	private Integer employeeId;

	/**
	 * The first day in the pay period associated with this timesheet.
	 */
	private Date begin;

	/**
	 * Whether this timesheet has been completed by the employee.
	 */
	private Boolean completed;

	/**
	 * Whether this timesheet has been approved by the employee's supervisor.
	 */
	private Boolean approved;

	/**
	 * Whether this timesheet has been verified by payroll.
	 */
	private Boolean verified;

	/**
	 * Whether this timesheet has been exported by payroll.
	 */
	private Boolean exported;

	/**
	 * The id of the supervisor that approved this timesheet.
	 */
	private Integer approverId;

	/**
	 * The id of the payroll person that verified this timesheet.
	 */
	private Integer verifierId;

	/**
	 * The id of the payroll person that exported this timesheet.
	 */
	private Integer exporterId;

	/**
	 * Default constructor.
	 */
	public Timesheet() {
		// Nothing to do.
	}

	/**
	 * @return the unique id of this timesheet
	 */
	@XmlElement
	public Integer getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            the new unique id of this timesheet
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id value is invalid
	 */
	public Timesheet setId(Integer id) {
		if (id == null)
			throw new IllegalArgumentException("Invalid null id");
		if (id < 0)
			throw new IllegalArgumentException("Invalid negative id");

		this.id = id;
		return this;
	}

	/**
	 * @return the unique id of the employee that owns this timesheet
	 */
	@XmlElement
	public Integer getEmployeeId() {
		return this.employeeId;
	}

	/**
	 * @param employeeId
	 *            the new unique id of the employee that owns this timesheet
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id value is invalid
	 */
	public Timesheet setEmployeeId(Integer employeeId) {
		if (employeeId == null)
			throw new IllegalArgumentException("Invalid null employee id");
		if (employeeId < 0)
			throw new IllegalArgumentException("Invalid negative employee id");

		this.employeeId = employeeId;
		return this;
	}

	/**
	 * @return the first day in the pay period associated with this timesheet
	 */
	@XmlElement
	public Date getBegin() {
		return this.begin == null ? null : new Date(this.begin.getTime());
	}

	/**
	 * @param begin
	 *            the new value indicating the first day in the pay period
	 *            associated with this timesheet
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided begin value is invalid
	 */
	public Timesheet setBegin(Date begin) {
		if (begin == null)
			throw new IllegalArgumentException("Invalid null begin value");

		this.begin = begin;
		return this;
	}

	/**
	 * @return whether the timesheet has been completed by the employee
	 */
	@XmlElement
	public Boolean isCompleted() {
		return this.completed;
	}

	/**
	 * @param completed
	 *            the new value indicating whether this timesheet has been
	 *            completed by the employee
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided completed value is invalid
	 */
	public Timesheet setCompleted(Boolean completed) {
		if (completed == null)
			throw new IllegalArgumentException("Invalid null completed value");

		this.completed = completed;
		return this;
	}

	/**
	 * @return whether the timesheet has been approved by the employee's
	 *         supervisor
	 */
	@XmlElement
	public Boolean isApproved() {
		return this.approved;
	}

	/**
	 * @param approved
	 *            the new value indicating whether this timesheet has been
	 *            approved by the employee's supervisor
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided approved value is invalid
	 */
	public Timesheet setApproved(Boolean approved) {
		if (approved == null)
			throw new IllegalArgumentException("Invalid null approved value");

		this.approved = approved;
		return this;
	}

	/**
	 * @return whether the timesheet has been verified by payroll
	 */
	@XmlElement
	public Boolean isVerified() {
		return this.verified;
	}

	/**
	 * @param verified
	 *            the new value indicating whether this timesheet has been
	 *            verified by payroll
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided verified value is invalid
	 */
	public Timesheet setVerified(Boolean verified) {
		if (verified == null)
			throw new IllegalArgumentException("Invalid null verified value");

		this.verified = verified;
		return this;
	}

	/**
	 * @return whether the timesheet has been exported by payroll
	 */
	@XmlElement
	public Boolean isExported() {
		return this.exported;
	}

	/**
	 * @param exported
	 *            the new value indicating whether this timesheet has been
	 *            exported by payroll
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided exported value is invalid
	 */
	public Timesheet setExported(Boolean exported) {
		if (exported == null)
			throw new IllegalArgumentException("Invalid null exported value");

		this.exported = exported;
		return this;
	}

	/**
	 * @return the unique id of the supervisor that approved this timesheet
	 */
	@XmlElement
	public Integer getApproverId() {
		return this.approverId;
	}

	/**
	 * @param approverId
	 *            the new unique id of the supervisor that approved this
	 *            timesheet
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id value is invalid
	 */
	public Timesheet setApproverId(Integer approverId) {
		if (approverId == null)
			throw new IllegalArgumentException("Invalid null approver id");
		if (approverId < 0)
			throw new IllegalArgumentException("Invalid negative approver id");

		this.approverId = approverId;
		return this;
	}

	/**
	 * @return the unique id of the payroll person that verified this timesheet
	 */
	@XmlElement
	public Integer getVerifierId() {
		return this.verifierId;
	}

	/**
	 * @param verifierId
	 *            the new unique id of the payroll person that verified this
	 *            timesheet
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id value is invalid
	 */
	public Timesheet setVerifierId(Integer verifierId) {
		if (verifierId == null)
			throw new IllegalArgumentException("Invalid null verifier id");
		if (verifierId < 0)
			throw new IllegalArgumentException("Invalid negative verifier id");

		this.verifierId = verifierId;
		return this;
	}

	/**
	 * @return the unique id of the payroll person that exported this timesheet
	 */
	@XmlElement
	public Integer getExporterId() {
		return this.exporterId;
	}

	/**
	 * @param exporterId
	 *            the new unique id of the payroll person that exported this
	 *            timesheet
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id value is invalid
	 */
	public Timesheet setExporterId(Integer exporterId) {
		if (exporterId == null)
			throw new IllegalArgumentException("Invalid null exporter id");
		if (exporterId < 0)
			throw new IllegalArgumentException("Invalid negative exporter id");

		this.exporterId = exporterId;
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("id", getId());
		builder.append("employeeId", getEmployeeId());
		builder.append("begin", getBegin());
		builder.append("completed", isCompleted());
		builder.append("approved", isApproved());
		builder.append("verified", isVerified());
		builder.append("exported", isExported());
		builder.append("approverId", getApproverId());
		builder.append("verifierId", getVerifierId());
		builder.append("exporterId", getExporterId());
		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Timesheet) {
			Timesheet other = (Timesheet) obj;
			EqualsBuilder builder = new EqualsBuilder();
			builder.append(getId(), other.getId());
			builder.append(getEmployeeId(), other.getEmployeeId());
			builder.append(getBegin(), other.getBegin());
			builder.append(isCompleted(), other.isCompleted());
			builder.append(isApproved(), other.isApproved());
			builder.append(isVerified(), other.isVerified());
			builder.append(isExported(), other.isExported());
			builder.append(getApproverId(), other.getApproverId());
			builder.append(getVerifierId(), other.getVerifierId());
			builder.append(getExporterId(), other.getExporterId());
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
		builder.append(getEmployeeId());
		builder.append(getBegin());
		builder.append(isCompleted());
		builder.append(isApproved());
		builder.append(isVerified());
		builder.append(isExported());
		builder.append(getApproverId());
		builder.append(getVerifierId());
		builder.append(getExporterId());
		return builder.toHashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Timesheet other) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(other.getBegin(), getBegin());
		builder.append(getEmployeeId(), other.getEmployeeId());
		builder.append(getId(), other.getId());
		builder.append(isCompleted(), other.isCompleted());
		builder.append(isApproved(), other.isApproved());
		builder.append(isVerified(), other.isVerified());
		builder.append(isExported(), other.isExported());
		builder.append(getApproverId(), other.getApproverId());
		builder.append(getVerifierId(), other.getVerifierId());
		builder.append(getExporterId(), other.getExporterId());
		return builder.toComparison();
	}
}
