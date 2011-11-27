package com.tools.codegeneration.api.model.relationships;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Relation {
	
	public static final short ONE_TO_MANY_CODE = 1;
	public static final short MANY_TO_ONE_CODE = 2;
	public static final short ONE_TO_ONE_CODE = 3;
	public static final short MANY_TO_MANY_CODE = 4;
	public static final short NOT_RELATED_CODE = -1;
	
	public enum RelationType {
		ONE_TO_MANY(ONE_TO_MANY_CODE),
		MANY_TO_ONE(MANY_TO_ONE_CODE),
		ONE_TO_ONE(ONE_TO_ONE_CODE),
		MANY_TO_MANY(MANY_TO_MANY_CODE),
		NOT_RELATED(NOT_RELATED_CODE);
		
		private final short code;
		
		private RelationType(short code) {
			this.code = code;
		}
		
		private short code() {
			return code;
		}

		/**
		 * 
		 * @param code the code representing a {@link RelationType} enum
		 * constant
		 * 
		 * @return the {@link RelationType} enum constant corresponding to the
		 * specified <code>code</code>
		 */
		public static RelationType convert(short code) {
			for (RelationType relationType : values()) {
				if (relationType.code() == code) {
					return relationType;
				}
			} 
			return null;
		}
	}
	
	
	private One one;
	private Many many;
	private boolean oneToOne;
	private boolean manyToMany;
	private boolean bidirectional;

	/**
	 * @return the one
	 */
	public One getOne() {
		return one;
	}

	/**
	 * @param one
	 *            the one to set
	 */
	public void setOne(One one) {
		this.one = one;
	}

	/**
	 * @return the many
	 */
	public Many getMany() {
		return many;
	}

	/**
	 * @param many
	 *            the many to set
	 */
	public void setMany(Many many) {
		this.many = many;
	}

	/**
	 * @return the oneToOne
	 */
	public boolean isOneToOne() {
		return oneToOne;
	}

	/**
	 * @param oneToOne the oneToOne to set
	 */
	public void setOneToOne(boolean oneToOne) {
		this.oneToOne = oneToOne;
	}

	/**
	 * @return the manyToMany
	 */
	public boolean isManyToMany() {
		return manyToMany;
	}

	/**
	 * @param manyToMany the manyToMany to set
	 */
	public void setManyToMany(boolean manyToMany) {
		this.manyToMany = manyToMany;
	}

	/**
	 * @return the bidirectional
	 */
	public boolean isBidirectional() {
		return bidirectional;
	}

	/**
	 * @param bidirectional the bidirectional to set
	 */
	public void setBidirectional(boolean bidirectional) {
		this.bidirectional = bidirectional;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
					.append("one", one)
					.append("many", many)
					.append("oneToOne", oneToOne)
					.append("manyToMany", manyToMany)
					.append("bidirectional", bidirectional)
					.toString();
	}
}
