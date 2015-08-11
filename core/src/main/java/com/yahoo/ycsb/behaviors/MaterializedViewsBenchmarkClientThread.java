package com.yahoo.ycsb.behaviors;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.codec.binary.Base64;

import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.DBException;
import com.yahoo.ycsb.StringByteIterator;
import com.yahoo.ycsb.Utils;
import com.yahoo.ycsb.Workload;

/*
 * WARNING : This class assumes self-managed keys. Incrementation should be handled by the key generator
 */
public class MaterializedViewsBenchmarkClientThread extends ClientThread {

	private MultipleTablesBenchmarkInterface _es_workload;

	private HashMap<String, String> _previous_row;

	public MaterializedViewsBenchmarkClientThread(DB db, Workload workload,
			int threadid, int threadcount, Properties props, int opcount,
			double targetperthreadperms) {
		super(db, workload, threadid, threadcount, props, opcount,
				targetperthreadperms);
		this._es_workload = (MultipleTablesBenchmarkInterface) workload;
		this._previous_row = null;
	}

	@Override
	public void run() {
		try {
			_db.init();
		} catch (DBException e) {
			e.printStackTrace();
			e.printStackTrace(System.out);
			return;
		}

		// spread the thread operations out so they don't all hit the DB at the
		// same time
		try {
			// GH issue 4 - throws exception if _target>1 because random.nextInt
			// argument must be >0
			// and the sleep() doesn't make sense for granularities < 1 ms
			// anyway
			if ((_target > 0) && (_target <= 1.0)) {
				sleep(Utils.random().nextInt((int) (1.0 / _target)));
			}
		} catch (InterruptedException e) {
			// do nothing.
		}

		try {
			long st = System.currentTimeMillis();
			Boolean done = false;

			while (!done && !_workload.isStopRequested()) {
				if (doTransaction() == false)
					done = true;
				_opsdone++;
				done = done || checkIfDone();


				if (_target > 0) {
					// this is more accurate than other throttling approaches we
					// have tried,
					// like sleeping for (1/target throughput)-operation
					// latency,
					// because it smooths timing inaccuracies (from sleep()
					// taking an int,
					// current time in millis) over many operations
					while (System.currentTimeMillis() - st < ((double) _opsdone)
							/ _target) {
						try {
							sleep(1);
						} catch (InterruptedException e) {
							// do nothing.
						}

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			e.printStackTrace(System.out);
			System.exit(0);
		}

		try {
			_db.cleanup();
		} catch (DBException e) {
			e.printStackTrace();
			e.printStackTrace(System.out);
			return;
		}
	}

	public Boolean checkIfDone() {
		return _opsdone >= _opcount;
	}

	@SuppressWarnings("unchecked")
	public Boolean doTransaction() {
		// TODO stuff
		String tablename = selectTable();
		String action = selectActionForTable(tablename);

		if (action.equals("read")) {
			return prepareReadForTable(tablename, action);
		} else if (action.equals("insert")) {
			return prepareInsertForTable(tablename, action);
		} else if (action.equals("update")) {
			return prepareUpdateForTable(tablename, action);
		} else if (action.equals("scan")) {
			return prepareScanForTable(tablename, action);
		} else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	private Boolean prepareScanForTable(String tablename, String action) {
		// TODO Auto-generated method stub
		String serial = this._es_workload.getScanRange(tablename);

		ByteArrayInputStream bi = new ByteArrayInputStream(Base64.decodeBase64(serial));
		ObjectInputStream si;
		try {
			si = new ObjectInputStream(bi);
			ArrayList<String> scanKeyAndRange = (ArrayList<String>) si
					.readObject();

			HashSet<String> fields = this._es_workload.getScanFields(tablename);
			if(this._db.scan(tablename, scanKeyAndRange.get(0),
					Integer.parseInt(scanKeyAndRange.get(1)), fields,
					new Vector<HashMap<String, ByteIterator>>()) == 0){
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private Boolean prepareUpdateForTable(String tablename, String action) {
		// TODO Auto-generated method stub
		HashMap<String, String> row = this._es_workload
				.buildUpdateRow(tablename);
	
		String key = this._es_workload.getNextKeyForTable(tablename, action);
		
		HashMap<String, ByteIterator> data = convertRow(row);
		if (this._db.update(tablename, key, data) == 0) {
			this._previous_row = (HashMap<String, String>) row.clone();
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	private Boolean prepareInsertForTable(String tablename, String action) {
		// TODO Auto-generated method stub
		HashMap<String, String> row = this._es_workload.buildRow(tablename);

		String keyName = this._es_workload.getTableKey(tablename);
		String key = row.remove(keyName);
		HashMap<String, ByteIterator> data = convertRow(row);
		if (this._db.insert(tablename, key, data) == 0) {
			this._previous_row = (HashMap<String, String>) row.clone();
			return true;
		} else {
			return false;
		}
	}

	private boolean prepareReadForTable(String tablename, String action) {
		String key = this._es_workload.getNextKeyForTable(tablename, action);
		HashSet<String> fields = this._es_workload.getReadFields(tablename);
		if(this._db.read(tablename, key, fields,
				new HashMap<String, ByteIterator>()) == 0){
			return true;
		} else {
			return false;
		}
	}

	private String selectActionForTable(String tablename) {
		return this._es_workload.getActionForTable(tablename);
	}

	private HashMap<String, ByteIterator> convertRow(HashMap<String, String> row) {
		// TODO Auto-generated method stub
		HashMap<String, ByteIterator> data = new HashMap<String, ByteIterator>();
		for (String key : row.keySet()) {
			data.put(key, new StringByteIterator(row.get(key)));
		}
		return data;
	}

	private String selectTable() {
		return this._es_workload.getNextTable(this._previous_row);
	}
}
