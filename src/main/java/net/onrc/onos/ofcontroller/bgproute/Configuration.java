package net.onrc.onos.ofcontroller.bgproute;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

public class Configuration {
	List<String> switches;
	Map<String, GatewayRouter> gateways;
	
	public Configuration() {
		// TODO Auto-generated constructor stub
	}

	public List<String> getSwitches() {
		return switches;
	}

	@JsonProperty("switches")
	public void setSwitches(List<String> switches) {
		this.switches = switches;
	}

	public Map<String, GatewayRouter> getGateways() {
		return gateways;
	}

	@JsonProperty("gateways")
	public void setGateways(Map<String, GatewayRouter> gateways) {
		this.gateways = gateways;
	}

}
