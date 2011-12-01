package com.tools.codegeneration.util;

import java.util.Map;

import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

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
 * A helper class for updating current properties list of an {@link Entity} by 
 * adding the related entities (as specified in relations defintion XML) 
 * as properties.
 * 
 * @author jigneshg
 *
 */
public class PropertiesUpdateHelper {

	private RelationType relationType;
	private Relation relation;
	private Map<String, Properties> entityFullyQualifiedNamePropertiesMap;
	private Map<String, Boolean> entityFullyQualifiedNameIsJPAEnabledMap;

	/**
	 * @return the relationType
	 */
	public RelationType getRelationType() {
		return relationType;
	}

	/**
	 * @param relationType the relationType to set
	 */
	public void setRelationType(RelationType relationType) {
		this.relationType = relationType;
	}

	/**
	 * @return the relation
	 */
	public Relation getRelation() {
		return relation;
	}

	/**
	 * @param relation
	 *            the relation to set
	 */
	public void setRelation(Relation relation) {
		this.relation = relation;
	}

	/**
	 * @return the entityFullyQualifiedNamePropertiesMap
	 */
	public Map<String, Properties> getEntityFullyQualifiedNamePropertiesMap() {
		return entityFullyQualifiedNamePropertiesMap;
	}

	/**
	 * @param entityFullyQualifiedNamePropertiesMap
	 *            the entityFullyQualifiedNamePropertiesMap to set
	 */
	public void setEntityFullyQualifiedNamePropertiesMap(
			Map<String, Properties> entityFullyQualifiedNamePropertiesMap) {
		this.entityFullyQualifiedNamePropertiesMap = entityFullyQualifiedNamePropertiesMap;
	}
	
	/**
	 * @return the entityFullyQualifiedNameIsJPAEnabledMap
	 */
	public Map<String, Boolean> getEntityFullyQualifiedNameIsJPAEnabledMap() {
		return entityFullyQualifiedNameIsJPAEnabledMap;
	}

	/**
	 * @param entityFullyQualifiedNameIsJPAEnabledMap the entityFullyQualifiedNameIsJPAEnabledMap to set
	 */
	public void setEntityFullyQualifiedNameIsJPAEnabledMap(
			Map<String, Boolean> entityFullyQualifiedNameIsJPAEnabledMap) {
		this.entityFullyQualifiedNameIsJPAEnabledMap = entityFullyQualifiedNameIsJPAEnabledMap;
	}



	private static final int CREATE_PROPERTY = 1;
	private static final int CREATE_COLLECTION_TYPE_PROPERTY = 2;
	private static final int CREATE_ONE_TO_ONE_RELATED_NON_PRIMARY_ENTITY_ID_PROPERTY = 3;
	
