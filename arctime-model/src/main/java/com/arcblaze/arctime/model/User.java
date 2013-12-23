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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Represents a user of this system.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class User implements Comparable<User>, Principal {
	/**
	 * The unique id of the user.
	 */
	private Integer id;

	/**
	 * The unique id of the company in which this user resides.
	 */
	private Integer companyId;

	/**
	 * The login name for the user.
	 */
	private String login;

	/**
	 * The hashed password value for the user.
	 */
	private String hashedPass;

	/**
	 * The user's email address.
	 */
	private String email;

	/**
	 * The user's first name.
	 */
	private String firstName;

	/**
	 * The user's last name.
	 */
	private String lastName;

	/**
	 * Whether this user is an active account or not.
	 */
	private Boolean active = true;

	/**
	 * The roles assigned to the account.
	 */
	private final Set<Role> roles = new TreeSet<>();

	/**
	 * The users that are being supervised by this user.
	 */
	private final Set<User> supervised = new TreeSet<>();

	/**
	 * When a user shows up in the supervised list, this determines whether the
	 * supervisor is a primary supervisor or not.
	 */
	private Boolean primary;

	/**
	 * The users that are supervisors of this user.
	 */
	private final Set<User> supervisors = new TreeSet<>();

	/**
	 * Default constructor.
	 */
	public User() {
		// Nothing to do.
	}

	/**
	 * @return the unique id of the user
	 */
	@XmlElement
	public Integer getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            the new unique user id value
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id value is invalid
	 */
	public User setId(Integer id) {
		if (id == null)
			throw new IllegalArgumentException("Invalid null id");
		if (id < 0)
			throw new IllegalArgumentException("Invalid negative id");

		this.id = id;
		return this;
	}

	/**
	 * @return the unique id of the company in which this user resides
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
	public User setCompanyId(Integer companyId) {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (companyId < 0)
			throw new IllegalArgumentException("Invalid negative company id");

		this.companyId = companyId;
		return this;
	}

	/**
	 * @return the user login value
	 */
	@Override
	@XmlTransient
	public String getName() {
		return getLogin();
	}

	/**
	 * @return the user login value
	 */
	@XmlElement
	public String getLogin() {
		return this.login;
	}

	/**
	 * @param login
	 *            the new user login value
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided login value is invalid
	 */
	public User setLogin(String login) {
		if (StringUtils.isBlank(login))
			throw new IllegalArgumentException("Invalid blank login");

		this.login = StringUtils.trim(login);
		return this;
	}

	/**
	 * @return the hashed value of the user's password
	 */
	@XmlElement
	public String getHashedPass() {
		return this.hashedPass;
	}

	/**
	 * @param hashedPass
	 *            the new hashed value of the user's password
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided hashed password value is invalid
	 */
	public User setHashedPass(String hashedPass) {
		if (StringUtils.isBlank(hashedPass))
			throw new IllegalArgumentException("Invalid blank hashed password");

		this.hashedPass = StringUtils.trim(hashedPass);
		return this;
	}

	/**
	 * @return the user's email address
	 */
	@XmlElement
	public String getEmail() {
		return this.email;
	}

	/**
	 * @param email
	 *            the new email address for the user
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided email value is invalid
	 */
	public User setEmail(String email) {
		if (StringUtils.isBlank(email))
			throw new IllegalArgumentException("Invalid blank email");

		this.email = StringUtils.trim(email);
		return this;
	}

	/**
	 * @return the user's first name
	 */
	@XmlElement
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * @param firstName
	 *            the new first name value for the user
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided first name value is invalid
	 */
	public User setFirstName(String firstName) {
		if (StringUtils.isBlank(firstName))
			throw new IllegalArgumentException("Invalid blank first name");

		this.firstName = StringUtils.trim(firstName);
		return this;
	}

	/**
	 * @return the user's last name
	 */
	@XmlElement
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * @param lastName
	 *            the new last name value for the user
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided last name value is invalid
	 */
	public User setLastName(String lastName) {
		if (StringUtils.isBlank(lastName))
			throw new IllegalArgumentException("Invalid blank last name");

		this.lastName = StringUtils.trim(lastName);
		return this;
	}

	/**
	 * @return the full name of the user
	 */
	@XmlElement
	public String getFullName() {
		StringBuilder name = new StringBuilder();
		name.append(getFirstName());
		name.append(" ");
		name.append(getLastName());
		return name.toString();
	}

	/**
	 * @return whether this user represents an active account in the system
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
	public User setActive(Boolean active) {
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
	@XmlElementWrapper
	@XmlElement(name = "role")
	public Set<Role> getRoles() {
		return Collections.unmodifiableSet(this.roles);
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
	public User setRoles(Role... newRoles) {
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
	public User setRoles(Collection<Role> newRoles) {
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
	public User addRoles(Role... newRoles) {
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
	public User addRoles(Collection<Role> newRoles) {
		synchronized (this.roles) {
			if (newRoles != null)
				for (Role role : newRoles)
					if (role != null)
						this.roles.add(role);
		}
		return this;
	}

	/**
	 * @return {@code this}
	 */
	public User clearRoles() {
		this.roles.clear();
		return this;
	}

	/**
	 * @return whether this user is a system administrator
	 */
	@XmlElement
	public boolean isAdmin() {
		return this.roles.contains(Role.ADMIN);
	}

	/**
	 * @return whether this user is involved in managing payroll
	 */
	@XmlElement
	public boolean isPayroll() {
		return this.roles.contains(Role.PAYROLL);
	}

	/**
	 * @return whether this user is a system manager
	 */
	@XmlElement
	public boolean isManager() {
		return this.roles.contains(Role.MANAGER);
	}

	/**
	 * @return all of the users being supervised by this account
	 */
	@XmlElementWrapper
	@XmlElement
	public Set<User> getSupervised() {
		return Collections.unmodifiableSet(this.supervised);
	}

	/**
	 * @return {@code this}
	 */
	public User clearSupervised() {
		this.supervised.clear();
		return this;
	}

	/**
	 * @param newSupervised
	 *            the new users to be assigned to this account
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided supervised users value is invalid
	 */
	public User setSupervised(User... newSupervised) {
		if (newSupervised == null)
			throw new IllegalArgumentException("Invalid null supervised");

		return this.setSupervised(Arrays.asList(newSupervised));
	}

	/**
	 * @param newSupervised
	 *            the new users to be assigned to this account
	 * 
	 * @return {@code this}
	 */
	public User setSupervised(Collection<User> newSupervised) {
		synchronized (this.supervised) {
			this.supervised.clear();
			if (newSupervised != null)
				for (User user : newSupervised)
					if (user != null)
						this.supervised.add(user);
		}
		return this;
	}

	/**
	 * @param newSupervised
	 *            the new users to be assigned to this account
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided supervised users value is invalid
	 */
	public User addSupervised(User... newSupervised) {
		if (newSupervised == null)
			throw new IllegalArgumentException("Invalid null supervised");

		return this.addSupervised(Arrays.asList(newSupervised));
	}

	/**
	 * @param newSupervised
	 *            the new users to be assigned to this account
	 * 
	 * @return {@code this}
	 */
	public User addSupervised(Collection<User> newSupervised) {
		synchronized (this.supervised) {
			if (newSupervised != null)
				for (User user : newSupervised)
					if (user != null)
						this.supervised.add(user);
		}
		return this;
	}

	/**
	 * @return whether this user is a supervisor
	 */
	@XmlElement
	public boolean isSupervisor() {
		return !this.supervised.isEmpty();
	}

	/**
	 * @return if the supervisor for this user is the primary supervisor
	 */
	@XmlElement
	public Boolean isPrimary() {
		return this.primary;
	}

	/**
	 * @param primary
	 *            the new value indicating whether the supervisor for this user
	 *            is a primary supervisor
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided primary value is invalid
	 */
	public User setPrimary(Boolean primary) {
		if (primary == null)
			throw new IllegalArgumentException("Invalid null primary value");

		this.primary = primary;
		return this;
	}

	/**
	 * @return all of the supervisors for this user
	 */
	@XmlElementWrapper
	@XmlElement(name = "supervisor")
	public Set<User> getSupervisors() {
		return Collections.unmodifiableSet(this.supervisors);
	}

	/**
	 * @return {@code this}
	 */
	public User clearSupervisors() {
		this.supervisors.clear();
		return this;
	}

	/**
	 * @param newSupervisors
	 *            the new supervisors to be assigned to this account
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided supervisors value is invalid
	 */
	public User setSupervisors(User... newSupervisors) {
		if (newSupervisors == null)
			throw new IllegalArgumentException("Invalid null supervisors");

		return this.setSupervisors(Arrays.asList(newSupervisors));
	}

	/**
	 * @param newSupervisors
	 *            the new supervisors to be assigned to this account
	 * 
	 * @return {@code this}
	 */
	public User setSupervisors(Collection<User> newSupervisors) {
		synchronized (this.supervisors) {
			this.supervisors.clear();
			if (newSupervisors != null)
				for (User user : newSupervisors)
					if (user != null)
						this.supervisors.add(user);
		}
		return this;
	}

	/**
	 * @param newSupervisors
	 *            the new supervisors to be assigned to this account
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided supervisors value is invalid
	 */
	public User addSupervisors(User... newSupervisors) {
		if (newSupervisors == null)
			throw new IllegalArgumentException("Invalid null supervisors");

		return this.addSupervisors(Arrays.asList(newSupervisors));
	}

	/**
	 * @param newSupervisors
	 *            the new supervisors to be assigned to this account
	 * 
	 * @return {@code this}
	 */
	public User addSupervisors(Collection<User> newSupervisors) {
		synchronized (this.supervisors) {
			if (newSupervisors != null)
				for (User user : newSupervisors)
					if (user != null)
						this.supervisors.add(user);
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
		builder.append("active", isActive());
		builder.append("roles", StringUtils.join(getRoles(), ","));
		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof User) {
			User other = (User) obj;
			EqualsBuilder builder = new EqualsBuilder();
			builder.append(getId(), other.getId());
			builder.append(getCompanyId(), other.getCompanyId());
			builder.append(getLogin(), other.getLogin());
			// HashedPass is specifically left out.
			builder.append(getEmail(), other.getEmail());
			builder.append(getFirstName(), other.getFirstName());
			builder.append(getLastName(), other.getLastName());
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
		builder.append(isActive());
		return builder.toHashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(User other) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(other.isActive(), isActive());
		builder.append(getLastName(), other.getLastName());
		builder.append(getFirstName(), other.getFirstName());
		builder.append(getLogin(), other.getLogin());
		builder.append(getCompanyId(), other.getCompanyId());
		builder.append(getId(), other.getId());
		builder.append(getEmail(), other.getEmail());
		// HashedPass is specifically left out.
		return builder.toComparison();
	}
}
