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
package hudson.plugins.ccm;

import hudson.plugins.ccm.parser.CCMReport;
import hudson.plugins.ccm.parser.Metric;
import hudson.plugins.ccm.util.Messages;

import java.text.DecimalFormat;
import java.util.List;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 1.0
 */
public class ReportSummary {

	public static String createReportSummary(
			CCMReport report,
			CCMReport previous) 
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append("<a href=\"" + CCMBuildAction.URL_NAME + "\">");
        builder.append(report.getFormattedAverageComplexityPerMethod());
        if(previous != null){
            printDifference(
            		report.getAverageComplexityPerMethod(),
            		previous.getAverageComplexityPerMethod(), 
            		builder);
        }
        builder.append( " " + Messages.CCM_Report_Summary_AverageComplexity() + "</a> ");
        builder.append( Messages.CCM_Report_Summary_In() + " ");
        builder.append( report.getNumberOfMethods() );
        if(previous != null){
            printDifference(
            		report.getNumberOfMethods(), 
            		previous.getNumberOfMethods(), 
            		builder);
        }
        builder.append(" " + Messages.CCM_Report_Summary_TotalCC() + " ");
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

		builder.append( Messages.CCM_Report_Summary_TopFifteen() );
		builder.append("<table border=\"1\">\n");
		int i = 0 ;
		
		List<Metric> metrics = report.getMetrics();
		if ( metrics.size() > 0 )
		{
			builder.append("<tr>\n");
			builder.append("<th style='background-color: #eee;'>" + Messages.CCM_Report_Details_FileTitle() + "</th>\n");
			builder.append("<th style='background-color: #eee;'>" + Messages.CCM_Report_Details_UnitTitle() + "</th>\n");
			builder.append("<th style='background-color: #eee;'>" + Messages.CCM_Report_Details_ClassificationTitle() + "</th>\n");
			builder.append("<th style='background-color: #eee;'>" + Messages.CCM_Report_Details_ComplexityTitle() + "</th>\n");
			builder.append("</tr>\n");
		}
        for(Metric metric: metrics){
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
        builder.append(new DecimalFormat("###.##").format( difference ));
        builder.append(")");
    }

}