	/**
	 * Updates the current list of {@link Property} instances, held by
	 * an {@link Entity#getProperties()} instance, by appending {@link Property}
	 * instance(s) of types represented by the 
	 * {@link PropertiesUpdateHelper#getRelation()} instance 
	 * 
	 */
	public void updateProperties() {
		String[] jpaAnnotationTypesArr = null;
		Properties oneEndEntityPropertiesWrapper = null;
		Properties manyEndEntityPropertiesWrapper = null;
		
		One one = relation.getOne();
		Many many = relation.getMany();
		
		String oneEndEntityType = one.getType();
		String manyEndEntityType = many.getType();
		
		String propertyName = null;
		String propertyType = null;
		String genericTypeParameter = null;
		String mappedBy = null;
		String oneToOnePrimaryEntityPropertyName = null;
		
		String[] addTypesFullNameShortName = null;
		
		boolean isEntityJPAEnabled = false;
		
		switch (relationType) {
			case ONE_TO_MANY:
				// Create collectionType property and add it to "one" end 
				// entity properties list
				
				String collectionType = many.getCollectionType();
				
				// In this case two types needs to be added to the related entity
				// 1. the type of property
				// 2. the type of collection
				// And both of these should be present in import statements
				addTypesFullNameShortName = new String[] { 
						manyEndEntityType,
						collectionType
				};
				
				oneEndEntityPropertiesWrapper = entityFullyQualifiedNamePropertiesMap.get(oneEndEntityType);
				
				propertyName = PropertiesHelper.createPropertyNameForCollectionTypeRelatedEntity(many);
				
				propertyType = collectionType;
				genericTypeParameter = manyEndEntityType;
				
				isEntityJPAEnabled = entityFullyQualifiedNameIsJPAEnabledMap.get(oneEndEntityType);
				
				if (isEntityJPAEnabled) {
					jpaAnnotationTypesArr = new String[] {
							JPAAnnotationsMarkerHelper.getRelationTypeFullyQualifiedName(
									relationType),
							JPAConstants.CASCADE_ENUM_TYPE
					};
					
					if (relation.isBidirectional()) {
						mappedBy = PropertiesHelper.createPropertyNameFromTypeName(oneEndEntityType);
					}	
				}
				
				createAndAddProperty(
						CREATE_COLLECTION_TYPE_PROPERTY, 
						propertyName, propertyType, relationType, 
						jpaAnnotationTypesArr, genericTypeParameter, 
						mappedBy, oneToOnePrimaryEntityPropertyName, 
						oneEndEntityPropertiesWrapper, addTypesFullNameShortName);
				
				break;
			
			case MANY_TO_ONE:
				// Create "one" end entity type property and add it to
				// "many" end entity properties list
				
				addTypesFullNameShortName = new String[] { 
						oneEndEntityType
				};
				
				manyEndEntityPropertiesWrapper = 
						entityFullyQualifiedNamePropertiesMap.get(manyEndEntityType);
				
				propertyName = PropertiesHelper.createPropertyNameFromTypeName(oneEndEntityType);
				propertyType = oneEndEntityType;
				
				isEntityJPAEnabled = entityFullyQualifiedNameIsJPAEnabledMap.get(manyEndEntityType);
				
				if (isEntityJPAEnabled) {
					jpaAnnotationTypesArr = new String[] {
							JPAAnnotationsMarkerHelper.getRelationTypeFullyQualifiedName(
									relationType),
							JPAConstants.CASCADE_ENUM_TYPE
					};
				}
				
				createAndAddProperty(
						CREATE_PROPERTY, 
						propertyName, propertyType, relationType, 
						jpaAnnotationTypesArr, genericTypeParameter, 
						mappedBy, oneToOnePrimaryEntityPropertyName, 
						manyEndEntityPropertiesWrapper, addTypesFullNameShortName);
				
				break;
			
			case ONE_TO_ONE:
				// =============== PRIMARY ENTITY
				// Create "many" end entity type property and add it to 
				// "one" end entity properties list
				// The "one" end entity is considered as Primary entity in one-to-one
				// relation and the entity at "many" end is considered as 
				// Non-Primary entity.
				
				addTypesFullNameShortName = new String[] { 
						manyEndEntityType
				};
				
				oneEndEntityPropertiesWrapper = 
						entityFullyQualifiedNamePropertiesMap.get(oneEndEntityType);
				
				propertyName = PropertiesHelper.createPropertyNameFromTypeName(manyEndEntityType);
				propertyType = manyEndEntityType;
				
				isEntityJPAEnabled = entityFullyQualifiedNameIsJPAEnabledMap.get(oneEndEntityType);
				
				if (isEntityJPAEnabled) {
					// The Primary entity requires the "cascade" attribute 
					// The Primary entity requires javax.persistence.PrimaryKeyJoinColumn annotation
					jpaAnnotationTypesArr = new String[] {
							JPAAnnotationsMarkerHelper.getRelationTypeFullyQualifiedName(
									relationType),
							JPAConstants.CASCADE_ENUM_TYPE,
							JPAConstants.PRIMARY_KEY_JOIN_COLUMN_TYPE
					};	
				}
				
				createAndAddProperty(
						CREATE_PROPERTY, 
						propertyName, propertyType, relationType, 
						jpaAnnotationTypesArr, genericTypeParameter, 
						mappedBy, oneToOnePrimaryEntityPropertyName, 
						oneEndEntityPropertiesWrapper, addTypesFullNameShortName);
				
				// =============== NON-PRIMARY ENTITY
				// Create "one" end entity type property in "many" end entity
				// and add it to "many" end entity properties list
				
				addTypesFullNameShortName = new String[] { 
						oneEndEntityType
				};
				
				manyEndEntityPropertiesWrapper = 
						entityFullyQualifiedNamePropertiesMap.get(manyEndEntityType);
				
				propertyName = PropertiesHelper.createPropertyNameFromTypeName(oneEndEntityType);
				propertyType = oneEndEntityType;
				
				isEntityJPAEnabled = entityFullyQualifiedNameIsJPAEnabledMap.get(manyEndEntityType);
				
				if (isEntityJPAEnabled) {
					// The Non-Primary entity does not require the "cascade" attribute
					// Thus not added the javax.persistence.CascadeType type to 
					// jpaAnnotationTypesArr
					jpaAnnotationTypesArr = new String[] {
							JPAAnnotationsMarkerHelper.getRelationTypeFullyQualifiedName(
									relationType)
					};
					
					// The Non-Primary entity requires the "mappedBy" attribute on
					// the "one" end entity type property added to non-primary 
					// entity's current properties list
					mappedBy = PropertiesHelper.createPropertyNameFromTypeName(manyEndEntityType);
				}
				
				createAndAddProperty(
						CREATE_PROPERTY, 
						propertyName, propertyType, relationType, 
						jpaAnnotationTypesArr, genericTypeParameter, 
						mappedBy, oneToOnePrimaryEntityPropertyName,
						manyEndEntityPropertiesWrapper, addTypesFullNameShortName);
				
				if (isEntityJPAEnabled) {
					// =============== NON-PRIMARY ENTITY
					// Add "id" property in "many" end entity with required annotations
					// for making the One-to-One relation work correctly.
					// Example:
					
					// @Id
					// @Column(name = "booking_id", unique = true, nullable = false)//TODO: this remains in case of Id property for one-to-one non-primary entity 
					// @GeneratedValue(generator="foreign")
					// @GenericGenerator(name="foreign", strategy = "foreign", parameters={
					//		@Parameter(name="property", value="<PRIMARY_ENTITY_PROPERTY_NAME_ADDED_IN_NON_PRIMARY_ENTITY>")
					// })
					// private Long id;
					jpaAnnotationTypesArr = new String[] {
							JPAConstants.ID_TYPE,
							JPAConstants.GENERATED_VALUE_TYPE,
							HibernateORMConstants.GENERIC_GENERATOR_TYPE,
							HibernateORMConstants.PARAMETER_TYPE
					};
					
					propertyName = UtilityConstants.ID_PROPERTY_NAME;
					propertyType = UtilityConstants.ID_PROPERTY_DEFAULT_TYPE;
					oneToOnePrimaryEntityPropertyName = 
							PropertiesHelper.createPropertyNameFromTypeName(oneEndEntityType);
					
					createAndAddProperty(
							CREATE_ONE_TO_ONE_RELATED_NON_PRIMARY_ENTITY_ID_PROPERTY, 
							propertyName, propertyType, relationType, 
							jpaAnnotationTypesArr, genericTypeParameter, 
							mappedBy, oneToOnePrimaryEntityPropertyName,
							manyEndEntityPropertiesWrapper, addTypesFullNameShortName);
				}
				
				break;
		}
		
	}
	
