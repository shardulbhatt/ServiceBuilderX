/**
 * 
 */
package com.tools.codegeneration.api.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tools.codegeneration.api.ContentBuilder;
import com.tools.codegeneration.api.model.Entity;
import com.tools.codegeneration.api.model.Properties;
import com.tools.codegeneration.api.model.Property;
import com.tools.codegeneration.constants.JPAConstants;
import com.tools.codegeneration.constants.JavaReservedConstants;
import com.tools.codegeneration.constants.StringConstants;
import com.tools.codegeneration.constants.UtilityConstants;
import com.tools.codegeneration.util.JPAAnnotationsMarkerHelper;
import com.tools.codegeneration.util.PropertyTypeName;
import com.tools.codegeneration.util.StringUtil;

/**
 * @author jignesh
 *
 */
public class JavaEntityContentBuilder implements ContentBuilder<Entity> {

	public Collection<String> createContent(Entity entity) {
		
		String packageName = entity.getEntityPackage().getName().getName();
		String entityName = entity.getEntityName().getName();
		Properties propertiesWrapper = entity.getProperties();
		String defaultPropertyType = propertiesWrapper.getDefaultType();
		
		List<Property> entityProperties = propertiesWrapper.getProperties();
		
		List<String> entityContents = new ArrayList<String>();
		
		// 1. Add the package statement to the linked list
		entityContents.add(
				JavaEntityStructureBuilderHelper.createPackageStatement(packageName));
		
		// 2. Add the import statement(s) 
		// Note: Import statements should be unique i.e. each type should be present only once
		entityContents.add(
				JavaEntityStructureBuilderHelper.createImportStatementsForAnEntity(
						entityProperties, defaultPropertyType));
		
		boolean isEntityJPAEnabled = entity.isJpaEnabled();
		
		if (isEntityJPAEnabled) {
			// Add JPA Entity Type Import Statement
			entityContents.add(
				JavaEntityStructureBuilderHelper.createImportStatement(
						JPAConstants.ENTITY_TYPE));
			entityContents.add(StringConstants.LINE_BREAK);
			
			// Add javax.persistence.Entity annotation before class start clause
			// (@Entity)
			entityContents.add(JPAAnnotationsMarkerHelper.createEntityAnnotation());
		}
		
		// 3. Add the class name
		entityContents.add(
				JavaEntityStructureBuilderHelper.createClassStartClause(entityName));
		
		// 4. Add the properties as instance variables
		entityContents.add(
				JavaEntityStructureBuilderHelper.createPropertiesInstanceVariables(
						entity, defaultPropertyType));
		
		// 5. Add the setterXXX and getterXXX of the properties
		for (Property entityProperty : entityProperties) {
			entityContents.add(
					JavaEntityStructureBuilderHelper.createPropertySetter(
							entityProperty, defaultPropertyType));
			entityContents.add(
					JavaEntityStructureBuilderHelper.createPropertyGetter(
							entityProperty, defaultPropertyType));
		} 
		
		// Add class end clause
		entityContents.add(JavaEntityStructureBuilderHelper.createClassEndClause());
		
		return entityContents;
	}
	
	/**
	 * 
	 * @author jigneshg
	 *
	 */
	private static class JavaEntityStructureBuilderHelper {
		
		/**
		 * Example: entityPropertyName: customer.This method returns Customer.
		 * A helper method for creating a property's getterXXX and setterXXX
		 * method.
		 * 
		 * @param originalEntityPropertyName the entity property name as specified
		 * in entities definition XML.
		 * 
		 * @return the specified <code>originalEntityPropertyName</code> with its
		 * first character in upper case.
		 */
		private static String getEntityPropertyNameWithFirstCharInUpperCase(
					String originalEntityPropertyName) {
			return StringUtil.convertCharacterCase(
							originalEntityPropertyName, 0, false); 
		}
		
