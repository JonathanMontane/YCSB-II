package com.yahoo.ycsb.behaviors;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Vector;

import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.DBException;
import com.yahoo.ycsb.StringByteIterator;
import com.yahoo.ycsb.Utils;
import com.yahoo.ycsb.Workload;

/*
 * WARNING : This class assumes self-managed keys. Incrementation should be handled by the key generator
 */
public class ConsistencyBenchmarkClientThread extends ClientThread {

	private static final String EXPERIMENT_NAME = "experimentname";

	private static final String EXPERIMENT_NAME_DEFAULT = "test";

	private ConsistencyBenchmarkInterface _es_workload;

	private HashMap<String, String> _previous_row;

	private int _opstodo;

	private String behavior = null;
	private String experimentname;

	public ConsistencyBenchmarkClientThread(DB db, Workload workload,
			int threadid, int threadcount, Properties props, int opcount,
			double targetperthreadperms) {
		super(db, workload, threadid, threadcount, props, opcount,
				targetperthreadperms);

		this._es_workload = (ConsistencyBenchmarkInterface) workload;
		this._previous_row = null;
		this._opstodo = (_opcount*(_threadid +1)/_threadcount - _opcount*_threadid/_threadcount);

		this.experimentname = props.getProperty(EXPERIMENT_NAME, EXPERIMENT_NAME_DEFAULT);
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
				done = done || checkIfDone();
				_opsdone++;

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
		return _opsdone >= (_opcount*(_threadid +1)/_threadcount - _opcount*_threadid/_threadcount);
	}

	@SuppressWarnings("unchecked")
	public Boolean doTransaction() {

		this.behavior = this._es_workload.getBehavior(this.behavior);

		if(this.behavior.compareTo("WAIT") == 0){
			doWait();
			return null;
		}

		if(this.behavior.compareTo("GENERATE") == 0){
			//TODO stuff
			doGenerate();
			return null;
		}

		if(this.behavior.compareTo("CHECK") == 0){
			//TODO stuff
			doCheck();
			return null;
		}
	}

	private Boolean doCheck() {
		// TODO Auto-generated method stub
		String outputtable = this._es_workload.selectExperimentOutputTable(this.experimentname);
		String action = this._es_workload.selectActionForExperimentOutput(outputtable);
		String key = this._es_workload.getOutputKeyForExperiment(this.experimentname, action);
		if (action.equals("read")) {
			// TODO
			HashSet<String> fields = this._es_workload.getReadOutputFields(this.experimentname);
			HashMap<String, Byterator> read = new HashMap<String, ByteIterator>();

			if(this._db.read(outputtable, key, fields, read) == 0){
				return this._es_workload.compareReadToExpected(read);
			} else {
				return false;
			}
		} else if (action.equals("scan")) {
			// TODO
			int length = this._es_workload.getScanLength(this.experimentname);
			HashSet<String> fields = this._es_workload.getScanOutputFields(this.experimentname);
			Vector<HashMap<String, ByteIterator>> scan = new Vector<HashMap<String, ByteIterator>>();
			if(this._db.scan(outputtable, key, length, fields, scan) == 0){
				return this._es_workload.compareScanToExpected(scan);
			} else {
				return false;
			}
		} else {
			// TODO unrecognized action type
			return false;
		}
	}

	private Boolean doGenerate() {
		// TODO Auto-generated method stub
		String inputtable = this._es_workload.selectExperimentInputTable(this.experimentname);
		String key = this._es_workload.getNextKeyForInputTable(this.experimentname, "read");
		HashSet<String> fields = this._es_workload.getReadInputFields(this.experimentname);

		HashMap<String, Byterator> read = new HashMap<String, ByteIterator>();

		if(this._db.read(inputtable, key, fields, read) == 0){
			HashMap<String, String> row = this._es_workload.buildUpdateRowForExperiment(inputtable, key);

			String keyName = this._es_workload.getInputTableKey(this.experimentname, inputtable);
			row.remove(keyName);
			HashMap<String, ByteIterator> update = convertRow(row);
			this.behavior = this._es_workload.setCurrentAndUpdateData(key, read, update);
			if (this._db.update(inputtable, key, update) == 0) {
				return true;
			} else {
				//TODO if failure, update CurrentAndUpdateData.
				return false;
			}

		} else {
			//TODO if failure, release lock
			return false;
		}
	}

	private Boolean doWait() {
		// TODO Auto-generated method stub
		sleep(1);
		return null;
	}

	private HashMap<String, ByteIterator> convertRow(HashMap<String, String> row) {
		// TODO Auto-generated method stub
		HashMap<String, ByteIterator> data = new HashMap<String, ByteIterator>();
		for (String key : row.keySet()) {
			data.put(key, new StringByteIterator(row.get(key)));
		}
		return data;
	}
}
