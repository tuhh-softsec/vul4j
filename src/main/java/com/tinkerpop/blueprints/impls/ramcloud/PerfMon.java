package com.tinkerpop.blueprints.impls.ramcloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Singleton class
public final class PerfMon {
    private static final ThreadLocal<PerfMon> instance = new ThreadLocal<PerfMon>() {
	@Override
	protected PerfMon initialValue() {
	    return new PerfMon();
	}
    };

   public final long measureAllTimeProp = Long.valueOf(System.getProperty("benchmark.measureAll", "0"));
   private final static Logger log = LoggerFactory.getLogger(PerfMon.class);

   private static final int debug = 0;

   private long read_latency_sum;
   private long read_latency_cnt;
   private int read_flag;

   private long multiread_latency_sum;
   private long multiread_latency_cnt;
   private int multiread_flag;

   private long multiwrite_latency_sum;
   private long multiwrite_latency_cnt;
   private int multiwrite_flag;

   private long indexread_latency_sum;
   private long indexread_latency_cnt;
   private int indexread_flag;

   private long write_latency_sum;
   private long write_latency_cnt;

   private int write_flag;

   private long indexwrite_latency_sum;
   private long indexwrite_latency_cnt;
   private int indexwrite_flag;

   private long serialize_latency_sum;
   private long serialize_latency_cnt;
   private int ser_flag;

   private long indexserialize_latency_sum;
   private long indexserialize_latency_cnt;
   private int indexser_flag;

   private long proto_serialize_latency_sum;
   private long proto_serialize_latency_cnt;
   private int protoser_flag;

   private long deserialize_latency_sum;
   private long deserialize_latency_cnt;
   private int deser_flag;

   private long indexdeserialize_latency_sum;
   private long indexdeserialize_latency_cnt;
   private int indexdeser_flag;

   private long proto_deserialize_latency_sum;
   private long proto_deserialize_latency_cnt;
   private int protodeser_flag;

   private long addsw_time;
   private long addport_time;
   private long addlink_time;
   private long addport_cnt;
   private long addflowpath_time;
   private long addflowentry_time;
   private long addflowentry_cnt;

   private long ser_time;
   private long indexser_time;
   private long protoser_time;

   private long deser_time;
   private long indexdeser_time;
   private long protodeser_time;

   private long write_time;
   private long multiwrite_time;
   private long write_condfails;
   private long indexwrite_time;
   private long indexwrite_condfails;
   private long read_time;
   private long multiread_time;
   private long indexread_time;
   private long read_whole_topology_time;

   public static PerfMon getInstance() {
        return instance.get();
    }
   private PerfMon(){
   }

   private void clear(){
   	read_latency_sum=0L;
   	read_latency_cnt=0L;
	multiread_latency_sum=0L;
   	multiread_latency_cnt=0L;
	multiwrite_latency_sum=0L;
   	multiwrite_latency_cnt=0L;
   	indexread_latency_sum=0L;
   	indexread_latency_cnt=0L;
	write_latency_sum=0L;
   	write_latency_cnt=0L;
	indexwrite_latency_sum=0L;
   	indexwrite_latency_cnt=0L;
   	serialize_latency_sum=0L;
   	serialize_latency_cnt=0L;
	indexserialize_latency_sum=0L;
   	indexserialize_latency_cnt=0L;
   	deserialize_latency_sum=0L;
   	deserialize_latency_cnt=0L;
   	indexdeserialize_latency_sum=0L;
   	indexdeserialize_latency_cnt=0L;
	read_flag=multiread_flag=indexread_flag=write_flag=indexwrite_flag=deser_flag=indexdeser_flag=ser_flag=indexser_flag=0;
	addflowpath_time = addflowentry_time = addflowentry_cnt = 0;
	write_condfails = indexwrite_condfails = 0;
	read_whole_topology_time = 0;
	protoser_time = protodeser_time = 0;
	proto_deserialize_latency_cnt = proto_deserialize_latency_sum = proto_serialize_latency_cnt = proto_serialize_latency_sum = 0;
        //log.error("flag cleared");
   }

