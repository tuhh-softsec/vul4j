package net.onrc.onos.ofcontroller.flowmanager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for collecting performance measurements
 */
public class PerformanceMonitor {
    private final static Map<String, Measurement> map = new ConcurrentHashMap<String, Measurement>();;
    private final static Logger log = LoggerFactory.getLogger(PerformanceMonitor.class);
    private static long overhead;    
    
    /**
     * Start a performance measurement, identified by a tag
     * 
     * Note: Only a single measurement can use the same tag at a time.
     * 
     * @param tag for performance measurement
     */
    public static void start(String tag) {
	long start = System.nanoTime();
	Measurement m = new Measurement();
	if(map.put(tag, m) != null) {
	    // if there was a previous entry, we have just overwritten it
	    log.error("Tag {} already exists", tag);
	}
	m.start();
	overhead += System.nanoTime() - start;
    }
    
    /**
     * Stop a performance measurement. 
     * 
     * You must have already started a measurement with tag.
     * 
     * @param tag for performance measurement
     */
    public static void stop(String tag) {
	long time = System.nanoTime();
	Measurement m = map.get(tag);
	if(m == null) {
	    log.error("Tag {} does not exist", tag);
	}
	else {
	    map.get(tag).stop(time);
	}
	overhead += System.nanoTime() - time;
    }
    
    /**
     * Find a measurement, identified by tag, and return the result
     * 
     * @param tag for performance measurement
     * @return the time in nanoseconds
     */
    public static long result(String tag) {
	Measurement m = map.get(tag);
	if(m != null) {
	    return m.elapsed();
	}
	else {
	    return -1;
	}
    }
    
    /**
     * Clear all performance measurements.
     */
    public static void clear() {
	map.clear();
	overhead = 0;
    }
    
    /**
     * Write all performance measurements to the log
     */
    public static void report() {
	double overheadMilli = overhead / Math.pow(10, 6);
	log.error("Performance Results: {} with measurement overhead: {} ms", map, overheadMilli);
    }

    /**
     * Write the performance measurement for a tag to the log
     *
     * @param tag the tag name.
     */
    public static void report(String tag) {
	Measurement m = map.get(tag);
	if (m != null) {
	    log.error("Performance Results: tag = {} start = {} stop = {} elapsed = {}",
		      tag, m.start, m.stop, m.toString());
	} else {
	    log.error("Performance Results: unknown tag {}", tag);
	}
    }

    /**
     * A single performance measurement
     */
    static class Measurement {
	long start;
	long stop;
	
	/** 
	 * Start the measurement
	 */
	public void start() {
	    start = System.nanoTime();
	}
	
	/**
	 * Stop the measurement
	 */
	public void stop() {
	    stop = System.nanoTime();
	}
	
	/**
	 * Stop the measurement at a specific time
	 * @param time to stop
	 */
	public void stop(long time){
	    stop = time;
	}
	
	/**
	 * Compute the elapsed time of the measurement in nanoseconds
	 * 
	 * @return the measurement time in nanoseconds, or -1 if the measurement is stil running.
	 */
	public long elapsed() {
	    if(stop == 0) {
		return -1;
	    }
	    else {
		return stop - start;
	    }
	}
	
	/**
	 * Returns the number of milliseconds for the measurement as a String.
	 */
	public String toString() {
	    double milli = elapsed() / Math.pow(10, 6);
	    return Double.toString(milli) + " ms";
	}
    }
    
    public static void main(String args[]){
	// test the measurement overhead
	String tag;
	for(int i = 0; i < 100; i++){
	    tag = "foo foo foo";
	    start(tag); stop(tag);
	    tag = "bar";
	    start(tag); stop(tag);
	    tag = "baz";
	    start(tag); stop(tag);
	    report();
	    clear();
	}
	for(int i = 0; i < 100; i++){
	    tag = "a";
	    start(tag); stop(tag);
	    tag = "b";
	    start(tag); stop(tag);
	    tag = "c";
	    start(tag); stop(tag);
	    report();
	    clear();
	}
    }
}
