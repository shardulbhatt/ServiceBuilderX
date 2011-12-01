package com.tools.codegeneration.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Id;

import com.tools.codegeneration.api.model.Entity;
import com.tools.codegeneration.api.model.Properties;
import com.tools.codegeneration.api.model.Property;
import com.tools.codegeneration.api.model.relationships.Many;
import com.tools.codegeneration.api.model.relationships.Relation;
import com.tools.codegeneration.api.model.relationships.Relation.RelationType;
import com.tools.codegeneration.constants.JPAConstants;
import com.tools.codegeneration.constants.UtilityConstants;

/**
 * A helper class for handling {@link Property} related data.
 * 
 * @author jigneshg
 *
 */
public class PropertiesHelper {

	/**
	 * Updates the existing list of {@link Property} instances, held by
	 * the {@link Entity} instance, specified by <code>entities</code>, 
	 * by appending {@link Property} instances of types,
	 * represented by {@link Relation} instances held by specified 
	 * <code>categorizedRelationsMap</code>, based on the {@link RelationType}
	 * the {@link Relation} instances are mapped to.
	 * 
	 * <br/><br/>
	 * 
	 * For instance lets say an entity named com.sample.EntityA holds the following
	 * properties as specified in the <i>entities definition XML</i>.
	 * 
	 * <ul>
	 * 		<li>{@link String} name</li>
	 * 		<li>{@link String} value</li>
	 * </ul>
	 * 
	 * Now <b>com.sample.EntityA</b> holds a relation of type 
	 * <i>One-To-Many</i> with <b>com.sample.EntityB</b> and the collection type
	 * that would be holding instances of type <b>com.sample.EntityB</b>, in
	 * <b>com.sample.EntityA</b>, is specified as {@link Set}.
	 * 
	 * <br/><br/>
	 * 
	 * This method would add another property to the current list named
	 * <i>entityBSet</i> having its type {@link Set}.Therefore final properties 
	 * list of <b>com.sample.EntityA</b> would look like:
	 * 
	 * <ul>
	 * 		<li>{@link String} name</li>
	 * 		<li>{@link String} value</li>
	 * 		<li> {@link Set} entityBSet</li>
	 * </ul>
	 * 
	 * 
	 * @param entities the {@link List} holding instances of type {@link Entity}
	 * 
	 * @param categorizedRelationsMap a {@link Map} holding {@link RelationType}
	 * as the key and a {@link List}, holding instance of type {@link Relation}
	 * ,as value
	 * 
	 * @param entityFullyQualifiedNameIsJPAEnabledMap a {@link Map} holding
     * the fully qualified name of an {@link Entity}, in {@link String} representation,
     * as the key and a boolean value <code>true</code> if the entity definition 
     * is marked as jpaEnabled (as specified in the 
     * <i>entities definition XML</i>), <code>false</code> otherwise
	 */
	public static final void updateProperties(
			List<Entity> entities, 
			Map<RelationType, List<Relation>> categorizedRelationsMap,
			Map<String, Boolean> entityFullyQualifiedNameIsJPAEnabledMap) {
		
		Properties propertiesWrapper = null;
		// Maps entity fully qualified name to its properties wrapper instance
		Map<String, Properties> entityFullyQualifiedNamePropertiesMap = 
				new HashMap<String, Properties>();
		
		for (Entity entity : entities) {
			propertiesWrapper = entity.getProperties();
			
			entityFullyQualifiedNamePropertiesMap.put(
					entity.getEntityFullyQualifiedName(), propertiesWrapper);
			
		}
		
		// Add related entities as properties in required places
		Set<RelationType> relationTypes = categorizedRelationsMap.keySet();
		
		for (RelationType relationType : relationTypes) {
			updatePropertiesListUsingRelations(categorizedRelationsMap,
					entityFullyQualifiedNamePropertiesMap, 
					entityFullyQualifiedNameIsJPAEnabledMap, relationType);
		}
		
		// This method should be invoked only after all the relations have been
		// processed through.Because this method checks each property for 
		// whether the property is an Id property or a property related with 
		// another entity.
		// If none of the above mentioned conditions are true then only
		// this method marks the property to have a javax.persistence.Column
		// annotation.
		addIdPropertyAndAddJPAColumnAnnotationToJPAEnabledEntities(
				entityFullyQualifiedNamePropertiesMap, 
				entityFullyQualifiedNameIsJPAEnabledMap);
		
	}
	
