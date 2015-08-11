package com.yahoo.ycsb.extensions.rdbms.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="range")
public class Range {

	private String tablename;
	private int limit;

	@XmlAttribute(name="tablename")
	public void setName(String name){
		this.tablename = name;
	}
	
	@XmlAttribute(name="limit")
	public void setLimit(int limit){
		this.limit = limit;
	}
	
	public String getTableName(){
		return this.tablename;
	}
	
	public int getLimit(){
		return this.limit;
	}
}
