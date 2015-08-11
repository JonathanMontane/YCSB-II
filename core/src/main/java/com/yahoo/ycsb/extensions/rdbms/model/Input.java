package com.yahoo.ycsb.extensions.rdbms.model;

import javax.xml.bind.annotation.XmlAttribute;


public class Input {

	private String name;
	private String value;
	
	public String getName() {
		return name;
	}
	
	@XmlAttribute(name="name")
	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}
	
	@XmlAttribute(name="value")
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString(){
		return "name -> "+ this.name + " | value -> "+ this.value;
	}

}
