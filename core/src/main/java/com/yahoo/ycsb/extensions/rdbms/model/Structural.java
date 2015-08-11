package com.yahoo.ycsb.extensions.rdbms.model;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.yahoo.ycsb.WorkloadException;

@XmlRootElement
public class Structural {

	private ArrayList<Table> tables;
	private HashMap<String, Table> tablenameMap;
	
	
	@XmlElementWrapper(name="tables")
	@XmlElement(name="table")
	public void setTables(ArrayList<Table> _tables) throws WorkloadException{
		this.tables = _tables;
		this.tablenameMap = new HashMap<String, Table>();
		for(Table table : _tables){
			this.tablenameMap.put(table.getName(), table);
		}
	}
	
	public ArrayList<Table> getTables(){
		return this.tables;
	}
	
	public Table getTable(Integer index){
		return this.tables.get(index);
	}
	
	public Table getTable(String tablename){
		return this.tablenameMap.get(tablename);
	}
}