		/**
		 * Creates the specified <code>entityProperty</code>'s setterXXX method.
		 * 
		 * @param entityProperty the entity property
		 * @param defaultType the property's default type
		 * 
		 * @return the property's setterXXX method
		 */
		static String createPropertySetter(Property entityProperty, String defaultType) {
			// Assuming the names are entered in camel-case as per convention 
			// public void set<PROPERTY_NAME_FIRST_LETTER_UPPER_CASE>(<PROPERT_TYPE> <PROPERTY_NAME>) {
			// 		this.<PROPERTY_NAME> = <PROPERTY_NAME>;
			// }
			
			// For generic property type the setter should look like:
			// public void set<PROPERTY_NAME_FIRST_LETTER_UPPER_CASE>(
			// 					<GENERIC_TYPE_SHORT_NAME><<SHORT_NAME_OF_TYPE_WHOSE_INSTANCES_ARE_HELD_BY_GENERIC_TYPE>> <PROPERTY_NAME>) {
			//		this.<PROPERTY_NAME> = <PROPERTY_NAME>;
			// }
			
			// E.g.
			// public void setEntities(List<Entity> entities) {
			// 		this.entities = entities;
			// }
			
			String originalEntityPropertyName = entityProperty.getName();
			String entityPropertyName = 
					getEntityPropertyNameWithFirstCharInUpperCase(
							originalEntityPropertyName);
			
			StringBuilder propertySetterBuilder = new StringBuilder();
			propertySetterBuilder.append(StringConstants.TAB_4_WIDTH);
			propertySetterBuilder.append(JavaReservedConstants.PUBLIC_ACCESSOR);
			propertySetterBuilder.append(StringConstants.SPACE);
			propertySetterBuilder.append(JavaReservedConstants.VOID);
			propertySetterBuilder.append(StringConstants.SPACE);
			propertySetterBuilder.append(UtilityConstants.SETTER_PREFIX);
			propertySetterBuilder.append(entityPropertyName);
			propertySetterBuilder.append(StringConstants.LEFT_PARENTHESES);
			
			propertySetterBuilder.append(
					appendPropertyTypeName(entityProperty, defaultType));
			
			propertySetterBuilder.append(StringConstants.SPACE);
			propertySetterBuilder.append(originalEntityPropertyName);
			propertySetterBuilder.append(StringConstants.RIGHT_PARENTHESES);
			propertySetterBuilder.append(StringConstants.SPACE);
			propertySetterBuilder.append(StringConstants.LEFT_CURLY_BRACE);
			propertySetterBuilder.append(StringConstants.LINE_BREAK);
			propertySetterBuilder.append(StringConstants.TAB_4_WIDTH)
								 .append(StringConstants.TAB_4_WIDTH);
			propertySetterBuilder.append(JavaReservedConstants.THIS);
			propertySetterBuilder.append(StringConstants.PERIOD);
			propertySetterBuilder.append(originalEntityPropertyName);
			propertySetterBuilder.append(StringConstants.EQUALS_PREFIXED_SUFFIXED_WITH_SPACE);
			propertySetterBuilder.append(originalEntityPropertyName);
			propertySetterBuilder.append(StringConstants.SEMICOLON);
			propertySetterBuilder.append(StringConstants.LINE_BREAK);
			propertySetterBuilder.append(StringConstants.TAB_4_WIDTH);
			propertySetterBuilder.append(StringConstants.RIGHT_CURLY_BRACE);
			propertySetterBuilder.append(StringConstants.LINE_BREAK)
								 .append(StringConstants.LINE_BREAK);
			
			return propertySetterBuilder.toString();
		}
		
