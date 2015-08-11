package com.yahoo.ycsb.extensions.rdbms.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Column {

	private String name;
	private GeneratorElement distribution;
	private ArrayList<Parameter> parameters = new ArrayList<Parameter>();
	
	
	public String getName() {
		return name;
	}
	
	@XmlAttribute
	public void setName(String name) {
		this.name = name;
	}

	
	public GeneratorElement getDistribution() {
		return distribution;
	}
	
	@XmlElement(name="generator")
	public void setDistribution(GeneratorElement distribution) {
		this.distribution = distribution;
	}

	public ArrayList<Parameter> getParameters(){
		return this.parameters;
	}
	
	@XmlElementWrapper(name="parameters")
	@XmlElement(name="parameter")
	public void setParameters(ArrayList<Parameter> params){
		this.parameters = params;
	}
}
