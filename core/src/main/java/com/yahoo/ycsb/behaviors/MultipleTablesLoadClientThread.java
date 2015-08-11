package com.yahoo.ycsb.behaviors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.DBException;
import com.yahoo.ycsb.StringByteIterator;
import com.yahoo.ycsb.Utils;
import com.yahoo.ycsb.Workload;

/*
 * WARNING : This class assumes self-managed keys. Incrementation should be handled by the key generator
 */
public class MultipleTablesLoadClientThread extends ClientThread{

	private MultipleTablesLoadInterface _es_workload;
	
	//_ctranges = Current Thread Ranges
	private HashMap<String, Integer> _ctranges;
	
	private HashMap<String, String> _previous_row;
	
	public MultipleTablesLoadClientThread(
			DB db,
			Workload workload,
			int threadid,
			int threadcount,
			Properties props,
			int opcount,
			double targetperthreadperms)
	{
		super(db, workload, threadid, threadcount, props, opcount, targetperthreadperms);
		this._es_workload = (MultipleTablesLoadInterface) workload;
		this._previous_row = null;
		this._ctranges = new HashMap<String, Integer>();
	}

	@Override
	public void run(){
		try
		{
			_db.init();
		}
		catch (DBException e)
		{
			e.printStackTrace();
			e.printStackTrace(System.out);
			return;
		}

		this.prepareClientThreadRanges();

		//spread the thread operations out so they don't all hit the DB at the same time
		try
		{
			//GH issue 4 - throws exception if _target>1 because random.nextInt argument must be >0
			//and the sleep() doesn't make sense for granularities < 1 ms anyway
			if ( (_target>0) && (_target<=1.0) ) 
			{
				sleep(Utils.random().nextInt((int)(1.0/_target)));
			}
		}
		catch (InterruptedException e)
		{
			// do nothing.
		}

		try
		{
			long st=System.currentTimeMillis();
			Boolean done = false;
			
			

			while(!done && !_workload.isStopRequested()){
				if(doInsert() == false){
					done=true;
				} else {
					done = checkIfDone();
				}
				_opsdone++;
				
				if (_target>0)
				{
					//this is more accurate than other throttling approaches we have tried,
					//like sleeping for (1/target throughput)-operation latency,
					//because it smooths timing inaccuracies (from sleep() taking an int, 
					//current time in millis) over many operations
					while (System.currentTimeMillis()-st<((double)_opsdone)/_target)
					{
						try
						{
							sleep(1);
						}
						catch (InterruptedException e)
						{
							// do nothing.
						}

					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			e.printStackTrace(System.out);
			System.exit(0);
		}

		try
		{
			_db.cleanup();
		}
		catch (DBException e)
		{
			e.printStackTrace();
			e.printStackTrace(System.out);
			return;
		}
	}
	
	public HashMap<String, Integer> getClientThreadRanges(){
		return this._ctranges;
	}

	public void prepareClientThreadRanges(){
		HashMap<String, Integer> tableranges = this._es_workload.getTableRanges();
		for(String key : tableranges.keySet()){
			//How many operations should be done by this thread on each table
			_ctranges.put(key,tableranges.get(key)*(_threadid+1)/_threadcount-(tableranges.get(key)*_threadid/_threadcount));
		}
	}
	
	public Boolean checkIfDone() {
		return this._ctranges.keySet().size() == 0 && this._es_workload.hasUnboundedLoadTables() == false;
	}

	@SuppressWarnings("unchecked")
	public Boolean doInsert(){
		//TODO stuff
		String tablename = selectTable();
		HashMap<String, String> row = this._es_workload.buildRow(tablename);
		this._previous_row = (HashMap<String, String>) row.clone();
		//this makes no sense to me right now
		//TODO change this to something correct
		String keyName = this._es_workload.getTableKey(tablename);
		String key = row.remove(keyName);
		HashMap<String, ByteIterator> data = convertRow(row);
		if(this._db.insert(tablename, key, data) == 0){
			Integer count = this._ctranges.get(tablename);
			if(count != null){
				if(count > 1) {
					this._ctranges.put(tablename, count-1);
				} else {
					this._ctranges.remove(tablename);
					System.out.println("this was called "+ this._ctranges.keySet().size());
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	private HashMap<String, ByteIterator> convertRow(HashMap<String, String> row) {
		// TODO Auto-generated method stub
		HashMap<String, ByteIterator> data = new HashMap<String, ByteIterator>();
		for(String key : row.keySet()){
			data.put(key, new StringByteIterator(row.get(key)));
		}
		return data;
	}
	
	private String selectTable(){
		for(String name : this._ctranges.keySet()){
			if(this._ctranges.get(name) > 0){
				return name;
			} else {
				this._ctranges.remove(name);
			}
		}
		return this._es_workload.getNextUnboundedLoadTable(this._previous_row);
	}
}