		/**
		 * Creates the specified <code>entityProperty</code>'s getterXXX method.
		 * 
		 * @param entityProperty the entity property
		 * @param defaultType the property's default type
		 * 
		 * @return the property's getterXXX method
		 */
		static String createPropertyGetter(Property entityProperty, String defaultType) {
			// Assuming the names are entered in camel-case as per convention 
			// public <PROPERT_TYPE> get<PROPERTY_NAME_FIRST_LETTER_UPPER_CASE>() {
			// 		return <PROPERTY_NAME>;
			// }
			
			// For generic property type the getter should look like:
			// public <GENERIC_TYPE_SHORT_NAME><<SHORT_NAME_OF_TYPE_WHOSE_INSTANCES_ARE_HELD_BY_GENERIC_TYPE>> 
			//			get<PROPERTY_NAME_FIRST_LETTER_UPPER_CASE>() {
			//		return <PROPERTY_NAME>;
			// }
			
			// E.g.
			// public List<Entity> getEntities() {
			//		return entities;
			// }
			
			String originalEntityPropertyName = entityProperty.getName();
			String entityPropertyName = 
					getEntityPropertyNameWithFirstCharInUpperCase(
							originalEntityPropertyName);
			
			StringBuilder propertyGetterBuilder = new StringBuilder();
			propertyGetterBuilder.append(StringConstants.TAB_4_WIDTH);
			propertyGetterBuilder.append(JavaReservedConstants.PUBLIC_ACCESSOR);
			propertyGetterBuilder.append(StringConstants.SPACE);
			
			propertyGetterBuilder.append(
					appendPropertyTypeName(entityProperty, defaultType));
			
			propertyGetterBuilder.append(StringConstants.SPACE);
			propertyGetterBuilder.append(UtilityConstants.GETTER_PREFIX);
			propertyGetterBuilder.append(entityPropertyName);
			propertyGetterBuilder.append(StringConstants.LEFT_PARENTHESES);
			propertyGetterBuilder.append(StringConstants.RIGHT_PARENTHESES);
			propertyGetterBuilder.append(StringConstants.SPACE);
			propertyGetterBuilder.append(StringConstants.LEFT_CURLY_BRACE);
			propertyGetterBuilder.append(StringConstants.LINE_BREAK);
			propertyGetterBuilder.append(StringConstants.TAB_4_WIDTH)
								 .append(StringConstants.TAB_4_WIDTH);
			propertyGetterBuilder.append(JavaReservedConstants.RETURN);
			propertyGetterBuilder.append(StringConstants.SPACE);
			propertyGetterBuilder.append(originalEntityPropertyName);
			propertyGetterBuilder.append(StringConstants.SEMICOLON);
			propertyGetterBuilder.append(StringConstants.LINE_BREAK);
			propertyGetterBuilder.append(StringConstants.TAB_4_WIDTH);
			propertyGetterBuilder.append(StringConstants.RIGHT_CURLY_BRACE);
			propertyGetterBuilder.append(StringConstants.LINE_BREAK)
								 .append(StringConstants.LINE_BREAK);
			
			return propertyGetterBuilder.toString();
		}

