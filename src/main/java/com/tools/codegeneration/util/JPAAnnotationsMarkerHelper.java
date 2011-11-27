package com.tools.codegeneration.util;

import java.util.HashSet;
import java.util.Set;

import com.tools.codegeneration.api.model.relationships.Relation.RelationType;
import com.tools.codegeneration.constants.JPAConstants;
import com.tools.codegeneration.constants.StringConstants;

public class JPAAnnotationsMarkerHelper {

	private static final Set<String> jpaAnnotationTypes = new HashSet<String>();
	
	public static final void addJPAAnnotationType(String annotationTypeFullyQualifiedName) {
		if (!jpaAnnotationTypes.contains(annotationTypeFullyQualifiedName)) {
			jpaAnnotationTypes.add(annotationTypeFullyQualifiedName);	
		}
	}
	
	public static final Set<String> getJPAAnnotationTypes() {
		return jpaAnnotationTypes;
	}
	
	/**
	 * 
	 * @param relationType
	 * @return
	 */
	public static final String createRelationAnnotation(RelationType relationType) {
		
		switch (relationType) {
			case ONE_TO_MANY:
				return createAnnotation(JPAConstants.ONE_TO_MANY_TYPE, true);
				
			case MANY_TO_ONE:
				return createAnnotation(JPAConstants.MANY_TO_ONE_TYPE, true);
				
			case ONE_TO_ONE:
				return createAnnotation(JPAConstants.ONE_TO_ONE_TYPE, true);
				
			case MANY_TO_MANY:
				return createAnnotation(JPAConstants.MANY_TO_MANY_TYPE, true);
				
		}
		
		return StringConstants.EMPTY_STRING;
	}

	/**
	 * 
	 * @param relationType
	 * @return
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
	 * @return
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
	 * @return
	 */
	public static final String createEntityAnnotation() {
		return createAnnotation(JPAConstants.ENTITY_TYPE, true);
	}
	
	/**
	 * 
	 * @return
	 */
	public static final String createColumnAnnotation() {
		return createAnnotation(JPAConstants.COLUMN_TYPE, true);
	}
	
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
