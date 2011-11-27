package com.tools.codegeneration.constants;

import java.io.File;

public interface UtilityConstants {
	public static final String PERIOD_REGEX = "\\.";
	public static final String SYSTEM_DEPENDENT_FILE_PATH_SEPARATOR = File.separator;
	public static final String JAVA_FILE_EXTENSION = ".java";
	public static final String SETTER_PREFIX = "set";
	public static final String GETTER_PREFIX = "get";
	public static final String ID_PROPERTY_NAME = "id";
	public static final String ID_PROPERTY_DEFAULT_TYPE = "java.lang.Long";
}
