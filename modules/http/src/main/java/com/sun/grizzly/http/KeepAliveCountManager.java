/*
 * The contents of this file are subject to the terms 
 * of the Common Development and Distribution License 
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at 
 * https://glassfish.dev.java.net/public/CDDLv1.0.html or
 * glassfish/bootstrap/legal/CDDLv1.0.txt.
 * See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL 
 * Header Notice in each file and include the License file 
 * at glassfish/bootstrap/legal/CDDLv1.0.txt.  
 * If applicable, add the following below the CDDL Header, 
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 */
package com.sun.grizzly.http;


import java.nio.channels.SelectionKey;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Keep Alive subsystems. This class will cancel <code>SelectionKey<code>
 * based on the maxKeepAliveRequests.
 *
 * @author Jeanfrancois Arcand
 */
public class KeepAliveCountManager{
    
    
    public final static int KEEP_ALIVE_RULE = 0;
    
    /**
     * The maximum number of Thread
     */
    private int maxThreads = 1;
   
    
    /**
     * The Thread Priority
     */
    private int priority = Thread.NORM_PRIORITY;
    
    
    /**
     * The port used.
     */
    private int port = 8080;
        

    /**
     * The name of this Pipeline
     */
    private String name;
    
    
    /**
     * Has the pipeline already started
     */
    private boolean isStarted = false; 
    
    
    /*
     * Number of seconds before idle accepted connections expire.
     * Default is 30 seconds as per domain.xml DTD.
     */
    private int firstReadTimeout = Constants.DEFAULT_TIMEOUT; 
    
    /*
     * Number of seconds before idle keep-alive connections expire.
     * Default is 30 seconds as per domain.xml DTD.
     */
    private int keepAliveTimeoutInSeconds = Constants.DEFAULT_TIMEOUT;
    
    
    /**
     * Placeholder for keep-alive count monitoring.
     */
    protected ConcurrentHashMap<SelectionKey,Integer> keepAliveCounts;
    
    
    /**
     * Maximum number of requests in a single transaction.
     */
    protected int maxKeepAliveRequests = -1;
    
    
    /**
     * The <code>PipelineStatistic</code> objects used when gathering statistics.
     */
    protected PipelineStatistic pipelineStat;     
      
    
    /**
     * The stats object used to gather statistics.
     */
    private KeepAliveStats keepAliveStats;


    // ----------------------------------------------- Constructor ----------//
    
    /**
     * Default constructor.
     */
    public KeepAliveCountManager(){
        initPipeline();
    }
   
    // ------------------------------------------------ Lifecycle ------------/
    
    /**
     * Init the <code>Pipeline</code> by initializing the required
     * <code>WorkerThread</code>. 
     */
    public void initPipeline(){
        if (isStarted){
            return;
        }
        isStarted = true;
        keepAliveCounts = new ConcurrentHashMap<SelectionKey,Integer>();     
    }

    
    /**
     * Start the <code>Pipeline</code> and all associated 
     * <code>WorkerThread</code>
     */
    public void startPipeline(){
        if (isStarted){
            return;
        }
        ; // Do nothing
    }
    

    /**
     * Stop the <code>Pipeline</code> and all associated
     * <code>WorkerThread</code>
     */
    public void stopPipeline(){
        if (!isStarted){
            return;
        }
        isStarted = false;
        keepAliveCounts.clear();
    }
    
    // ---------------------------------------------------- Queue ------------//
  
    
    /**
     * Add an object to this pipeline
     */
    public void addTask(Task task){
        throw new UnsupportedOperationException();
    }


    /**
     * Return a <code>SelectionKey</code> object available in the pipeline.
     * All Threads will synchronize on that method
     * @Return null This pipeline doesn't supports this method.
     */
    public Task getTask() {
        return null;
    }
    

    /**
     * Returns the number of tasks in this <code>Pipeline</code>.
     *
     * @return Number of tasks in this <code>Pipeline</code>.
     */
    public int size() {
        return 0;
    }

    
    // --------------------------------------------------Properties ----------//