   private long getSum(){
       return read_latency_sum + multiread_latency_sum + indexread_latency_sum + write_latency_sum + indexwrite_latency_sum + serialize_latency_sum + indexserialize_latency_sum + deserialize_latency_sum + indexdeserialize_latency_sum;
   }

   public void addswitch_start(){
        if(measureAllTimeProp==0)
		return;

	clear();
	addsw_time = System.nanoTime();
   }
   public void addswitch_end(){
        if(measureAllTimeProp==0)
		return;

        long delta;
        long sum;

        delta = System.nanoTime() - addsw_time;
	sum = getSum();
        log.error("Performance add_switch {}"
        	+ " read {} ({})"
        	+ " multiread {} ({})"
        	+ " index_read {} ({})"
        	+ " write {} ({})"
        	+ " multiwrite {} ({})"
        	+ " index_write {} ({})"
        	+ " serialize {} ({})"
        	+ " indexserialize {} ({})"
        	+ " proto_serialize {} ({})"
        	+ " deserialize {} ({})"
        	+ " indexdeserialize {} ({})"
        	+ " proto_deserialize {} ({})"
        	+ " rwsd total {} other {} ({})"
        	+ " writefail ({}) indexwritefail({})",
	       delta,
	       read_latency_sum, read_latency_cnt,
	       multiread_latency_sum, multiread_latency_cnt,
	       indexread_latency_sum, indexread_latency_cnt,
	       write_latency_sum, write_latency_cnt,
	       multiwrite_latency_sum, multiwrite_latency_cnt,
	       indexwrite_latency_sum, indexwrite_latency_cnt,
	       serialize_latency_sum, serialize_latency_cnt,
	       indexserialize_latency_sum, indexserialize_latency_cnt,
	       proto_serialize_latency_sum, proto_serialize_latency_cnt,
	       deserialize_latency_sum, deserialize_latency_cnt,
	       indexdeserialize_latency_sum, indexdeserialize_latency_cnt,
	       proto_deserialize_latency_sum, proto_deserialize_latency_cnt,
	       sum, delta - sum, (delta - sum) * 100.0 / (delta),
	       write_condfails, indexwrite_condfails);
   }
   public void addport_start(){
        if(measureAllTimeProp==0)
		return;
	clear();
        addport_cnt = 0;
	addport_time = System.nanoTime();
   }
   public void addport_incr(){
        if(measureAllTimeProp==0)
		return;
        addport_cnt ++;
   }
   public void addport_end(){
        if(measureAllTimeProp==0)
		return;
        long delta;
        long sum;
        delta = System.nanoTime() - addport_time;
	sum = getSum();
        log.error("Performance add_port {} ( {} ports )"
        	+ " read {} ({})"
        	+ " multiread {} ({})"
        	+ " index_read {} ({})"
        	+ " write {} ({})"
        	+ " multiwrite {} ({})"
        	+ " index_write {} ({})"
        	+ " serialize {} ({})"
        	+ " indexserialize {} ({})"
        	+ " proto_serialize {} ({})"
        	+ " deserialize {} ({})"
        	+ " indexdeserialize {} ({})"
        	+ " proto_deserialize {} ({})"
        	+ " rwsd total {} other {} ({})"
        	+ " writefail ({}) indexwritefail({})",
	       delta, addport_cnt,
	       read_latency_sum, read_latency_cnt,
	       multiread_latency_sum, multiread_latency_cnt,
	       indexread_latency_sum, indexread_latency_cnt,
	       write_latency_sum, write_latency_cnt,
	       multiwrite_latency_sum, multiwrite_latency_cnt,
	       indexwrite_latency_sum, indexwrite_latency_cnt,
	       serialize_latency_sum, serialize_latency_cnt,
	       indexserialize_latency_sum, indexserialize_latency_cnt,
	       proto_serialize_latency_sum, proto_serialize_latency_cnt,
	       deserialize_latency_sum, deserialize_latency_cnt,
	       indexdeserialize_latency_sum, indexdeserialize_latency_cnt,
	       proto_deserialize_latency_sum, proto_deserialize_latency_cnt,
	       sum, delta - sum, (delta - sum) * 100.0 / (delta),
	       write_condfails, indexwrite_condfails);
   }
   public void addlink_start(){
        if(measureAllTimeProp==0)
		return;
	clear();
	addlink_time = System.nanoTime();
   }
   public void addlink_end(){
        if(measureAllTimeProp==0)
		return;
        long delta;
        long sum;
        delta = System.nanoTime() - addlink_time;
	sum = getSum();
        log.error("Performance add_link {}"
        	+ " read {} ({})"
        	+ " multiread {} ({})"
        	+ " index_read {} ({})"
        	+ " write {} ({})"
        	+ " multiwrite {} ({})"
        	+ " index_write {} ({})"
        	+ " serialize {} ({})"
        	+ " indexserialize {} ({})"
        	+ " proto_serialize {} ({})"
        	+ " deserialize {} ({})"
        	+ " indexdeserialize {} ({})"
        	+ " proto_deserialize {} ({})"
        	+ " rwsd total {} other {} ({})"
        	+ " writefail ({}) indexwritefail({})",
	       delta,
	       read_latency_sum, read_latency_cnt,
	       multiread_latency_sum, multiread_latency_cnt,
	       indexread_latency_sum, indexread_latency_cnt,
	       write_latency_sum, write_latency_cnt,
	       multiwrite_latency_sum, multiwrite_latency_cnt,
	       indexwrite_latency_sum, indexwrite_latency_cnt,
	       serialize_latency_sum, serialize_latency_cnt,
	       indexserialize_latency_sum, indexserialize_latency_cnt,
	       proto_serialize_latency_sum, proto_serialize_latency_cnt,
	       deserialize_latency_sum, deserialize_latency_cnt,
	       indexdeserialize_latency_sum, indexdeserialize_latency_cnt,
	       proto_deserialize_latency_sum, proto_deserialize_latency_cnt,
	       sum, delta - sum, (delta - sum) * 100.0 / (delta),
	       write_condfails, indexwrite_condfails);
   }

