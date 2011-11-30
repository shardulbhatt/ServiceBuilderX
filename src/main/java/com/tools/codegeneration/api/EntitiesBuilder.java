package com.tools.codegeneration.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Id;

import com.tools.codegeneration.api.model.Entity;
import com.tools.codegeneration.api.model.Properties;
import com.tools.codegeneration.api.model.Property;
import com.tools.codegeneration.api.model.relationships.Relation;
import com.tools.codegeneration.api.model.relationships.Relation.RelationType;
import com.tools.codegeneration.constants.JPAConstants;
import com.tools.codegeneration.constants.UtilityConstants;
import com.tools.codegeneration.util.EntityHelper;
import com.tools.codegeneration.util.InputOutputUtil;
import com.tools.codegeneration.util.PackageHelper;
import com.tools.codegeneration.util.PropertiesHelper;
import com.tools.codegeneration.util.PropertyTypeName;
import com.tools.codegeneration.util.RelationsHelper;

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
		
		// Categorize Relation instances, based on the relations specified
		// in relations definition XML.
		Map<RelationType, List<Relation>> categorizedRelationsMap = 
						RelationsHelper.categorizeRelationsByRelationType(relations);
		
		// Update current list of each entity's property(s) by adding the related
		// entities as properties to its existing property list.
		PropertiesHelper.updateProperties(entities, categorizedRelationsMap);
		
		// Create entities content
		String packageName = null;
		String entityName = null;
		String entityFullyQualifiedName = null;
		Collection<String> entityContents = null;
		
		Properties propertiesWrapper = null;
		
		boolean isEntityJPAEnabled = false;
		for (Entity entity : entities) {
			
			packageName = entity.getEntityPackage().getName().getName();
			entityName = entity.getEntityName().getName();
			entityFullyQualifiedName = EntityHelper.getEntityFullyQualifiedName(packageName, entityName);
			propertiesWrapper = entity.getProperties();
			PackageHelper.addToPackageEntitiesNameMap(
					packageName, entityName, packageEntityNamesMap);
			
			isEntityJPAEnabled = entity.isJpaEnabled();
			if (isEntityJPAEnabled) {
				addIdProperty(propertiesWrapper);
				// This is for creating the import statements
				setJPAAnnotationTypeColumnForPropertyNotIdAndNotRelated(
							propertiesWrapper);
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
			absolutePackagePath = PackageHelper.createPackage(pkgName, sourceGenerationPath);

			fileAbsolutePath = null;
			for (String className : entry.getValue()) {
				fileAbsolutePath = EntityHelper.getEntityAbsolutePath(className, absolutePackagePath);
				// NOTE: If the file already exists it would not get created
				if (InputOutputUtil.createFile(fileAbsolutePath)) {
					filesCreatedCount++;

					// Write the file contents
					InputOutputUtil.writeToFile(fileAbsolutePath, 
						entityNameFullyQualifiedNameEntityContentsMap.get(
								EntityHelper.getEntityFullyQualifiedName(pkgName, className))
					);
				}
			}
		}
		
		System.out.println("Files Created : " + filesCreatedCount);
	}
}
