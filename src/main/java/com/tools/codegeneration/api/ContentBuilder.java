/**
 * 
 */
package com.tools.codegeneration.api;

import java.util.Collection;

/**
 * @author jignesh
 *
 */
public interface ContentBuilder<T> {

	/**
	 * Creates the content in a format represented by the generic type <code>T</code>
	 * 
	 * @param t the generic type <code>T</code> representing the format of the 
	 * content to be created by this method
	 * 
	 * @return a {@link Collection} holding content of type {@link String}
	 */
	public Collection<String> createContent(T t);
}
