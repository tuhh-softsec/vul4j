package net.onrc.onos.ofcontroller.flowmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.util.FlowId;
import net.onrc.onos.ofcontroller.util.FlowPath;

public class ParallelFlowDatabaseOperation extends FlowDatabaseOperation {
    private final static Logger log = LoggerFactory.getLogger(FlowDatabaseOperation.class);

    private final static int numThreads = 20;
    private final static ExecutorService executor = Executors.newFixedThreadPool(numThreads);

    static ArrayList<FlowPath> getAllFlows(GraphDBOperation dbHandler) {
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

    static ArrayList<FlowPath> getFlows(GraphDBOperation dbHandler,
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
    
    private final static class GetFlowTask implements Callable<FlowPath> {
	private final GraphDBOperation dbHandler;
	private final FlowId flowId;
	
	GetFlowTask(GraphDBOperation dbHandler, FlowId flowId) {
	    this.dbHandler = dbHandler;
	    this.flowId = flowId;
	}
	@Override
	public FlowPath call() throws Exception{
	    return getFlow(dbHandler, flowId);
	}
    }
    
    static boolean addFlow(GraphDBOperation dbHandler, FlowPath flowPath) {
	executor.submit(new AddFlowTask(dbHandler, flowPath));
	// TODO: If we need the results, submit returns a Future that contains
	// the result. 
	return true;
    }
    
    private final static class AddFlowTask implements Callable<Boolean> {
	private final GraphDBOperation dbHandler;
	private final FlowPath flowPath;
	
	AddFlowTask(GraphDBOperation dbHandler, FlowPath flowPath) {
	    this.dbHandler = dbHandler;
	    this.flowPath = flowPath;
	}
	
	@Override
	public Boolean call() throws Exception {
	    return FlowDatabaseOperation.addFlow(dbHandler, flowPath);
	}
    }

    static boolean deleteFlow(GraphDBOperation dbHandler, FlowId flowId) {
	executor.submit(new DeleteFlowTask(dbHandler, flowId));
	// TODO: If we need the results, submit returns a Future that contains
	// the result. 
	return true;
    }
    
    private final static class DeleteFlowTask implements Callable<Boolean> {
	private final GraphDBOperation dbHandler;
	private final FlowId flowId;
	
	DeleteFlowTask(GraphDBOperation dbHandler, FlowId flowId) {
	    this.dbHandler = dbHandler;
	    this.flowId = flowId;
	}
	@Override
	public Boolean call() throws Exception{
	    return FlowDatabaseOperation.deleteFlow(dbHandler, flowId);
	}
    }
}
