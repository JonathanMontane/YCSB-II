package com.yahoo.ycsb.behaviors;

import java.util.ArrayList;

public interface MultipleCF {

	byte[] getColumnFamily(String table, String column);
	ArrayList<byte[]> getColumnFamilies();
}
