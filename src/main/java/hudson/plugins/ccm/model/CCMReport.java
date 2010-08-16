/**
 *	 __                                        
 *	/\ \      __                               
 *	\ \ \/'\ /\_\    ___     ___   __  __  __  
 *	 \ \ , < \/\ \ /' _ `\  / __`\/\ \/\ \/\ \ 
 *	  \ \ \\`\\ \ \/\ \/\ \/\ \L\ \ \ \_/ \_/ \
 *	   \ \_\ \_\ \_\ \_\ \_\ \____/\ \___x___/'
 *	    \/_/\/_/\/_/\/_/\/_/\/___/  \/__//__/  
 *                                          
 * Copyright (c) 1999-present Kinow
 * Casa Verde - São Paulo - SP. Brazil.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Kinow ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Kinow.                                      
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 16/08/2010
 */
package hudson.plugins.ccm.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Report of the cyclomatic complexity from the project.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 16/08/2010
 */
public class CCMReport 
implements Serializable {

	private int numberOfMethods;
	private float averageComplexityPerMethod;
	private int totalComplexity;

	private List<Metric> metrics = new ArrayList<Metric>();
	
	public void setMetrics(List<Metric> metrics) {
		this.metrics = metrics;
	}

	public List<Metric> getMetrics() {
		return this.metrics;
	}

	/**
	 * Updates report numbers such as number of methods, total complexity 
	 * and average complexity per method. These numbers are updated based on 
	 * the values parsed from ccm xml result file.
	 */
	public void updateNumbers()
	{
		
		// CCM generates one metric entry for each method in a Class
		this.numberOfMethods = this.metrics.size();
		
		int sum = 0;
		for(Metric metric : this.metrics)
		{
			sum += metric.getComplexity();
		}
		this.totalComplexity = sum;
		
		if ( sum > 0 )
		{
			this.averageComplexityPerMethod = (float)sum/this.numberOfMethods;
		}
		
	}

	/**
	 * @return Total of methods.
	 */
	public int getNumberOfMethods() 
	{
		return this.numberOfMethods;
	}
	
	/**
	 * @return A simple average of the total CC per methods.
	 */
	public float getAverageComplexityPerMethod() 
	{
		return this.averageComplexityPerMethod;
	}
	
	/**
	 * @return Total CC of the project. It is the sum of all CC of all methods.
	 */
	public int getTotalComplexity() 
	{
		return totalComplexity;
	}
	
	@Override
	public String toString() {
		return "CCMReport [averageComplexityPerMethod="
				+ averageComplexityPerMethod + ", numberOfMethods="
				+ numberOfMethods + ", totalComplexity=" + totalComplexity
				+ "]";
	}

	
}
