package com.tools.codegeneration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.junit.Test;

import com.tools.codegeneration.api.model.Entities;
import com.tools.codegeneration.api.model.Entity;
import com.tools.codegeneration.api.model.Name;
import com.tools.codegeneration.api.model.Package;
import com.tools.codegeneration.api.model.Properties;
import com.tools.codegeneration.api.model.Property;

public class CopyOfUnmarshallingTest {

	//TODO: Configure Log4j and use SL4J
	private static final String TEST_ENTITIES_XML_FILE = "/test-entities.xml";
	private static final String TEST_ENTITIES_TEMPLATE_FILE = "/test-entity-template.txt";
	private static final String SOURCE_PATH = 
			"/media/jdata/My-Technical-Work/Programming/Languages/Java/Practice/Standalone/CodeGeneration/codegeneration-tool/src/main/java";
	
	@Test
	public void testUnmarshallEntitiesJibxBinding() throws JiBXException {
		Entities entities = unmarshallEntitiesXml();
		System.out.println(entities); //TODO: Replace with log statement
	}

	private static final String PACKAGE_TOKEN = "@package@";
	private static final String ENTITY_NAME_TOKEN = "@EntityName@";
	private static final String ENTITY_PROPERTY_NAME_TOKEN = "@EntityPropertyName@";
	private static final String ENTITY_PROPERTY_TYPE_TOKEN = "@EntityPropertyType@";
	
	@Test
	public void testGenerateEntity() throws JiBXException {
		Entities entities = unmarshallEntitiesXml();
		
		
		Map<String, String> tokensMap = new HashMap<String, String>();
		
		Name entityName = null;
		Package entityPackage = null;
		List<Property> entityProperties = null;
		Properties propertiesWrapper = null;
		String entityPropertyDefaultType = null;
		String[][] propertiesArr = null;
		for (Entity entity : entities.getEntities()) {
			entityName = entity.getEntityName();
			tokensMap.put(ENTITY_NAME_TOKEN, entityName.getName());
			
			entityPackage = entity.getEntityPackage();
			tokensMap.put(PACKAGE_TOKEN, entityPackage.getName().getName());
			
			propertiesWrapper = entity.getProperties();
			entityProperties = propertiesWrapper.getProperties();
			entityPropertyDefaultType = propertiesWrapper.getDefaultType();
			
			// {{ <PROPERTY_TYPE_FULLY_QUALIFIED_NAME>, <PROPERTY_TYPE>, <PROPERTY_NAME> }}
			propertiesArr = new String[entityProperties.size()][3];
			
			int i = 0;
			String type = null;
			for (Property property : entityProperties) {
				type = property.getType();
				
				if (type == null) {
					type = entityPropertyDefaultType;
				}
				propertiesArr[i][0] = type;
				propertiesArr[i][1] = type.substring((type.lastIndexOf(".") + 1));
				propertiesArr[i][2] = property.getName();
				i++;
			}
		}
		
		System.out.println("========================");
		System.out.println("Tokens Map:");
		System.out.println(tokensMap);
		System.out.println("========================");
		System.out.println("Properties Array:");
		
		for (int i = 0; i < propertiesArr.length; i++) {
			System.out.println(propertiesArr[i][0] + " " + propertiesArr[i][1] + " " + propertiesArr[i][2]);
		}
		System.out.println("========================");
		
		// Create the package at source path
		String appPackage = tokensMap.get(PACKAGE_TOKEN);
		String packagePath = appPackage.replaceAll("\\.", File.separator);
		
		File source = new File(SOURCE_PATH + File.separator + packagePath);
		
		if (!source.exists()) {
			boolean flag = source.mkdirs();
			if (flag) {
				System.out.println("Successfully created package : " + appPackage);
			} else {
				System.out.println("Could not create package : " + appPackage);
			}	
		}
		
		
		// Get the input stream for the entity template
//		InputStream inputStream = getInputStreamForClasspathResource(TEST_ENTITIES_TEMPLATE_FILE);
//		if (inputStream != null) {
//			Scanner scanner = new Scanner(inputStream);
//			StringBuilder sb = new StringBuilder();
//			while(scanner.hasNext()) {
//				
//			}
//		}
		
		PushbackReader pushbackReader = new PushbackReader(new StringReader("1234567890${token1}abcdefg${token2}XYZ$000"));
		try {
			while(pushbackReader.read() != -1) {
				int data = pushbackReader.read();
			    System.out.println((char) data);
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	private InputStream getInputStreamForClasspathResource(String resourceName) {
		return this.getClass().getResourceAsStream(resourceName);
	}
	
}
