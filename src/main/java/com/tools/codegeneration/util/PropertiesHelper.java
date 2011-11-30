package com.tools.codegeneration.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tools.codegeneration.api.model.Entity;
import com.tools.codegeneration.api.model.Properties;
import com.tools.codegeneration.api.model.Property;
import com.tools.codegeneration.api.model.relationships.Many;
import com.tools.codegeneration.api.model.relationships.One;
import com.tools.codegeneration.api.model.relationships.Relation;
import com.tools.codegeneration.api.model.relationships.Relation.RelationType;
import com.tools.codegeneration.constants.JPAConstants;

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
		
		for (Entity entity : entities) {
			
			packageName = entity.getEntityPackage().getName().getName();
			entityName = entity.getEntityName().getName();
			entityFullyQualifiedName = EntityHelper.getEntityFullyQualifiedName(packageName, entityName);
			propertiesWrapper = entity.getProperties();
			
			entityFullyQualifiedNamePropertiesMap.put(
					entityFullyQualifiedName, propertiesWrapper);
		}

		updatePropertiesListUsingRelations(categorizedRelationsMap,
				entityFullyQualifiedNamePropertiesMap, RelationType.ONE_TO_MANY);
		
		updatePropertiesListUsingRelations(categorizedRelationsMap,
				entityFullyQualifiedNamePropertiesMap, RelationType.MANY_TO_ONE);
		
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
				
				addPropertiesHavingOneToManyRelation(entityPropertiesWrapper, many);
				break;
			
			case MANY_TO_ONE:
				entityPropertiesWrapper = 
					entityFullyQualifiedNamePropertiesMap.get(many.getType());
				
				// many-to-one: Update the "many" side entity by adding a property
				// to it of type specified for the entity at "one" end
				// of the relation
				addPropertiesHavingManyToOneRelation(entityPropertiesWrapper, one);
				
				break;
		}
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
		
		List<Property> entityProperties = entityPropertiesWrapper.getProperties();
		
		String type = one.getType();
		
		Property property = new Property();
		// Set Name
		property.setName(createPropertyNameFromTypeName(type));
		// Set Type
		property.setType(type);
		
		// Set the type of relation this property has with an entity
		property.setRelationType(RelationType.MANY_TO_ONE);
		property.setJpaAnnotationTypes(
				new String[] {
				JPAAnnotationsMarkerHelper.getRelationTypeFullyQualifiedName(
						RelationType.MANY_TO_ONE),
				JPAConstants.CASCADE_ENUM_TYPE
						
			});
		
		// adding the type to
		// com.tools.codegeneration.util.PropertyTypeName which is used for 
		// creating import statements 
		PropertyTypeName.addTypeName(type);
		
		// Add only the collection type property created above to current 
		// entityProperties list
		entityProperties.add(property);
	}



	/**
	 * Updates an entity's property's current list to append
	 * its related entity(s), having <b>ONE-TO-MANY relationship</b>, as properties 
	 * and set its type information, its JPA Annotation relation information, 
	 * etc.
	 * 
	 * @param entityPropertiesWrapper a given entity's {@link Properties} instance
	 * 
	 * @param many the {@link Many} instance representing the type of a related 
	 * entity 
	 */
	private static void addPropertiesHavingOneToManyRelation(
			Properties entityPropertiesWrapper, Many many) {
		List<Property> entityProperties = entityPropertiesWrapper.getProperties();
		
		// In this case two types needs to be added to the related entity
		// 1. the type of property
		// 2. the type of collection
		// And both of these should be present in import statements
		
		String type = many.getType();
		
		// Property type's instance creation is not needed in this case
		// as it will be the collection type property that will need to be 
		// added to the entity.Thus just adding the type to
		// com.tools.codegeneration.util.PropertyTypeName which is used for 
		// creating import statements 
		PropertyTypeName.addTypeName(type);
		
		// =========================Create Collection Type Property
		String collectionType = many.getCollectionType();
		Property collectionTypeProperty = new Property();
		// Create the property name using Type and Collection type
		String collectionTypePropertyName = 
				createPropertyNameForCollectionTypeRelatedEntity(many);
		collectionTypeProperty.setName(collectionTypePropertyName);
		// The collection type name in properties definition should be 
		// in generic format.For e.g.
		// For type: "com.generated.entities.Address" and 
		// for collection type: "java.util.Set" the
		// type, in property instance variable and its getter and setter, 
		// should be specified as Set<Address> while in Import statement
		// it should be in normal form.Thus setting 
		// com.tools.codegeneration.api.model.Property.Property's "genericType"
		// and "genericTypeParameter" properties
		collectionTypeProperty.setType(collectionType);
		collectionTypeProperty.setGenericType(true);
		// Set name of the type whose instances are holded by this
		// collection type
		collectionTypeProperty.setGenericTypeParameter(type);
		// Add the type to com.tools.codegeneration.util.PropertyTypeName 
		// too which is used for creating import statements
		PropertyTypeName.addTypeName(collectionTypeProperty.getType());
	
		// Set the type of relation this property has with an entity
		collectionTypeProperty.setRelationType(RelationType.ONE_TO_MANY);
		collectionTypeProperty.setJpaAnnotationTypes(
				new String[] {
				JPAAnnotationsMarkerHelper.getRelationTypeFullyQualifiedName(
						RelationType.ONE_TO_MANY),
				JPAConstants.CASCADE_ENUM_TYPE
			});
		
		// Add only the collection type property created above to current 
		// entityProperties list
		entityProperties.add(collectionTypeProperty);
		
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
