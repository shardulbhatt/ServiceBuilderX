package com.tools.codegeneration.util;

import com.tools.codegeneration.constants.StringConstants;


public class StringUtil {

	/**
	 * 
	 * @param sourceString
	 * @param charPosition
	 * @param toLower boolean <code>true</code> to converting to lower case,
	 * <code>false</code> for converting to upper case
	 * 
	 * @return
	 */
	public static final String convertCharacterCase(
			String sourceString, int charPosition, boolean toLower) {
		
		char[] sourceStringCharArr = sourceString.toCharArray();
		
		char character = sourceStringCharArr[charPosition];
		
		boolean isCharacterInLowerCase = Character.isLowerCase(character);
		
		if(isCharacterInLowerCase && !toLower) {
			sourceStringCharArr[charPosition] = Character.toUpperCase(character);
		} else if (!isCharacterInLowerCase && toLower) {
			sourceStringCharArr[charPosition] = Character.toLowerCase(character);
		} else {
			return sourceString;
		}
		
		return String.valueOf(sourceStringCharArr);
	}
	
	/**
	 * 
	 * @param fullyQualifiedTypeName
	 * @return
	 */
	public static final String getShortNameFromFullyQualifiedTypeName(String fullyQualifiedTypeName) {
		int periodLastIndex = fullyQualifiedTypeName.lastIndexOf(StringConstants.PERIOD);
		if (periodLastIndex < 0) {
			return fullyQualifiedTypeName;
		}
		String shortName = fullyQualifiedTypeName.substring(periodLastIndex + 1);
		return shortName;
	}
}