	/**
	 * Creates a {@link Property} instance and sets its required values and adds
	 * it to the current list  of {@link Property} instances held by the specified
	 * <code>entityPropertiesWrapper</code> for a given {@link Entity}
	 * 
	 * @param propertyCategory the type of property to be created i.e.
	 * a collection type property, a property with simple annotations
	 * or an Id property for a non-primary entity in case of One-To-One relation.
	 *  
	 * @param propertyName the property name to set
	 * 
	 * @param propertyType the fully qualified type name of 
	 * the property.
	 * 
	 * @param relationType the {@link RelationType} enum value
	 * 
	 * @param jpaAnnotationTypesArr an array of {@link String} holding the types
	 * of JPA annotation(s) to be added to the property
	 * 
	 * @param genericTypeParameter the parameterised type of generic collection
	 * type if the property to be created is a collection type property
	 *  
	 *  
	 * @param mappedBy if the specified <code>relationType</code> is
	 * 
	 * <ul>
	 * 		<li>
	 * 			{@link RelationType#ONE_TO_MANY} and is marked as bidirectional
	 * 			then this parameter holds value for the <i>mappedBy<i> attribute
	 * 			of the relationship annotation i.e. {@link OneToMany} on
	 * 			the entity specified at "one" end of the relation in <i>relations
	 * 			definition XML</i>
	 * 		</li>
	 * 
	 * 		<li>
	 * 			{@link RelationType#ONE_TO_ONE} 
	 * 			then this parameter holds value for the <i>mappedBy<i> attribute
	 * 			of the relationship annotation i.e. {@link OneToOne}  on
	 * 			the entity specified at "many" end of the relation in <i>relations
	 * 			definition XML</i>
	 * 		</li>
	 * </ul> 
	 *   
	 * 
	 * @param oneToOnePrimaryEntityPropertyName if the specified 
	 * <code>relationType</code> is {@link RelationType#ONE_TO_ONE} then this
	 * parameter holds the {@link GenericGenerator}'s {@link Parameter} 
	 * annotation's {@link Parameter#value()} attribute's value 
	 * 
	 * @param entityPropertiesWrapper the {@link Properties} instance of a given 
	 * entity
	 * 
	 * @param addTypesFullNameShortName var-args of type {@link String} holding 
	 * the property type(s) to be added to {@link PropertyTypeName} for 
	 * creating a short name of the type against the fully qualified type name
	 */
	private void createAndAddProperty(
			int propertyCategory,
			String propertyName, 
			String propertyType, 
			RelationType relationType, 
			String[] jpaAnnotationTypesArr, 
			String genericTypeParameter, 
			String mappedBy,
			String oneToOnePrimaryEntityPropertyName,
			Properties entityPropertiesWrapper,
			String... addTypesFullNameShortName) {
		
		Property newProperty = null;
		
		if (CREATE_COLLECTION_TYPE_PROPERTY == propertyCategory) {
			
			newProperty = createCollectionProperty(
					propertyName, 
					propertyType, relationType, 
					jpaAnnotationTypesArr, 
					genericTypeParameter, mappedBy);
			
		} else if (CREATE_PROPERTY == propertyCategory) {
			
			newProperty = createProperty(
					propertyName, propertyType, 
					relationType, jpaAnnotationTypesArr);
			
			if (mappedBy != null) {
				newProperty.setMappedBy(mappedBy);
			}
			
		} else if (CREATE_ONE_TO_ONE_RELATED_NON_PRIMARY_ENTITY_ID_PROPERTY == propertyCategory) {
			
			newProperty = 
				createOneToOneRelatedNonPrimaryEntityIdProperty(
					propertyName, propertyType, 
					jpaAnnotationTypesArr, 
					oneToOnePrimaryEntityPropertyName);
			
		}
		
		addNewPropertyToCurrentPropertyList(entityPropertiesWrapper, newProperty);

		addPropertyTypesName(addTypesFullNameShortName);
	}
	
