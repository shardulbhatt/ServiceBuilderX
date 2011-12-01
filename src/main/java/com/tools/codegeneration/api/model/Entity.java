package com.tools.codegeneration.api.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.tools.codegeneration.util.EntityHelper;

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
	
	/**
	 * 
	 * @return the {@link Entity}'s fully qualified name
	 */
	public String getEntityFullyQualifiedName() {
		assert(entityPackage != null && entityPackage.getName() != null);
		assert(entityName != null && entityName.getName() != null);
		
		return EntityHelper.getEntityFullyQualifiedName(
					entityPackage.getName().getName(), entityName.getName());
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
