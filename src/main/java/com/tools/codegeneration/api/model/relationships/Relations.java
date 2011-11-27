package com.tools.codegeneration.api.model.relationships;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Relations {

	private List<Relation> relations;

	/**
	 * @return the relations
	 */
	public List<Relation> getRelations() {
		return relations;
	}

	/**
	 * @param relations the relations to set
	 */
	public void setRelations(List<Relation> relations) {
		this.relations = relations;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
					.append("relations", relations)
					.toString();
	}
	
	private static List<Relation> relationListFactory() {
		return new ArrayList<Relation>();
	}
}