	/**
	 * Adds the specified <code>newProperty</code> to the specified 
	 * <code>entityPropertiesWrapper</code>.
	 * 
	 * @param entityPropertiesWrapper the {@link Properties} instance of a given 
	 * entity
	 * 
	 * @param newProperty the {@link Property} instance to be added to the
	 * specified <code>entityPropertiesWrapper</code>
	 */
	private void addNewPropertyToCurrentPropertyList(
			Properties entityPropertiesWrapper, Property newProperty) {
		
		if (newProperty.isId()) {
			// Id Property should be first in the properties list
			entityPropertiesWrapper.getProperties().add(0, newProperty);	
		} else {
			entityPropertiesWrapper.getProperties().add(newProperty);
		}
	}
	
	/**
	 * 
	 * @param propertyName the property name to set
	 * 
	 * @param propertyTypeFullyQualifiedName the fully qualified type name of 
	 * the property.
	 * 
	 * @param propertyRelationType the {@link RelationType} enum value
	 * 
	 * @param jpaAnnotationTypes an array of {@link String} holding the types
	 * of JPA annotation(s) to be added to the property
	 * 
	 * @return the {@link Property} instance whose properties are set to values
	 * held by the arguments to this method.
	 */
	private Property createProperty(
			String propertyName, String propertyTypeFullyQualifiedName, 
			RelationType propertyRelationType, String[] jpaAnnotationTypes) {
		
		return createProperty(
				propertyName, 
				propertyTypeFullyQualifiedName, 
				propertyRelationType, 
				jpaAnnotationTypes, 
				false, null, null, 
				false, null, null);
		
	}
	
