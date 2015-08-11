package com.yahoo.ycsb.extensions.rdbms.model;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="load")
public class LoadContextual {
	private HashMap<String, Table> tablemap = new HashMap<String, Table>();
	private ArrayList<Table> tables = new ArrayList<Table>();
	private ArrayList<Range> ranges = new ArrayList<Range>();
	private Access access;
	
	@XmlElementWrapper(name="tables")
	@XmlElement(name="table")
	public void setTables(ArrayList<Table> _tables){
		this.tables = _tables;
		for(Table table : _tables){
			this.tablemap.put(table.getName(), table);
		}
	}
	
	public ArrayList<Table> getTables(){
		return this.tables;
	}
	
	public Table getTable(String name){
		return this.tablemap.get(name);
	}
	
	@XmlElementWrapper(name="ranges")
	@XmlElement(name="range")
	public void setTableRanges(ArrayList<Range> ranges){
		this.ranges = ranges;
	}
	
	public ArrayList<Range> getTableRanges(){
		return this.ranges;
	}
	
	@XmlElement(name="access")
	public void setAccessDistribution(Access access){
		this.access = access;
	}
	
	public Access getAccessDistribution(){
		return this.access;
	}
}
