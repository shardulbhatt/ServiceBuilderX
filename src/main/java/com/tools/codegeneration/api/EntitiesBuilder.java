package com.tools.codegeneration.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.tools.codegeneration.api.model.Entity;
import com.tools.codegeneration.api.model.relationships.Relation;
import com.tools.codegeneration.api.model.relationships.Relation.RelationType;
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
	 * <i>entities definition XML</i>
	 */
	public void buildEntities(List<Entity> entities, List<Relation> relations) {
		// Gather properties type for creating import statements
		PropertyTypeName.addTypesNameFromEntities(entities);
		
		// System.out.println(PropertyTypeName.getAllTypesName());
		// Create a Map for holding entries mapped by PACKAGE NAME to its 
		// DEFINED ENTITIES 
		Map<String, Set<String>> packageEntityNamesMap = 
							new HashMap<String, Set<String>>();
		
		// Create a Map for holding entries mapped by ENTITY FULLY QUALIFIED
		// NAME to its DEFINED ENTITY CONTENT
		Map<String, Collection<String>> entityNameFullyQualifiedNameEntityContentsMap 
						= new HashMap<String, Collection<String>>();
		
		// Maps entity fully qualified name to its JPA enabled status
		Map<String, Boolean> entityFullyQualifiedNameIsJPAEnabledMap = 
					getEntityFullyQualifiedNameIsJPAEnabledMap(entities);
		
		
		// Categorize Relation instances, based on the relations specified
		// in relations definition XML.
		Map<RelationType, List<Relation>> categorizedRelationsMap = 
						RelationsHelper.categorizeRelationsByRelationType(
								entities, relations, 
								entityFullyQualifiedNameIsJPAEnabledMap);
		
		if(categorizedRelationsMap != null && !categorizedRelationsMap.isEmpty()) {
			// Update current list of each entity's property(s) by adding the related
			// entities as properties to its existing property list.
			PropertiesHelper.updateProperties(
					entities, categorizedRelationsMap, 
					entityFullyQualifiedNameIsJPAEnabledMap);	
		}
		
		
		// Create entities content
		String packageName = null;
		String entityName = null;
		Collection<String> entityContents = null;
		
		for (Entity entity : entities) {
			
			packageName = entity.getEntityPackage().getName().getName();
			entityName = entity.getEntityName().getName();
			
			PackageHelper.addToPackageEntitiesNameMap(
					packageName, entityName, packageEntityNamesMap);
			
			entityContents = entityContentBuilder.createContent(entity);
			
//			System.out.println("============" + entityFullyQualifiedName  + "==============");
//			System.out.println(entityContents);
			
			entityNameFullyQualifiedNameEntityContentsMap.put(
					entity.getEntityFullyQualifiedName(), entityContents);
			
		}
		
		// Generate packages and its entities
		generatePackageAndEntities(
				packageEntityNamesMap, 
				entityNameFullyQualifiedNameEntityContentsMap, 
				getSourceGenerationPath());
	}
	
	private static Map<String, Boolean> getEntityFullyQualifiedNameIsJPAEnabledMap(
				List<Entity> entities) {
		// Maps entity fully qualified name to its JPA enabled status
		Map<String, Boolean> entityFullyQualifiedNameIsJPAEnabledMap = 
				new HashMap<String, Boolean>();
		
		for (Entity entity : entities) {
			entityFullyQualifiedNameIsJPAEnabledMap.put(
					entity.getEntityFullyQualifiedName(), entity.isJpaEnabled());
		}
		
		return entityFullyQualifiedNameIsJPAEnabledMap;
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