   public void addflowpath_start(){
	if(measureAllTimeProp==0) return;
	clear();
	addflowpath_time = System.nanoTime();
   }
   public void addflowpath_end(){
       if(measureAllTimeProp==0) return;
       long delta;
       long sum;
       delta = System.nanoTime() - addflowpath_time;
       sum = getSum();
       log.error("Performance add_flowpath {}"
        	+ " read {} ({})"
        	+ " multiread {} ({})"
        	+ " index_read {} ({})"
        	+ " write {} ({})"
        	+ " multiwrite {} ({})"
        	+ " index_write {} ({})"
        	+ " serialize {} ({})"
        	+ " indexserialize {} ({})"
        	+ " proto_serialize {} ({})"
        	+ " deserialize {} ({})"
        	+ " indexdeserialize {} ({})"
        	+ " proto_deserialize {} ({})"
        	+ " rwsd total {} other {} ({})"
        	+ " writefail ({}) indexwritefail({})",
	       delta,
	       read_latency_sum, read_latency_cnt,
	       multiread_latency_sum, multiread_latency_cnt,
	       indexread_latency_sum, indexread_latency_cnt,
	       write_latency_sum, write_latency_cnt,
	       multiwrite_latency_sum, multiwrite_latency_cnt,
	       indexwrite_latency_sum, indexwrite_latency_cnt,
	       serialize_latency_sum, serialize_latency_cnt,
	       indexserialize_latency_sum, indexserialize_latency_cnt,
	       proto_serialize_latency_sum, proto_serialize_latency_cnt,
	       deserialize_latency_sum, deserialize_latency_cnt,
	       indexdeserialize_latency_sum, indexdeserialize_latency_cnt,
	       proto_deserialize_latency_sum, proto_deserialize_latency_cnt,
	       sum, delta - sum, (delta - sum) * 100.0 / (delta),
	       write_condfails, indexwrite_condfails);
   }

