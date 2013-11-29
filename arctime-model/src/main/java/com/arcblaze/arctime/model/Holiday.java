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
 * Represents a holiday.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Holiday implements Comparable<Holiday> {
	/**
	 * The unique id of the holiday.
	 */
	private Integer id;

	/**
	 * The unique id of the company that owns this holiday.
	 */
	private Integer companyId;

	/**
	 * The description for the holiday.
	 */
	private String description;

	/**
	 * The configuration of this holiday.
	 */
	private String config;

	/**
	 * Default constructor.
	 */
	public Holiday() {
		// Nothing to do.
	}

	/**
	 * @return the unique id of the holiday
	 */
	@XmlElement
	public Integer getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            the new unique holiday id value
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id value is invalid
	 */
	public Holiday setId(Integer id) {
		if (id == null)
			throw new IllegalArgumentException("Invalid null id");
		if (id < 0)
			throw new IllegalArgumentException("Invalid negative id");

		this.id = id;
		return this;
	}

	/**
	 * @return the unique id of the company that owns this holiday
	 */
	@XmlElement
	public Integer getCompanyId() {
		return this.companyId;
	}

	/**
	 * @param companyId
	 *            the new unique id of the company that owns this holiday
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided company id value is invalid
	 */
	public Holiday setCompanyId(Integer companyId) {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (companyId < 0)
			throw new IllegalArgumentException("Invalid negative company id");

		this.companyId = companyId;
		return this;
	}

	/**
	 * @return the holiday description
	 */
	@XmlElement
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description
	 *            the new holiday description
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided description value is invalid
	 */
	public Holiday setDescription(String description) {
		if (StringUtils.isBlank(description))
			throw new IllegalArgumentException("Invalid blank description");

		this.description = StringUtils.trim(description);
		return this;
	}

	/**
	 * @return the configuration of this holiday
	 */
	@XmlElement
	public String getConfig() {
		return this.config;
	}

	/**
	 * @param config
	 *            the new value determining when this holiday occurs
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided config value is invalid
	 */
	public Holiday setConfig(String config) {
		if (config == null)
			throw new IllegalArgumentException("Invalid null config value");

		this.config = config;
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
		builder.append("config", getConfig());
		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Holiday) {
			Holiday other = (Holiday) obj;
			EqualsBuilder builder = new EqualsBuilder();
			builder.append(getId(), other.getId());
			builder.append(getCompanyId(), other.getCompanyId());
			builder.append(getDescription(), other.getDescription());
			builder.append(getConfig(), other.getConfig());
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
		builder.append(getConfig());
		return builder.toHashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Holiday other) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(getCompanyId(), other.getCompanyId());
		builder.append(other.getConfig(), getConfig());
		builder.append(getDescription(), other.getDescription());
		builder.append(getId(), other.getId());
		return builder.toComparison();
	}
}
