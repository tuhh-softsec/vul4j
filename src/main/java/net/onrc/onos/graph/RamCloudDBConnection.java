/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.onrc.onos.graph;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.ramcloud.RamCloudGraph;
import com.tinkerpop.frames.FramedGraph;
import java.io.File;
import java.util.Set;
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
    private FramedGraph<RamCloudGraph> fg;
    private static Logger log = LoggerFactory.getLogger(RamCloudDBConnection.class);
    
    public RamCloudDBConnection(final String dbConfigFile) {
        final String coordinatorURL = open(getConfiguration(new File(dbConfigFile)));
	graph = new RamCloudGraph(coordinatorURL);
	Set<String> s = graph.getIndexedKeys(Vertex.class);
        if (!s.contains("dpid")) {
            graph.createKeyIndex("dpid", Vertex.class);
        }
        if (!s.contains("port_id")) {
            graph.createKeyIndex("port_id", Vertex.class);
        }
        if (!s.contains("type")) {
            graph.createKeyIndex("type", Vertex.class);
        }
        if (!s.contains("dl_addr")) {
            graph.createKeyIndex("dl_addr", Vertex.class);
        }
        if (!s.contains("flow_id")) {
            graph.createKeyIndex("flow_id", Vertex.class);
        }
        if (!s.contains("flow_entry_id")) {
            graph.createKeyIndex("flow_entry_id", Vertex.class);
        }
        if (!s.contains("switch_state")) {
            graph.createKeyIndex("switch_state", Vertex.class);
	}
	if (!s.contains("ipv4_address")) {
	    graph.createKeyIndex("ipv4_address", Vertex.class);
	}
        fg = new FramedGraph<RamCloudGraph>(graph);
    }
    
    @Override
    public FramedGraph getFramedGraph() {
        if (isValid()) {
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
	final String coordinatorIp = configuration.getString("ramcloud.coordinatorIp", null);
	final String coordinatorPort = configuration.getString("ramcloud.coordinatorPort", null);
	final String coordinatorURL = coordinatorIp + "," + coordinatorPort;
	if (coordinatorURL == null) {
	    throw new RuntimeException("Configuration must contain a valid 'coordinatorURL' setting");
	}
	return coordinatorURL;
    }
}
