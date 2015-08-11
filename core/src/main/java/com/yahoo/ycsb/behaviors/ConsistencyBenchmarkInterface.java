package com.yahoo.ycsb.behaviors;

import java.util.HashMap;
import java.util.HashSet;

public class ConsistencyBenchmarkInterface {

	public String selectExperimentOutputTable(String experimentname) {
		// TODO Auto-generated method stub
		return null;
	}

	public synchronized String getBehavior(String behavior) {
		// TODO Auto-generated method stub
		return null;
	}

	public String selectActionForExperimentOutput(String outputtable) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getOutputKeyForExperiment(String experimentname, String action) {
		// TODO Auto-generated method stub
		return null;
	}

	public HashSet<String> getReadOutputFields(String experimentname) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getScanLength(String experimentname) {
		// TODO Auto-generated method stub
		return 0;
	}

	public HashSet<String> getScanOutputFields(String experimentname) {
		// TODO Auto-generated method stub
		return null;
	}

	public String selectExperimentInputTable(String experimentname) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNextKeyForInputTable(String experimentname, String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public HashSet<String> getReadInputFields(String experimentname) {
		// TODO Auto-generated method stub
		return null;
	}

	public HashMap<String, String> buildUpdateRowForExperiment(
			String inputtable, String key) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getInputTableKey(String experimentname, String inputtable) {
		// TODO Auto-generated method stub
		return null;
	}

}
