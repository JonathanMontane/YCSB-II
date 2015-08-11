package com.yahoo.ycsb.extensions.rdbms.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TestEnvironment{
	private String testEnvironmentName;
	private ArrayList<Table> input;
	private ArrayList<Table> output;

	@XmlAttribute(name="name")
	public void setTestEnvironmentName(String name){
		this.testEnvironmentName = name;
	}

	public String getTestEnvironmentName(){
		return this.testEnvironmentName;
	}

	public ArrayList<Table> getInput() {
		return input;
	}

	@XmlElementWrapper(name="inputs")
	@XmlElement(name="table")
	public void setInput(ArrayList<Table> input) {
		this.input = input;
	}

	public ArrayList<Table> getOutput() {
		return output;
	}

	@XmlElementWrapper(name="outputs")
	@XmlElement(name="table")
	public void setOutput(ArrayList<Table> output) {
		this.output = output;
	}
}