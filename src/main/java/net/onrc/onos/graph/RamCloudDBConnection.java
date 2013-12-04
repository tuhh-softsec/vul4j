/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.onrc.onos.graph;

import com.tinkerpop.blueprints.impls.ramcloud.RamCloudGraph;
import com.tinkerpop.frames.FramedGraph;
import java.io.File;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nickkaranatsios
 */
public class RamCloudDBConnection extends DBConnection {
    private RamCloudGraph graph;
    private static Logger log = LoggerFactory.getLogger(RamCloudDBConnection.class);
    
    public static final ThreadLocal<RamCloudGraph> RamCloudThreadLocal = new ThreadLocal<RamCloudGraph>();

    public RamCloudDBConnection(final String dbConfigFile) {
        //final String coordinatorURL = open(getConfiguration(new File(dbConfigFile)));
	//System.out.println("coordinatorURL "+ coordinatorURL);
        //graph = new RamCloudGraph(coordinatorURL);
	//graph = RamCloudThreadLocal.get();
	System.out.println("ThreadId = " + Thread.currentThread().getId() + "graph = " + graph);
	if (graph == null) {
	    graph = new RamCloudGraph("fast+udp:host=10.128.4.104,port=12246");
	    RamCloudThreadLocal.set(graph);
	}
    }
    
    @Override
    public FramedGraph getFramedGraph() {
        if (isValid()) {
            FramedGraph<RamCloudGraph> fg = new FramedGraph<RamCloudGraph>(graph);
            return fg;
        } else {
            log.error("new FramedGraph failed");
            return null;
        }
    }

    @Override
    public void addEventListener(LocalGraphChangedListener listener) {
        //TO-DO
    }

    @Override
    public Boolean isValid() {
        return (graph != null);
    }

    @Override
    public void commit() {
        try {
            graph.commit();
        } catch (Exception e) {
            log.error("{}", e.toString());
        }
    }

    @Override
    public void rollback() {
        try {
            graph.rollback();
        } catch (Exception e) {
            log.error("{}", e.toString());
        }
    }

    @Override
    public void close() {
        commit();
    }
    
     private static final Configuration getConfiguration(final File dirOrFile) {
        if (dirOrFile == null) {
            throw new IllegalArgumentException("Need to specify a configuration file or storage directory");
        }

        if (!dirOrFile.isFile()) {
            throw new IllegalArgumentException("Location of configuration must be a file");
        }

        try {
            return new PropertiesConfiguration(dirOrFile);
        } catch (ConfigurationException e) {
            throw new IllegalArgumentException("Could not load configuration at: " + dirOrFile, e);
        }
    }
     
     private String open(final Configuration configuration) {
         final String coordinatorURL = configuration.getString("ramcloud.coordinator", null);
         if (coordinatorURL == null) {
             throw new RuntimeException("Configuration must contain a valid 'coordinatorURL' setting");
         }
         return coordinatorURL;
     }
}
