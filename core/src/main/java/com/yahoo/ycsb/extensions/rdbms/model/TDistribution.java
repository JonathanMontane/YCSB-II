package com.yahoo.ycsb.extensions.rdbms.model;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.yahoo.ycsb.generator.Generator;

@XmlRootElement
public class TDistribution extends Generator{
	private GeneratorElement generator;
	private ArrayList<Mode> modes;
	private HashMap<String, Mode> modemapper = new HashMap<String, Mode>();
	
	@XmlElement(name="generator")
	public void setGenerator(GeneratorElement generator){
		this.generator = generator;
	}
	
	public GeneratorElement getGeneratorElement(){
		return this.generator;
	}
	
	@XmlElementWrapper(name="modes")
	@XmlElement(name="mode")
	public void setModes(ArrayList<Mode> _modes){
		this.modes= _modes;
		for(Mode _mode : _modes){
			modemapper.put(_mode.getType(), _mode);
		}
	}
	
	public ArrayList<Mode> getModes(){
		return this.modes;
	}

	public Mode getModeByType(String type){
		return modemapper.get(type);
	}
	
	@Override
	public String next(HashMap<String, String> row) {
		return this.generator.next(row);
	}

	@Override
	public String current(HashMap<String, String> row) {
		// TODO Auto-generated method stub
		return this.generator.current(row);
	}

	@Override
	public String lastString() {
		// TODO Auto-generated method stub
		return this.generator.lastString();
	}

	@Override
	public String nextString() {
		// TODO Auto-generated method stub
		return this.generator.nextString();
	}
}
