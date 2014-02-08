package com.tinkerpop.blueprints.impls.ramcloud;

import edu.stanford.ramcloud.JRamCloud;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RamCloudWrite {
    public enum PerfMonEnum {
	WRITE,
	INDEXWRITE
    }
    private final static Logger log = LoggerFactory.getLogger(RamCloudGraph.class);
    
    public static boolean writeWithRules(long tableId, byte[] rcKey, byte[] rcValue, long expectedVersion, RamCloudGraph graph, PerfMonEnum perfMonKind) {
	JRamCloud.RejectRules rules = graph.getRcClient().new RejectRules();

	if (expectedVersion == 0) {
	    rules.setExists();
	} else {
	    rules.setNeVersion(expectedVersion);
	}

	PerfMon pm = PerfMon.getInstance();
	try {
	    JRamCloud rcClient = graph.getRcClient();
	    if (perfMonKind.equals(PerfMonEnum.WRITE)) {
		pm.write_start("RamCloudIndex writeWithRules()");
	    } else if (perfMonKind.equals(PerfMonEnum.INDEXWRITE)) {
		pm.indexwrite_start("RamCloudIndex writeWithRules()");
	    }
	    rcClient.writeRule(tableId, rcKey, rcValue, rules);
            if (perfMonKind.equals(PerfMonEnum.WRITE)) {
                pm.write_end("RamCloudIndex writeWithRules()");
            } else if (perfMonKind.equals(PerfMonEnum.INDEXWRITE)) {
                pm.indexwrite_end("RamCloudIndex writeWithRules()");
            }
	} catch (Exception e) {
	    if (perfMonKind.equals(PerfMonEnum.WRITE)) {
		pm.write_end("RamCloudIndex writeWithRules()");
		pm.write_condfail("RamCloudIndex writeWithRules()");
	    	log.debug("Cond. Write property: {} failed {} expected version: {}", RamCloudVertex.rcKeyToId(rcKey), e, expectedVersion);
	    } else if (perfMonKind.equals(PerfMonEnum.INDEXWRITE)) {
		pm.indexwrite_end("RamCloudIndex writeWithRules()");
		pm.indexwrite_condfail("RamCloudIndex writeWithRules()");
	    	log.debug("Cond. Write index property: {} failed {} expected version: {}", RamCloudIndex.rcKeyToIndexName(rcKey), e, expectedVersion);
	    }
	    return false;
	}
	return true;
    }
}
