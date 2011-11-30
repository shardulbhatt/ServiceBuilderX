package com.tools.codegeneration;

import java.io.InputStream;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.junit.Before;
import org.junit.Test;

import com.tools.codegeneration.api.EntitiesBuilder;
import com.tools.codegeneration.api.impl.JavaEntityContentBuilder;
import com.tools.codegeneration.api.model.Entities;
import com.tools.codegeneration.api.model.relationships.Relations;

public class TestEntitiesBuilder {

	//TODO: Configure Log4j and use SL4J
	// Replace System.out.println with log statements in this file
	private static final String TEST_ENTITIES_XML_FILE = "/test-entities.xml";
	private static final String TEST_RELATIONS_XML_FILE = "/test-relationships.xml";
	private static final String SOURCE_PATH = 
			"/media/jdata/My-Technical-Work/Programming/Languages/Java/Practice/Standalone/CodeGeneration/codegeneration-tool/src/main/java";
	
	private Entities entities = null;
	private Relations relations = null;
	
	@Before
	public void unmarshallEntities() throws JiBXException {
		entities = unmarshallEntitiesXml();
		relations = unmarshallRelationsXml();
	}
	
	@Test
	public void testUnmarshallEntitiesAndRelations() throws JiBXException {
		System.out.println(entities);
		System.out.println(relations);
	}
	
	
//	@Ignore
	@Test
	public void testBuildEntities() throws JiBXException {
		EntitiesBuilder entitiesBuilder = new EntitiesBuilder();
		entitiesBuilder.setEntityContentBuilder(new JavaEntityContentBuilder());
		entitiesBuilder.setSourceGenerationPath(SOURCE_PATH);
		
		entitiesBuilder.buildEntities(entities.getEntities(), relations.getRelations());
	}

	/**
	 * @throws JiBXException
	 */
	private Entities unmarshallEntitiesXml() throws JiBXException {
		IBindingFactory bFactory = BindingDirectory.getFactory(Entities.class);
		IUnmarshallingContext unmarshallingContext = bFactory.createUnmarshallingContext();
		
		InputStream inputStream = getInputStreamForClasspathResource(TEST_ENTITIES_XML_FILE);
		
		if (inputStream != null) {
			return (Entities) unmarshallingContext.unmarshalDocument(inputStream, null);
		}
		return null;
	}
	
	/**
	 * 
	 * @return
	 * @throws JiBXException
	 */
	private Relations unmarshallRelationsXml() throws JiBXException {
		IBindingFactory bFactory = BindingDirectory.getFactory(Relations.class);
		IUnmarshallingContext unmarshallingContext = bFactory.createUnmarshallingContext();
		
		InputStream inputStream = getInputStreamForClasspathResource(TEST_RELATIONS_XML_FILE);
		
		if (inputStream != null) {
			return (Relations) unmarshallingContext.unmarshalDocument(inputStream, null);
		}
		return null;
	}
	
	private InputStream getInputStreamForClasspathResource(String resourceName) {
		return this.getClass().getResourceAsStream(resourceName);
	}
	
}
