package com.tools.codegeneration.util;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.tools.codegeneration.api.model.Property;
import com.tools.codegeneration.api.model.relationships.Relation.RelationType;
import com.tools.codegeneration.constants.HibernateORMConstants;
import com.tools.codegeneration.constants.JPAConstants;
import com.tools.codegeneration.constants.StringConstants;

public class JPAAnnotationsMarkerHelper {

	/**
	 * if an {@link EntityHelper} is specified as JPA enabled in the 
	 * <b>entities definition XML</b> then this method would add the required 
	 * JPA Annotations to the property(s) of the Java Bean to be generated. 
	 *  
	 * @param property the {@link Property} instance representing the property 
	 * to be added to the Java Bean to be generated.
	 *  
	 * @return a formatted string representing the JPA annotations to be
	 * added to a given property in the Java Bean to be generated.  
	 */
	public static final String addJPAAnnotationsToProperty(Property property) {
		
		StringBuilder jpaAnnotationsBuilder = new StringBuilder();
		
		jpaAnnotationsBuilder.append(StringConstants.TAB_4_WIDTH);

		JPAProperties jpaProperties = new JPAProperties();
		
		RelationType relationType = property.getRelationType();
		
		
		if (property.isId()) {
		
			IdAnnotationType propertyIdAnnotationType = property.getIdAnnotationType();
			
			if (IdAnnotationType.ONE_TO_ONE == propertyIdAnnotationType) {
				// Create ID annotation required for creating a ONE-To-ONE
				// relation between entities
				jpaProperties.setIdGeneratorType(JPAConstants.ID_GENERATOR_TYPE_FOREIGN);
				jpaProperties.setGenericGeneratorUniqueName(JPAConstants.ID_GENERATOR_TYPE_FOREIGN);
				jpaProperties.setGenericGeneratorStrategy(JPAConstants.ID_GENERATOR_TYPE_FOREIGN);
				String oneToOnePrimaryEntityPropertyName = 
						property.getOneToOnePrimaryEntityPropertyName();
				jpaProperties.setGenericGeneratorParametersPropertyName(
							new String[] { oneToOnePrimaryEntityPropertyName });
				
				jpaAnnotationsBuilder
					.append(JPAAnnotationsMarkerHelper.createIdAnnotationForOneToOneRelationship(jpaProperties));
			} else if (IdAnnotationType.SIMPLE == propertyIdAnnotationType) {
				jpaProperties.setIdGeneratorStrategy(JPAConstants.AUTO_PRIMARY_KEY_GENERATION_STRATEGY);
				// Create Simple ID annotation 
				jpaAnnotationsBuilder
					.append(JPAAnnotationsMarkerHelper.createIdAnnotation(jpaProperties));	
			}
			
		} else {
			
			if (RelationType.NOT_RELATED != relationType) {
				// Get the JPA Annotation Types set on the property
				// and check if cascade is available.If yes,
				// then only append the cascade attribute.
				String[] jpaAnnotationTypes = property.getJpaAnnotationTypes();
				
				if (jpaAnnotationTypes != null && jpaAnnotationTypes.length > 0) {
					List<String> jpaAnnotationTypesList = Arrays.asList(jpaAnnotationTypes);
					
					if (jpaAnnotationTypesList.contains(JPAConstants.CASCADE_ENUM_TYPE)) {
						jpaProperties.setAddCascade(true);
					}
				}
				
				// Get the mappedBy set on the property
				// and check if its value is set.If yes,
				// then only append the mappedBy attribute.
				String mappedBy = property.getMappedBy();
				
				// "mappedBy" attribute should be added in case of 
				// @OneToMany and @OneToOne annotations only.
				if ((mappedBy != null) && 
						(RelationType.ONE_TO_MANY == relationType 
							|| RelationType.ONE_TO_ONE == relationType)) {
					
					mappedBy = mappedBy.trim();
					if (!mappedBy.isEmpty()) {
						jpaProperties.setAddMappedBy(true);
						jpaProperties.setMappedBy(mappedBy);	
					}
				}
				
				jpaAnnotationsBuilder
					.append(JPAAnnotationsMarkerHelper.createRelationAnnotation(
									relationType, jpaProperties));
				
				
			} else {
				jpaAnnotationsBuilder
					.append(JPAAnnotationsMarkerHelper.createColumnAnnotation());	
			}
		}
		
		return jpaAnnotationsBuilder.toString();
	}
	/**
	 * 
	 * @param relationType the {@link RelationType} enum value
	 * 
	 * @param jpaProperties an instance of {@link JPAProperties} holding the
	 * JPA Annotation's properties/attributes values to set
	 * 
	 * @return a formatted string representing a JPA relationship annotation
	 */
	public static final String createRelationAnnotation(
				RelationType relationType, JPAProperties jpaProperties) {
		
		String annotation = StringConstants.EMPTY_STRING;
		
		boolean addCascade = jpaProperties.isAddCascade();
		boolean addMappedBy = jpaProperties.isAddMappedBy();
		
		boolean addLineBreakAtTheEnd = (addCascade || addMappedBy) ? false : true;
		
		switch (relationType) {
			case ONE_TO_MANY:
				annotation = createAnnotation(
									JPAConstants.ONE_TO_MANY_TYPE, addLineBreakAtTheEnd);
				break;
				
			case MANY_TO_ONE:
				annotation = createAnnotation(
									JPAConstants.MANY_TO_ONE_TYPE, addLineBreakAtTheEnd);
				break;
				
			case ONE_TO_ONE:
				annotation = createAnnotation(
									JPAConstants.ONE_TO_ONE_TYPE, addLineBreakAtTheEnd);
				break;
				
			case MANY_TO_MANY:
				annotation = createAnnotation(
									JPAConstants.MANY_TO_MANY_TYPE, addLineBreakAtTheEnd);
		}
		
		if (addCascade || addMappedBy) {
			// Examples: 
			// @ManyToOne(cascade = CascadeType.ALL)
			// @OneToMany(cascade = CascadeType.ALL, mappedBy="<SOME_PROPERTY_NAME>")

			StringBuilder sb = new StringBuilder(annotation);
			sb.append(StringConstants.LEFT_PARENTHESES);
			if (addCascade && !addMappedBy) {
				// Add only cascade attribute
				createCascadeAttribute(sb);
			} else if (addMappedBy && !addCascade) {
				// Add only mappedBy attribute
				createMappedByAttribute(sb, jpaProperties.getMappedBy());
			} else {
				// Add both mappedBy and cascade attributes
				createCascadeAttribute(sb);
				sb.append(StringConstants.COMMA_SUFFIXED_WITH_SPACE);
				createMappedByAttribute(sb, jpaProperties.getMappedBy());
			}
			sb.append(StringConstants.RIGHT_PARENTHESES);
			sb.append(StringConstants.LINE_BREAK);
			annotation = sb.toString();
		}
		
		return annotation;
	}
	
