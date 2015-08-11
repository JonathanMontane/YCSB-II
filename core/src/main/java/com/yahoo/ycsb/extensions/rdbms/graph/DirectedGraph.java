package com.yahoo.ycsb.extensions.rdbms.graph;

import java.util.ArrayList;
import java.util.HashMap;

public class DirectedGraph {
	
	private ArrayList<String> nodeNames;
	private HashMap<String, Node> nodemapper;
	
	public DirectedGraph(){
		this.nodemapper = new HashMap<String, Node>();
		this.nodeNames = new ArrayList<String>();
	}
	
	public void addNode(String name, Node node){
		this.nodemapper.put(name, node);
		this.nodeNames.add(name);
	}

	public void addEdge(String parent, String child){
		this.nodemapper.get(child).addParent(this.nodemapper.get(parent));
		this.nodemapper.get(parent).addChild(this.nodemapper.get(child));
	}
	
	public void addEdgeAndNodesIfNotExist(String parent, Object parentContent, String child, Object childContent){
		Node parentNode = this.nodemapper.get(parent);
		Node childNode = this.nodemapper.get(child);
		
		if(parentNode == null){
			parentNode = new Node(parentContent);
			this.nodeNames.add(parent);
		}
		if(childNode == null){
			childNode = new Node(childContent);
			this.nodeNames.add(child);
		}
		
		parentNode.addChild(childNode);
		childNode.addParent(parentNode);
		
		this.nodemapper.put(child, childNode);
		this.nodemapper.put(parent, parentNode);
	}
	
	public ArrayList<Node> findLeaves(){
		ArrayList<Node> leaves = new ArrayList<Node>();
		for(String name : this.nodeNames){
			if(this.nodemapper.get(name).getChildren() == null){
				leaves.add(this.nodemapper.get(name));
			}
		}
		return leaves;
	}
	
	public void evalDepth(Node root){
		if(root.getDepth() != -1) return;
		if(root.getChildren() == null){
			root.setDepth(0);
			return;
		}
		ArrayList<Node> children = root.getChildren();
		int depthChildren = -1;
		for(Node node : children){
			if(node.getDepth() < 0) this.evalDepth(node);
			if(depthChildren > node.getDepth() || depthChildren == -1) depthChildren = node.getDepth();
		}
		
		root.setDepth(depthChildren+1);
		return;
	}
	
	public ArrayList<Node> generateBottomTopOrder(){
		ArrayList<Node> ordered = new ArrayList<Node>();
		HashMap<Integer, ArrayList<Node>> stacks = new HashMap<Integer, ArrayList<Node>>();
		int max = -1;
		
		for(String name : this.nodeNames){
			Node node = this.nodemapper.get(name);
			ArrayList<Node> stack = stacks.get(node.getDepth());
			if(stack == null) stack = new ArrayList<Node>();
			stack.add(node);
			stacks.put(node.getDepth(), stack);
			if(max < node.getDepth()) max = node.getDepth();
		}
		
		for(int i = 0 ; i <= max ; i++){
			ordered.addAll(stacks.get(i));
		}
		
		return ordered;
	}
	
	public HashMap<String,Node> getNodeMapper(){
		return this.nodemapper;
	}
	
	public ArrayList<String> getNodeNames(){
		return this.nodeNames;
	}
}
