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
package hudson.plugins.ccm.util;

import hudson.plugins.ccm.CCMBuildAction;
import hudson.plugins.ccm.CCMResult;
import hudson.plugins.ccm.model.CCMReport;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

/**
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 16/08/2010
 */
public class ChartUtil
{
	
	/**
	 * Creates the CCM Trend Chart.
	 * 
	 * @param dataset of XY series
	 * @return a chart
	 */
	public static JFreeChart buildXYChart(XYDataset dataset) {

        final JFreeChart chart = ChartFactory.createXYLineChart(
                null,                   // chart title
                "Build #",                   // unused
                null,                    // range axis label
                dataset,                  // data
                PlotOrientation.VERTICAL, // orientation
                true,                     // include legend
                true,                     // tooltips
                false                     // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        final LegendTitle legend = chart.getLegend();
        legend.setPosition(RectangleEdge.RIGHT);

        chart.setBackgroundPaint(Color.white);

        final XYPlot plot = chart.getXYPlot();

        plot.setBackgroundPaint(Color.lightGray);
        //    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(1, true);
        plot.setRenderer(renderer);

     // change the auto tick unit selection to integer units only...
        /*final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());*/
        // OPTIONAL CUSTOMISATION COMPLETED.

        return chart;
    }
	
	/**
	 * Creates the XY dataset required to create the CCM Trend graph.
	 * 
	 * @param lastAction
	 * @return
	 */
	public static final XYDataset createXYDataset( CCMBuildAction lastAction )
	{
		
		CCMBuildAction tempAction = lastAction;
		final XYSeriesCollection dataset = new XYSeriesCollection();
		final XYSeries avgCCseries = new XYSeries( "Average Complexity" );
		final XYSeries totalCCseries = new XYSeries( "Total Complexity" );
		final XYSeries numberOfMethodsSeries = new XYSeries( "Number of Methods" );
		dataset.addSeries(avgCCseries);
		dataset.addSeries(totalCCseries);
		dataset.addSeries(numberOfMethodsSeries);
		do 
		{
			CCMResult result = tempAction.getResult();
			CCMReport report = result.getReport();
			int buildNumber = tempAction.getBuild().number;			
			avgCCseries.add(buildNumber, report.getAverageComplexityPerMethod());
			totalCCseries.add(buildNumber, report.getTotalComplexity());
			numberOfMethodsSeries.add(buildNumber, report.getNumberOfMethods());
			tempAction = tempAction.getPreviousAction();
		} while ( tempAction != null );
		
		return dataset;
		
	}
	
}
