package com.yahoo.ycsb.behaviors;

import java.util.Properties;

import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.DBException;
import com.yahoo.ycsb.Utils;
import com.yahoo.ycsb.Workload;
import com.yahoo.ycsb.WorkloadException;

public class DefaultLoadClientThread extends ClientThread {

	public DefaultLoadClientThread(
	    DB db,
	    Workload workload,
	    int threadid,
	    int threadcount,
	    Properties props,
	    int opcount,
	    double targetperthreadperms) {

		super(db, workload, threadid, threadcount, props, opcount, targetperthreadperms);
		// TODO Auto-generated constructor stub
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

		try {
			_workloadstate = _workload.initThread(_props, _threadid, _threadcount);
		} catch (WorkloadException e) {
			e.printStackTrace();
			e.printStackTrace(System.out);
			return;
		}

		//spread the thread operations out so they don't all hit the DB at the same time
		try {
			//GH issue 4 - throws exception if _target>1 because random.nextInt argument must be >0
			//and the sleep() doesn't make sense for granularities < 1 ms anyway
			if ( (_target > 0) && (_target <= 1.0) ) {
				sleep(Utils.random().nextInt((int)(1.0 / _target)));
			}
		} catch (InterruptedException e) {
			// do nothing.
		}

		try {
			long st = System.currentTimeMillis();

			while (((_opcount == 0) || (_opsdone < _opcount)) && !_workload.isStopRequested()) {

				if (!_workload.doInsert(_db, _workloadstate)) {
					break;
				}

				_opsdone++;

				//throttle the operations
				if (_target > 0) {
					//this is more accurate than other throttling approaches we have tried,
					//like sleeping for (1/target throughput)-operation latency,
					//because it smooths timing inaccuracies (from sleep() taking an int,
					//current time in millis) over many operations
					while (System.currentTimeMillis() - st < ((double)_opsdone) / _target) {
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
}
