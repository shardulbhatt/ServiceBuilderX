package com.tools.codegeneration.api.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Name {
	private String name;
	private String alias;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @param alias
	 *            the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
					.append("name", name)
					.append("alias", alias).toString();
	}
}
