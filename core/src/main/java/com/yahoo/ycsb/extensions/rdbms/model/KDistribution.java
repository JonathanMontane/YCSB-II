package com.yahoo.ycsb.extensions.rdbms.model;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.yahoo.ycsb.generator.Generator;

@XmlRootElement
public class KDistribution extends Generator{
	private GeneratorElement generator;
	
	@XmlElement(name="generator")
	public void setGenerator(GeneratorElement generator){
		this.generator = generator;
	}
	
	public GeneratorElement getGeneratorElement(){
		return this.generator;
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
