package com.yahoo.ycsb.extensions.rdbms.model;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.yahoo.ycsb.extensions.rdbms.graph.DirectedGraph;
import com.yahoo.ycsb.extensions.rdbms.graph.Node;


@XmlRootElement
public class Table {

	private String name = null;
	private Column key = null;
	private ArrayList<Column> columns = null;
	private ArrayList<Column> ordered = null;
	private ArrayList<Column> rootColumns = new ArrayList<Column>();
	private HashMap<String, Column> namemapper = new HashMap<String, Column>();
	private ArrayList<Dependency> dependencies = new ArrayList<Dependency>();
	private TDistribution operationchooser = null;
	
	
	@XmlElement(name="tdistribution")
	public void setTableDistribution(TDistribution chooser){
		this.operationchooser = chooser;
	}
	
	public TDistribution getTDistribution(){
		return this.operationchooser;
	}
	
	public String getName() {
		return name;
	}
	
	@XmlAttribute
	public void setName(String name) {
		this.name = name;
	}
	
	public ArrayList<Column> getColumns() {
		return this.columns;
	}
	
	@XmlElementWrapper(name="columns")
	@XmlElement(name="column")
	public void setColumns(ArrayList<Column> columns) {
		this.columns = new ArrayList<Column>();
		for(Column col : columns){
			this.columns.add(col);
		}
		for(Column column : this.columns){
			this.namemapper.put(column.getName(), column);
		}
		if(this.key != null && this.columns != null  && this.dependencies != null) this.generateColumnOrder();
		return;
	}	

	public Column getKey() {
		return key;
	}

	@XmlElement(name="key")
	public void setKey(Column key) {
		this.key = key;
		this.namemapper.put(this.key.getName(), this.key);
		if(this.key != null && this.columns != null && this.dependencies != null) this.generateColumnOrder();
	}
	
	public ArrayList<Dependency> getDependencies() {
		return dependencies;
	}

	@XmlElementWrapper(name="dependencies")
	@XmlElement(name="dependency")
	public void setDependencies(ArrayList<Dependency> dependencies) {
		this.dependencies = dependencies;
		if(this.key != null && this.columns != null  && this.dependencies != null) this.generateColumnOrder();
	}
	
	private void generateColumnOrder(){
		DirectedGraph graph = new DirectedGraph();
		ArrayList<Node> nodes = new ArrayList<Node>();
		for(Column col : this.columns){
			Node node = new Node(col);
			graph.addNode(col.getName(), node);
			nodes.add(node);
		}
		
		Node key = new Node(this.key);
		
		graph.addNode(this.key.getName(), key);
		
		for(Dependency dep : this.dependencies){
			graph.addEdge(dep.getSource(), dep.getDependency());
		}
		
		
		for(Node node: nodes){
			graph.evalDepth(node);
		}
		graph.evalDepth(key);
		
		ArrayList<Column> ordered_columns = new ArrayList<Column>();
		ArrayList<Node> order = graph.generateBottomTopOrder();
		for(Node node : order){
			ordered_columns.add((Column)node.getContent());
			if(node.getDepth() == 0){
				this.rootColumns.add((Column)node.getContent());
			}
		}
		
		this.ordered = ordered_columns;
	}
	
	public ArrayList<Column> getOrderedColumns(){
		return this.ordered;
	}
	
	public ArrayList<Column> getRootColumns(){
		return this.rootColumns;
	}

	public Column getColumnByName(String colName) {
		return this.namemapper.get(colName);
	}
}
