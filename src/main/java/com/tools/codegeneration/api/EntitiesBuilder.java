package com.tools.codegeneration.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import com.tools.codegeneration.constants.JPAConstants;
import com.tools.codegeneration.constants.StringConstants;
import com.tools.codegeneration.constants.UtilityConstants;
import com.tools.codegeneration.util.InputOutputUtil;
import com.tools.codegeneration.util.JPAAnnotationsMarkerHelper;
import com.tools.codegeneration.util.PropertyTypeName;
import com.tools.codegeneration.util.StringUtil;

/**
 * 
 * @author jignesh
 *
 */
public class EntitiesBuilder {

	//TODO: Replace System.out.println with log statements in this file
	
	private ContentBuilder<Entity> entityContentBuilder;
	private String sourceGenerationPath;
	
	/**
	 * @return the entityContentBuilder
	 */
	public ContentBuilder<Entity> getEntityContentBuilder() {
		return entityContentBuilder;
	}

	/**
	 * @param entityContentBuilder the entityContentBuilder to set
	 */
	public void setEntityContentBuilder(
			ContentBuilder<Entity> entityContentBuilder) {
		this.entityContentBuilder = entityContentBuilder;
	}

	/**
	 * @return the sourceGenerationPath
	 */
	public String getSourceGenerationPath() {
		return sourceGenerationPath;
	}

	/**
	 * @param sourceGenerationPath the sourceGenerationPath to set
	 */
	public void setSourceGenerationPath(String sourceGenerationPath) {
		this.sourceGenerationPath = sourceGenerationPath;
	}

	/**
	 * 
	 * @param relations
	 * @return
	 */
	private Map<One, Set<Many>> processRelations(List<Relation> relations) {
		boolean isBidirectional = false;
		boolean isOneToOne = false;
		boolean isManyToMany = false;
		List<Relation> incorrectRelations = new ArrayList<Relation>();
		
		// One - fully qualified type name of entity related at the ONE end
		// Many - fully qualified type name of entity related at the MANY end
		Map<One, Set<Many>> oneToManyRelationsMap = new HashMap<One, Set<Many>>();
		
		One one = null;
		Many many = null;
		
		for (Relation relation : relations) {
			// a relation cannot be one-to-one and many-to-many simultaneously
			// this is a wrong definition.This incorrect relation should not be 
			// processed and the user should be informed about such incorrect
			// relations
			isOneToOne = relation.isManyToMany();
			isManyToMany = relation.isOneToOne();
			
			if (isOneToOne && isManyToMany) {
				incorrectRelations.add(relation);
			} else {
				
				if (!isOneToOne && !isManyToMany) {
					one = relation.getOne();
					many = relation.getMany();
					
					if (isBidirectional) { // means one-to-many or many-to-one
						//TODO: Think! about this later.Does this fit into Many-To-One scenario?
					} else { // means one-to-many
						addToOneToManyRelationsMap(one, many, oneToManyRelationsMap);
					}
				} else {
					if (isOneToOne) {
						//TODO
					} else if (isManyToMany) {
						//TODO
					}
				}
				
			}
		}
		
		// TODO: Change this implementation
		// For now returning the oneToManyRelationsMap
		return oneToManyRelationsMap;
	}
	
