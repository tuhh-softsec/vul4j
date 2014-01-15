package net.onrc.onos.ofcontroller.flowmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.graph.DBOperation;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.util.Dpid;
import net.onrc.onos.ofcontroller.util.FlowEntry;
import net.onrc.onos.ofcontroller.util.FlowEntrySwitchState;
import net.onrc.onos.ofcontroller.util.FlowId;
import net.onrc.onos.ofcontroller.util.FlowPath;
import net.onrc.onos.ofcontroller.util.serializers.KryoFactory;

import com.esotericsoftware.kryo2.Kryo;

/**
 * Class for performing parallel Flow-related operations on the Database.
 * 
 * This class is mostly a wrapper of FlowDatabaseOperation with a thread pool
 * for parallelization.
 * 
 * @author Brian O'Connor <brian@onlab.us>
 */
public class ParallelFlowDatabaseOperation extends FlowDatabaseOperation {
    private final static Logger log = LoggerFactory.getLogger(FlowDatabaseOperation.class);

    private final static int numThreads = Integer.valueOf(System.getProperty("parallelFlowDatabase.numThreads", "32"));
    private final static ExecutorService executor = Executors.newFixedThreadPool(numThreads);

    private static KryoFactory kryoFactory = new KryoFactory();

    /**
     * Get all installed flows by first querying the database for all FlowPaths
     * and then populating them from the database in parallel.
     * 
     * @param dbHandler the Graph Database handler to use.
     * @return the Flow Paths if found, otherwise an empty list.
     */
    static ArrayList<FlowPath> getAllFlows(DBOperation dbHandler) {
	Iterable<IFlowPath> flowPathsObj = null;
	ArrayList<FlowPath> flowPaths = new ArrayList<FlowPath>();

	try {
	    flowPathsObj = dbHandler.getAllFlowPaths();
	} catch (Exception e) {
	    // TODO: handle exceptions
	    dbHandler.rollback();
	    log.error(":getAllFlowPaths failed");
	    return flowPaths;
	}
	if ((flowPathsObj == null) || (flowPathsObj.iterator().hasNext() == false)) {
	    dbHandler.commit();
	    return flowPaths;	// No Flows found
	}
	
	CompletionService<FlowPath> tasks = new ExecutorCompletionService<>(executor);
	int numTasks = 0;
	for(IFlowPath flowObj : flowPathsObj) {
	    tasks.submit(new ExtractFlowTask(flowObj));
	    numTasks++;
	}
	for(int i = 0; i < numTasks; i++) {
	    try {
		FlowPath flowPath = tasks.take().get();
		if(flowPath != null) {
		    flowPaths.add(flowPath);
		}
	    } catch (InterruptedException | ExecutionException e) {
		log.error("Error reading FlowPath from IFlowPath object");
	    }
	}
	dbHandler.commit();
	return flowPaths;	
    }
    
    /**
     * Query the database for all flow paths that have their source switch
     * in the provided collection
     * 
     * Note: this function is implemented naively and inefficiently
     * 
     * @param dbHandler the Graph Database handler to use.
     * @param switches a collection of switches whose flow paths you want
     * @return the Flow Paths if found, otherwise an empty list.
     */
    static ArrayList<FlowPath> getFlowsForSwitches(DBOperation dbHandler, Collection<Dpid> switches) {
	Iterable<IFlowPath> flowPathsObj = null;
	ArrayList<FlowPath> flowPaths = new ArrayList<FlowPath>();

	try {
	    flowPathsObj = dbHandler.getAllFlowPaths();
	} catch (Exception e) {
	    // TODO: handle exceptions
	    dbHandler.rollback();
	    log.error(":getAllFlowPaths failed");
	    return flowPaths;
	}
	if ((flowPathsObj == null) || (flowPathsObj.iterator().hasNext() == false)) {
	    dbHandler.commit();
	    return flowPaths;	// No Flows found
	}
	
	// convert the collection of switch dpids into a set of strings
	Set<String> switchSet = new HashSet<>();
	for(Dpid dpid : switches) {
	    switchSet.add(dpid.toString());
	}
	
	CompletionService<FlowPath> tasks = new ExecutorCompletionService<>(executor);
	int numTasks = 0;
	for(IFlowPath flowObj : flowPathsObj) {
	    if(switchSet.contains(flowObj.getSrcSwitch())) {
		tasks.submit(new ExtractFlowTask(flowObj));
		numTasks++;
	    }
	}
	for(int i = 0; i < numTasks; i++) {
	    try {
		FlowPath flowPath = tasks.take().get();
		if(flowPath != null) {
		    flowPaths.add(flowPath);
		}
	    } catch (InterruptedException | ExecutionException e) {
		log.error("Error reading FlowPath from IFlowPath object");
	    }
	}
	dbHandler.commit();
	return flowPaths;	
    }
    
