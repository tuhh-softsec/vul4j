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

/**
 *
 * @author nickkaranatsios
 */
public class RamCloudDBConnection extends DBConnection {
    private RamCloudGraph graph;

    public RamCloudDBConnection(final String dbConfigFile) {
        System.out.println("dbconfigfile is + "+ dbConfigFile);
        final String coordinatorURL = open(getConfiguration(new File(dbConfigFile)));
        graph = new RamCloudGraph(coordinatorURL);
    }
    
    @Override
    public FramedGraph getFramedGraph() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addEventListener(LocalGraphChangedListener listener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean isValid() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void commit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void rollback() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
