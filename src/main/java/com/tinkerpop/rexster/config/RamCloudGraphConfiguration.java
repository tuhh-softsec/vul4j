package com.tinkerpop.rexster.config;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.ramcloud.RamCloudGraph;
import com.tinkerpop.rexster.config.GraphConfiguration;
import org.apache.commons.configuration.Configuration;


public class RamCloudGraphConfiguration implements GraphConfiguration {

    public Graph configureGraphInstance(final Configuration properties) throws GraphConfigurationException {
        return new RamCloudGraph("fast+udp:host=127.0.0.1,port=12246");
    }

}
