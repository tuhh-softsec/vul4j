/**
 * 
 */
package hudson.plugins.ccm.report;

import hudson.plugins.ccm.model.CCM;
import hudson.plugins.ccm.model.Metric;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author Bruno P. Kinoshita
 *
 */
public class CCMReport 
implements Serializable 
{
	
	private CCM ccm;
	
	/**
	 * @param ccmXmlNode
	 */
	public CCMReport(CCM ccmXmlNode) {
		ccm = ccmXmlNode;
	}

	public CCM getCCM()
	{
		return ccm;
	}
	
	public int getMetricsCount()
	{
		return this.ccm.getMetrics().size();
	}
	
	public List<Metric> getMetrics()
	{
		return this.ccm.getMetrics();
	}
	
	public float getAverageComplexity()
	{
		int total = 0;
		
		List<Metric> metrics = getMetrics();
		
		for (Iterator<Metric> iterator = metrics.iterator(); iterator.hasNext();) {
			Metric metric = iterator.next();
			total += metric.getComplexity();
		}
		
		return total / getMetricsCount();
	}
	
	public String getAverageClassification()
	{
		String average = "N/A";
		List<Metric> metrics = getMetrics();
		
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		
		for (Iterator<Metric> iterator = metrics.iterator(); iterator.hasNext();) {
			final Metric metric = iterator.next();
			final String key = metric.getClassification();
			if ( map.containsKey( key ))
			{	
				Integer value = map.get(key);
				Integer newValue = Integer.valueOf((value.intValue() +1));
				map.put(key, newValue);
			} else {
				map.put(key, new Integer(1));
			}
		}
		
		int higher = -1;		
		Set<Entry<String, Integer>> s = map.entrySet();
		for ( Iterator<Entry<String, Integer>> it = s.iterator() ; it.hasNext() ; )
		{
			Entry<String, Integer> entry = it.next();
			if ( entry.getValue().intValue() > higher )
			{
				higher = entry.getValue();
				average = entry.getKey();
			}
		}
		
		return average;
	}
	
}
