package com.tools.codegeneration.api.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Package {
	private Name name;
	
	/**
	 * @return the name
	 */
	public Name getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(Name name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
					.append("name", name)
					.toString();
	}
}
