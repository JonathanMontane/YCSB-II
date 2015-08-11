package com.yahoo.ycsb.behaviors;


import java.util.ArrayList;
import java.util.HashMap;


public interface MultipleTablesLoadInterface {
	public HashMap<String, Integer> getTableRanges();
	
	public Boolean hasUnboundedLoadTables();
	public String getNextUnboundedLoadTable(HashMap<String, String> row);
	
	public HashMap<String, String> buildRow(String tablename);
	public String getTableKey(String tablename);
}
