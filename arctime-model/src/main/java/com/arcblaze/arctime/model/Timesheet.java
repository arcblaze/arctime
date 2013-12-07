package com.arcblaze.arctime.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

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
	 * The unique id of the company in which this timesheet resides.
	 */
	private Integer companyId;

	/**
	 * The unique id of the employee that owns this timesheet.
	 */
	private Integer employeeId;

	/**
	 * The employee that owns this timesheet.
	 */
	private Employee employee;

	/**
	 * The first day in the pay period associated with this timesheet.
	 */
	private Date begin;

	/**
	 * The pay period for which this timesheet contains hours.
	 */
	private PayPeriod payPeriod;

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
	 * The employee that approved this timesheet (if it has been approved).
	 */
	private Employee approver;

	/**
	 * The id of the payroll person that verified this timesheet.
	 */
	private Integer verifierId;

	/**
	 * The payroll employee that verified this timesheet (if it has been
	 * verified).
	 */
	private Employee verifier;

	/**
	 * The id of the payroll person that exported this timesheet.
	 */
	private Integer exporterId;

	/**
	 * The payroll employee that exported this timesheet (if it has been
	 * exported).
	 */
	private Employee exporter;

	/**
	 * The tasks to which the employee can charge hours during this pay period.
	 */
	private Set<Task> tasks = new TreeSet<>();

	/**
	 * The audit logs associated with this timesheet.
	 */
	private Set<AuditLog> auditLogs = new TreeSet<>();

	/**
	 * The company holidays used to flag specific days in the pay period as
	 * being holidays or not.
	 */
	private Set<Holiday> holidays = new TreeSet<>();

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
	 * @return the unique id of the company that owns this timesheet
	 */
	@XmlElement
	public Integer getCompanyId() {
		return this.companyId;
	}

	/**
	 * @param companyId
	 *            the new unique id of the company that owns this timesheet
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id value is invalid
	 */
	public Timesheet setCompanyId(Integer companyId) {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (companyId < 0)
			throw new IllegalArgumentException("Invalid negative company id");

		this.companyId = companyId;
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
	 * @return the employee that owns this timesheet
	 */
	@XmlElement
	public Employee getEmployee() {
		return this.employee;
	}

	/**
	 * @param employee
	 *            the new employee that owns this timesheet
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided value is invalid
	 */
	public Timesheet setEmployee(Employee employee) {
		if (employee == null)
			throw new IllegalArgumentException("Invalid null employee");

		this.employee = employee;
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
	 * @return the pay period associated with this timesheet
	 */
	@XmlElement
	public PayPeriod getPayPeriod() {
		return this.payPeriod;
	}

	/**
	 * @param payPeriod
	 *            the new pay period associated with this timesheet
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided pay period value is invalid
	 */
	public Timesheet setPayPeriod(PayPeriod payPeriod) {
		if (payPeriod == null)
			throw new IllegalArgumentException("Invalid null pay period value");

		this.payPeriod = payPeriod;
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
	 * @return the person that approved this timesheet
	 */
	@XmlElement
	public Employee getApprover() {
		return this.approver;
	}

	/**
	 * @param approver
	 *            the new value for the person that approved this timesheet
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided value is invalid
	 */
	public Timesheet setApprover(Employee approver) {
		if (approver == null)
			throw new IllegalArgumentException("Invalid null approver");

		this.approver = approver;
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
	 * @return the payroll person that verified this timesheet
	 */
	@XmlElement
	public Employee getVerifier() {
		return this.verifier;
	}

	/**
	 * @param verifier
	 *            the new value for the payroll person that verified this
	 *            timesheet
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided value is invalid
	 */
	public Timesheet setVerifier(Employee verifier) {
		if (verifier == null)
			throw new IllegalArgumentException("Invalid null verifier");

		this.verifier = verifier;
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
	 * @return the payroll person that exported this timesheet
	 */
	@XmlElement
	public Employee getExporter() {
		return this.exporter;
	}

	/**
	 * @param exporter
	 *            the new value for the payroll person that exported this
	 *            timesheet
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided value is invalid
	 */
	public Timesheet setExporter(Employee exporter) {
		if (exporter == null)
			throw new IllegalArgumentException("Invalid null exporter");

		this.exporter = exporter;
		return this;
	}

	/**
	 * @return all of the tasks the employee can bill hours to
	 */
	@XmlElement
	public Set<Task> getTasks() {
		return Collections.unmodifiableSet(this.tasks);
	}

	/**
	 * @return {@code this}
	 */
	public Timesheet clearTasks() {
		this.tasks.clear();
		return this;
	}

	/**
	 * @param newTasks
	 *            the new tasks to which the employee can bill hours
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided tasks value is invalid
	 */
	public Timesheet setTasks(Task... newTasks) {
		if (newTasks == null)
			throw new IllegalArgumentException("Invalid null tasks");

		return this.setTasks(Arrays.asList(newTasks));
	}

	/**
	 * @param newTasks
	 *            the new tasks to which the employee can bill hours
	 * 
	 * @return {@code this}
	 */
	public Timesheet setTasks(Collection<Task> newTasks) {
		synchronized (this.tasks) {
			this.tasks.clear();
			if (newTasks != null)
				for (Task task : newTasks)
					if (task != null)
						this.tasks.add(task);
		}
		return this;
	}

	/**
	 * @param newTasks
	 *            the new tasks to which the employee can bill hours
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided tasks value is invalid
	 */
	public Timesheet addTasks(Task... newTasks) {
		if (newTasks == null)
			throw new IllegalArgumentException("Invalid null tasks");

		return this.addTasks(Arrays.asList(newTasks));
	}

	/**
	 * @param newTasks
	 *            the new tasks to which the employee can bill hours
	 * 
	 * @return {@code this}
	 */
	public Timesheet addTasks(Collection<Task> newTasks) {
		synchronized (this.tasks) {
			if (newTasks != null)
				for (Task task : newTasks)
					if (task != null)
						this.tasks.add(task);
		}
		return this;
	}

	/**
	 * @return all of the auditLogs the employee can bill hours to
	 */
	@XmlElement
	public Set<AuditLog> getAuditLogs() {
		return Collections.unmodifiableSet(this.auditLogs);
	}

	/**
	 * @return {@code this}
	 */
	public Timesheet clearAuditLogs() {
		this.auditLogs.clear();
		return this;
	}

	/**
	 * @param newAuditLogs
	 *            the new auditLogs to which the employee can bill hours
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided auditLogs value is invalid
	 */
	public Timesheet setAuditLogs(AuditLog... newAuditLogs) {
		if (newAuditLogs == null)
			throw new IllegalArgumentException("Invalid null auditLogs");

		return this.setAuditLogs(Arrays.asList(newAuditLogs));
	}

	/**
	 * @param newAuditLogs
	 *            the new auditLogs to which the employee can bill hours
	 * 
	 * @return {@code this}
	 */
	public Timesheet setAuditLogs(Collection<AuditLog> newAuditLogs) {
		synchronized (this.auditLogs) {
			this.auditLogs.clear();
			if (newAuditLogs != null)
				for (AuditLog auditLog : newAuditLogs)
					if (auditLog != null)
						this.auditLogs.add(auditLog);
		}
		return this;
	}

	/**
	 * @param newAuditLogs
	 *            the new auditLogs to which the employee can bill hours
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided auditLogs value is invalid
	 */
	public Timesheet addAuditLogs(AuditLog... newAuditLogs) {
		if (newAuditLogs == null)
			throw new IllegalArgumentException("Invalid null auditLogs");

		return this.addAuditLogs(Arrays.asList(newAuditLogs));
	}

	/**
	 * @param newAuditLogs
	 *            the new auditLogs to which the employee can bill hours
	 * 
	 * @return {@code this}
	 */
	public Timesheet addAuditLogs(Collection<AuditLog> newAuditLogs) {
		synchronized (this.auditLogs) {
			if (newAuditLogs != null)
				for (AuditLog auditLog : newAuditLogs)
					if (auditLog != null)
						this.auditLogs.add(auditLog);
		}
		return this;
	}

	/**
	 * @return all of the holidays the employee can bill hours to
	 */
	@XmlElement
	public Set<Holiday> getHolidays() {
		return Collections.unmodifiableSet(this.holidays);
	}

	/**
	 * @return {@code this}
	 */
	public Timesheet clearHolidays() {
		this.holidays.clear();
		return this;
	}

	/**
	 * @param newHolidays
	 *            the new holidays to which the employee can bill hours
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided holidays value is invalid
	 */
	public Timesheet setHolidays(Holiday... newHolidays) {
		if (newHolidays == null)
			throw new IllegalArgumentException("Invalid null holidays");

		return this.setHolidays(Arrays.asList(newHolidays));
	}

	/**
	 * @param newHolidays
	 *            the new holidays to which the employee can bill hours
	 * 
	 * @return {@code this}
	 */
	public Timesheet setHolidays(Collection<Holiday> newHolidays) {
		synchronized (this.holidays) {
			this.holidays.clear();
			if (newHolidays != null)
				for (Holiday holiday : newHolidays)
					if (holiday != null)
						this.holidays.add(holiday);
		}
		return this;
	}

	/**
	 * @param newHolidays
	 *            the new holidays to which the employee can bill hours
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided holidays value is invalid
	 */
	public Timesheet addHolidays(Holiday... newHolidays) {
		if (newHolidays == null)
			throw new IllegalArgumentException("Invalid null holidays");

		return this.addHolidays(Arrays.asList(newHolidays));
	}

	/**
	 * @param newHolidays
	 *            the new holidays to which the employee can bill hours
	 * 
	 * @return {@code this}
	 */
	public Timesheet addHolidays(Collection<Holiday> newHolidays) {
		synchronized (this.holidays) {
			if (newHolidays != null)
				for (Holiday holiday : newHolidays)
					if (holiday != null)
						this.holidays.add(holiday);
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
			builder.append(getCompanyId(), other.getCompanyId());
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
		builder.append(getCompanyId());
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
		builder.append(getCompanyId(), getCompanyId());
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
