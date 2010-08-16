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
 * Kinow ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Kinow.                                      
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 16/08/2010
 */
package hudson.plugins.ccm;

import hudson.plugins.ccm.model.CCMReport;
import hudson.plugins.ccm.model.Metric;

public class ReportSummary {

	public static String createReportSummary(
			CCMReport report,
			CCMReport previous) 
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append("<a href=\"" + CCMBuildAction.URL_NAME + "\">");
        builder.append(report.getAverageComplexityPerMethod());
        if(previous != null){
            printDifference(
            		report.getAverageComplexityPerMethod(),
            		previous.getAverageComplexityPerMethod(), 
            		builder);
        }
        builder.append(" average complexity per method.</a> in ");
        builder.append(report.getNumberOfMethods());
        if(previous != null){
            printDifference(
            		report.getNumberOfMethods(), 
            		previous.getNumberOfMethods(), 
            		builder);
        }
        builder.append(" methods. Project's total Cyclomatic Complexity: ");
        builder.append(report.getTotalComplexity());
        if(previous != null){
            printDifference(
            		report.getTotalComplexity(),
            		previous.getTotalComplexity(),
            		builder);
        }
        builder.append(".");
		
		return builder.toString();
	}

	public static String createReportSummaryDetails(
			CCMReport report,
			CCMReport previous) 
	{
		StringBuilder builder = new StringBuilder();

		builder.append("Top 15 CC methods.");
		builder.append("<table border=\"1\">\n");
		int i = 0 ;
        for(Metric metric: report.getMetrics()){
        	builder.append("<tr>\n");
            // TOTHINK: Perhaps we could make some reference to the previous 
            // metric's?
        	
        	// TBD: colors depending on complexity level 
        	builder.append("<td>"+metric.getFile()+"</td>");
        	builder.append("<td>"+metric.getUnit()+"</td>");
        	builder.append("<td>"+metric.getClassification()+"</td>");
        	builder.append("<td>"+metric.getComplexity()+"</td>\n");
        	
        	builder.append("</tr>\n");
        	i = i + 1;
        	if ( i == 15 ) 
        	{
        		break;
        	}
        }
        builder.append("</table>");
        return builder.toString();
	}
	
	private static void printDifference(float current, float previous, StringBuilder builder){
        float difference = current - previous;
        builder.append(" (");

        if(difference >= 0.0){
            builder.append('+');
        }
        builder.append(difference);
        builder.append(")");
    }

}

