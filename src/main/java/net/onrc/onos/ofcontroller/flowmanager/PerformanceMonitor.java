package net.onrc.onos.ofcontroller.flowmanager;

import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for collecting performance measurements
 */
public class PerformanceMonitor {
    private final static Logger log = LoggerFactory.getLogger(PerformanceMonitor.class);

    // experiment name -> PerformanceMonitor
    private static final ConcurrentHashMap<String,PerformanceMonitor> perfMons = new ConcurrentHashMap<>();
    public static PerformanceMonitor experiment(String name) {
	PerformanceMonitor pm = perfMons.get(name);
	if (pm == null) {
	    pm = new PerformanceMonitor();
	    PerformanceMonitor existing = perfMons.putIfAbsent(name, pm);
	    if (existing != null) {
		pm = existing;
	    }
	}
	return pm;
    }

    // tag -> Measurements
    private final ConcurrentHashMap<String, Queue<Measurement>> map = new ConcurrentHashMap<>();
    private long overhead;
    private long experimentStart = Long.MAX_VALUE;
    private final static double normalization = Math.pow(10, 6);

    /**
     * Start a performance measurement, identified by a tag
     *
     * Note: Only a single measurement can use the same tag at a time.
     * ..... not true anymore.
     *
     * @param tag for performance measurement
     */
    public Measurement startStep(String tag) {
	long start = System.nanoTime();
	if(start < experimentStart) {
	    experimentStart = start;
	}
	Queue<Measurement> list = map.get(tag);
	if(list == null) {
	    list = new ConcurrentLinkedQueue<Measurement>();
	    Queue<Measurement> existing_list = map.putIfAbsent(tag, list);
	    if (existing_list != null) {
		// someone concurrently added, using theirs
		list = existing_list;
	    }
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
    public void stopStep(String tag) {
	long time = System.nanoTime();
	Queue<Measurement> list = map.get(tag);
	if(list == null || list.size() == 0) {
	    log.error("Tag {} does not exist", tag);
	}
	list.peek().stop(time);
	if(list.size() > 1) {
	    log.error("Tag {} has multiple measurements", tag);
	}
	overhead += System.nanoTime() - time;
    }

    /**
     * Clear all performance measurements.
     */
    public void reset() {
	map.clear();
	overhead = 0;
	experimentStart = Long.MAX_VALUE;
    }

    /**
     * Write all performance measurements to the log
     */
    public void reportAll() {
	String result = "Performance Results: (avg/start/stop/count)\n";
	if(map.size() == 0) {
	    result += "No Measurements";
	    log.error(result);
	    return;
	}
	long experimentEnd = -1;
	for(Entry<String, Queue<Measurement>> e : map.entrySet()) {
	    String key = e.getKey();
	    Queue<Measurement> list = e.getValue();
	    long total = 0, count = 0;
	    long start = Long.MAX_VALUE, stop = -1;
	    for(Measurement m : list) {
		if(m.stop < 0) {
		    continue; // measurement has not been stopped
		}
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
    public void reportStep(String tag) {
	Queue<Measurement> list = map.get(tag);
	if(list == null) {
	    return; //TODO
	}
	//TODO: fix this;
	Measurement m = list.peek();
	if (m != null) {
	    log.error("Performance Result: tag = {} start = {} stop = {} elapsed = {}",
		      tag, m.start, m.stop, m.toString());
	} else {
	    log.error("Performance Result: unknown tag {}", tag);
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
	    if(start <= 0) {
		start = System.nanoTime();
	    }
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
	    if(stop <= 0) {
		stop = time;
	    }
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
	@Override
	public String toString() {
	    double milli = elapsed() / normalization;
	    double startMs = start / normalization;
	    double stopMs = stop / normalization;

	    return milli + "ms/" + startMs + '/' + stopMs;
	}
    }

    @Deprecated
    private static final PerformanceMonitor theInstance = new PerformanceMonitor();

    @Deprecated
    public static Measurement start(String tag) {
	return theInstance.startStep(tag);
    }

    @Deprecated
    public static void stop(String tag) {
	theInstance.stopStep(tag);;
    }

    @Deprecated
    public static void clear() {
	theInstance.reset();;
    }

    @Deprecated
    public static void report() {
	theInstance.reportAll();;
    }

    @Deprecated
    public static void report(String tag) {
	theInstance.reportStep(tag);
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
	for(int i = 0; i < 100; i++){
	    tag = "a";
	    start(tag); stop(tag);
	    start(tag); stop(tag);

	    start(tag); stop(tag);
	    start(tag); stop(tag);
	    start(tag); stop(tag);
	    start(tag); stop(tag);
	    start(tag); stop(tag);
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
