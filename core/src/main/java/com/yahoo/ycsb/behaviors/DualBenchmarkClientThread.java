package com.yahoo.ycsb.behaviors;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
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
import com.yahoo.ycsb.reworked.Client;
import com.yahoo.ycsb.reworked.UnknownDBException;

/*
 * WARNING : This class assumes self-managed keys. Incrementation should be handled by the key generator
 */
public class DualBenchmarkClientThread extends ClientThread {

	private final static String SPLIT_THREAD_COUNT = "testthreadcount";
	private final static String SPLIT_THREAD_COUNT_DEFAULT = "0";

	public static final String ENVIRONMENT_CLASS_PROPERTY="firstclass";
	public static final String ENVIRONMENT_CLASS_PROPERTY_DEFAULT="com.yahoo.ycsb.behaviors.MaterializedViewsBenchmarkClientThread";

	public static final String TEST_CLASS_PROPERTY="secondclass";
	public static final String TEST_CLASS_PROPERTY_DEFAULT="com.yahoo.ycsb.behaviors.MaterializedViewsBenchmarkClientThread";

	
	private ConsistencyBenchmarkInterface _es_workload;

	private HashMap<String, String> _previous_row;
	private int _splitthreadcount;
	private ClientThread _innerthread;

	public DualBenchmarkClientThread(DB db, Workload workload,
			int threadid, int threadcount, Properties props, int opcount,
			double targetperthreadperms) {
		super(db, workload, threadid, threadcount, props, opcount,
				targetperthreadperms);
		
		ClassLoader classLoader = Client.class.getClassLoader();
		Class<?> ctclass;
		
		try{
			this._splitthreadcount = Integer.parseInt(props.getProperty(SPLIT_THREAD_COUNT, SPLIT_THREAD_COUNT_DEFAULT));
		} catch (NumberFormatException e){
			e.printStackTrace();
			e.printStackTrace(System.out);
			System.exit(0);
		}
		
		try {
			if(threadid < _splitthreadcount){
				ctclass = classLoader.loadClass(props.getProperty(TEST_CLASS_PROPERTY,TEST_CLASS_PROPERTY_DEFAULT));	
			} else {
				ctclass = classLoader.loadClass(props.getProperty(ENVIRONMENT_CLASS_PROPERTY,ENVIRONMENT_CLASS_PROPERTY_DEFAULT));	
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			e.printStackTrace(System.out);
			System.exit(0);
		}
		
		try
		{
			if(threadid < _splitthreadcount){
				this._innerthread=(ClientThread)ctclass.getConstructor(ClientThread.getConstructorTypes()).newInstance(
							db,
							workload,
							threadid,
							_splitthreadcount,
							props,
							opcount,
							targetperthreadperms
						);	
			} else {
				this._innerthread=(ClientThread)ctclass.getConstructor(ClientThread.getConstructorTypes()).newInstance(
							db,
							workload,
							threadid-_splitthreadcount,
							threadcount-_splitthreadcount,
							props,
							opcount,
							targetperthreadperms
						);	
			}			
		}
		catch (UnknownDBException e)
		{
			System.out.println("Unknown DB "+dbname);
			System.exit(0);
		} catch (InstantiationException e) {
			e.printStackTrace();
			e.printStackTrace(System.out);
			System.exit(0);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			e.printStackTrace(System.out);
			System.exit(0);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			e.printStackTrace(System.out);
			System.exit(0);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			e.printStackTrace(System.out);
			System.exit(0);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			e.printStackTrace(System.out);
			System.exit(0);
		} catch (SecurityException e) {
			e.printStackTrace();
			e.printStackTrace(System.out);
			System.exit(0);
		}
	}

	@Override
	public void run() {
		this._innerthread.run();
	}

	@Override
	public int getOpsDone()
	{
		return this._innerthread.getOpsDone();
	}
}