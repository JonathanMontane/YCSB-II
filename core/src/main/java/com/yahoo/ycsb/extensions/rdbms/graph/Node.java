package com.yahoo.ycsb.extensions.rdbms.graph;

import java.util.ArrayList;

public class Node {
	private ArrayList<Node> parents = new ArrayList<Node>();
	private ArrayList<Node> children = new ArrayList<Node>();
	private Object content = null;
	private int depth;
	
	public Node(ArrayList<Node> parents, ArrayList<Node> children, Object content){
		this.parents = parents;
		this.children = children;
		this.content = content;
		this.depth = -1;
	}
	
	public Node(Object content){
		this.content = content;
		this.depth = -1;
	}
	
	public Node(){
		this.depth = -1;
	}
	
	public void addChild(Node child){
		this.children.add(child);
	}
	
	public void setContent(Object content){
		this.content = content;
	}
	
	public void addParent(Node parent){
		this.parents.add(parent);
	}
	
	public ArrayList<Node> getChildren(){
		return this.children;
	}
	
	public ArrayList<Node> getParents(){
		return this.parents;
	}
	
	public Object getContent(){
		return this.content;
	}
	
	public void setDepth(int depth){
		this.depth = depth;
	}
	
	public int getDepth(){
		return this.depth;
	}
}
