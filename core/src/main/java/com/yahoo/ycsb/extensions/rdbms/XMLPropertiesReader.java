package com.yahoo.ycsb.extensions.rdbms;

import java.io.File;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.yahoo.ycsb.extensions.rdbms.model.XMLProperties;


public class XMLPropertiesReader {

	private static final String XML_PROPERTIES_PATH = "xmlpath";
	private static final String XML_PROPERTIES_PATH_DEFAULT = null;
	
	private String path;
	XMLProperties properties;
	Properties setup;
	
	public XMLPropertiesReader(Properties p){
		this.setup = p;
		
		this.path = p.getProperty(XML_PROPERTIES_PATH, XML_PROPERTIES_PATH_DEFAULT); 
		
		try {
			File file = new File(this.path);
			JAXBContext jaxbContext = JAXBContext.newInstance(XMLProperties.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			this.properties = (XMLProperties) jaxbUnmarshaller.unmarshal(file);

		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	public XMLProperties getProperties() {
		// TODO Auto-generated method stub
		return this.properties;
	}

}