	/**
	 * Adds Id property to the current properties list of an entity and sets
	 * the information to annotate each property, which is not id and 
	 * not a related property, with {@link Column} annotation, if the entity
	 * is found JPA enabled
	 * 
	 * @param entityFullyQualifiedNamePropertiesMap a {@link Map} holding
	 * the fully qualified name of an {@link Entity}, in {@link String} representation,
	 * as the key and the {@link Properties} instance holding the 
	 * {@link Property} instances of the entity (as specified in the 
	 * <i>entities definition XML</i>) having its fully qualified name
	 * set as the key of this map.
	 * 
	 * @param entityFullyQualifiedNameIsJPAEnabledMap a {@link Map} holding
     * the fully qualified name of an {@link Entity}, in {@link String} representation,
     * as the key and a boolean value <code>true</code> if the entity definition 
     * is marked as jpaEnabled (as specified in the 
     * <i>entities definition XML</i>), <code>false</code> otherwise
     *  
	 */
	private static void addIdPropertyAndAddJPAColumnAnnotationToJPAEnabledEntities(
			Map<String, Properties> entityFullyQualifiedNamePropertiesMap, 
			Map<String, Boolean> entityFullyQualifiedNameIsJPAEnabledMap) {
		
		// Add Id property to each entity that is JPA enabled by iterating through
		// entityFullyQualifiedNameIsJPAEnabledMap.
		Set<String> entityFullyQualifiedNames = entityFullyQualifiedNameIsJPAEnabledMap.keySet();
		
		Properties entityPropertiesWrapper = null;
		Boolean isEntityJPAEnabled =  false;
		for (String entityFullyQualifiedTypeName : entityFullyQualifiedNames) {
			isEntityJPAEnabled = 
					entityFullyQualifiedNameIsJPAEnabledMap.get(
								entityFullyQualifiedTypeName);
			
			if (isEntityJPAEnabled) {
				
				entityPropertiesWrapper = 
						entityFullyQualifiedNamePropertiesMap.get(
										entityFullyQualifiedTypeName); 
				
				//System.out.println("Entity: [" + entityFullyQualifiedTypeName + "]");
				setJPAAnnotationTypeColumnForPropertyNotIdAndNotRelated(
							entityPropertiesWrapper);
			}
		}
	}
	
	/**
	 * Sets the value for {@link Property#getJpaAnnotationTypes()} property.
	 * A given entity's, detected as JPA enabled, each property, which is not of
	 * type id and not a related entity, should be marked with {@link Column} 
	 * annotation until and unless a property is explicitly marked as transient.
	 *  
	 * TODO: Add support for marking the properties as transient
	 * 
	 * @param propertiesWrapper the {@link Properties} instance of a given 
	 * entity 
	 */
	private static void setJPAAnnotationTypeColumnForPropertyNotIdAndNotRelated(
				Properties propertiesWrapper) {
		List<Property> currentEntityProperties = propertiesWrapper.getProperties();
		
		RelationType propertyRelationType = null;
		boolean isIdPropertyFound = false;
		
		for (Property property : currentEntityProperties) {
			propertyRelationType = property.getRelationType();
			
			// In case of one-to-one relation already an Id property is added 
			// to the properties list.Thus this check for an "id" property.
			// If no id property is found, we can add a simple id property to
			// the current properties list
			if (!isIdPropertyFound) {
				isIdPropertyFound = property.isId();	
			}
			 
			if (!property.isId() && (RelationType.NOT_RELATED == propertyRelationType)) {
				property.setJpaAnnotationTypes(
						new String[] { 
								JPAConstants.COLUMN_TYPE 
						});
			} 
		}
		
		//System.out.println("Id Property Found: [" + isIdPropertyFound + "]");
		if(!isIdPropertyFound) {
			// Update entities property list to include an Id property at first
			// position properties list
			addIdProperty(propertiesWrapper);
		}
	}
	
	/**
	 * Adds a property, to the current properties list of a given entity which is 
	 * detected as JPA enabled, named "id" annotation with JPA 
	 * {@link Id} and its related annotation(s).
	 * 
	 * @param propertiesWrapper the {@link Properties} instance of a given 
	 * entity
	 */
	private static void addIdProperty(Properties propertiesWrapper) {
		// Update entity's current properties list to include a "id" property
		// of default type java.lang.Long
		
		String[] jpaAnnotationTypesArr = new String[] {
				JPAConstants.ID_TYPE,
				JPAConstants.GENERATED_VALUE_TYPE,
				JPAConstants.GENERATION_TYPE
		};
		
		createIdProperty(
				UtilityConstants.ID_PROPERTY_NAME, 
				UtilityConstants.ID_PROPERTY_DEFAULT_TYPE,
				jpaAnnotationTypesArr, propertiesWrapper);
		
	}
	
	
	/**
	 * This method iterates through the {@link Relation} instances there
	 * in the <code>categorizedRelationsMap</code> corresponding to the
	 * specified <code>relationType</code> and updates the current list 
	 * of property(s) for an entity by appending {@link Property}
	 * instance(s) of types represented by {@link Relation} instances there
	 * in the <code>categorizedRelationsMap</code>. 
	 * 
	 * @param categorizedRelationsMap a {@link Map} holding {@link RelationType}
	 * as the key and a {@link List}, holding instance of type {@link Relation}
	 * ,as value
	 * 
	 * @param entityFullyQualifiedNamePropertiesMap a {@link Map} holding
	 * the fully qualified name of an {@link Entity}, in {@link String} representation,
	 * as the key and the {@link Properties} instance holding the 
	 * {@link Property} instances of the entity (as specified in the 
	 * <i>entities definition XML</i>) having its fully qualified name
	 * set as the key of this map.
	 * 
	 * @param entityFullyQualifiedNameIsJPAEnabledMap a {@link Map} holding
     * the fully qualified name of an {@link Entity}, in {@link String} representation,
     * as the key and a boolean value <code>true</code> if the entity definition 
     * is marked as jpaEnabled (as specified in the 
     * <i>entities definition XML</i>), <code>false</code> otherwise
	 * 
	 * @param relationType the {@link RelationType} enum value
	 * 
	 * @see #updateProperties(List, Map)
	 */
	private static final void updatePropertiesListUsingRelations(
			Map<RelationType, List<Relation>> categorizedRelationsMap,
			Map<String, Properties> entityFullyQualifiedNamePropertiesMap,
			Map<String, Boolean> entityFullyQualifiedNameIsJPAEnabledMap,
			RelationType relationType) {
		List<Relation> relations = categorizedRelationsMap.get(relationType);
		
		PropertiesUpdateHelper propertiesUpdateHelper = new PropertiesUpdateHelper();
		propertiesUpdateHelper.setEntityFullyQualifiedNamePropertiesMap(entityFullyQualifiedNamePropertiesMap);
		propertiesUpdateHelper.setEntityFullyQualifiedNameIsJPAEnabledMap(entityFullyQualifiedNameIsJPAEnabledMap);
		
		for (Relation relation : relations) {
			propertiesUpdateHelper.setRelation(relation);
			propertiesUpdateHelper.setRelationType(relationType);
			propertiesUpdateHelper.updateProperties();
	    }
	}
	
	
	

