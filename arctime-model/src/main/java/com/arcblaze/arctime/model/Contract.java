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
 * Represents a contract.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Contract implements Comparable<Contract> {
	/**
	 * The unique id of the contract.
	 */
	private Integer id;

	/**
	 * The unique id of the company in which this contract resides.
	 */
	private Integer companyId;

	/**
	 * The description of the contract as displayed to employees in the system
	 * on their timesheets.
	 */
	private String description;

	/**
	 * The contract number of the contract used by company management personnel.
	 */
	private String contractNum;

	/**
	 * The contract job code used by payroll personnel to track the contract in
	 * finance systems.
	 */
	private String jobCode;

	/**
	 * Whether this contract is administrative and available to all employees in
	 * the company.
	 */
	private Boolean administrative;

	/**
	 * Whether this contract is active and available for employees to charge
	 * against.
	 */
	private Boolean active = true;

	/**
	 * Default constructor.
	 */
	public Contract() {
		// Nothing to do.
	}

	/**
	 * @return the unique id of the contract
	 */
	@XmlElement
	public Integer getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            the new unique contract id value
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id value is invalid
	 */
	public Contract setId(Integer id) {
		if (id == null)
			throw new IllegalArgumentException("Invalid null id");
		if (id < 0)
			throw new IllegalArgumentException("Invalid negative id");

		this.id = id;
		return this;
	}

	/**
	 * @return the unique id of the company in which this contract resides
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
	public Contract setCompanyId(Integer companyId) {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");
		if (companyId < 0)
			throw new IllegalArgumentException("Invalid negative company id");

		this.companyId = companyId;
		return this;
	}

	/**
	 * @return the contract description
	 */
	@XmlElement
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description
	 *            the new contract description value
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided description value is invalid
	 */
	public Contract setDescription(String description) {
		if (StringUtils.isBlank(description))
			throw new IllegalArgumentException("Invalid blank login");

		this.description = StringUtils.trim(description);
		return this;
	}

	/**
	 * @return the contract number for this contract
	 */
	@XmlElement
	public String getContractNum() {
		return this.contractNum;
	}

	/**
	 * @param contractNum
	 *            the new contract number value for this contract
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided hashed password value is invalid
	 */
	public Contract setContractNum(String contractNum) {
		if (StringUtils.isBlank(contractNum))
			throw new IllegalArgumentException("Invalid blank contract number");

		this.contractNum = StringUtils.trim(contractNum);
		return this;
	}

	/**
	 * @return the job code associated with this contract
	 */
	@XmlElement
	public String getJobCode() {
		return this.jobCode;
	}

	/**
	 * @param jobCode
	 *            the new job code for the contract
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided email value is invalid
	 */
	public Contract setJobCode(String jobCode) {
		if (StringUtils.isBlank(jobCode))
			throw new IllegalArgumentException("Invalid blank job code");

		this.jobCode = StringUtils.trim(jobCode);
		return this;
	}

	/**
	 * @return whether this contract is administrative and available to all
	 *         employees
	 */
	@XmlElement
	public Boolean isAdministrative() {
		return this.administrative;
	}

	/**
	 * @param administrative
	 *            the new value indicating whether this is an administrative
	 *            contract
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided administrative value is invalid
	 */
	public Contract setAdministrative(Boolean administrative) {
		if (administrative == null)
			throw new IllegalArgumentException(
					"Invalid null administrative value");

		this.administrative = administrative;
		return this;
	}

	/**
	 * @return whether this contract is active and available for hours to be
	 *         charged against it
	 */
	@XmlElement
	public Boolean isActive() {
		return this.active;
	}

	/**
	 * @param active
	 *            the new value indicating whether this is an active contract
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided active value is invalid
	 */
	public Contract setActive(Boolean active) {
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
		builder.append("companyId", getCompanyId());
		builder.append("description", getDescription());
		builder.append("contractNum", getContractNum());
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
		if (obj instanceof Contract) {
			Contract other = (Contract) obj;
			EqualsBuilder builder = new EqualsBuilder();
			builder.append(getId(), other.getId());
			builder.append(getCompanyId(), other.getCompanyId());
			builder.append(getDescription(), other.getDescription());
			builder.append(getContractNum(), other.getContractNum());
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
		builder.append(getContractNum());
		builder.append(getJobCode());
		builder.append(isAdministrative());
		builder.append(isActive());
		return builder.toHashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Contract other) {
		CompareToBuilder builder = new CompareToBuilder();
		builder.append(getCompanyId(), other.getCompanyId());
		builder.append(other.isActive(), isActive());
		builder.append(isAdministrative(), other.isAdministrative());
		builder.append(getDescription(), other.getDescription());
		builder.append(getContractNum(), other.getContractNum());
		builder.append(getJobCode(), other.getJobCode());
		builder.append(getId(), other.getId());
		return builder.toComparison();
	}
}