     /**
     * Return the number of waiting threads.
     */
    public int getWaitingThread(){
        return 0;
    }
    
    
    /** 
     * Set the number of threads used by this pipeline.
     */
    public void setMaxThreads(int maxThreads){
        this.maxThreads = maxThreads;
    }    
    
    
    /** 
     * Return the number of threads used by this pipeline.
     */
    public int getMaxThreads(){
        return maxThreads;
    }
    
    
    /**
     * Return the current number of active threads.
     */
    public int getCurrentThreadCount() {
        return 1;
    }
    
    
    /**
     * Return the current number of active threads.
     */
    public int getCurrentThreadsBusy() {
        return 1;
    }
    
    
    /**
     * Return the maximum spare thread.
     */
    public int getMaxSpareThreads() {
        return 0;
    }     

    
    /**
     * Set the thread priority of the <code>Pipeline</code>
     */
    public void setPriority(int priority){
        this.priority = priority;
    }
    
    
    /**
     * Set the name of this <code>Pipeline</code>
     */
    public void setName(String name){
        this.name = name;
    }
    
    
    /**
     * Return the name of this <code>Pipeline</code>
     * @return the name of this <code>Pipeline</code>
     */
    public String getName(){
        return name+port;
    }    

    
    /**
     * Set the port used by this <code>Pipeline</code>
     * @param port the port used by this <code>Pipeline</code>
     */
    public void setPort(int port){
        this.port = port;
    }
    
    
    /**
     * Set the minimum thread this <code>Pipeline</code> will creates
     * when initializing.
     * @param minThreads the minimum number of threads.
     */
    public void setMinThreads(int minThreads){
        throw new UnsupportedOperationException();
    }
    
    
    public String toString(){
       return "name: " + name + " maxThreads: " + maxThreads ;        
    }

    
    /**
     * Set the maximum pending connection this <code>Pipeline</code>
     * can handle.
     */
    public void setQueueSizeInBytes(int maxQueueSizeInBytes){
        throw new UnsupportedOperationException(); 
    }
    
    
    public void setThreadsIncrement(int threadsIncrement){
       throw new UnsupportedOperationException();
    }
    
    
    public void setThreadsTimeout(int threadsTimeout){
        this.firstReadTimeout = threadsTimeout;
    }


    /**
     * Return the minimum spare thread.
     */
    public int getMinSpareThreads() {
        throw new UnsupportedOperationException();
    }


    /**
     * Set the minimum space thread this <code>Pipeline</code> can handle.
     */
    public void setMinSpareThreads(int minSpareThreads) {
        throw new UnsupportedOperationException();
    }

    
    // ------------------------------------------------ maxKeepAliveRequests --/
    
    /**
     * Monitor keep-alive request count for the given connection.
     * @return true if the request can be processed, false if the maximun
     *              number of requests has been reached.
     */
    public boolean trap(SelectionKey key){
        if ( maxKeepAliveRequests == -1) return true;
        
        Integer count = keepAliveCounts.get(key);
        if ( count == null ){
            count = 0;
            if (keepAliveStats != null) {
                keepAliveStats.incrementCountConnections();
            }
        }
        
        if ((count++) > maxKeepAliveRequests){
            if (keepAliveStats != null) {
                keepAliveStats.incrementCountRefusals();
            }
            return false;
        }

        keepAliveCounts.put(key, count);
        if (keepAliveStats != null) {
            keepAliveStats.incrementCountHits();
        }

        return true;
    }
    
    
    /**
     * Stop monitoring keep-alive request count for the given connection.
     */
    public void untrap(SelectionKey key){
        if ( maxKeepAliveRequests == -1) return;
        
        Integer count = keepAliveCounts.remove(key);
        if (keepAliveStats != null && count != null) {
            keepAliveStats.decrementCountConnections();
        }
    }
    
    
    /**
     * Return the maximum number of keep-alive requests per connection.
     */
    public int getMaxKeepAliveRequests() {
        return maxKeepAliveRequests;
    }
    
    
    /** 
     * Set the maximum number of Keep-Alive requests that we will 
     * honor per connection. A value < 0 will disabled the keep-alive mechanism.
     */
    public void setMaxKeepAliveRequests(int maxKeepAliveRequests) {
        this.maxKeepAliveRequests = maxKeepAliveRequests;
    }
    
        
    /**
     * Sets the number of seconds before a keep-alive connection that has
     * been idle times out and is closed.
     *
     * @param keepAliveTimeout Keep-alive timeout in number of seconds
     */
    public void setKeepAliveTimeoutInSeconds(int keepAliveTimeout){
        this.keepAliveTimeoutInSeconds = keepAliveTimeout;
    }
                                                                                
                                                                                
    /**
     * Gets the number of seconds before a keep-alive connection that has
     * been idle times out and is closed.
     *
     * @return Keep-alive timeout in number of seconds
     */
    public int getKeepAliveTimeoutInSeconds(){
        return keepAliveTimeoutInSeconds;
    }
    

    /**
     * Return <code>true</code> if we need to close the connection just after 
     * the first request.
     */
    public boolean dropConnection(){
        return (keepAliveTimeoutInSeconds == 0 
                    || firstReadTimeout == 0 || maxKeepAliveRequests == 0);
    }
       
    
    
    /**
     * Set the <code>PipelineStatistic</code> object used
     * to gather statistic;
     */
    public void setPipelineStatistic(PipelineStatistic pipelineStatistic){
        this.pipelineStat = pipelineStatistic;
    }
    
    
    /**
     * Return the <code>PipelineStatistic</code> object used
     * to gather statistic;
     */
    public PipelineStatistic getPipelineStatistic(){
        return pipelineStat;
    }
    

    /**
     * Sets the given KeepAliveStats, which is responsible for storing
     * keep-alive statistics.
     *
     * @param keepAliveStats The KeepAliveStats object responsible for 
     * storing keep-alive statistics
     */
    void setKeepAliveStats(KeepAliveStats keepAliveStats) {
        this.keepAliveStats = keepAliveStats;
    }

    
    /**
     * Interrupt the <code>Thread</code> using it thread id
     */
    public boolean interruptThread(long threadId){
        return false;
    }
}

