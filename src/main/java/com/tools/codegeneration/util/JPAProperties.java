package com.tools.codegeneration.util;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class JPAProperties {
	private boolean addCascade;
	private boolean addMappedBy;
	private String mappedBy;
	// Following properties corresponds to values specific to Hibernate' org.hibernate.annotations.GenericGenerator attributes 
	private String idGeneratorType;
	private String idGeneratorStrategy;
	private String genericGeneratorUniqueName;
	private String genericGeneratorStrategy;
	private String[] genericGeneratorParametersPropertyName;

	/**
	 * @return the addCascade
	 */
	public boolean isAddCascade() {
		return addCascade;
	}

	/**
	 * @param addCascade
	 *            the addCascade to set
	 */
	public void setAddCascade(boolean addCascade) {
		this.addCascade = addCascade;
	}

	/**
	 * @return the addMappedBy
	 */
	public boolean isAddMappedBy() {
		return addMappedBy;
	}

	/**
	 * @param addMappedBy
	 *            the addMappedBy to set
	 */
	public void setAddMappedBy(boolean addMappedBy) {
		this.addMappedBy = addMappedBy;
	}

	/**
	 * @return the mappedBy
	 */
	public String getMappedBy() {
		return mappedBy;
	}

	/**
	 * @param mappedBy
	 *            the mappedBy to set
	 */
	public void setMappedBy(String mappedBy) {
		this.mappedBy = mappedBy;
	}
	
	/**
	 * @return the idGeneratorType
	 */
	public String getIdGeneratorType() {
		return idGeneratorType;
	}

	/**
	 * @param idGeneratorType the idGeneratorType to set
	 */
	public void setIdGeneratorType(String idGeneratorType) {
		this.idGeneratorType = idGeneratorType;
	}
	
	/**
	 * @return the idGeneratorStrategy
	 */
	public String getIdGeneratorStrategy() {
		return idGeneratorStrategy;
	}

	/**
	 * @param idGeneratorStrategy the idGeneratorStrategy to set
	 */
	public void setIdGeneratorStrategy(String idGeneratorStrategy) {
		this.idGeneratorStrategy = idGeneratorStrategy;
	}
	
	/**
	 * @return the genericGeneratorUniqueName
	 */
	public String getGenericGeneratorUniqueName() {
		return genericGeneratorUniqueName;
	}

	/**
	 * @param genericGeneratorUniqueName the genericGeneratorUniqueName to set
	 */
	public void setGenericGeneratorUniqueName(String genericGeneratorUniqueName) {
		this.genericGeneratorUniqueName = genericGeneratorUniqueName;
	}

	/**
	 * @return the genericGeneratorStrategy
	 */
	public String getGenericGeneratorStrategy() {
		return genericGeneratorStrategy;
	}

	/**
	 * @param genericGeneratorStrategy the genericGeneratorStrategy to set
	 */
	public void setGenericGeneratorStrategy(String genericGeneratorStrategy) {
		this.genericGeneratorStrategy = genericGeneratorStrategy;
	}

	/**
	 * @return the genericGeneratorParametersPropertyNames
	 */
	public String[] getGenericGeneratorParametersPropertyName() {
		return genericGeneratorParametersPropertyName;
	}

	/**
	 * @param genericGeneratorParametersPropertyNames the genericGeneratorParametersPropertyNames to set
	 */
	public void setGenericGeneratorParametersPropertyName(
			String[] genericGeneratorParametersPropertyName) {
		this.genericGeneratorParametersPropertyName = genericGeneratorParametersPropertyName;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
					.append("addCascade", addCascade)
					.append("addMappedBy", addMappedBy)
					.append("mappedBy", mappedBy)
					.append("idGeneratorType", idGeneratorType)
					.append("idGeneratorStrategy", idGeneratorStrategy)
					.append("genericGeneratorUniqueName", genericGeneratorUniqueName)
					.append("genericGeneratorStrategy", genericGeneratorStrategy)
					.append("genericGeneratorParametersPropertyName", genericGeneratorParametersPropertyName)
					.toString();
	}

}

