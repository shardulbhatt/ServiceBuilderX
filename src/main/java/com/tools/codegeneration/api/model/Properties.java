package com.tools.codegeneration.api.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Properties {
	private String defaultType;
	private List<Property> properties;
	
	
	/**
	 * @return the defaultType
	 */
	public String getDefaultType() {
		return defaultType;
	}

	/**
	 * @param defaultType the defaultType to set
	 */
	public void setDefaultType(String defaultType) {
		this.defaultType = defaultType;
	}

	/**
	 * @return the properties
	 */
	public List<Property> getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
					.append("defaultType", defaultType)
					.append("properties", properties)
					.toString();
	}
	
	private static List<Entity> listFactory() {
		return new ArrayList<Entity>();
	}
}
