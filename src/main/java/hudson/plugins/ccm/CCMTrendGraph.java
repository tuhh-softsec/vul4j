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
import hudson.util.ChartUtil;
import hudson.util.ColorPalette;
import hudson.util.DataSetBuilder;
import hudson.util.Graph;
import hudson.util.ShiftedCategoryAxis;
import hudson.util.StackedAreaRenderer2;

import java.awt.Color;
import java.awt.Paint;
import java.util.Calendar;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleInsets;

/**
 * @author Bruno P. Kinoshita
 *
 */
public class CCMTrendGraph 
extends Graph {

	private CCMBuildAction lastAction;

	public CCMTrendGraph(CCMBuildAction lastAction, Calendar timestamp, int defaultW, int defaultH) {
		super(timestamp, defaultW, defaultH);
		this.lastAction = lastAction;
	}
	
	protected DataSetBuilder<String, String> createDataSet()
	{
		DataSetBuilder<String, String> data = new DataSetBuilder<String, String>();
		
		CCMBuildAction tempAction = lastAction;
		
		do {
			CCMResult result = tempAction.getResult();
			CCMReport report = result.getReport();
			
			data.add( report.getAverageComplexityPerMethod(), "0Average", "Label?");
			data.add(report.getNumberOfMethods(), "1Number", "Label2?");
			data.add(report.getTotalComplexity(), "2Total", "Label3?");
			
			tempAction = tempAction.getPreviousAction();		
		} while(tempAction != null);
		
        return data;
	}

	/* (non-Javadoc)
	 * @see hudson.util.Graph#createGraph()
	 */
	@Override
	protected JFreeChart createGraph() {
		final CategoryDataset dataset = createDataSet().build();
		
		final JFreeChart chart = ChartFactory.createStackedAreaChart(null, // chart
                // title
		null, // unused
		"MyLabel", // range axis label
		dataset, // data
		PlotOrientation.VERTICAL, // orientation
		false, // include legend
		true, // tooltips
		false // urls
		);
		
		chart.setBackgroundPaint(Color.white);
		
		final CategoryPlot plot = chart.getCategoryPlot();

        // plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setBackgroundPaint(Color.WHITE);
		plot.setOutlinePaint(null);
        plot.setForegroundAlpha(0.8f);
        // plot.setDomainGridlinesVisible(true);
        // plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.black);
        
        CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
        plot.setDomainAxis(domainAxis);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
        domainAxis.setCategoryMargin(0.0);
        
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        ChartUtil.adjustChebyshev(dataset, rangeAxis);
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setAutoRange(true);
        
        StackedAreaRenderer ar = new StackedAreaRenderer2() {
            @Override
            public Paint getItemPaint(int row, int column) {
                return super.getItemPaint(row, column);
            }

            /*@Override
            public String generateURL(CategoryDataset dataset, int row,
                    int column) {
                String label = (String) dataset.getColumnKey(column);
                return label.getUrl();
            }*/

            @Override
            public String generateToolTip(CategoryDataset dataset, int row,
                    int column) {
            	return "Tooltip";
            }
        };
        plot.setRenderer(ar);
        ar.setSeriesPaint(0,ColorPalette.RED); // Failures.
        ar.setSeriesPaint(1,ColorPalette.YELLOW); // Skips.
        ar.setSeriesPaint(2,ColorPalette.BLUE); // Total.

        // crop extra space around the graph
        plot.setInsets(new RectangleInsets(0, 0, 0, 5.0));

        return chart;
	}

	
	
}
