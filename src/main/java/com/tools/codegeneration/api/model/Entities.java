package com.tools.codegeneration.api.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Entities {
	private List<Entity> entities;
	
	/**
	 * @return the entities
	 */
	public List<Entity> getEntities() {
		return entities;
	}


	/**
	 * @param entities the entities to set
	 */
	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}


	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				   .append("entities", entities)
				   .toString();
	}
	
	private static List<Entity> listFactory() {
		return new ArrayList<Entity>();
	}
}