   public void addflowentry_start(){
       if(measureAllTimeProp==0) return;
	clear();
	if ( debug==1 )
		log.error("addflowentry_start");
	addflowentry_time = System.nanoTime();
   }
   public void addflowentry_incr(){
       if(measureAllTimeProp==0) return;
       addflowentry_cnt++;
   }
   public void addflowentry_end(){
       if(measureAllTimeProp==0) return;
       long delta;
       long sum;
       delta = System.nanoTime() - addflowentry_time;
       sum = getSum();
       log.error("Performance add_flowentry {} ( {} flows )"
        	+ " read {} ({})"
        	+ " multiread {} ({})"
        	+ " index_read {} ({})"
        	+ " write {} ({})"
        	+ " multiwrite {} ({})"
        	+ " index_write {} ({})"
        	+ " serialize {} ({})"
        	+ " indexserialize {} ({})"
        	+ " proto_serialize {} ({})"
        	+ " deserialize {} ({})"
        	+ " indexdeserialize {} ({})"
        	+ " proto_deserialize {} ({})"
        	+ " rwsd total {} other {} ({})"
        	+ " writefail ({}) indexwritefail({})",
	       delta, addflowentry_cnt,
	       read_latency_sum, read_latency_cnt,
	       multiread_latency_sum, multiread_latency_cnt,
	       indexread_latency_sum, indexread_latency_cnt,
	       write_latency_sum, write_latency_cnt,
	       multiwrite_latency_sum, multiwrite_latency_cnt,
	       indexwrite_latency_sum, indexwrite_latency_cnt,
	       serialize_latency_sum, serialize_latency_cnt,
	       indexserialize_latency_sum, indexserialize_latency_cnt,
	       proto_serialize_latency_sum, proto_serialize_latency_cnt,
	       deserialize_latency_sum, deserialize_latency_cnt,
	       indexdeserialize_latency_sum, indexdeserialize_latency_cnt,
	       proto_deserialize_latency_sum, proto_deserialize_latency_cnt,
	       sum, delta - sum, (delta - sum) * 100.0 / (delta),
	       write_condfails, indexwrite_condfails);
   }

   public void read_whole_topology_start(){
       if(measureAllTimeProp==0) return;
	if ( debug==1 )
	    log.error("read_whole_topology_start");

	clear();
	read_whole_topology_time = System.nanoTime();
   }
   public void read_whole_topology_end(){
       if(measureAllTimeProp==0) return;
       long delta;
       long sum;
       delta = System.nanoTime() - read_whole_topology_time;
       sum = getSum();
       log.error("Performance read_whole_topology {}"
        	+ " read {} ({})"
        	+ " multiread {} ({})"
        	+ " index_read {} ({})"
        	+ " write {} ({})"
        	+ " multiwrite {} ({})"
        	+ " index_write {} ({})"
        	+ " serialize {} ({})"
        	+ " indexserialize {} ({})"
        	+ " proto_serialize {} ({})"
        	+ " deserialize {} ({})"
        	+ " indexdeserialize {} ({})"
        	+ " proto_deserialize {} ({})"
        	+ " rwsd total {} other {} ({})"
        	+ " writefail ({}) indexwritefail({})",
	       delta,
	       read_latency_sum, read_latency_cnt,
	       multiread_latency_sum, multiread_latency_cnt,
	       indexread_latency_sum, indexread_latency_cnt,
	       write_latency_sum, write_latency_cnt,
	       multiwrite_latency_sum, multiwrite_latency_cnt,
	       indexwrite_latency_sum, indexwrite_latency_cnt,
	       serialize_latency_sum, serialize_latency_cnt,
	       indexserialize_latency_sum, indexserialize_latency_cnt,
	       proto_serialize_latency_sum, proto_serialize_latency_cnt,
	       deserialize_latency_sum, deserialize_latency_cnt,
	       indexdeserialize_latency_sum, indexdeserialize_latency_cnt,
	       proto_deserialize_latency_sum, proto_deserialize_latency_cnt,
	       sum, delta - sum, (delta - sum) * 100.0 / (delta),
	       write_condfails, indexwrite_condfails);
   }


