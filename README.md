Yahoo! Cloud System Benchmark II (YCSB)
====================================

Getting Started
---------------

1. Clone the repository
    
2. Set up a database to benchmark. There is a README file under each binding 
   directory.

3. Run YCSB command. 
    
    ```sh
    bin/ycsb load basic -P workloads/workloada
    bin/ycsb run basic -P workloads/workloada
    ```

  Running the `ycsb` command without any argument will print the usage. 
   
  See https://github.com/brianfrankcooper/YCSB/wiki/Running-a-Workload
  for a detailed documentation on how to run a workload.

  See https://github.com/brianfrankcooper/YCSB/wiki/Core-Properties for 
  the list of available workload properties.

Improvement in YCSB II
---------------

Note:
  YCSB II is backward-compatible with YCSB I and supposedly any extension
  made on it.

1. New Workload:
    
    ```sh
    RDBMSWorkload
    ```


  RDBMSWorkload is a generic workload who supports multiple table setups, with
  a high degree of customization. It uses an XML file to describe the database
  and the way it should be populated by the client.

  found in core/src/com/yahoo/ycsb/extensions/rdbms

2. Introduction of the Behavior concept:
  
  The ClientThread class that was a private class of the Client class has been
  made public and abstract. Class extending the ClientThread object define
  the behavior of the client when accessing the database. For instance, the 
  behavior of sequential queries can be implemented. This has the benefits of
  allowing reuse of the behaviors between workloads, and to allow a workload
  to support multiple behaviors.

  found in core/src/com/yahoo/ycsb/behaviors

3. New Binding for HBase:

  Many bindings do not allow for cloud serving system specific customizations,
  such as the use of multiple column families for HBase. Additionally, the 
  abstract DB binding had no way to communicate with the workload on details
  of the implementation. We created an additional DB binding for HBase that
  supports multiple column families.

  By default, this Hbase binding is disabled, and the previous binding is
  enabled.

4. Consistency Benchmarking:

  Basic behaviors for consistency and delta-atomicity benchmarking have been 
  defined.