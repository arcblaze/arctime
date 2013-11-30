package com.arcblaze.arctime.model;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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

/**
 * Represents an employee.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Employee implements Comparable<Employee>, Principal {
	/**
	 * The unique id of the employee.
	 */
	private Integer id;

	/**
	 * The unique id of the company in which this employee resides.
	 */
	private Integer companyId;

	/**
	 * The login name for the employee.
	 */
	private String login;

	/**
	 * The hashed password value for the employee.
	 */
	private String hashedPass;

	/**
	 * The employee's email address.
	 */
	private String email;

	/**
	 * The employee's first name.
	 */
	private String firstName;

	/**
	 * The employee's last name.
	 */
	private String lastName;

	/**
	 * The employee's name suffix.
	 */
	private String suffix;

	/**
	 * The division within the company in which the employee resides.
	 */
	private String division;

	/**
	 * Describe's the employee's personnel category type.
	 */
	private PersonnelType personnelType;

	/**
	 * Whether this employee is an active account or not.
	 */
	private Boolean active = true;

	/**
	 * The roles assigned to the account.
	 */
	private Set<Role> roles = new TreeSet<>();

	/**
	 * The employees that are being supervised by this employee.
	 */
	private Set<Employee> supervised = new TreeSet<>();

	/**
	 * When an employee shows up in the supervised list, this determines whether
	 * the supervisor is a primary supervisor or not.
	 */
	private Boolean primary;

	/**
	 * The employees that are supervisors of this employee.
	 */
	private Set<Employee> supervisers = new TreeSet<>();

	/**
	 * Default constructor.
	 */
	public Employee() {
		// Nothing to do.
	}

	/**
	 * @return the unique id of the employee
	 */
	@XmlElement
	public Integer getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            the new unique employee id value
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id value is invalid
	 */
	public Employee setId(Integer id) {
		if (id == null)
			throw new IllegalArgumentException("Invalid null id");
		if (id < 0)
			throw new IllegalArgumentException("Invalid negative id");

		this.id = id;
		return this;
	}

	/**
	 * @return the unique id of the company in which this employee resides
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
	public Employee setCompanyId(Integer companyId) {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (companyId < 0)
			throw new IllegalArgumentException("Invalid negative company id");

		this.companyId = companyId;
		return this;
	}

	/**
	 * @return the employee login value
	 */
	@Override
	@XmlTransient
	public String getName() {
		return getLogin();
	}

	/**
	 * @return the employee login value
	 */
	@XmlElement
	public String getLogin() {
		return this.login;
	}

	/**
	 * @param login
	 *            the new employee login value
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided login value is invalid
	 */
	public Employee setLogin(String login) {
		if (StringUtils.isBlank(login))
			throw new IllegalArgumentException("Invalid blank login");

		this.login = StringUtils.trim(login);
		return this;
	}

	/**
	 * @return the hashed value of the employee's password
	 */
	@XmlElement
	public String getHashedPass() {
		return this.hashedPass;
	}

	/**
	 * @param hashedPass
	 *            the new hashed value of the employee's password
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided hashed password value is invalid
	 */
	public Employee setHashedPass(String hashedPass) {
		if (StringUtils.isBlank(hashedPass))
			throw new IllegalArgumentException("Invalid blank hashed password");

		this.hashedPass = StringUtils.trim(hashedPass);
		return this;
	}

	/**
	 * @return the employee's email address
	 */
	@XmlElement
	public String getEmail() {
		return this.email;
	}

	/**
	 * @param email
	 *            the new email address for the employee
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided email value is invalid
	 */
	public Employee setEmail(String email) {
		if (StringUtils.isBlank(email))
			throw new IllegalArgumentException("Invalid blank email");

		this.email = StringUtils.trim(email);
		return this;
	}

	/**
	 * @return the employee's first name
	 */
	@XmlElement
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * @param firstName
	 *            the new first name value for the employee
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided first name value is invalid
	 */
	public Employee setFirstName(String firstName) {
		if (StringUtils.isBlank(firstName))
			throw new IllegalArgumentException("Invalid blank first name");

		this.firstName = StringUtils.trim(firstName);
		return this;
	}

	/**
	 * @return the employee's last name
	 */
	@XmlElement
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * @param lastName
	 *            the new last name value for the employee
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided last name value is invalid
	 */
	public Employee setLastName(String lastName) {
		if (StringUtils.isBlank(lastName))
			throw new IllegalArgumentException("Invalid blank last name");

		this.lastName = StringUtils.trim(lastName);
		return this;
	}

	/**
	 * @return the employee's name suffix
	 */
	@XmlElement
	public String getSuffix() {
		return this.suffix;
	}

	/**
	 * @param suffix
	 *            the new name suffix value for the employee
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided name suffix value is invalid
	 */
	public Employee setSuffix(String suffix) {
		this.suffix = StringUtils.trimToNull(suffix);
		return this;
	}

	/**
	 * @return the full name of the employee
	 */
	@XmlElement
	public String getFullName() {
		StringBuilder name = new StringBuilder();
		name.append(getFirstName());
		name.append(" ");
		name.append(getLastName());

		String suf = getSuffix();
		if (StringUtils.isNotBlank(suf)) {
			name.append(" ");
			name.append(suf);
		}
		return name.toString();
	}

	/**
	 * @return the employee's company division
	 */
	@XmlElement
	public String getDivision() {
		return this.division;
	}

	/**
	 * @param division
	 *            the new company division value for the employee
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided division value is invalid
	 */
	public Employee setDivision(String division) {
		this.division = StringUtils.trimToNull(division);
		return this;
	}

	/**
	 * @return the employee's personnel type
	 */
	@XmlElement
	public PersonnelType getPersonnelType() {
		return this.personnelType;
	}

	/**
	 * @param personnelType
	 *            the new personnel type value for the employee
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided personnel type value is invalid
	 */
	public Employee setPersonnelType(PersonnelType personnelType) {
		if (personnelType == null)
			throw new IllegalArgumentException("Invalid null personnel type");

		this.personnelType = personnelType;
		return this;
	}

	/**
	 * @return whether this employee represents an active account in the system
	 */
	@XmlElement
	public Boolean isActive() {
		return this.active;
	}

	/**
	 * @param active
	 *            the new value indicating whether this is an active account in
	 *            the system
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided active value is invalid
	 */
	public Employee setActive(Boolean active) {
		if (active == null)
			throw new IllegalArgumentException("Invalid null active value");

		this.active = active;
		return this;
	}

	/**
	 * @return the privileges available to the user, based on the roles
	 */
	@XmlElement
	public String getPrivileges() {
		Set<Character> privs = new TreeSet<>();
		for (Role role : getRoles())
			privs.add(role.name().charAt(0));
		return StringUtils.join(privs, " ");
	}

	/**
	 * @return all of the roles authorized for this account
	 */
	@XmlElement
	public Set<Role> getRoles() {
		return Collections.unmodifiableSet(this.roles);
	}

	/**
	 * @return {@code this}
	 */
	public Employee clearRoles() {
		this.roles.clear();
		return this;
	}

	/**
	 * @param newRoles
	 *            the new role values to be assigned to this account
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided roles value is invalid
	 */
	public Employee setRoles(Role... newRoles) {
		if (newRoles == null)
			throw new IllegalArgumentException("Invalid null roles");

		return this.setRoles(Arrays.asList(newRoles));
	}

	/**
	 * @param newRoles
	 *            the new role values to be assigned to this account
	 * 
	 * @return {@code this}
	 */
	public Employee setRoles(Collection<Role> newRoles) {
		synchronized (this.roles) {
			this.roles.clear();
			if (newRoles != null)
				for (Role role : newRoles)
					if (role != null)
						this.roles.add(role);
		}
		return this;
	}

	/**
	 * @param newRoles
	 *            the new role values to be assigned to this account
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided roles value is invalid
	 */
	public Employee addRoles(Role... newRoles) {
		if (newRoles == null)
			throw new IllegalArgumentException("Invalid null roles");

		return this.addRoles(Arrays.asList(newRoles));
	}

	/**
	 * @param newRoles
	 *            the new role values to be assigned to this account
	 * 
	 * @return {@code this}
	 */
	public Employee addRoles(Collection<Role> newRoles) {
		synchronized (this.roles) {
			if (newRoles != null)
				for (Role role : newRoles)
					if (role != null)
						this.roles.add(role);
		}
		return this;
	}

	/**
	 * @return whether this employee is a system administrator
	 */
	@XmlElement
	public boolean isAdmin() {
		return this.roles.contains(Role.ADMIN);
	}

	/**
	 * @return whether this employee is involved in managing payroll
	 */
	@XmlElement
	public boolean isPayroll() {
		return this.roles.contains(Role.PAYROLL);
	}

	/**
	 * @return whether this employee is a system manager
	 */
	@XmlElement
	public boolean isManager() {
		return this.roles.contains(Role.MANAGER);
	}

	/**
	 * @return all of the employees being supervised by this account
	 */
	@XmlElement
	public Set<Employee> getSupervised() {
		return Collections.unmodifiableSet(this.supervised);
	}

	/**
	 * @return {@code this}
	 */
	public Employee clearSupervised() {
		this.supervised.clear();
		return this;
	}

	/**
	 * @param newSupervised
	 *            the new employees to be assigned to this account
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided supervised employees value is invalid
	 */
	public Employee setSupervised(Employee... newSupervised) {
		if (newSupervised == null)
			throw new IllegalArgumentException("Invalid null supervised");

		return this.setSupervised(Arrays.asList(newSupervised));
	}

	/**
	 * @param newSupervised
	 *            the new employees to be assigned to this account
	 * 
	 * @return {@code this}
	 */
	public Employee setSupervised(Collection<Employee> newSupervised) {
		synchronized (this.supervised) {
			this.supervised.clear();
			if (newSupervised != null)
				for (Employee employee : newSupervised)
					if (employee != null)
						this.supervised.add(employee);
		}
		return this;
	}

	/**
	 * @param newSupervised
	 *            the new employees to be assigned to this account
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided supervised employees value is invalid
	 */
	public Employee addSupervised(Employee... newSupervised) {
		if (newSupervised == null)
			throw new IllegalArgumentException("Invalid null supervised");

		return this.addSupervised(Arrays.asList(newSupervised));
	}

	/**
	 * @param newSupervised
	 *            the new employees to be assigned to this account
	 * 
	 * @return {@code this}
	 */
	public Employee addSupervised(Collection<Employee> newSupervised) {
		synchronized (this.supervised) {
			if (newSupervised != null)
				for (Employee employee : newSupervised)
					if (employee != null)
						this.supervised.add(employee);
		}
		return this;
	}

	/**
	 * @return whether this employee is a supervisor
	 */
	@XmlElement
	public boolean isSupervisor() {
		return !this.supervised.isEmpty();
	}

	/**
	 * @return if the supervisor for this employee is the primary supervisor
	 */
	@XmlElement
	public Boolean isPrimary() {
		return this.primary;
	}

	/**
	 * @param primary
	 *            the new value indicating whether the supervisor for this
	 *            employee is a primary supervisor
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided primary value is invalid
	 */
	public Employee setPrimary(Boolean primary) {
		if (primary == null)
			throw new IllegalArgumentException("Invalid null primary value");

		this.primary = primary;
		return this;
	}

	/**
	 * @return all of the supervisors for this employee
	 */
	@XmlElement
	public Set<Employee> getSupervisers() {
		return Collections.unmodifiableSet(this.supervisers);
	}

	/**
	 * @return {@code this}
	 */
	public Employee clearSupervisers() {
		this.supervisers.clear();
		return this;
	}

	/**
	 * @param newSupervisers
	 *            the new supervisors to be assigned to this account
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided supervisors value is invalid
	 */
	public Employee setSupervisers(Employee... newSupervisers) {
		if (newSupervisers == null)
			throw new IllegalArgumentException("Invalid null supervisers");

		return this.setSupervisers(Arrays.asList(newSupervisers));
	}

	/**
	 * @param newSupervisers
	 *            the new supervisors to be assigned to this account
	 * 
	 * @return {@code this}
	 */
	public Employee setSupervisers(Collection<Employee> newSupervisers) {
		synchronized (this.supervisers) {
			this.supervisers.clear();
			if (newSupervisers != null)
				for (Employee employee : newSupervisers)
					if (employee != null)
						this.supervisers.add(employee);
		}
		return this;
	}

	/**
	 * @param newSupervisers
	 *            the new supervisors to be assigned to this account
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided supervisors value is invalid
	 */
	public Employee addSupervisers(Employee... newSupervisers) {
		if (newSupervisers == null)
			throw new IllegalArgumentException("Invalid null supervisers");

		return this.addSupervisers(Arrays.asList(newSupervisers));
	}

	/**
	 * @param newSupervisers
	 *            the new supervisors to be assigned to this account
	 * 
	 * @return {@code this}
	 */
	public Employee addSupervisers(Collection<Employee> newSupervisers) {
		synchronized (this.supervisers) {
			if (newSupervisers != null)
				for (Employee employee : newSupervisers)
					if (employee != null)
						this.supervisers.add(employee);
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
		builder.append("login", getLogin());
		builder.append("hashedPass", getHashedPass());
		builder.append("email", getEmail());
		builder.append("firstName", getFirstName());
		builder.append("lastName", getLastName());
		builder.append("suffix", getSuffix());
		builder.append("division", getDivision());
		builder.append("personnelType", getPersonnelType());
		builder.append("active", isActive());
		builder.append("roles", StringUtils.join(getRoles(), ","));
		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Employee) {
			Employee other = (Employee) obj;
			EqualsBuilder builder = new EqualsBuilder();
			builder.append(getId(), other.getId());
			builder.append(getCompanyId(), other.getCompanyId());
			builder.append(getLogin(), other.getLogin());
			// HashedPass is specifically left out.
			builder.append(getEmail(), other.getEmail());
			builder.append(getFirstName(), other.getFirstName());
			builder.append(getLastName(), other.getLastName());
			builder.append(getSuffix(), other.getSuffix());
			builder.append(getDivision(), other.getDivision());
			builder.append(getPersonnelType(), other.getPersonnelType());
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
		builder.append(getLogin());
		// HashedPass is specifically left out.
		builder.append(getEmail());
		builder.append(getFirstName());
		builder.append(getLastName());
		builder.append(getSuffix());
		builder.append(getDivision());
		builder.append(getPersonnelType());
		builder.append(isActive());
		return builder.toHashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Employee other) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(other.isActive(), isActive());
		builder.append(getLastName(), other.getLastName());
		builder.append(getFirstName(), other.getFirstName());
		builder.append(getSuffix(), other.getSuffix());
		builder.append(getLogin(), other.getLogin());
		builder.append(getCompanyId(), other.getCompanyId());
		builder.append(getId(), other.getId());
		builder.append(getEmail(), other.getEmail());
		builder.append(getDivision(), other.getDivision());
		builder.append(getPersonnelType(), other.getPersonnelType());
		// HashedPass is specifically left out.
		return builder.toComparison();
	}
}
