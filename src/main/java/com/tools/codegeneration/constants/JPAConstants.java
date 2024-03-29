package com.tools.codegeneration.constants;

public interface JPAConstants {

	public static final String ID_TYPE = "javax.persistence.Id";
	public static final String ENTITY_TYPE = "javax.persistence.Entity";
	public static final String COLUMN_TYPE = "javax.persistence.Column";
	public static final String GENERATED_VALUE_TYPE = "javax.persistence.GeneratedValue";
	public static final String GENERATION_TYPE = "javax.persistence.GenerationType";
	public static final String ONE_TO_MANY_TYPE = "javax.persistence.OneToMany";
	public static final String MANY_TO_ONE_TYPE = "javax.persistence.ManyToOne";
	public static final String ONE_TO_ONE_TYPE = "javax.persistence.OneToOne";
	public static final String MANY_TO_MANY_TYPE = "javax.persistence.ManyToMany";
	public static final String CASCADE_ENUM_TYPE = "javax.persistence.CascadeType";
	public static final String PRIMARY_KEY_JOIN_COLUMN_TYPE = "javax.persistence.PrimaryKeyJoinColumn";
	
	
	public static final String AUTO_PRIMARY_KEY_GENERATION_STRATEGY = "GenerationType.AUTO";
	public static final String GENERATED_VALUE_TYPE_STRATEGY_ATTR = "strategy";
	public static final String CASCADE_ATTR = "cascade";
	public static final String CASCADE_ALL = "CascadeType.ALL";
	public static final String MAPPED_BY_ATTR = "mappedBy";
	public static final String ID_GENERATOR_ATTR = "generator";
	public static final String ID_GENERATOR_TYPE_FOREIGN = "foreign";
}

