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
import com.tools.codegeneration.api.model.relationships.One;
import com.tools.codegeneration.api.model.relationships.Relation;
import com.tools.codegeneration.api.model.relationships.Relation.RelationType;
import com.tools.codegeneration.constants.HibernateORMConstants;
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
	 */
	public static final void updateProperties(
			List<Entity> entities, 
			Map<RelationType, List<Relation>> categorizedRelationsMap) {
		
		String packageName = null;
		String entityName = null;
		String entityFullyQualifiedName = null;
		Properties propertiesWrapper = null;
		// Maps entity fully qualified name to its properties wrapper instance
		Map<String, Properties> entityFullyQualifiedNamePropertiesMap = 
				new HashMap<String, Properties>();
		
		// Maps entity fully qualified name to its JPA enabled status
		Map<String, Boolean> entityFullyQualifiedNameIsJPAEnabledMap = 
				new HashMap<String, Boolean>();
		
		for (Entity entity : entities) {
			
			packageName = entity.getEntityPackage().getName().getName();
			entityName = entity.getEntityName().getName();
			entityFullyQualifiedName = EntityHelper.getEntityFullyQualifiedName(packageName, entityName);
			propertiesWrapper = entity.getProperties();
			
			entityFullyQualifiedNamePropertiesMap.put(
					entityFullyQualifiedName, propertiesWrapper);
			
			entityFullyQualifiedNameIsJPAEnabledMap.put(
					entityFullyQualifiedName, entity.isJpaEnabled());
		}
		
		// Add related entities as properties in required places
		Set<RelationType> relationTypes = categorizedRelationsMap.keySet();
		
		for (RelationType relationType : relationTypes) {
			updatePropertiesListUsingRelations(categorizedRelationsMap,
					entityFullyQualifiedNamePropertiesMap, relationType);
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
	 * @param relationType the {@link RelationType} enum value
	 * 
	 * @see #updateProperties(List, Map)
	 */
	private static final void updatePropertiesListUsingRelations(
			Map<RelationType, List<Relation>> categorizedRelationsMap,
			Map<String, Properties> entityFullyQualifiedNamePropertiesMap,
			RelationType relationType) {
		List<Relation> relations = categorizedRelationsMap.get(relationType);
		
		for (Relation relation : relations) {
			PropertiesHelper.addPropertiesRepresentingTheRelation(
				relationType, relation, entityFullyQualifiedNamePropertiesMap);
	    }
	}
	
	/**
	 * Updates the current list of {@link Property} instances, held by
	 * an {@link Entity#getProperties()} instance, by appending {@link Property}
	 * instance(s) of types represented by specified <code>relation</code> 
	 * 
	 * @param relationType the {@link RelationType} enum value
	 * 
	 * @param relation the {@link Relation} instance
	 * 
	 * @param entityFullyQualifiedNamePropertiesMap a {@link Map} holding
	 * the fully qualified name of an {@link Entity}, in {@link String} representation,
	 * as the key and the {@link Properties} instance holding the 
	 * {@link Property} instances of the entity (as specified in the 
	 * <i>entities definition XML</i>) having its fully qualified name
	 * set as the key of this map.
	 * 
	 */
	private static final void addPropertiesRepresentingTheRelation(
			RelationType relationType, Relation relation, 
			Map<String, Properties> entityFullyQualifiedNamePropertiesMap) {
		
		One one = relation.getOne();
		Many many = relation.getMany();
		Properties entityPropertiesWrapper = null;
		
		switch (relationType) {
			case ONE_TO_MANY:
				entityPropertiesWrapper = 
					entityFullyQualifiedNamePropertiesMap.get(one.getType());
				
				// one-to-many: Update the "one" side entity by adding a property
				// to it having its type set to collection type, specified in "many"
				// instance as argument to this method, holding the parameterized
				// type as the type of the entity specified at the "many" end
				// of the relation
				addPropertiesHavingOneToManyRelation(entityPropertiesWrapper, relation);
				break;
			
			case MANY_TO_ONE:
				entityPropertiesWrapper = 
					entityFullyQualifiedNamePropertiesMap.get(many.getType());
				
				// many-to-one: Update the "many" side entity by adding a property
				// to it of type specified for the entity at "one" end
				// of the relation
				addPropertiesHavingManyToOneRelation(entityPropertiesWrapper, one);
				
				break;
			
			case ONE_TO_ONE:
				entityPropertiesWrapper = 
					entityFullyQualifiedNamePropertiesMap.get(many.getType());
			
				Properties oneEndEntityPropertiesWrapper = 
						entityFullyQualifiedNamePropertiesMap.get(one.getType());
			
				// one-to-one: 
				addPropertiesHavingOneToOneRelation(
						oneEndEntityPropertiesWrapper, entityPropertiesWrapper, 
						relation);
				break;
		}
	}
	
	/**
	 * Updates an entity's property's current list to append
	 * its related entity(s), having <b>ONE-TO-ONE relationship</b>, 
	 * as properties and set its type information, its JPA Annotation relation 
	 * information, etc. This also adds an Id property and sets its required
	 * annotations information for a one-to-one relation to work successfully 
	 * at runtime.
	 * 
	 * @param oneEndEntityPropertiesWrapper the {@link Properties} instance 
	 * of {@link Entity} whose type is specified at "one" end of the relation 
	 * in <i>relations definition XML</i>
	 * 
	 * @param manyEndEntityPropertiesWrapper the {@link Properties} instance 
	 * of {@link Entity} whose type is specified at "many" end of the relation 
	 * in <i>relations definition XML</i>
	 *  
	 * @param relation the {@link Relation} instance
	 */
	private static void addPropertiesHavingOneToOneRelation(
			Properties oneEndEntityPropertiesWrapper, 
			Properties manyEndEntityPropertiesWrapper, Relation relation) {
		
		One one = relation.getOne();
		Many many = relation.getMany();
				
		// In this case both the "one" end entity and "many" end entity needs to
		// be updated.
		String oneEndEntityType = one.getType();
		String manyEndEntityType = many.getType();
		
		//====================== ONE end entity
		// In "one" end entity a property of type specified for "many" end entity
		// is to be added and this property needs to be marked with @PrimaryKeyJoinColumn
		// annotation in addition to @OneToOne annotation.
		
		String[] jpaAnnotationTypesArr = new String[] {
				JPAAnnotationsMarkerHelper.getRelationTypeFullyQualifiedName(
						RelationType.ONE_TO_ONE),
				JPAConstants.CASCADE_ENUM_TYPE
		};
		
		// Note:Using the overloaded version (not passing in relation which means
		// mappedBy attribute is NOT TO BE SET on @OneToOne annotation to be set
		// for this property)
		createAndAddProperty(manyEndEntityType, RelationType.ONE_TO_ONE, 
				jpaAnnotationTypesArr, oneEndEntityPropertiesWrapper);
		
		//====================== MANY end entity
		// In the "many" end entity a property of type 
		// specified for "one" end entity is to be added.This property is to be
		// annotated with @OneToOne and should use the "mappedBy" attribute of this
		// annotation whose value should be set to the name of the property added
		// in "one" end entity for specifying this one-to-one relation.Also
		// the "id" property
		// needs to be updated with following annotations for sharing the same 
		// primary key across entities that have one-to-one relation
		
		// @Id
		// @Column(name = "booking_id", unique = true, nullable = false)//TODO: this remains in case of Id property for one-to-one non-primary entity 
		// @GeneratedValue(generator="foreign")
		// @GenericGenerator(name="foreign", strategy = "foreign", parameters={
		//		@Parameter(name="property", value="booking")
		// })

		// No cascade required
		String[] relatedEntityJPAAnnotationTypesArr = new String[] {
				JPAAnnotationsMarkerHelper.getRelationTypeFullyQualifiedName(
						RelationType.ONE_TO_ONE),
		};
		
		// Note: Using the overloaded version (passing in relation which means
		// "mappedBy" attribute is TO BE SET on @OneToOne annotation to be set
		// for this property)
		createAndAddProperty(oneEndEntityType, RelationType.ONE_TO_ONE, 
				relatedEntityJPAAnnotationTypesArr, manyEndEntityPropertiesWrapper,
				relation);
		
		// Add an Id property too here which requires special annotations for
		// the one-to-one relation to work successfully by sharing the same
		// primary key between primary and non-primary entities 
		String[] oneToOneIdJPAAnnotationTypesArr = new String[] {
				JPAConstants.ID_TYPE,
				JPAConstants.GENERATED_VALUE_TYPE,
				HibernateORMConstants.GENERIC_GENERATOR_TYPE,
				HibernateORMConstants.PARAMETER_TYPE
		};
		
		createOneToOneNonPrimaryEntityIdProperty(
				UtilityConstants.ID_PROPERTY_NAME, 
				UtilityConstants.ID_PROPERTY_DEFAULT_TYPE, 
				oneToOneIdJPAAnnotationTypesArr, manyEndEntityPropertiesWrapper,
				oneEndEntityType);
		
	}
	/**
	 * Updates an entity's property's current list to append
	 * its related entity(s), having <b>MANY-TO-ONE relationship</b>, 
	 * as properties and set its type information, its JPA Annotation relation 
	 * information, etc. 
	 * 
	 * @param entityPropertiesWrapper a given entity's {@link Properties} instance
	 * @param one the {@link One} instance representing the type of a related 
	 * entity 
	 */
	private static void addPropertiesHavingManyToOneRelation(
				Properties entityPropertiesWrapper, One one) {
		
		String type = one.getType();

		String[] jpaAnnotationTypesArr = new String[] {
				JPAAnnotationsMarkerHelper.getRelationTypeFullyQualifiedName(
						RelationType.MANY_TO_ONE),
				JPAConstants.CASCADE_ENUM_TYPE
		};
		createAndAddProperty(
				type, RelationType.MANY_TO_ONE, jpaAnnotationTypesArr, 
				entityPropertiesWrapper);
	}

	/**
	 * Updates an entity's property's current list to append
	 * its related entity(s), having <b>ONE-TO-MANY relationship</b>, as properties 
	 * and set its type information, its JPA Annotation relation information, 
	 * etc.
	 * 
	 * @param entityPropertiesWrapper a given entity's {@link Properties} instance
	 * 
	 * @param relation the {@link Relation} instance representing the relation
	 */
	private static void addPropertiesHavingOneToManyRelation(
			Properties entityPropertiesWrapper, Relation relation) {
		
		// In this case two types needs to be added to the related entity
		// 1. the type of property
		// 2. the type of collection
		// And both of these should be present in import statements
		
		// Property type's instance creation is not needed in this case
		// as it will be the collection type property that will need to be 
		// added to the entity.Thus just adding the type to
		// com.tools.codegeneration.util.PropertyTypeName which is used for 
		// creating import statements 
		PropertyTypeName.addTypeName(relation.getMany().getType());
		
		// =========================Create Collection Type Property
		
		String[] jpaAnnotationTypesArr = new String[] {
				JPAAnnotationsMarkerHelper.getRelationTypeFullyQualifiedName(
						RelationType.ONE_TO_MANY),
				JPAConstants.CASCADE_ENUM_TYPE
		};
		
		createCollectionTypeProperty(
				RelationType.ONE_TO_MANY, jpaAnnotationTypesArr, 
				entityPropertiesWrapper, relation);
		
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
		
		createAndAddProperty(
				propertyName, 
				propertyFullyQualifiedTypeName, 
				null, 
				jpaAnnotationTypesArr, entityPropertiesWrapper, null, true);
		
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
	 * @param jpaAnnotationTypesArr an array of {@link String} holding the types
	 * of JPA annotation(s) to be added to the property
	 * 
	 * @param entityPropertiesWrapper the {@link Properties} instance of an
	 * {@link Entity}
	 * 
	 * @param oneToOneRelatedPrimaryEntityTypeFullyQualifiedName the fully 
	 * qualified type name of the primary entity involved in a One-to-One 
	 * relation.
	 */
	private static void createOneToOneNonPrimaryEntityIdProperty(
			String propertyName, String propertyFullyQualifiedTypeName,
			String[] jpaAnnotationTypesArr, Properties entityPropertiesWrapper,
			String oneToOneRelatedPrimaryEntityTypeFullyQualifiedName) {
		
		Property property = new Property();
		property.setId(true);
		property.setIdAnnotationType(IdAnnotationType.ONE_TO_ONE);
		property.setName(propertyName);
		property.setType(propertyFullyQualifiedTypeName);
		property.setJpaAnnotationTypes(jpaAnnotationTypesArr);
		// This would be required in case creating Id annotation
		// on non-primary entities having a one-to-one relation
		// with a primary entity.Typically the entity specified at "one"
		// end in relations definition XML is considered to be the 
		// "primary entity" and the one at specified at "many" end is
		// considered as "non-primary entity"
		property.setOneToOnePrimaryEntityPropertyName(
				createPropertyNameFromTypeName(
						oneToOneRelatedPrimaryEntityTypeFullyQualifiedName));
		entityPropertiesWrapper.getProperties().add(0, property);
	}

	
	/**
	 * Creates a {@link Property} instance and sets its required values and adds
	 * it to the current list  of {@link Property} instances held by the specified
	 * <code>entityPropertiesWrapper</code> for a given {@link Entity}
	 * 
	 * @param relationType the {@link RelationType} enum value
	 * 
	 * @param jpaAnnotationTypesArr an array of {@link String} holding the types
	 * of JPA annotation(s) to be added to the property
	 * 
	 * @param entityPropertiesWrapper the {@link Properties} instance of an
	 * {@link Entity}
	 * 
	 * @param relation the {@link Relation} instance holding the type information
	 * of an another related entity.
	 */
	private static void createCollectionTypeProperty(
			RelationType relationType, 
			String[] jpaAnnotationTypesArr, Properties entityPropertiesWrapper,
			Relation relation) {
		createAndAddProperty(null, null, RelationType.ONE_TO_MANY, 
				jpaAnnotationTypesArr, entityPropertiesWrapper, relation, false);
	}
	
	/**
	 * Creates a {@link Property} instance and sets its required values and adds
	 * it to the current list  of {@link Property} instances held by the specified
	 * <code>entityPropertiesWrapper</code> for a given {@link Entity}
	 * 
	 * @param propertyFullyQualifiedTypeName the fully qualified type name of 
	 * the property
	 * 
	 * @param relationType the {@link RelationType} enum value
	 * 
	 * @param jpaAnnotationTypesArr an array of {@link String} holding the types
	 * of JPA annotation(s) to be added to the property
	 * 
	 * @param entityPropertiesWrapper the {@link Properties} instance of an
	 * {@link Entity}
	 * 
	 * 
	 * @see #createAndAddProperty(String, RelationType, String[], Properties, Relation)
	 */
	private static void createAndAddProperty(
			String propertyFullyQualifiedTypeName,
			RelationType relationType, String[] jpaAnnotationTypesArr,
			Properties entityPropertiesWrapper) {
		createAndAddProperty(null, propertyFullyQualifiedTypeName, relationType, 
				jpaAnnotationTypesArr, entityPropertiesWrapper, null, false);
	}
	
	/**
	 * Creates a {@link Property} instance and sets its required values and adds
	 * it to the current list  of {@link Property} instances held by the specified
	 * <code>entityPropertiesWrapper</code> for a given {@link Entity}
	 * 
	 * @param propertyFullyQualifiedTypeName the fully qualified type name of 
	 * the property
	 * 
	 * @param relationType the {@link RelationType} enum value
	 * 
	 * @param jpaAnnotationTypesArr an array of {@link String} holding the types
	 * of JPA annotation(s) to be added to the property
	 * 
	 * @param entityPropertiesWrapper the {@link Properties} instance of an
	 * {@link Entity}
	 * 
	 * @param relation the {@link Relation} instance holding the type information
	 * of an another related entity.
	 * 
	 * @see #createAndAddProperty(String, RelationType, String[], Properties, Relation)
	 */
	private static void createAndAddProperty(
			String propertyFullyQualifiedTypeName,
			RelationType relationType, String[] jpaAnnotationTypesArr,
			Properties entityPropertiesWrapper, Relation relation) {
		createAndAddProperty(null, propertyFullyQualifiedTypeName, relationType, 
				jpaAnnotationTypesArr, entityPropertiesWrapper, relation, false);
	}
	
	/**
	 * Creates a {@link Property} instance and sets its required values and adds
	 * it to the current list  of {@link Property} instances held by the specified
	 * <code>entityPropertiesWrapper</code> for a given {@link Entity}
	 * 
	 * @param propertyName the property name to set.If found <code>NULL</code>
	 * or empty, the property name is set from the specified 
	 * <code>propertyFullyQualifiedTypeName</code>
	 *  
	 * @param propertyFullyQualifiedTypeName the fully qualified type name of 
	 * the property.If found <code>NULL</code> or empty, the property name 
	 * is set from the information held by the specified <code>relation</code> 
	 * instance 
	 * 
	 * @param relationType the {@link RelationType} enum value
	 * 
	 * @param jpaAnnotationTypesArr an array of {@link String} holding the types
	 * of JPA annotation(s) to be added to the property
	 *  
	 * @param entityPropertiesWrapper the {@link Properties} instance of an
	 * {@link Entity}
	 * 
	 * @param relation the {@link Relation} instance holding the type information
	 * of an another related entity.If found <code>NULL</code>, the property's 
	 * type is set to the specified <code>propertyFullyQualifiedTypeName</code>
	 * 
	 * @param isIdProperty a boolean value <code>true</code> indicating the 
	 * property to be created is Id type property, <code>false</code> otherwise.
	 */
	private static void createAndAddProperty(
			String propertyName,
			String propertyFullyQualifiedTypeName,
			RelationType relationType, String[] jpaAnnotationTypesArr,
			Properties entityPropertiesWrapper, Relation relation, 
			boolean isIdProperty) {
		
		Many many = null;
		
		if (relation != null) {
			many = relation.getMany();	
		}

		// Instantiate Property
		Property property = new Property();
		
		// Set Property Name
		if (propertyName == null || propertyName.trim().isEmpty()) {
			propertyName = getPropertyName(propertyFullyQualifiedTypeName, many);
		}
		
		property.setName(propertyName);
		
		// Set Property Type
		if (many != null) {

			boolean isRelationBidirectional = relation.isBidirectional();
			boolean isRelationOneToOne = relation.isOneToOne();
			
			One one = relation.getOne();
			String oneEndEntityTypePropertyName = 
						createPropertyNameFromTypeName(one.getType());
			String manyEndEntityTypePropertyName = 
					createPropertyNameFromTypeName(many.getType());
			
			// In case of one-to-one relation set the "mappedBy" attribute on 
			// related entity not on master entity.For e.g.
			// com.generated.entities.Customer ("customer")
			// com.generated.entities.Address ("address")
			// are two entities which are specified to have a one-to-one
			// relation.In this case, if "customer" is considered as master
			// and its primary key is to be shared with "address" the
			// the "mappedBy" attribute should be present in the @OneToOne 
			// annotation placed on property "Customer customer" in "address" 
			// entity 
			if (isRelationOneToOne) {  
				property.setMappedBy(manyEndEntityTypePropertyName);
				property.setType(propertyFullyQualifiedTypeName);
				
			} else {
				// The collection type property should have its type specified in
				// generic format.For e.g.
				// For type: "com.generated.entities.Address" and 
				// for collection type: "java.util.Set" the
				// type, in property instance variable and its getter and setter, 
				// should be specified as Set<Address> while in Import statement
				// it should be in normal form.Thus setting 
				// com.tools.codegeneration.api.model.Property.Property's "genericType"
				// and "genericTypeParameter" properties
				property.setType(many.getCollectionType());
				property.setGenericType(true);
				// Set name of the type whose instances are holded by this
				// collection type
				property.setGenericTypeParameter(many.getType());
				
				// If the relation is bi-directional then the new property
				// marked with @OneToMany annotation should have a 
				// "mappedBy" attribute value set to name created from "one" end entity type
				if (isRelationBidirectional) { // Set the mappedBy attribute value for the Relationship Annotation
					property.setMappedBy(oneEndEntityTypePropertyName);
				}
			}
		} else {
			property.setType(propertyFullyQualifiedTypeName);	
		}
		
		
		// Set the type of relation this property has with an entity
		if (relationType != null) {
			property.setRelationType(relationType);	
		}
		
		// Set the JPA Annotation types required by this property
		if (jpaAnnotationTypesArr != null && jpaAnnotationTypesArr.length > 0) {
			property.setJpaAnnotationTypes(jpaAnnotationTypesArr);	
		}
		
		// adding the type to
		// com.tools.codegeneration.util.PropertyTypeName which is used for 
		// creating import statements 
		PropertyTypeName.addTypeName(property.getType());
		
		// Add only the collection type property created above to current 
		// entityProperties list
		
		if (isIdProperty) {
			property.setId(true);
			property.setIdAnnotationType(IdAnnotationType.SIMPLE);
			// if Id property, then it should first in the properties list
			entityPropertiesWrapper.getProperties().add(0, property);
		} else {
			entityPropertiesWrapper.getProperties().add(property);	
		}
		
	}
	
	/**
	 * 
	 * @param propertyFullyQualifiedTypeName the fully qualified type name of
	 * a property 
	 * 
	 * @param many the {@link Many} instance holding the type information of an
	 * another related entity
	 * 
	 * @return the simple type name or collection type name of a property using the
	 * specified <code>propertyFullyQualifiedTypeName</code> and <code>many</code>
	 * instance
	 */
	private static String getPropertyName(String propertyFullyQualifiedTypeName, Many many) {
		boolean createPropertyNameUsingMany = false;
		
		if (many != null) {
			if (many.getCollectionType() == null) { 
				// This would be the case when "collectionType" attribute is not set.
				// It can be the case when relationship is specified as one-to-one
				createPropertyNameUsingMany = false;
			} else {
				createPropertyNameUsingMany = true;
			}
		} else {
			createPropertyNameUsingMany = false;
		}
		
		String propertyName = null;
		if (createPropertyNameUsingMany) {
			propertyName = createPropertyNameForCollectionTypeRelatedEntity(many);	
		} else {
			propertyName = createPropertyNameFromTypeName(propertyFullyQualifiedTypeName);
		}
		
		return propertyName;
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
	private static String createPropertyNameForCollectionTypeRelatedEntity(Many many) {
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
	private static String createPropertyNameFromTypeName(String fullyQualifiedTypeName) {
		String shortNameForType = 
				StringUtil.getShortNameFromFullyQualifiedTypeName(
								fullyQualifiedTypeName);
		return StringUtil.convertCharacterCase(shortNameForType, 0, true);
	}

}