	/**
	 * Creates a {@link Property} instance and sets its required values and adds
	 * it to the current list  of {@link Property} instances, at first position,
	 * held by the specified <code>entityPropertiesWrapper</code> for a given 
	 * {@link Entity}
	 * 
	 * @param propertyName the property name to set.
	 * 
	 * @param propertyFullyQualifiedTypeName the fully qualified type name of 
	 * the property.
	 * 
	 * @param jpaAnnotationTypesArr  an array of {@link String} holding the types
	 * of JPA annotation(s) to be added to the property
	 * 
	 * @param entityPropertiesWrapper the {@link Properties} instance of an
	 * {@link Entity}
	 */ 
	private static void createIdProperty(
			String propertyName, String propertyFullyQualifiedTypeName,
			String[] jpaAnnotationTypesArr, Properties entityPropertiesWrapper) {
		
		Property property = new Property();
		property.setName(propertyName);
		property.setType(propertyFullyQualifiedTypeName);
		property.setJpaAnnotationTypes(jpaAnnotationTypesArr);
		property.setId(true);
		property.setIdAnnotationType(IdAnnotationType.SIMPLE);
		
		entityPropertiesWrapper.getProperties().add(0, property);
		
	}
	
	
	/**
	 * Creates property name for an entity found as related to another entity.
	 * The name is created using the values found for properties
	 * {@link Many#getType()} and {@link Many#getCollectionType()}.
	 * 
	 * Example: 
	 * Conside an entity com.generated.entities.Customer (customer) has 
	 * OneToMany relationship with another entity 
	 * com.generated.entities.Address (address) and the collection type is specified
	 * as java.util.Set for address.Thus customer should hold a property named
	 * "addressSet" in customer entity.This method does this. 
	 * 
	 * @param many the {@link Many} instance representing the related entity type
	 * 
	 * @return the property name
	 */
	public static final String createPropertyNameForCollectionTypeRelatedEntity(Many many) {
		String type = many.getType();
		String collectionType = many.getCollectionType();
		
		// Get short name of the type with first character converted to lower case
		String typeShortNameWithFirstCharLower =  
						createPropertyNameFromTypeName(type);
		
		// Get short name of the collection type
		String collectionShortName = 
				StringUtil.getShortNameFromFullyQualifiedTypeName(collectionType);
		
		// Convert first character of type's short name to lower case and 
		// append the resulting string with collectionShortName
		StringBuilder sb = new StringBuilder();
		sb.append(typeShortNameWithFirstCharLower);
		sb.append(collectionShortName);
		
		return sb.toString();
	}
	
	/**
	 * Creates property name from the type of the property.
	 *  
	 * For e.g. for a {@link Property} having its type set to 
	 * "com.generated.entities.Address" may not have its name set.In such case 
	 * the property name is derived from the specified 
	 * <code>fullyQualifiedTypeName</code> by retaining only 
	 * the string after the last period in the specified 
	 * <code>fullyQualifiedTypeName</code>.That is in our example case property 
	 * name would be returned as "address".
	 *  
	 * @param fullyQualifiedTypeName the fully qualified type name
	 * 
	 * @return String the property name
	 */
	public static final String createPropertyNameFromTypeName(String fullyQualifiedTypeName) {
		String shortNameForType = 
				StringUtil.getShortNameFromFullyQualifiedTypeName(
								fullyQualifiedTypeName);
		return StringUtil.convertCharacterCase(shortNameForType, 0, true);
	}

}
