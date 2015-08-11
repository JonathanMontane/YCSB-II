package com.yahoo.ycsb.extensions.rdbms.model;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.yahoo.ycsb.WorkloadException;

@XmlRootElement(name="benchmark")
public class BenchmarkContextual {
	private ArrayList<Table> tables;
	private HashMap<String, Table> tablemapper = new HashMap<String, Table>();
	private Access access;
	private ArrayList<TestEnvironment> envs;
	
	@XmlElementWrapper(name="tables")
	@XmlElement(name="table")
	public void setTables(ArrayList<Table> _tables) throws WorkloadException{
		this.tables = _tables;
		for(Table table : _tables){
			tablemapper.put(table.getName(), table);
		}
	}
	
	public ArrayList<Table> getTables(){
		return tables;
	}
	
	public Table getTableByName(String tablename){
		return tablemapper.get(tablename);
	}
	
	@XmlElement(name="access")
	public void setAccessDistribution(Access access){
		this.access = access;
	}
	
	public Access getAccessDistribution(){
		return this.access;
	}
	
	@XmlElementWrapper(name="tests")
	@XmlElement(name="environment")
	public ArrayList<TestEnvironment> getEnvs() {
		return envs;
	}

	public void setEnvs(ArrayList<TestEnvironment> envs) {
		this.envs = envs;
	}
}