		/**
		 * 
		 * Creates the instance variables representing the properties of the
		 * specified <code>entity</code>.
		 * 
		 * @param entity the {@link Entity} instance
		 * @param defaultPropertyType the property's default type
		 * 
		 * 
		 * @return a formatted string representing the instance variables 
		 * representing the properties of the specified <code>entity</code>.
		 */
		static String createPropertiesInstanceVariables(
				Entity entity, String defaultPropertyType) {
			// private <TYPE_SHORT_NAME> <instance_variable>;
			
			// If generic type then,
			// private <GENERIC_TYPE_SHORT_NAME><<TYPE_SHORT_NAME>> <instance_variable>;
			
			// If JPA Enabled then,
			// @<JPA_ANNOTATION>
			// private <GENERIC_TYPE_SHORT_NAME><<TYPE_SHORT_NAME>> <instance_variable>;
			
			boolean isEntityJPAEnabled = entity.isJpaEnabled();
			
			Properties propertiesWrapper = entity.getProperties();
			List<Property> entityProperties = propertiesWrapper.getProperties();
					
			StringBuilder propertiesInstanceVariablesBuilder = new StringBuilder();
			
			for (Property property : entityProperties) {
				
				if (isEntityJPAEnabled) {
					propertiesInstanceVariablesBuilder.append(
							JPAAnnotationsMarkerHelper
									.addJPAAnnotationsToProperty(property));
				}
				
				propertiesInstanceVariablesBuilder
						.append(StringConstants.TAB_4_WIDTH);
				propertiesInstanceVariablesBuilder
						.append(JavaReservedConstants.PRIVATE_ACCESSOR);
				propertiesInstanceVariablesBuilder
						.append(StringConstants.SPACE);
				
				propertiesInstanceVariablesBuilder.append(
						appendPropertyTypeName(property, defaultPropertyType));
				
				propertiesInstanceVariablesBuilder
						.append(StringConstants.SPACE);
				propertiesInstanceVariablesBuilder
						.append(property.getName());
				propertiesInstanceVariablesBuilder
						.append(StringConstants.SEMICOLON);
				
				propertiesInstanceVariablesBuilder
						.append(StringConstants.LINE_BREAK)
						.append(StringConstants.LINE_BREAK);
			}
			
			return propertiesInstanceVariablesBuilder.toString();
		}
		
		/**
		 * 
		 * @param property
		 * @param defaultPropertyType
		 * @return
		 */
		private static String appendPropertyTypeName(Property property, String defaultPropertyType) {
			if (property.isId()) {
				PropertyTypeName.addTypeName(property.getType());
			}
			
			String propertyType = getPropertyType(property.getType(), defaultPropertyType);
			// Short Name of property type
			String propertyTypeShortName = 
					PropertyTypeName.getShortNameForFullyQualifiedTypeName(
								propertyType); 
			
			StringBuilder sb = new StringBuilder();
			if (property.isGenericType()) {
				// Create generic type name.For example:
				// For type: "com.generated.entities.Address" and 
				// for collection type: "java.util.Set" the
				// type, in property instance variable and its getter and setter, 
				// should be specified as Set<Address>.
				sb.append(
						createGenericTypeName(
								propertyTypeShortName,
								StringUtil.getShortNameFromFullyQualifiedTypeName(
										property.getGenericTypeParameter())));	
			} else {
				sb.append(propertyTypeShortName);
			}
			return sb.toString();
		}
		
		/**
		 * Example: for a generic type 
		 * java.util.Collection<com.generated.entities.Address>
		 * this method would return Collection<Address>. 
		 * 
		 * @param genericTypeShortName the generic type name
		 * @param genericTypeParameterShortName the generic type parameter name
		 * 
		 * @return generic type name for a generic property
		 */
		private static String createGenericTypeName(
				String genericTypeShortName, String genericTypeParameterShortName) {
			StringBuilder sb = new StringBuilder();
			sb.append(genericTypeShortName);
			sb.append(StringConstants.LESS_THAN);
			sb.append(genericTypeParameterShortName);
			sb.append(StringConstants.GREATER_THAN);
			return sb.toString();
		}
		/**
		 * 
		 * @param type
		 * @param defaultType
		 * @return
		 */
		static String getPropertyType(String type, String defaultType) {
			if (type == null) {
				return defaultType;
			}
			return type;
		}
		
		/**
		 * 
		 * @param entityName the name of the class
		 * 
		 * @return the formatted string representing a Java class's start clause
		 */
		static String createClassStartClause(String entityName) {
			StringBuilder classNameBuilder = new StringBuilder();
			classNameBuilder.append(JavaReservedConstants.PUBLIC_ACCESSOR);
			classNameBuilder.append(StringConstants.SPACE);
			classNameBuilder.append(JavaReservedConstants.CLASS);
			classNameBuilder.append(StringConstants.SPACE);
			classNameBuilder.append(entityName);
			classNameBuilder.append(StringConstants.SPACE);
			classNameBuilder.append(StringConstants.LEFT_CURLY_BRACE);
			
			classNameBuilder.append(StringConstants.LINE_BREAK)
							.append(StringConstants.LINE_BREAK);
			
			return classNameBuilder.toString();
		}
		
