package com.tools.codegeneration.api.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.tools.codegeneration.api.model.relationships.Relation.RelationType;
import com.tools.codegeneration.util.IdAnnotationType;

public class Property {
	private String name;
	private String type;
	// following fields are useful when the property type would be be found 
	// generic.For example: java.util.Collection<T> 
	private boolean genericType;
	private String genericTypeParameter;
	private boolean id;
	private String[] jpaAnnotationTypes;
	private String mappedBy;
	private String oneToOnePrimaryEntityPropertyName;
	private IdAnnotationType idAnnotationType;
	
	/** 
	 * Default relation type is {@link RelationType#NOT_RELATED}
	 */
	private RelationType relationType = RelationType.NOT_RELATED;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return the genericType
	 */
	public boolean isGenericType() {
		return genericType;
	}

	/**
	 * @param genericType the genericType to set
	 */
	public void setGenericType(boolean genericType) {
		this.genericType = genericType;
	}

	/**
	 * @return the genericTypeParameter
	 */
	public String getGenericTypeParameter() {
		return genericTypeParameter;
	}

	/**
	 * @param genericTypeParameter the genericTypeParameter to set
	 */
	public void setGenericTypeParameter(String genericTypeParameter) {
		assert(genericType == true);
		this.genericTypeParameter = genericTypeParameter;
	}
	
	/**
	 * @return the id
	 */
	public boolean isId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(boolean id) {
		this.id = id;
	}
	
	/**
	 * @return the relationType
	 */
	public RelationType getRelationType() {
		return relationType;
	}

	/**
	 * @param relationType the relationType to set
	 */
	public void setRelationType(RelationType relationType) {
		this.relationType = relationType;
	}
	
	/**
	 * @return the jpaAnnotationTypes
	 */
	public String[] getJpaAnnotationTypes() {
		return jpaAnnotationTypes;
	}

	/**
	 * @param jpaAnnotationTypes the jpaAnnotationTypes to set
	 */
	public void setJpaAnnotationTypes(String[] jpaAnnotationTypes) {
		this.jpaAnnotationTypes = jpaAnnotationTypes;
	}
	
	/**
	 * @return the mappedBy
	 */
	public String getMappedBy() {
		return mappedBy;
	}

	/**
	 * @param mappedBy the mappedBy to set
	 */
	public void setMappedBy(String mappedBy) {
		this.mappedBy = mappedBy;
	}
	
	/**
	 * @return the oneToOnePrimaryEntityPropertyName
	 */
	public String getOneToOnePrimaryEntityPropertyName() {
		return oneToOnePrimaryEntityPropertyName;
	}

	/**
	 * @param oneToOnePrimaryEntityPropertyName the oneToOnePrimaryEntityPropertyName to set
	 */
	public void setOneToOnePrimaryEntityPropertyName(
			String oneToOnePrimaryEntityPropertyName) {
		this.oneToOnePrimaryEntityPropertyName = oneToOnePrimaryEntityPropertyName;
	}
	
	/**
	 * @return the idAnnotationType
	 */
	public IdAnnotationType getIdAnnotationType() {
		return idAnnotationType;
	}

	/**
	 * @param idAnnotationType the idAnnotationType to set
	 */
	public void setIdAnnotationType(IdAnnotationType idAnnotationType) {
		this.idAnnotationType = idAnnotationType;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
					.append("name", name)
					.append("type", type)
					.append("genericType", genericType)
					.append("genericTypeParameter", genericTypeParameter)
					.append("id", id)
					.append("relationType", relationType)
					.append("jpaAnnotationTypes", jpaAnnotationTypes)
					.append("mappedBy", mappedBy)
					.append("oneToOnePrimaryEntityPropertyName", oneToOnePrimaryEntityPropertyName)
					.append("idAnnotationType", idAnnotationType)
					.toString();
	}
}
