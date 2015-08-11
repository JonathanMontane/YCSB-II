package com.yahoo.ycsb.extensions.rdbms.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Dependency {
	private String source;
	private String dependency;

	public String getSource() {
		return this.source;
	}

	@XmlAttribute(name="source")
	public void setName(String source) {
		this.source = source;
	}
	
	public String getDependency() {
		return this.dependency;
	}

	@XmlAttribute(name="dependency")
	public void setDependency(String dependency) {
		this.dependency = dependency;
	}
}
