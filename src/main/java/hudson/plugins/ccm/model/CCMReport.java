/* 
 * The MIT License
 * 
 * Copyright (c) 2010 Bruno P. Kinoshita <http://www.kinoshita.eti.br>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.plugins.ccm.model;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Report of the cyclomatic complexity from the project.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
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
	
	public String getFormattedAverageComplexityPerMethod()
	{
		return new DecimalFormat("###.##").format( averageComplexityPerMethod );
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
