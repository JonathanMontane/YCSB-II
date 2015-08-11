package com.yahoo.ycsb.behaviors;


import java.util.HashMap;
import java.util.HashSet;

public interface MultipleTablesBenchmarkInterface {

	String getNextTable(HashMap<String, String> _previous_row);

	String getActionForTable(String tablename);

	HashSet<String> getReadFields(String tablename);

	String getNextKeyForTable(String tablename, String action);

	String getTableKey(String tablename);

	HashMap<String, String> buildRow(String tablename);

	HashMap<String, String> buildUpdateRow(String tablename);

	String getScanRange(String tablename);

	HashSet<String> getScanFields(String tablename);

}
