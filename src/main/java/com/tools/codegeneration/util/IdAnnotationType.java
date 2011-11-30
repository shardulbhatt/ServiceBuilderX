package com.tools.codegeneration.util;

import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

public enum IdAnnotationType {
	/**
	 * Represents the usual JPA {@link Id} annotation to a JPA enabled 
	 * entity with its "strategy" attribute set to {@link GenerationType#AUTO}.
	 */
	SIMPLE,
	/**
	 * Represents the JPA {@link Id} annotations definition to be applied to 
	 * a non-primary entity related to a primary entity by {@link OneToOne}
	 * relation.
	 */
	ONE_TO_ONE;
}