    /**
     * The basic parallelization unit for extracting FlowEntries from the database.
     * 
     * This is simply a wrapper for FlowDatabaseOperation.extractFlowPath()
     */
    private final static class ExtractFlowTask implements Callable<FlowPath> {
	private final IFlowPath flowObj;
	
	ExtractFlowTask(IFlowPath flowObj){
	    this.flowObj = flowObj;
	}
	@Override 
	public FlowPath call() throws Exception {
	    return extractFlowPath(flowObj);
	}
    }

    /**
     * Get a subset of installed flows in parallel.
     *
     * @param dbHandler the Graph Database handler to use.
     * @param flowIds the collection of Flow IDs to get.
     * @return the Flow Paths if found, otherwise an empty list.
     */
    static ArrayList<FlowPath> getFlows(DBOperation dbHandler,
		  			Collection<FlowId> flowIds) {
	ArrayList<FlowPath> flowPaths = new ArrayList<FlowPath>();

	CompletionService<FlowPath> tasks = new ExecutorCompletionService<>(executor);
	int numTasks = 0;
	for (FlowId flowId : flowIds) {
	    tasks.submit(new GetFlowTask(dbHandler, flowId));
	    numTasks++;
	}
	for(int i = 0; i < numTasks; i++) {
	    try {
		FlowPath flowPath = tasks.take().get();
		if(flowPath != null) {
		    flowPaths.add(flowPath);
		}
	    } catch (InterruptedException | ExecutionException e) {
		log.error("Error reading FlowPath from database");
	    }
	}
	// TODO: should we commit?
	//dbHandler.commit();
	return flowPaths;
    }
    
    /**
     * The basic parallelization unit for getting FlowEntries.
     * 
     * This is simply a wrapper for FlowDatabaseOperation.getFlow()
     */
    private final static class GetFlowTask implements Callable<FlowPath> {
	private final DBOperation dbHandler;
	private final FlowId flowId;

	GetFlowTask(DBOperation dbHandler, FlowId flowId) {
	    this.dbHandler = dbHandler;
	    this.flowId = flowId;
	}
	@Override
	public FlowPath call() throws Exception{
	    return getFlow(dbHandler, flowId);
	}
    }
    
    /**
     * Add a flow by creating a database task, then waiting for the result.
     * Mostly, a wrapper for FlowDatabaseOperation.addFlow() which overs little
     * performance benefit.
     *
     * @param dbHandler the Graph Database handler to use.
     * @param flowPath the Flow Path to install.
     * @return true on success, otherwise false.
     */
    static boolean addFlow(DBOperation dbHandler, FlowPath flowPath) {
	Future<Boolean> result = executor.submit(new AddFlowTask(dbHandler, flowPath, null));
	// NOTE: This function is blocking
	try {
	    return result.get();
	} catch (InterruptedException | ExecutionException e) {
	    return false;
	}
    }
    
    /**
     * Add a flow asynchronously by creating a database task.
     *
     * @param dbHandler the Graph Database handler to use.
     * @param flowPath the Flow Path to install.
     * @param datagridService the notification service for when the task is completed
     * @return true always
     */
    static boolean addFlow(DBOperation dbHandler, FlowPath flowPath, IDatagridService datagridService) {
	executor.submit(new AddFlowTask(dbHandler, flowPath, datagridService));
	// TODO: If we need the results, submit returns a Future that contains
	// the result. 
	return true;

    }
    
    /**
     * The basic parallelization unit for adding FlowPaths.
     * 
     * This is simply a wrapper for FlowDatabaseOperation.addFlow(), 
     * which also sends a notification if a datagrid services is provided
     */
    private final static class AddFlowTask implements Callable<Boolean> {
	private final DBOperation dbHandler;
	private final FlowPath flowPath;
	private final IDatagridService datagridService;

	AddFlowTask(DBOperation dbHandler,
		    FlowPath flowPath,
		    IDatagridService datagridService) {
	    this.dbHandler = dbHandler;

	    // Create a copy of the FlowPath object
	    Kryo kryo = kryoFactory.newKryo();
	    this.flowPath = kryo.copy(flowPath);
	    kryoFactory.deleteKryo(kryo);
	    
	    this.datagridService = datagridService;
	}
	