   public void read_start(String key){
        if(measureAllTimeProp==0)
		return;
	if ( debug==1 )
            log.error("read_start {}", key);
        if ( read_flag != 0){
            log.error("read_start called twice");
	}
	read_flag = 1;

	read_time=System.nanoTime();
   }
   public void read_end(String key){
        if(measureAllTimeProp==0)
		return;
	if ( debug==1 )
            log.error("read_end {}", key);

	read_latency_sum += System.nanoTime() - read_time;
	read_latency_cnt ++;

        if ( read_flag != 1){
            log.error("read_end called before read_start");
	}
	read_flag = 0;
   }
   public void multiread_start(String key){
        if(measureAllTimeProp==0)
		return;
	if ( debug==1 )
            log.error("multiread_start {}", key);
	if ( multiread_flag != 0){
            log.error("multiread_start called twice");
	}
	multiread_flag = 1;

	multiread_time=System.nanoTime();
   }
   public void multiread_end(String key){
        if(measureAllTimeProp==0)
		return;

	multiread_latency_sum += System.nanoTime() - multiread_time;
        multiread_latency_cnt ++;

	if ( debug==1 )
            log.error("multiread_end {}", key);
        if ( multiread_flag != 1){
            log.error("multiread_end called before multiread_start");
	}
	multiread_flag = 0;
   }
   public void multiwrite_start(String key){
       if(measureAllTimeProp==0)
		return;
	if ( debug==1 )
           log.error("multiwrite_start {}", key);
	if ( multiwrite_flag != 0){
           log.error("multiwrite_start called twice");
	}
	multiwrite_flag = 1;

	multiwrite_time=System.nanoTime();
  }
  public void multiwrite_end(String key){
       if(measureAllTimeProp==0)
		return;

	multiwrite_latency_sum += System.nanoTime() - multiwrite_time;
       multiwrite_latency_cnt ++;

	if ( debug==1 )
           log.error("multiwrite_end {}", key);
       if ( multiwrite_flag != 1){
           log.error("multiwrite_end called before multiwrite_start");
	}
	multiwrite_flag = 0;
  }
   public void indexread_start(String key){
        if(measureAllTimeProp==0)
		return;
	if ( debug==1 )
            log.error("indexread_start {}", key);
        if ( indexread_flag != 0){
            log.error("indexread_start called twice");
	}
	indexread_flag = 1;

    	indexread_time=System.nanoTime();
   }
   public void indexread_end(String key){
        if(measureAllTimeProp==0)
		return;

	indexread_latency_sum += System.nanoTime() - indexread_time;
        indexread_latency_cnt ++;

	if ( debug==1 )
            log.error("indexread_end {}", key);
        if ( indexread_flag != 1){
            log.error("indexread_end called before indexread_start");
	}
	indexread_flag = 0;
   }
   public void write_start(String key){
        if(measureAllTimeProp==0)
		return;
	if ( debug==1 )
            log.error("write start_{}", key);
        if ( write_flag != 0){
            log.error("write_start called twice");
	}
	write_flag = 1;

	write_time = System.nanoTime();
   }
   public void write_end(String key){
        if(measureAllTimeProp==0)
		return;

        write_latency_sum += System.nanoTime() - write_time;
        write_latency_cnt ++;

        if ( debug==1 )
            log.error("write_end {}", key);
        if ( write_flag != 1){
            log.error("write_end called before write_start");
	}
	write_flag = 0;
   }
   public void write_condfail(String key){
       if(measureAllTimeProp==0)
		return;
       write_condfails++;
   }

   public void indexwrite_start(String key){
        if(measureAllTimeProp==0)
		return;
	if ( debug==1 )
            log.error("index_write start {}", key);
        if ( indexwrite_flag != 0){
            log.error("indexwrite_start called twice");
	}
	indexwrite_flag = 1;
	indexwrite_time = System.nanoTime();
   }
   public void indexwrite_end(String key){
        if(measureAllTimeProp==0)
		return;

	indexwrite_latency_sum += System.nanoTime() - indexwrite_time;
        indexwrite_latency_cnt ++;

        if ( debug==1 )
            log.error("indexwrite_end {}", key);
        if ( indexwrite_flag != 1){
            log.error("indexwrite_end called before indexwrite_start");
	}
	indexwrite_flag = 0;
   }
   public void indexwrite_condfail(String key){
       if(measureAllTimeProp==0)
		return;
       indexwrite_condfails++;
   }

