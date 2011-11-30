package com.tools.codegeneration.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.tools.codegeneration.api.model.Package;
import com.tools.codegeneration.constants.UtilityConstants;

/**
 * A helper class for handling {@link Package} related data.
 * 
 * @author jigneshg
 *
 */
public class PackageHelper {
	/**
	 * 
	 * @param packageName the entity's package name
	 * @param sourceGenerationPath the absolute path on the file system where the
	 * code is to be generated 
	 * 
	 * @return absolute path of the package created on the file system
	 */
	public static final String createPackage(String packageName, String sourceGenerationPath) {
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
	public static final String getAbsolutePackagePath(String packageName, String sourceGenerationPath) {
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
	 * @param packageName the package name
	 * @param entityName the entity name
	 * @param packageEntityNamesMap a {@link Map} for holding entries mapped 
	 * by PACKAGE NAME to its DEFINED ENTITIES 
	 */
	public static final void addToPackageEntitiesNameMap(
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
}
