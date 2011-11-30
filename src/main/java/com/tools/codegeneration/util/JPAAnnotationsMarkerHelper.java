package com.tools.codegeneration.util;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.tools.codegeneration.api.model.relationships.Relation.RelationType;
import com.tools.codegeneration.constants.JPAConstants;
import com.tools.codegeneration.constants.StringConstants;

public class JPAAnnotationsMarkerHelper {

	/**
	 * 
	 * @param relationType the {@link RelationType} enum value
	 * @param addCascadeValue a boolean value.<code>true</code> for adding
	 * the cascade attribute to the JPA Relationship annotation, <code>false</code>
	 * otherwise.
	 * 
	 * @return a formatted string representing a JPA relationship annotation
	 */
	public static final String createRelationAnnotation(
				RelationType relationType, boolean addCascadeValue) {
		
		String annotation = StringConstants.EMPTY_STRING;
		
		switch (relationType) {
			case ONE_TO_MANY:
				annotation = createAnnotation(JPAConstants.ONE_TO_MANY_TYPE, false);
				break;
				
			case MANY_TO_ONE:
				annotation = createAnnotation(JPAConstants.MANY_TO_ONE_TYPE, false);
				break;
				
			case ONE_TO_ONE:
				annotation = createAnnotation(JPAConstants.ONE_TO_ONE_TYPE, false);
				break;
				
			case MANY_TO_MANY:
				annotation = createAnnotation(JPAConstants.MANY_TO_MANY_TYPE, false);
				break;
		}
		
		if (addCascadeValue) {
			//Example: @ManyToOne(cascade = CascadeType.ALL)
			StringBuilder sb = new StringBuilder(annotation);
			sb.append(StringConstants.LEFT_PARENTHESES);
			sb.append(JPAConstants.CASCADE_ATTR);
			sb.append(StringConstants.EQUALS_PREFIXED_SUFFIXED_WITH_SPACE);
			sb.append(JPAConstants.CASCADE_ALL);
			sb.append(StringConstants.RIGHT_PARENTHESES);
			
			sb.append(StringConstants.LINE_BREAK);
			return sb.toString();
		}
		
		return annotation;
	}

	/**
	 * 
	 * @param relationType the {@link RelationType} enum value
	 * 
	 * @return the JPA type name corresponding to the specified <code>relationType</code>
	 */
	public static final String getRelationTypeFullyQualifiedName(RelationType relationType) {
		switch (relationType) {
			case ONE_TO_MANY:
				return JPAConstants.ONE_TO_MANY_TYPE;
				
			case MANY_TO_ONE:
				return JPAConstants.MANY_TO_ONE_TYPE;
				
			case ONE_TO_ONE:
				return JPAConstants.ONE_TO_ONE_TYPE;
				
			case MANY_TO_MANY:
				return JPAConstants.MANY_TO_MANY_TYPE;
				
		}
		return StringConstants.EMPTY_STRING;
	}
	
	/**
	 * 
	 * @return a formatted string representing a JPA {@link Id} annotation
	 */
	public static final String createIdAnnotation() {
		
		String idAnnotation = createAnnotation(JPAConstants.ID_TYPE, true);
		
		StringBuilder sb = new StringBuilder();
		sb.append(idAnnotation);
		sb.append(StringConstants.TAB_4_WIDTH);
		sb.append(createAnnotation(JPAConstants.GENERATED_VALUE_TYPE, false));
		sb.append(StringConstants.LEFT_PARENTHESES);
		sb.append(JPAConstants.GENERATED_VALUE_TYPE_STRATEGY_ATTR);
		sb.append(StringConstants.EQUALS_PREFIXED_SUFFIXED_WITH_SPACE);
		sb.append(JPAConstants.GENERATION_TYPE_AUTO);
		sb.append(StringConstants.RIGHT_PARENTHESES);
		sb.append(StringConstants.LINE_BREAK);
		
		return sb.toString();
	}
	
	/**
	 * 
	 * @return a formatted string representing a JPA {@link Entity} annotation
	 */
	public static final String createEntityAnnotation() {
		return createAnnotation(JPAConstants.ENTITY_TYPE, true);
	}
	
	/**
	 * 
	 * @return a formatted string representing a JPA {@link Column} annotation
	 */
	public static final String createColumnAnnotation() {
		return createAnnotation(JPAConstants.COLUMN_TYPE, true);
	}
	
	/**
	 * 
	 * @param jpaTypeFullyQualifiedName the fully qualified type name of a JPA 
	 * type
	 *  
	 * @param addLineBreakAtTheEnd a boolean value.<code>true</code> for adding
	 * a line break at the end of the returned string, <code>false</code>
	 * otherwise.
	 * 
	 * @return a formatted string representing a JPA annotation
	 */
	private static final String createAnnotation(
			String jpaTypeFullyQualifiedName, boolean addLineBreakAtTheEnd) {
		StringBuilder annotationBuilder = new StringBuilder();
		annotationBuilder.append(StringConstants.AT_SIGN);
		// Short Name of type
		String shortNameOfEntityType = 
				StringUtil.getShortNameFromFullyQualifiedTypeName(jpaTypeFullyQualifiedName);
		
		annotationBuilder.append(shortNameOfEntityType);
		
		if (addLineBreakAtTheEnd) {
			annotationBuilder.append(StringConstants.LINE_BREAK);	
		}
		return annotationBuilder.toString();
	}
}
