package com.tools.codegeneration.util;

import com.tools.codegeneration.api.model.Entity;
import com.tools.codegeneration.constants.StringConstants;
import com.tools.codegeneration.constants.UtilityConstants;
/**
 * A helper class for handling {@link Entity} related data.
 * 
 * @author jigneshg
 *
 */
public class EntityHelper {
	/**
	 * 
	 * @param fileName the entity name
	 * @param absolutePackagePath absolute path of the package on the file system
	 * under which the entity with the specified <code>fileName</code> needs to
	 * be generated
	 * 
	 * @return the absolute path of the entity on the file system
	 */
	public static final String getEntityAbsolutePath(String fileName, String absolutePackagePath) {
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
	 * @return the fully qualified name of the entity, specified by 
	 * <code>entityName</code> defined under package specified by 
	 * <code>packageName</code>
	 */
	public static final String getEntityFullyQualifiedName(String packageName, String entityName) {
		StringBuilder sb = new StringBuilder();
		sb.append(packageName);
		sb.append(StringConstants.PERIOD);
		sb.append(entityName);
		
		return sb.toString(); 
	}
}
