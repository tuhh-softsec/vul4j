package net.onrc.onos.ofcontroller.flowmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for collecting performance measurements
 */
public class PerformanceMonitor {
    private final static Map<String, List<Measurement>> map = new ConcurrentHashMap<>();;
    private final static Logger log = LoggerFactory.getLogger(PerformanceMonitor.class);
    private static long overhead;
    private static long experimentStart = Long.MAX_VALUE;
    private final static double normalization = Math.pow(10, 6);

    /**
     * Start a performance measurement, identified by a tag
     * 
     * Note: Only a single measurement can use the same tag at a time.
     * ..... not true anymore.
     * 
     * @param tag for performance measurement
     */
    public static Measurement start(String tag) {
	long start = System.nanoTime();
	if(start < experimentStart) {
	    experimentStart = start;
	}
	List<Measurement> list = map.get(tag);
	if(list == null) {
	    list = new ArrayList<Measurement>();
	    map.put(tag, list);
	}
	Measurement m = new Measurement();
	list.add(m);
	m.start();
	overhead += System.nanoTime() - start;
	return m;
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
	List<Measurement> list = map.get(tag);
	if(list == null) {
	    log.error("Tag {} does not exist", tag);
	}
	else if(list.size() == 1) {
	    list.get(0).stop(time);
	}
	else {
	    log.error("Tag {} has multiple measurements", tag);
	}
	overhead += System.nanoTime() - time;
    }
        
    /**
     * Clear all performance measurements.
     */
    public static void clear() {
	map.clear();
	overhead = 0;
	experimentStart = Long.MAX_VALUE;
    }
    
    /**
     * Write all performance measurements to the log
     */
    public static void report() {
	String result = "Performance Results: (avg/start/stop/count)\n";
	long experimentEnd = -1;
	for(Entry<String, List<Measurement>> e : map.entrySet()) {
	    String key = e.getKey();
	    List<Measurement> list = e.getValue();
	    int total = 0, count = 0;
	    long start = Long.MAX_VALUE, stop = -1;
	    for(Measurement m : list) {
		// Collect overall start and end times
		if(m.start < start) {
		    start = m.start;
		}
		if(m.stop > stop) {
		    stop = m.stop;
		    if(stop > experimentEnd) {
			experimentEnd = stop;
		    }
		}
		
		// Collect statistics for average
		total += m.elapsed();
		count++;
	    }
	    double avg = (double) total / count;
	    // Normalize start/stop
	    start -= experimentStart;
	    stop -= experimentStart;
	    result += key + '=' + 
		    (avg / normalization) + '/' + 
		    (start / normalization) + '/' + 
		    (stop / normalization) + '/' + 
		    count + '\n';
	}
	double overheadMs = overhead / normalization;
	double experimentElapsed = (experimentEnd - experimentStart) / normalization;
	result += "TotalTime:" + experimentElapsed + "/Overhead:" + overheadMs;
	log.error(result);
//	log.error("Performance Results: {} with measurement overhead: {} ms", map, overheadMilli);
    }

    /**
     * Write the performance measurement for a tag to the log
     *
     * @param tag the tag name.
     */
    public static void report(String tag) {
	List<Measurement> list = map.get(tag);
	if(list == null) {
	    return; //TODO
	}
	//TODO: fix this;
	Measurement m = list.get(0);
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
    public static class Measurement {
	long start;
	long stop = -1;
	
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
	    long now = System.nanoTime();
	    stop(now);
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
	    if(stop <= 0) {
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
	    double milli = elapsed() / normalization;
	    double startMs = start / normalization;
	    double stopMs = stop / normalization;
	    
	    return milli + "ms/" + startMs + '/' + stopMs;
	}
    }
    
    public static void main(String args[]){
	// test the measurement overhead
	String tag;
	for(int i = 0; i < 2; i++){
	    tag = "foo foo foo";
	    Measurement m;
	    m = start(tag); System.out.println(tag); m.stop();
	    m = start(tag); System.out.println(tag); m.stop();
	    m = start(tag); System.out.println(tag); m.stop();
	    m = start(tag); System.out.println(tag); m.stop();
	    tag = "bar";
	    start(tag); stop(tag);
	    tag = "baz";
	    start(tag); stop(tag);
	    report();
	    clear();
	}
//	for(int i = 0; i < 100; i++){
//	    tag = "a";
//	    start(tag); stop(tag);
//	    tag = "b";
//	    start(tag); stop(tag);
//	    tag = "c";
//	    start(tag); stop(tag);
//	    report();
//	    clear();
//	}
    }
}
