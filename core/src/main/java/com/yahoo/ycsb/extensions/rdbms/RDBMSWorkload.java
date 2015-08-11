package com.yahoo.ycsb.extensions.rdbms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.codec.binary.Base64;

import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.Client;
import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.RandomByteIterator;
import com.yahoo.ycsb.StringByteIterator;
import com.yahoo.ycsb.Utils;
import com.yahoo.ycsb.Workload;
import com.yahoo.ycsb.WorkloadException;
import com.yahoo.ycsb.behaviors.ClientThread;
import com.yahoo.ycsb.behaviors.MultipleTablesBenchmarkInterface;
import com.yahoo.ycsb.behaviors.MultipleTablesLoadInterface;
import com.yahoo.ycsb.behaviors.MultipleCF;
import com.yahoo.ycsb.extensions.rdbms.model.Column;
import com.yahoo.ycsb.extensions.rdbms.model.Parameter;
import com.yahoo.ycsb.extensions.rdbms.model.Range;
import com.yahoo.ycsb.extensions.rdbms.model.Table;
import com.yahoo.ycsb.extensions.rdbms.model.XMLProperties;
import com.yahoo.ycsb.generator.ConstantIntegerGenerator;
import com.yahoo.ycsb.generator.CounterGenerator;
import com.yahoo.ycsb.generator.DiscreteGenerator;
import com.yahoo.ycsb.generator.ExponentialGenerator;
import com.yahoo.ycsb.generator.Generator;
import com.yahoo.ycsb.generator.HistogramGenerator;
import com.yahoo.ycsb.generator.HotspotIntegerGenerator;
import com.yahoo.ycsb.generator.IntegerGenerator;
import com.yahoo.ycsb.generator.ScrambledZipfianGenerator;
import com.yahoo.ycsb.generator.SkewedLatestGenerator;
import com.yahoo.ycsb.generator.UniformIntegerGenerator;
import com.yahoo.ycsb.generator.ZipfianGenerator;
import com.yahoo.ycsb.measurements.Measurements;

