package com.yahoo.ycsb.extensions.rdbms.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Mode {

	private String type;
	private CDistribution columnchooser;
	private KDistribution keychooser;
	
	@XmlElement(name="cdistribution")
	public void setColumnDistribution(CDistribution chooser){
		this.columnchooser = chooser;
	}
	
	public CDistribution getCDistribution(){
		return this.columnchooser;
	}

	@XmlElement(name="kdistribution")
	public void setKeyDistribution(KDistribution chooser){
		this.keychooser = chooser;
	}
	
	public KDistribution getKDistribution(){
		return this.keychooser;
	}
	
	public String getType() {
		return type;
	}

	@XmlAttribute(name="type")
	public void setType(String type) {
		this.type = type;
	}
}