	/**
	 * 
	 * @param propertyName the property name to set
	 * 
	 * @param propertyTypeFullyQualifiedName the fully qualified type name of 
	 * the property.
	 * 
	 * @param propertyRelationType the {@link RelationType} enum value
	 * 
	 * @param jpaAnnotationTypes an array of {@link String} holding the types
	 * of JPA annotation(s) to be added to the property
	 * 
	 * @param genericTypeParameter  the parameterised type of generic collection
	 * type if the property to be created is a collection type property
	 * 
	 * @param mappedBy if the specified <code>propertyRelationType</code> is
	 * 
	 * <ul>
	 * 		<li>
	 * 			{@link RelationType#ONE_TO_MANY} and is marked as bidirectional
	 * 			then this parameter holds value for the <i>mappedBy<i> attribute
	 * 			of the relationship annotation i.e. {@link OneToMany} on
	 * 			the entity specified at "one" end of the relation in <i>relations
	 * 			definition XML</i>
	 * 		</li>
	 * 
	 * 		<li>
	 * 			{@link RelationType#ONE_TO_ONE} 
	 * 			then this parameter holds value for the <i>mappedBy<i> attribute
	 * 			of the relationship annotation i.e. {@link OneToOne}  on
	 * 			the entity specified at "many" end of the relation in <i>relations
	 * 			definition XML</i>
	 * 		</li>
	 * </ul> 
	 * 
	 * @return the {@link Property} instance whose properties are set to values
	 * held by the arguments to this method.
	 */
	private Property createCollectionProperty(String propertyName, 
			String propertyTypeFullyQualifiedName,
			RelationType propertyRelationType,
			String[] jpaAnnotationTypes,
			String genericTypeParameter,
			String mappedBy) {
		
		return createProperty(
					propertyName, 
					propertyTypeFullyQualifiedName, 
					propertyRelationType, 
					jpaAnnotationTypes, 
					false, null, null, 
					true, genericTypeParameter, mappedBy);
	}
	
	/**
	 * 
	 * @param propertyName the property name to set
	 * 
	 * @param propertyTypeFullyQualifiedName the fully qualified type name of 
	 * the property.
	 * 
	 * @param jpaAnnotationTypes an array of {@link String} holding the types
	 * of JPA annotation(s) to be added to the property
	 * 
	 * @param oneToOnePrimaryEntityPropertyName then this
	 * parameter holds the {@link GenericGenerator}'s {@link Parameter} 
	 * annotation's {@link Parameter#value()} attribute's value to be added to
	 * the Id property of a Non-primary entity in a one-to-one relation 
	 * 
	 * @return the {@link Property} instance whose properties are set to values
	 * held by the arguments to this method.
	 */
	private Property createOneToOneRelatedNonPrimaryEntityIdProperty(
			String propertyName, 
			String propertyTypeFullyQualifiedName,
			String[] jpaAnnotationTypes,
			String oneToOnePrimaryEntityPropertyName) {
		
		return createProperty(
					propertyName, 
					propertyTypeFullyQualifiedName, 
					null, jpaAnnotationTypes, 
					true, IdAnnotationType.ONE_TO_ONE, 
					oneToOnePrimaryEntityPropertyName, 
					false, null, null);
		
	}
	