   public void ser_start(String key){
        if(measureAllTimeProp==0)
		return;
	if ( debug==1 )
            log.error("ser_start {}", key);
        if ( ser_flag != 0 ){
            	log.error("ser_start called twice");
	}
	ser_flag = 1;

	ser_time = System.nanoTime();
   }
   public void ser_end(String key){
        if(measureAllTimeProp==0)
		return;

	serialize_latency_sum += System.nanoTime() - ser_time;
        serialize_latency_cnt ++;

        if ( debug==1 )
            log.error("ser_end {}", key);
        if ( ser_flag != 1 ){
            	log.error("ser_end called before ser_start");
	}
	ser_flag = 0;
   }

   public void indexser_start(String key){
        if(measureAllTimeProp==0)
		return;

	indexser_time = System.nanoTime();

	if ( debug==1 )
            log.error("indexser_start {}", key);
        if ( indexser_flag != 0 ){
            	log.error("indexser_start called twice");
	}
	indexser_flag = 1;
   }
   public void indexser_end(String key){
        if(measureAllTimeProp==0)
		return;

	indexserialize_latency_sum += System.nanoTime() - indexser_time;
        indexserialize_latency_cnt ++;

        if ( debug==1 )
            log.error("indexser_end {}", key);
        if ( indexser_flag != 1 ){
            	log.error("indexser_end called before indexser_start");
	}
	indexser_flag = 0;

   }

   public void deser_start(String key){
        if(measureAllTimeProp==0)
		return;
	if ( debug==1 )
            log.error("deser_start {}", key);
        if ( deser_flag != 0){
            log.error("deser_start called twice");
	}
	deser_flag = 1;

	deser_time = System.nanoTime();
   }
   public void deser_end(String key){
        if(measureAllTimeProp==0)
		return;

        deserialize_latency_sum += System.nanoTime() - deser_time;
        deserialize_latency_cnt ++;

        if ( debug==1 )
            log.error("deser_end {}", key);
        if ( deser_flag != 1){
            log.error("deser_end called before deser_start");
	}
	deser_flag = 0;
   }

   public void indexdeser_start(String key){
        if(measureAllTimeProp==0)
		return;
	if ( debug==1 )
            log.error("indexdeser_start {}", key);
        if ( indexdeser_flag != 0){
            log.error("indexdeser_start called twice");
	}
	indexdeser_flag = 1;

	indexdeser_time = System.nanoTime();
   }
   public void indexdeser_end(String key){
        if(measureAllTimeProp==0)
		return;

        indexdeserialize_latency_sum += System.nanoTime() - indexdeser_time;
        indexdeserialize_latency_cnt ++;

        if ( debug==1 )
            log.error("indexdeser_end {}", key);
        if ( indexdeser_flag != 1){
            log.error("indexdeser_end called before indexdeser_start");
	}
	indexdeser_flag = 0;
   }

   public void protoser_start(String key){
       if(measureAllTimeProp==0)
	   return;

       if ( debug==1 )
	   log.error("protoser_start {}", key);
       if ( protoser_flag != 0){
	   log.error("protoser_start called twice");
       }
       protoser_flag = 1;

       protoser_time = System.nanoTime();
   }
   public void protoser_end(String key){
       if(measureAllTimeProp==0)
	   return;

       proto_serialize_latency_sum += System.nanoTime() - protoser_time;
       proto_serialize_latency_cnt ++;

       if ( debug==1 )
	   log.error("protoser_end {}", key);
       if ( protoser_flag != 1){
	   log.error("protoser_end called before protoser_start");
       }
       protoser_flag = 0;
   }
   public void protodeser_start(String key){
       if(measureAllTimeProp==0)
	   return;
       if ( debug==1 )
	   log.error("protodeser_start {}", key);
       if ( protodeser_flag != 0){
	   log.error("protoser_start called twice");
       }
       protodeser_flag = 1;

       protodeser_time = System.nanoTime();
   }
   public void protodeser_end(String key){
       if(measureAllTimeProp==0)
	   return;

       proto_deserialize_latency_sum += System.nanoTime() - protodeser_time;
       proto_deserialize_latency_cnt ++;

       if ( debug==1 )
	   log.error("protodeser_end {}", key);
       if ( protodeser_flag != 1){
	   log.error("protodeser_end called before protodeser_start");
       }
       protodeser_flag = 0;
   }

}