public class RDBMSWorkload extends Workload implements
		MultipleTablesLoadInterface, MultipleCF,
		MultipleTablesBenchmarkInterface {

	public static final String XML_READER_PROPERTY = "xmlreaderclass";
	public static final String XML_READER_PROPERTY_DEFAULT = "com.yahoo.ycsb.extensions.rdbms.XMLPropertiesReader";

	private HashMap<String, byte[]> _cfs = new HashMap<String, byte[]>();
	private ArrayList<byte[]> _columnfamilies = new ArrayList<byte[]>();

	// Added part
	XMLProperties xml_properties;

	// TODO Partly Done
	IntegerGenerator keysequence;

	// TODO Done
	DiscreteGenerator operationchooser;

	// TODO Done
	DiscreteGenerator tablechooser;

	IntegerGenerator keychooser;

	// TODO Done
	Generator fieldchooser;

	CounterGenerator transactioninsertkeysequence;

	IntegerGenerator scanlength;

	boolean orderedinserts;

	int recordcount;

	private HashMap<String, Integer> tablerangesmap;

	/**
	 * Initialize the scenario. Called once, in the main client thread, before
	 * any operations are started.
	 */
	public void init(Properties p) throws WorkloadException {

		ClassLoader classLoader = Client.class.getClassLoader();
		try {
			Class<?> xmlReaderClass = classLoader.loadClass(p.getProperty(
					XML_READER_PROPERTY, XML_READER_PROPERTY_DEFAULT));
			XMLPropertiesReader xmlReader = (XMLPropertiesReader) xmlReaderClass
					.getConstructor(Properties.class).newInstance(p);
			this.xml_properties = xmlReader.getProperties();
			ArrayList<Range> ranges = this.xml_properties.getLoad()
					.getTableRanges();
			this.tablerangesmap = new HashMap<String, Integer>();
			for (Range range : ranges) {
				this.tablerangesmap.put(range.getTableName(), range.getLimit());
			}

			Set<byte[]> cf_set = new HashSet<byte[]>();

			ArrayList<Table> _tables = this.xml_properties.getStructure()
					.getTables();
			for (Table _table : _tables) {
				ArrayList<Column> _cols = _table.getColumns();
				for (Column _col : _cols) {
					ArrayList<Parameter> _params = _col.getParameters();
					for (Parameter _param : _params) {
						if (_param.getName().equals("columnFamily")) {
							_cfs.put(_table.getName() + ":::" + _col.getName(),
									_param.getValue().getBytes());
							cf_set.add(_param.getValue().getBytes());
						}
					}
				}
			}

			_columnfamilies.addAll(cf_set);
		} catch (Exception e) {
			e.printStackTrace();
			e.printStackTrace(System.out);
			System.exit(0);
		}

	}

	public XMLProperties getProperties() {
		return this.xml_properties;
	}

	@Override
	public boolean doInsert(DB arg0, Object arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doTransaction(DB arg0, Object arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public HashMap<String, Integer> getTableRanges() {
		return this.tablerangesmap;
	}

	@Override
	public Boolean hasUnboundedLoadTables() {
		return this.xml_properties.getLoad().getAccessDistribution() != null;
	}

	@Override
	public String getNextUnboundedLoadTable(HashMap<String, String> row) {
		if (this.hasUnboundedLoadTables()) {
			return this.xml_properties.getLoad().getAccessDistribution()
					.next(row);
		} else {
			return null;
		}
	}

	public String getTableKey(String tablename) {
		return this.xml_properties.getStructure().getTable(tablename).getKey()
				.getName();
	}

	@Override
	public HashMap<String, String> buildRow(String tablename) {

		HashMap<String, String> row = new HashMap<String, String>();
		Table table = this.xml_properties.getLoad().getTable(tablename);
		ArrayList<Column> columns = table.getOrderedColumns();

		for (Column col : columns) {
			row.put(col.getName(), col.getDistribution().next(row));
		}

		return row;
	}

	@Override
	public byte[] getColumnFamily(String table, String column) {
		// TODO Auto-generated method stub
		String key = table + ":::" + column;
		return _cfs.get(key);
	}

	@Override
	public ArrayList<byte[]> getColumnFamilies() {
		// TODO Auto-generated method stub
		return _columnfamilies;
	}

	@Override
	public String getNextTable(HashMap<String, String> _previous_row) {
		return this.xml_properties.getBenchmark().getAccessDistribution()
				.getGeneratorElement().next(_previous_row);
	}

	@Override
	public String getActionForTable(String tablename) {
		Table table = this.xml_properties.getBenchmark().getTableByName(
				tablename);
		return this.xml_properties.getBenchmark().getTableByName(tablename)
				.getTDistribution().next(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public HashSet<String> getReadFields(String tablename) {
		// TODO Auto-generated method stub
		// Warning. returning null means reading all fields
		HashSet<String> fields = null;
		String serial = this.xml_properties.getBenchmark()
				.getTableByName(tablename).getTDistribution()
				.getModeByType("read").getCDistribution().next(null);

		ByteArrayInputStream bi = new ByteArrayInputStream(Base64.decodeBase64(serial));
		ObjectInputStream si;
		try {
			si = new ObjectInputStream(bi);
			fields = (HashSet<String>) si.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fields;
	}

	@Override
	public String getNextKeyForTable(String tablename, String action) {
		return this.xml_properties.getBenchmark().getTableByName(tablename)
				.getTDistribution().getModeByType(action).getKDistribution()
				.next(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public HashMap<String, String> buildUpdateRow(String tablename) {
		// TODO Auto-generated method stub
		HashSet<String> fields = null;
		String serial = this.xml_properties.getBenchmark()
				.getTableByName(tablename).getTDistribution()
				.getModeByType("update").getCDistribution().next(null);

		ByteArrayInputStream bi = new ByteArrayInputStream(Base64.decodeBase64(serial));
		ObjectInputStream si;
		try {
			si = new ObjectInputStream(bi);
			fields = (HashSet<String>) si.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HashMap<String, String> row = new HashMap<String, String>();
		for (String colName : fields) {
			row.put(colName,
					this.xml_properties.getBenchmark()
							.getTableByName(tablename).getColumnByName(colName)
							.getDistribution().next(row));
		}

		return row;
	}

	@Override
	public HashSet<String> getScanFields(String tablename) {
		HashSet<String> fields = null;
		String serial = this.xml_properties.getBenchmark()
				.getTableByName(tablename).getTDistribution()
				.getModeByType("scan").getCDistribution().next(null);

		ByteArrayInputStream bi = new ByteArrayInputStream(serial.getBytes());
		ObjectInputStream si;
		try {
			si = new ObjectInputStream(bi);
			fields = (HashSet<String>) si.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fields;
	}

	@Override
	public String getScanRange(String tablename) {
		// TODO Auto-generated method stub
		return this.xml_properties.getBenchmark().getTableByName(tablename)
				.getTDistribution().getModeByType("scan").getKDistribution()
				.next(null);
	}
}