	/**
	 * 
	 * @param one
	 * @param many
	 * @param oneToManyRelationsMap
	 */
	private static void addToOneToManyRelationsMap(
			One one, Many many, 
			Map<One, Set<Many>> oneToManyRelationsMap) {
		
		boolean isOneEntryAvailable = oneToManyRelationsMap.containsKey(one);
		
		Set<Many> manySet = null;
		
		if (isOneEntryAvailable) {
			manySet = oneToManyRelationsMap.get(one);
			manySet.add(many);
		} else {
			manySet = new HashSet<Many>(1);
			manySet.add(many);
			oneToManyRelationsMap.put(one, manySet);
		}
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
	private static String createPropertyNameForRelatedEntity(Many many) {
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
	 * Updates a particular entity's properties current list to append
	 * its related entity(s) as properties and set its type information,
	 * its JPA Annotation relation information, etc.
	 * 
	 * @param entityPropertiesWrapper a given entity's {@link Properties} instance
	 * 
	 * @param relatedEntities a {@link Set} holding {@link Many} instances
	 * representing the types related to a given entity 
	 */
	private static void updateEntityProperties(
			Properties entityPropertiesWrapper, Set<Many> relatedEntities) {
		List<Property> entityProperties = entityPropertiesWrapper.getProperties();
		
		String type = null;
		String collectionType = null;
		
		String collectionTypePropertyName = null;
		
		// In this case two properties needs to be added for each related entity
		// found.
		// 1. the type of property
		// 2. the type of collection
		// And both of these should be present in import statements
		
		Property collectionTypeProperty = null;
		
		for (Many many : relatedEntities) {
			
			type = many.getType();
			
			// Property type's instance creation is not needed in this case
			// as it will be the collection type property that will need to be 
			// added to the entity.Thus just adding the type to
			// com.tools.codegeneration.util.PropertyTypeName which is used for 
			// creating import statements 
			PropertyTypeName.addTypeName(type);
			
			// =========================Create Collection Type Property
			collectionType = many.getCollectionType();
			collectionTypeProperty = new Property();
			// Create the property name using Type and Collection type
			collectionTypePropertyName = createPropertyNameForRelatedEntity(many);
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
							RelationType.ONE_TO_MANY) 
				});
			
			// Add only the collection type property created above to current 
			// entityProperties list
			entityProperties.add(collectionTypeProperty);
		}
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
	
	/**
	 * Creates the specified <code>entities</code> content, as defined in
	 * entities definition XML (<b>entities.xml</b>), generates the Java Beans 
	 * in the defined packages and writes each entity content to its respective 
	 * generated bean.
	 * 
	 * @param entities a {@link List} of {@link Entity} instances representing
	 * entities definitions specified in a predefined format in 
	 * entities definition XML (<b>entities.xml</b>)
	 */
	public void buildEntities(List<Entity> entities, List<Relation> relations) {
		// Gather properties type for creating import statements
		PropertyTypeName.addTypesNameFromEntities(entities);
		
		System.out.println(PropertyTypeName.getAllTypesName());
		// Create a Map for holding entries mapped by PACKAGE NAME to its 
		// DEFINED ENTITIES 
		Map<String, Set<String>> packageEntityNamesMap = 
							new HashMap<String, Set<String>>();
		
		// Create a Map for holding entries mapped by ENTITY FULLY QUALIFIED
		// NAME to its DEFINED ENTITY CONTENT
		Map<String, Collection<String>> entityNameFullyQualifiedNameEntityContentsMap 
						= new HashMap<String, Collection<String>>();
		

		Map<One, Set<Many>> oneToManyRelationsMap = processRelations(relations);
		
		// Create entities content
		String packageName = null;
		String entityName = null;
		String entityFullyQualifiedName = null;
		Collection<String> entityContents = null;
		
		Properties propertiesWrapper = null;
		
		Set<Many> relatedEntities = null;
		
		One one = null;
		boolean isEntityJPAEnabled = false;
		for (Entity entity : entities) {
			
			packageName = entity.getEntityPackage().getName().getName();
			entityName = entity.getEntityName().getName();
			entityFullyQualifiedName = getEntityFullyQualifiedName(packageName, entityName);
			propertiesWrapper = entity.getProperties();
			addToPackageEntitiesNameMap(
					packageName, entityName, packageEntityNamesMap);
			
			isEntityJPAEnabled = entity.isJpaEnabled();
			if (isEntityJPAEnabled) {
				addIdProperty(propertiesWrapper);
				// This is for creating the import statements
				setJPAAnnotationTypeColumnForPropertyNotIdAndNotRelated(
							propertiesWrapper);
			}
			
			one = new One();
			one.setType(entityFullyQualifiedName);
			
			if (oneToManyRelationsMap.containsKey(one)) {
				relatedEntities = oneToManyRelationsMap.get(one);
				// Update entity properties collection
				// with the related entities to be provided as property(s)
				// of the entity whose content needs to be created 
				updateEntityProperties(propertiesWrapper, relatedEntities);
			}
			
			entityContents = entityContentBuilder.createContent(entity);
			
//			System.out.println("============" + entityFullyQualifiedName  + "==============");
//			System.out.println(entityContents);
			
			entityNameFullyQualifiedNameEntityContentsMap.put(
					entityFullyQualifiedName, entityContents);
		}
		
		// Generate packages and its entities
		generatePackageAndEntities(
				packageEntityNamesMap, 
				entityNameFullyQualifiedNameEntityContentsMap, 
				getSourceGenerationPath());
	}

