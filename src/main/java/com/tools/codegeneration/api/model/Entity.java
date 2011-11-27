package com.tools.codegeneration.api.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Entity {
	private Name entityName;
	private Package entityPackage;
	private Properties properties;
	private boolean jpaEnabled;
	
	/**
	 * @return the entityName
	 */
	public Name getEntityName() {
		return entityName;
	}

	/**
	 * @param entityName the entityName to set
	 */
	public void setEntityName(Name entityName) {
		this.entityName = entityName;
	}

	/**
	 * @return the entityPackage
	 */
	public Package getEntityPackage() {
		return entityPackage;
	}

	/**
	 * @param entityPackage the entityPackage to set
	 */
	public void setEntityPackage(Package entityPackage) {
		this.entityPackage = entityPackage;
	}

	/**
	 * @return the properties
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	/**
	 * @return the jpaEnabled
	 */
	public boolean isJpaEnabled() {
		return jpaEnabled;
	}

	/**
	 * @param jpaEnabled the jpaEnabled to set
	 */
	public void setJpaEnabled(boolean jpaEnabled) {
		this.jpaEnabled = jpaEnabled;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
					.append("entityName", entityName)
					.append("entityPackage", entityPackage)
					.append("properties", properties)
					.append("jpaEnabled", jpaEnabled)
					.toString();
	}
}
