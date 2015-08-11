package com.yahoo.ycsb.extensions.rdbms.model;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.yahoo.ycsb.Client;
import com.yahoo.ycsb.generator.Generator;

@XmlRootElement(name = "generator")
public class GeneratorElement extends Generator {
	private String source = null;
	private ArrayList<Input> inputs = null;

	private static ClassLoader classLoader = Client.class.getClassLoader();
	private Generator generator = null;

	public String getSource() {
		return this.source;
	}

	@XmlElement(name = "source")
	public void setSource(String source) throws ClassNotFoundException,
			NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		this.source = source;
		if (this.inputs != null && this.source != null)
			this.setup();
	}

	public ArrayList<Input> getInputs() {
		return this.inputs;
	}

	@XmlElementWrapper(name = "inputs")
	@XmlElement(name = "input")
	public void setInputs(ArrayList<Input> inputs)
			throws ClassNotFoundException, NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		this.inputs = inputs;
		if (this.inputs != null && this.source != null)
			this.setup();
	}

	private Object[] extractValuesFromInputs(ArrayList<Input> inputs) {
		Object[] values = new String[inputs.size()];

		for (int i = 0, l = inputs.size(); i < l; i++) {
			values[i] = inputs.get(i).getValue();
		}

		return values;
	}

	private void setup() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{

		Class<?>[] parameters = new Class<?>[this.inputs.size()];
		Arrays.fill(parameters, String.class);

		Object[] values = this.extractValuesFromInputs(this.inputs);

		Class<?> classType = classLoader.loadClass(this.source);

		
			try {
				this.generator = (Generator) classType.getConstructor(parameters)
						.newInstance(values);
			} catch (InstantiationException e) {
				this.generator = (Generator) classType.getConstructor()
						.newInstance();
			} catch (IllegalAccessException e) {
				this.generator = (Generator) classType.getConstructor()
						.newInstance();
			} catch (IllegalArgumentException e) {
				this.generator = (Generator) classType.getConstructor()
						.newInstance();
			} catch (InvocationTargetException e) {
				this.generator = (Generator) classType.getConstructor()
						.newInstance();
			} catch (NoSuchMethodException e) {
				this.generator = (Generator) classType.getConstructor()
						.newInstance();
			} catch (SecurityException e) {
				this.generator = (Generator) classType.getConstructor()
						.newInstance();
			}
		
		Properties props = new Properties();

		for (Input input : this.inputs) {
			props.setProperty(input.getName(), input.getValue());
		}

		this.generator.setup(props);
	}

	public Generator getGenerator() {
		return this.generator;
	}

	@Override
	public String nextString() {
		// TODO Auto-generated method stub
		return this.generator.nextString();
	}

	@Override
	public String lastString() {
		// TODO Auto-generated method stub
		return this.generator.lastString();
	}

	@Override
	public String next(HashMap<String, String> row) {
		// TODO Auto-generated method stub
		return this.generator.next(row);
	}

	@Override
	public String current(HashMap<String, String> row) {
		// TODO Auto-generated method stub
		return this.generator.current(row);
	}
}
