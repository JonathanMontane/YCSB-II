package com.yahoo.ycsb.behaviors;

import java.util.Properties;

import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.Workload;

public class ClientThread extends Thread
{
	DB _db;
	Workload _workload;
	int _opcount;
	double _target;

	int _opsdone;
	int _threadid;
	int _threadcount;
	Object _workloadstate;
	Properties _props;


	/**
	 * Constructor.
	 * 
	 * @param db the DB implementation to use
	 * @param dotransactions true to do transactions, false to insert data
	 * @param workload the workload to use
	 * @param threadid the id of this thread 
	 * @param threadcount the total number of threads 
	 * @param props the properties defining the experiment
	 * @param opcount the number of operations (transactions or inserts) to do
	 * @param targetperthreadperms target number of operations per thread per ms
	 */
	public ClientThread(DB db, Workload workload, int threadid, int threadcount, Properties props, int opcount, double targetperthreadperms)
	{
		//TODO: consider removing threadcount and threadid
		_db=db;
		_workload=workload;
		_opcount=opcount;
		_opsdone=0;
		_target=targetperthreadperms;
		_threadid=threadid;
		_threadcount=threadcount;
		_props=props;
	}

	public static Class<?>[] getConstructorTypes(){
		Class<?>[] types = {DB.class, Workload.class, int.class, int.class, Properties.class, int.class, double.class};
		return types;
	}
	
	public int getOpsDone()
	{
		return _opsdone;
	}

	@Override
	public void run(){
	}
}