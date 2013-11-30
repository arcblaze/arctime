package com.arcblaze.arctime.model;

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
 * Represents a company.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Company implements Comparable<Company> {
	/**
	 * The unique id of the company.
	 */
	private Integer id;

	/**
	 * The name for the company.
	 */
	private String name;

	/**
	 * Whether this company is active or not.
	 */
	private Boolean active = true;

	/**
	 * Default constructor.
	 */
	public Company() {
		// Nothing to do.
	}

	/**
	 * @return the unique id of the company
	 */
	@XmlElement
	public Integer getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            the new unique company id value
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id value is invalid
	 */
	public Company setId(Integer id) {
		if (id == null)
			throw new IllegalArgumentException("Invalid null id");
		if (id < 0)
			throw new IllegalArgumentException("Invalid negative id");

		this.id = id;
		return this;
	}

	/**
	 * @return the company name
	 */
	@XmlElement
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *            the new company name
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided name value is invalid
	 */
	public Company setName(String name) {
		if (StringUtils.isBlank(name))
			throw new IllegalArgumentException("Invalid blank name");

		this.name = StringUtils.trim(name);
		return this;
	}

	/**
	 * @return whether this company has an active account in the system
	 */
	@XmlElement
	public Boolean isActive() {
		return this.active;
	}

	/**
	 * @param active
	 *            the new value indicating whether this is an active company in
	 *            the system
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided active value is invalid
	 */
	public Company setActive(Boolean active) {
		if (active == null)
			throw new IllegalArgumentException("Invalid null active value");

		this.active = active;
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("id", getId());
		builder.append("name", getName());
		builder.append("active", isActive());
		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Company) {
			Company other = (Company) obj;
			EqualsBuilder builder = new EqualsBuilder();
			builder.append(getId(), other.getId());
			builder.append(getName(), other.getName());
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
		builder.append(getName());
		builder.append(isActive());
		return builder.toHashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Company other) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(other.isActive(), isActive());
		builder.append(getName(), other.getName());
		builder.append(getId(), other.getId());
		return builder.toComparison();
	}
}
