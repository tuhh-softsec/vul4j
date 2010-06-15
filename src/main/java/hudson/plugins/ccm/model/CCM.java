package hudson.plugins.ccm.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CCM implements Serializable {

	private List<Metric> metrics;
	
	public CCM() {
		metrics = new ArrayList<Metric>();
	}

	public List<Metric> getMetrics() {
		return metrics;
	}

	public void addMetric(Metric metric)
	{
		this.metrics.add(metric);
	}
	
}