		/**
		 * 
		 * @return the formatted string representing a Java class's end clause
		 */
		static String createClassEndClause() {
			return StringConstants.RIGHT_CURLY_BRACE;
		}
		
		/**
		 * Creates import statements for an entity.
		 * 
		 * @param entityProperties the properties of an {@link Entity}
		 * 
		 * @param defaultType the property's default type
		 * 
		 * @return the formatted string representing the import statements for 
		 * a given {@link Entity}
		 */
		static String createImportStatementsForAnEntity(
				List<Property> entityProperties, String defaultType) {
			
			// Filter out duplicate type names
			Set<String> typeNames = new HashSet<String>();
			
			String propertyType = null;
			String[] jpaAnnotationTypes= null;
			for (Property property : entityProperties) {
				// For primitives import statements are not required
				propertyType =  getPropertyType(property.getType(), defaultType);
				if (propertyType.lastIndexOf(StringConstants.PERIOD) > 0) {
					typeNames.add(propertyType);	
					// For generic types the generic type and its parameterized
					// type both needs to be present in import statements
					
					if (property.isGenericType()) {
						typeNames.add(property.getGenericTypeParameter());
					}
					
					jpaAnnotationTypes = property.getJpaAnnotationTypes();
					
					if (jpaAnnotationTypes != null && jpaAnnotationTypes.length > 0) {
						addJPAImportStatementsForAnEntity(typeNames, jpaAnnotationTypes);
					}
				}
			}
			
			return createImportStatements(typeNames);
		}
		/**
		 * 
		 * @param typeNames
		 * @param jpaAnnotationTypes
		 */
		private static void addJPAImportStatementsForAnEntity(
				Set<String> typeNames, String[] jpaAnnotationTypes) {
			
			for (String jpaAnnotationType : jpaAnnotationTypes) {
				typeNames.add(jpaAnnotationType);
			}
		}
		
		/**
		 * 
		 * @param typeNames
		 * @return
		 */
		private static String createImportStatements(Set<String> typeNames) {
			// import <FULLY_QUALIFIED_TYPE_NAME>
			StringBuilder importStatementBuilder = new StringBuilder();
			
			for (String typeName : typeNames) {
				importStatementBuilder.append(createImportStatement(typeName));
			}
			
			importStatementBuilder.append(StringConstants.LINE_BREAK);
			
			return importStatementBuilder.toString();
		}

		/**
		 * 
		 * @param typeName
		 * @return
		 */
		private static String createImportStatement(String typeName) {
			StringBuilder importStatementBuilder = new StringBuilder();
			importStatementBuilder.append(JavaReservedConstants.IMPORT);
			importStatementBuilder.append(StringConstants.SPACE);
			importStatementBuilder.append(typeName);	
			importStatementBuilder.append(StringConstants.SEMICOLON);
			importStatementBuilder.append(StringConstants.LINE_BREAK);
			return importStatementBuilder.toString();
		}
		
		/**
		 * 
		 * @param packageName the package name to which a given entity belongs.
		 * 
		 * @return the formatted string representing the package statement for 
		 * a given {@link Entity}
		 */
		static String createPackageStatement(String packageName) {
			// package <PACKAGE_NAME>
			StringBuilder packageStatementBuilder = new StringBuilder();
			packageStatementBuilder.append(JavaReservedConstants.PACKAGE);
			packageStatementBuilder.append(StringConstants.SPACE);
			packageStatementBuilder.append(packageName);
			packageStatementBuilder.append(StringConstants.SEMICOLON);
			// Append a new line
			packageStatementBuilder.append(StringConstants.LINE_BREAK)
								   .append(StringConstants.LINE_BREAK);
			return packageStatementBuilder.toString();
		}
	}

}