	/**
	 * 
	 * @param propertyName the property name to set
	 * 
	 * @param propertyTypeFullyQualifiedName the fully qualified type name of 
	 * the property.
	 * 
	 * @param propertyRelationType the {@link RelationType} enum value
	 *  
	 * @param jpaAnnotationTypes an array of {@link String} holding the types
	 * of JPA annotation(s) to be added to the property
	 * 
	 * @param isIdProperty a boolean value <code>true</code> indicating the 
	 * property to be created is Id type property, <code>false</code> otherwise.
	 * 
	 * @param idAnnotationType the {@link IdAnnotationType} enum value
	 * 
	 * @param oneToOnePrimaryEntityPropertyName if the specified 
	 * <code>relationType</code> is {@link RelationType#ONE_TO_ONE} then this
	 * parameter holds the {@link GenericGenerator}'s {@link Parameter} 
	 * annotation's {@link Parameter#value()} attribute's value 
	 * 
	 * @param isPropertyTypeGeneric a boolean value <code>true</code> indicating the 
	 * property to be created is of a generic type, <code>false</code> otherwise.
	 * 
	 * @param genericTypeParameter the parameterised type of generic collection
	 * type if the property to be created is a collection type property
	 * 
	 * @param mappedBy if the specified <code>propertyRelationType</code> is
	 * 
	 * <ul>
	 * 		<li>
	 * 			{@link RelationType#ONE_TO_MANY} and is marked as bidirectional
	 * 			then this parameter holds value for the <i>mappedBy<i> attribute
	 * 			of the relationship annotation i.e. {@link OneToMany} on
	 * 			the entity specified at "one" end of the relation in <i>relations
	 * 			definition XML</i>
	 * 		</li>
	 * 
	 * 		<li>
	 * 			{@link RelationType#ONE_TO_ONE} 
	 * 			then this parameter holds value for the <i>mappedBy<i> attribute
	 * 			of the relationship annotation i.e. {@link OneToOne}  on
	 * 			the entity specified at "many" end of the relation in <i>relations
	 * 			definition XML</i>
	 * 		</li>
	 * </ul> 
	 * 
	 * @return the {@link Property} instance whose properties are set to values
	 * held by the arguments to this method.
	 */
	private Property createProperty(
			String propertyName, 
			String propertyTypeFullyQualifiedName,
			RelationType propertyRelationType,
			String[] jpaAnnotationTypes,
			boolean isIdProperty,
			IdAnnotationType idAnnotationType,
			String oneToOnePrimaryEntityPropertyName,
			boolean isPropertyTypeGeneric,
			String genericTypeParameter,
			String mappedBy ) {
		
		Property property = new Property();
		property.setName(propertyName);
		property.setType(propertyTypeFullyQualifiedName);
		property.setRelationType(propertyRelationType);
		property.setJpaAnnotationTypes(jpaAnnotationTypes);
		property.setMappedBy(mappedBy);
		
		if (isPropertyTypeGeneric) {
			property.setGenericType(isPropertyTypeGeneric);
			property.setGenericTypeParameter(genericTypeParameter);	
		}
		
		if (isIdProperty) {
			property.setId(isIdProperty);
			property.setIdAnnotationType(idAnnotationType);
			
			if (IdAnnotationType.ONE_TO_ONE == idAnnotationType) {
				property.setOneToOnePrimaryEntityPropertyName(
						oneToOnePrimaryEntityPropertyName);	
			}
		}
		
		return property;
	}
	
	/**
	 * 
	 * @param typesFullyQualifiedName var-args of type {@link String} holding 
	 * the property type(s) to be added to {@link PropertyTypeName} for 
	 * creating a short name of the type against the fully qualified type name
	 */
	private static void addPropertyTypesName(String... typesFullyQualifiedName) {
		for (String typeFullyQualifiedName : typesFullyQualifiedName) {
			PropertyTypeName.addTypeName(typeFullyQualifiedName);
		}
	}
	
}