	/**
	 * Adds a property, to the current properties list of a given entity which is 
	 * detected as JPA enabled, named "id" annotation with JPA 
	 * {@link Id} and its related annotation(s).
	 * 
	 * @param propertiesWrapper the {@link Properties} instance of a given 
	 * entity
	 */
	private void addIdProperty(Properties propertiesWrapper) {
		// Update entity's current properties list to include a "id" property
		// of default type java.lang.Long
		
		List<Property> currentEntityProperties = propertiesWrapper.getProperties();
		
		Property idProperty = new Property();
		idProperty.setName(UtilityConstants.ID_PROPERTY_NAME);
		idProperty.setType(UtilityConstants.ID_PROPERTY_DEFAULT_TYPE);
		idProperty.setId(true);
		// This is for creating the import statement
		idProperty.setJpaAnnotationTypes(new String[] {	
					JPAConstants.ID_TYPE,
					JPAConstants.GENERATED_VALUE_TYPE,
					JPAConstants.GENERATION_TYPE
				});
		currentEntityProperties.add(0, idProperty);
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
	private void setJPAAnnotationTypeColumnForPropertyNotIdAndNotRelated(
				Properties propertiesWrapper) {
		List<Property> currentEntityProperties = propertiesWrapper.getProperties();
		
		for (Property property : currentEntityProperties) {
			if (!property.isId() && 
					(RelationType.NOT_RELATED == property.getRelationType())) {
				property.setJpaAnnotationTypes(new String[] { JPAConstants.COLUMN_TYPE });
			}
		}
	}
	
	
	
	/**
	 * 
	 * @param packageEntityNamesMap a {@link Map} for holding entries mapped 
	 * by PACKAGE NAME to its DEFINED ENTITIES 
	 * @param entityNameFullyQualifiedNameEntityContentsMap a {@link Map} for 
	 * holding entries mapped by ENTITY FULLY QUALIFIED NAME to its DEFINED 
	 * ENTITY CONTENT
	 * @param sourceGenerationPath the absolute path on the file system where the
	 * code is to be generated
	 */
	private static void generatePackageAndEntities(
			Map<String, Set<String>> packageEntityNamesMap, 
			Map<String, Collection<String>> entityNameFullyQualifiedNameEntityContentsMap,
			String sourceGenerationPath) {
		// Generate packages and entities

		Set<Entry<String, Set<String>>> 
				packageEntityNamesEntries = packageEntityNamesMap.entrySet();
		
		String absolutePackagePath = null;
		String pkgName = null;
		String fileAbsolutePath = null;
		
		int filesCreatedCount = 0;
		for (Entry<String, Set<String>> entry : packageEntityNamesEntries) {
			pkgName = entry.getKey();
			absolutePackagePath = createPackage(pkgName, sourceGenerationPath);

			fileAbsolutePath = null;
			for (String className : entry.getValue()) {
				fileAbsolutePath = getEntityAbsolutePath(className, absolutePackagePath);
				// NOTE: If the file already exists it would not get created
				if (InputOutputUtil.createFile(fileAbsolutePath)) {
					filesCreatedCount++;

					// Write the file contents
					InputOutputUtil.writeToFile(fileAbsolutePath, 
						entityNameFullyQualifiedNameEntityContentsMap.get(
								getEntityFullyQualifiedName(pkgName, className))
					);
				}
			}
		}
		
		System.out.println("Files Created : " + filesCreatedCount);
	}
	
	/**
	 * 
	 * @param packageName the entity's package name
	 * @param sourceGenerationPath the absolute path on the file system where the
	 * code is to be generated 
	 * 
	 * @return absolute path of the package created on the file system
	 */
	private static String createPackage(String packageName, String sourceGenerationPath) {
		String absolutePackagePath = getAbsolutePackagePath(packageName, sourceGenerationPath);
		return InputOutputUtil.createDirectories(absolutePackagePath);
	}
	
	/**
	 * 
	 * @param packageName the entity's package name
	 * @param sourceGenerationPath the absolute path on the file system where the
	 * code is to be generated 
	 * @return absolute path of the package where it needs to be created on 
	 * the file system
	 */
	private static String getAbsolutePackagePath(String packageName, String sourceGenerationPath) {
		// convert period delimited package path to OS dependent filepath 
		// separator package path.For e.g.: com.entities to com/entities
		String packagePath = packageName.replaceAll(
				UtilityConstants.PERIOD_REGEX, 
				UtilityConstants.SYSTEM_DEPENDENT_FILE_PATH_SEPARATOR);
		return sourceGenerationPath + 
			   UtilityConstants.SYSTEM_DEPENDENT_FILE_PATH_SEPARATOR + 
			   packagePath;
	}
	
	/**
	 * 
	 * @param fileName the entity name
	 * @param absolutePackagePath absolute path of the package on the file system
	 * under which the entity with the specified <code>fileName</code> needs to
	 * be generated
	 * 
	 * @return the absolute path of the entity on the file system
	 */
	private static String getEntityAbsolutePath(String fileName, String absolutePackagePath) {
		StringBuilder sb = new StringBuilder();
		sb.append(absolutePackagePath);
		sb.append(UtilityConstants.SYSTEM_DEPENDENT_FILE_PATH_SEPARATOR);
		sb.append(fileName);
		sb.append(UtilityConstants.JAVA_FILE_EXTENSION);
		
		return sb.toString();
	}
	
	/**
	 * 
	 * @param packageName the package name
	 * @param entityName the entity name
	 * @param packageEntityNamesMap a {@link Map} for holding entries mapped 
	 * by PACKAGE NAME to its DEFINED ENTITIES 
	 */
	private static void addToPackageEntitiesNameMap(
			String packageName, String entityName, 
			Map<String, Set<String>> packageEntityNamesMap) {
		
		boolean isPackageEntryAvailable = packageEntityNamesMap.containsKey(packageName);
		
		Set<String> entityNameSet = null;
		
		if (isPackageEntryAvailable) {
			entityNameSet = packageEntityNamesMap.get(packageName);
			entityNameSet.add(entityName);
		} else {
			entityNameSet = new HashSet<String>(1);
			entityNameSet.add(entityName);
			packageEntityNamesMap.put(packageName, entityNameSet);
		}
	}
	
	/**
	 * 
	 * @param packageName the package name
	 * @param entityName the entity name
	 * @return the fully qualified name of the entity, specified by 
	 * <code>entityName</code> defined under package specified by 
	 * <code>packageName</code>
	 */
	private static String getEntityFullyQualifiedName(String packageName, String entityName) {
		StringBuilder sb = new StringBuilder();
		sb.append(packageName);
		sb.append(StringConstants.PERIOD);
		sb.append(entityName);
		
		return sb.toString(); 
	}
}
