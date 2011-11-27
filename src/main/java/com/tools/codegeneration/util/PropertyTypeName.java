package com.tools.codegeneration.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tools.codegeneration.api.model.Entity;
import com.tools.codegeneration.api.model.Properties;
import com.tools.codegeneration.api.model.Property;

public class PropertyTypeName {
	private static Map<String, String> propertyTypeNamesMap = new HashMap<String, String>();
	
	/**
	 * 
	 * @param entities
	 */
	public static void addTypesNameFromEntities(List<Entity> entities) {
		Properties propertiesWrapper = null;
		for (Entity entity : entities) {
			propertiesWrapper = entity.getProperties(); 
			addTypesName(propertiesWrapper.getProperties(), propertiesWrapper.getDefaultType());
		}
	}
	/**
	 * 
	 * @param fullyQualifiedTypeName
	 */
	public static void removeTypeName(String fullyQualifiedTypeName) {
		propertyTypeNamesMap.remove(fullyQualifiedTypeName);
	}
	
	/**
	 * 
	 */
	public static void removeAllTypesName() {
		propertyTypeNamesMap.clear();
	}
	
	/**
	 * 
	 */
	public static Map<String, String> getAllTypesName() {
		return new HashMap<String, String>(propertyTypeNamesMap);
	}
	
	/**
	 * 
	 * @param fullyQualifiedTypeName
	 */
	public static void addTypeName(String fullyQualifiedTypeName) {
		if (!propertyTypeNamesMap.containsKey(fullyQualifiedTypeName)) {
			propertyTypeNamesMap.put(
					fullyQualifiedTypeName, 
					StringUtil.getShortNameFromFullyQualifiedTypeName(
									fullyQualifiedTypeName));	
		}
	}
	
	/**
	 * 
	 * @param fullyQualifiedTypeName
	 * @return
	 */
	public static String getShortNameForFullyQualifiedTypeName(
									String fullyQualifiedTypeName) {
		return propertyTypeNamesMap.get(fullyQualifiedTypeName);
	}
	
	/**
	 * 
	 * @param entityProperties
	 * @param defaultPropertyType
	 */
	private static void addTypesName(List<Property> entityProperties, String defaultPropertyType) {
		// Gather properties type for creating import statements
		String propertyType = null;
		for (Property property : entityProperties) {
			propertyType = property.getType(); 
			if (propertyType == null) {
				propertyType = defaultPropertyType;
			}
			addTypeName(propertyType);
		}
	}
}
