package com.tools.codegeneration.api.model.relationships;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class OneToMany {
	
	private String isRelationshipBidirectional;
	private List<OneToMany> oneToManyList;
	

	/**
	 * @return the isRelationshipBidirectional
	 */
	public String getIsRelationshipBidirectional() {
		return isRelationshipBidirectional;
	}

	/**
	 * @param isRelationshipBidirectional
	 *            the isRelationshipBidirectional to set
	 */
	public void setIsRelationshipBidirectional(
			String isRelationshipBidirectional) {
		this.isRelationshipBidirectional = isRelationshipBidirectional;
	}
	
	/**
	 * @return the oneToManyList
	 */
	public List<OneToMany> getOneToManyList() {
		return oneToManyList;
	}

	/**
	 * @param oneToManyList the oneToManyList to set
	 */
	public void setOneToManyList(List<OneToMany> oneToManyList) {
		this.oneToManyList = oneToManyList;
	}
	
	/**
	 * 
	 * @param oneToMany
	 */
	public void addOneToMany(OneToMany oneToMany) {
		if (oneToManyList == null) {
			this.oneToManyList = new ArrayList<OneToMany>();
		}
		
		this.oneToManyList.add(oneToMany);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("isRelationshipBidirectional", isRelationshipBidirectional)
				.append("oneToManyList", oneToManyList).toString();
	}
	
	private static List<OneToMany> oneToManyListFactory() {
		return new ArrayList<OneToMany>();
	}	
}