	@Override
	public Boolean call() throws Exception {
//	    String tag1 = "FlowDatabaseOperation.AddFlow." + flowPath.flowId();
	    String tag1 = "FlowDatabaseOperation.AddFlow";
//	    String tag2 = "FlowDatabaseOperation.NotificationSend.FlowEntry." + flowPath.flowId();
	    String tag2 = "FlowDatabaseOperation.NotificationSend.FlowEntry";
	    PerformanceMonitor.Measurement m;
	    m = PerformanceMonitor.start(tag1);
	    boolean success = FlowDatabaseOperation.addFlow(dbHandler, flowPath);
//	    PerformanceMonitor.stop(tag1);
	    m.stop();
	    m = PerformanceMonitor.start(tag2);
	    if(success) {
		if(datagridService != null) {
		    // Send notifications for each Flow Entry
		    for (FlowEntry flowEntry : flowPath.flowEntries()) {
			if (flowEntry.flowEntrySwitchState() !=
			    FlowEntrySwitchState.FE_SWITCH_NOT_UPDATED) {
			    continue;
			}
			//
			// Write the Flow Entry to the Datagrid
			//
			switch (flowEntry.flowEntryUserState()) {
			case FE_USER_ADD:
			    datagridService.notificationSendFlowEntryAdded(flowEntry);
			    break;
			case FE_USER_MODIFY:
			    datagridService.notificationSendFlowEntryUpdated(flowEntry);
			    break;
			case FE_USER_DELETE:
			    datagridService.notificationSendFlowEntryRemoved(flowEntry.flowEntryId());
			    break;
			case FE_USER_UNKNOWN:
			    assert(false);
			    break;
			}
		    }
		}
	    }
	    else {
		log.error("Error adding flow path {} to database", flowPath);
	    }
	    m.stop();
//	    PerformanceMonitor.report(tag1);
//	    PerformanceMonitor.report(tag2);
	    return success;

	}
    }

    /**
     * Delete a previously added flow by creating a database task, then waiting 
     * for the result.
     * 
     * Mostly, a wrapper for FlowDatabaseOperation.addFlow() which overs little
     * performance benefit.
     *
     * @param dbHandler the Graph Database handler to use.
     * @param flowId the Flow ID of the flow to delete.
     * @return true on success, otherwise false.
     */
    static boolean deleteFlow(DBOperation dbHandler, FlowId flowId) {
	Future<Boolean> result = executor.submit(new DeleteFlowTask(dbHandler, flowId, null));
	// NOTE: This function is blocking
	try {
	    return result.get();
	} catch (InterruptedException | ExecutionException e) {
	    return false;
	}
    }    
    
    /**
     * Delete a previously added flow asynchronously by creating a database task.
     *
     * @param dbHandler the Graph Database handler to use.
     * @param flowId the Flow ID of the flow to delete.
     * @param datagridService the notification service for when the task is completed
     * @return true always
     */
    static boolean deleteFlow(DBOperation dbHandler, FlowId flowId, IDatagridService datagridService) {
	executor.submit(new DeleteFlowTask(dbHandler, flowId, datagridService));
	// TODO: If we need the results, submit returns a Future that contains
	// the result. 
	return true;
    }
    
    /**
     * The basic parallelization unit for deleting FlowPaths.
     * 
     * This is simply a wrapper for FlowDatabaseOperation.deleteFlow(),
     * which also sends a notification if a datagrid services is provided
     */
    private final static class DeleteFlowTask implements Callable<Boolean> {
	private final DBOperation dbHandler;
	private final FlowId flowId;
	private final IDatagridService datagridService;

	DeleteFlowTask(DBOperation dbHandler, FlowId flowId, IDatagridService datagridService) {
	    this.dbHandler = dbHandler;

	    // Create a copy of the FlowId object
	    Kryo kryo = kryoFactory.newKryo();
	    this.flowId = kryo.copy(flowId);
	    kryoFactory.deleteKryo(kryo);

	    this.datagridService = datagridService;
	}
	@Override
	public Boolean call() throws Exception{
	    boolean success = FlowDatabaseOperation.deleteFlow(dbHandler, flowId);
	    if(success) {
		if(datagridService != null) {
		    datagridService.notificationSendFlowIdRemoved(flowId);
		}
	    }
	    else {
		log.error("Error removing flow path {} from database", flowId);
	    }
	    return success;
	}
    }
}