	/**
	 * Creates JPA "cascade" attribute formatted string and appends it to the
	 * specified <code>sb</code>.
	 * 
	 * @param sb the {@link StringBuilder} that would be holding the JPA 
	 * Relation annotation's "cascade" attribute.
	 */
	private static void createCascadeAttribute(StringBuilder sb) {
		sb.append(JPAConstants.CASCADE_ATTR);
		sb.append(StringConstants.EQUALS_PREFIXED_SUFFIXED_WITH_SPACE);
		sb.append(JPAConstants.CASCADE_ALL);
	}
	
	/**
	 * Creates JPA "mappedBy" attribute formatted string and appends it to the
	 * specified <code>sb</code>.
	 * 
	 * @param sb the {@link StringBuilder} that would be holding the JPA 
	 * Relation annotation's "mappedBy" attribute.
	 * @param mappedBy the value to be set for the "mappedBy" attribute
	 */
	private static void createMappedByAttribute(StringBuilder sb, String mappedBy) {
		sb.append(JPAConstants.MAPPED_BY_ATTR);
		sb.append(StringConstants.EQUALS_PREFIXED_SUFFIXED_WITH_SPACE);
		sb.append(StringConstants.DOUBLE_QUOTE);
		sb.append(mappedBy);
		sb.append(StringConstants.DOUBLE_QUOTE);
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
	 * @param jpaProperties an instance of {@link JPAProperties} holding the
	 * JPA Annotation's properties/attributes values to set
	 * 
	 * @return a formatted string representing a JPA {@link Id} annotation
	 */
	private static String createIdAnnotation(JPAProperties jpaProperties) {
		
		String idAnnotation = createAnnotation(JPAConstants.ID_TYPE, true);
		
		StringBuilder sb = new StringBuilder();
		sb.append(idAnnotation);
		sb.append(StringConstants.TAB_4_WIDTH);
		sb.append(createAnnotation(JPAConstants.GENERATED_VALUE_TYPE, false));
		sb.append(StringConstants.LEFT_PARENTHESES);
		sb.append(JPAConstants.GENERATED_VALUE_TYPE_STRATEGY_ATTR);
		sb.append(StringConstants.EQUALS_PREFIXED_SUFFIXED_WITH_SPACE);
		sb.append(jpaProperties.getIdGeneratorStrategy());
		sb.append(StringConstants.RIGHT_PARENTHESES);
		sb.append(StringConstants.LINE_BREAK);
		
		return sb.toString();
	}
	
	/**
	 * 
	 * @param jpaProperties an instance of {@link JPAProperties} holding the
	 * JPA Annotation's properties/attributes values to set
	 * 
	 * @return a formatted string representing a JPA {@link Id} annotation for 
	 * a non-primary entity having One-to-One relationship with a primary entity.
	 * 
	 * <br/><br/>
	 * 
	 * Note: Typically the entity specified at "one"
	 * end in relations definition XML is considered to be the
	 * "primary entity" and the one at specified at "many" end is
	 * considered as "non-primary entity"
	 */
	private static String createIdAnnotationForOneToOneRelationship(JPAProperties jpaProperties) {
		// Reference: http://vigilbose.blogspot.com/2009/04/jpahibernate-one-to-one-mapping-quirks.html
		// @Id
		// @Column(name = "booking_id", unique = true, nullable = false)
		// @GeneratedValue(generator="foreign")
		// @GenericGenerator(name="foreign", strategy = "foreign", parameters={
		//		@Parameter(name="property", value="booking")
		// })
		String idAnnotation = createAnnotation(JPAConstants.ID_TYPE, true); // @Id annotation
		StringBuilder sb = new StringBuilder();
		sb.append(idAnnotation); 
		
		sb.append(StringConstants.TAB_4_WIDTH);
		sb.append(createAnnotation(JPAConstants.GENERATED_VALUE_TYPE, false)); // @GeneratedValue
		sb.append(StringConstants.LEFT_PARENTHESES);
		sb.append(JPAConstants.ID_GENERATOR_ATTR);
		sb.append(StringConstants.EQUALS_PREFIXED_SUFFIXED_WITH_SPACE);
		sb.append(StringConstants.DOUBLE_QUOTE);
		sb.append(jpaProperties.getIdGeneratorType());
		sb.append(StringConstants.DOUBLE_QUOTE);
		sb.append(StringConstants.RIGHT_PARENTHESES);
		sb.append(StringConstants.LINE_BREAK);
		
		sb.append(StringConstants.TAB_4_WIDTH);
		sb.append(createAnnotation(HibernateORMConstants.GENERIC_GENERATOR_TYPE, false)); // @GenericGenerator (org.hibernate.annotations.GenericGenerator)
		sb.append(StringConstants.LEFT_PARENTHESES);
		
		sb.append(HibernateORMConstants.GENERIC_GENERATOR_TYPE_NAME_ATTR_NAME); //  @GenericGenerator name attribute
		sb.append(StringConstants.EQUALS_PREFIXED_SUFFIXED_WITH_SPACE);
		sb.append(StringConstants.DOUBLE_QUOTE);
		sb.append(jpaProperties.getGenericGeneratorUniqueName());
		sb.append(StringConstants.DOUBLE_QUOTE);
		
		sb.append(StringConstants.COMMA_SUFFIXED_WITH_SPACE);
		
		sb.append(HibernateORMConstants.GENERIC_GENERATOR_TYPE_STRATEGY_ATTR_NAME); // @GenericGenerator strategy attribute
		sb.append(StringConstants.EQUALS_PREFIXED_SUFFIXED_WITH_SPACE);
		sb.append(StringConstants.DOUBLE_QUOTE);
		sb.append(jpaProperties.getGenericGeneratorStrategy());
		sb.append(StringConstants.DOUBLE_QUOTE);
		
		
		String[] genericGeneratorParametersPropertyNames = 
				jpaProperties.getGenericGeneratorParametersPropertyName();
		
		if (genericGeneratorParametersPropertyNames != null && 
				genericGeneratorParametersPropertyNames.length > 0) {
			
			sb.append(StringConstants.COMMA_SUFFIXED_WITH_SPACE);
			
			sb.append(HibernateORMConstants.GENERIC_GENERATOR_TYPE_PARAMETERS_ATTR_NAME); // @GenericGenerator parameters attribute
			sb.append(StringConstants.EQUALS_PREFIXED_SUFFIXED_WITH_SPACE);
			sb.append(StringConstants.LEFT_CURLY_BRACE);
			sb.append(StringConstants.LINE_BREAK);
			
			for (String parameterProperty : genericGeneratorParametersPropertyNames) {
				sb.append(StringConstants.TAB_4_WIDTH).append(StringConstants.TAB_4_WIDTH);
				sb.append(createAnnotation(HibernateORMConstants.PARAMETER_TYPE, false)); // @Parameter (org.hibernate.annotations.Parameter)
				sb.append(StringConstants.LEFT_PARENTHESES);
				
				sb.append(HibernateORMConstants.PARAMETER_TYPE_NAME_ATTR_NAME); // @Parameter name attribute
				sb.append(StringConstants.EQUALS_PREFIXED_SUFFIXED_WITH_SPACE);
				sb.append(StringConstants.DOUBLE_QUOTE);
				sb.append(HibernateORMConstants.PARAMETER_TYPE_NAME_ATTR_VALUE);
				sb.append(StringConstants.DOUBLE_QUOTE);
				
				sb.append(StringConstants.COMMA_SUFFIXED_WITH_SPACE);
				
				sb.append(HibernateORMConstants.PARAMETER_TYPE_VALUE_ATTR_NAME); // @Parameter value attribute
				sb.append(StringConstants.EQUALS_PREFIXED_SUFFIXED_WITH_SPACE);
				sb.append(StringConstants.DOUBLE_QUOTE);
				sb.append(parameterProperty); 
				sb.append(StringConstants.DOUBLE_QUOTE);
				
				sb.append(StringConstants.RIGHT_PARENTHESES);
				sb.append(StringConstants.LINE_BREAK);
			}
			sb.append(StringConstants.TAB_4_WIDTH);
			sb.append(StringConstants.RIGHT_CURLY_BRACE);
		}
		
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
	private static String createColumnAnnotation() {
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
	private static String createAnnotation(
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
