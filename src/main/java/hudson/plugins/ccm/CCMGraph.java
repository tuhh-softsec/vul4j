/*
 * The MIT License
 *
 * Copyright (c) <2011> <Bruno P. Kinoshita>
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

import hudson.model.AbstractBuild;
import hudson.util.Graph;
import hudson.util.ShiftedCategoryAxis;

import java.awt.BasicStroke;
import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

/**
 * CCM Graph object. It is set in a Stapler Response object to show the 
 * CCM trend graph to the user.
 * 
 * @author Bruno P. Kinoshita - http://www.kinoshita.eti.br
 * @since 2.6
 */
public class CCMGraph 
extends Graph
{

	/**
	 * Y label name.
	 */
	private final String yLabel;
	
	/**
	 * X label name.
	 */
	private final String xLabel;
	
	/**
	 * Graph data.
	 */
	private final CategoryDataset categoryDataset;

	/**
	 * Default chart width in pixels.
	 */
	public static final int DEFAULT_CHART_WIDTH = 500;
	
	/**
	 * Default chart height in pixels.
	 */
	public static final int DEFAULT_CHART_HEIGHT = 200;

	/**
	 * Constructs a new styled trend graph for given dataset.
	 * 
	 * @param owner Build which the graph is associated to.
	 * @param categoryDataset Category data for graph.
	 * @param yLabel Y label name.
	 * @param xLabel X label name.
	 * @param chartWidth Chart width in pixels.
	 * @param chartHeight Chart height in pixels.
	 */
	public CCMGraph( 
			AbstractBuild<?, ?> owner, 
			CategoryDataset categoryDataset, 
			String yLabel, 
			String xLabel )
	{
		super( owner.getTimestamp(), DEFAULT_CHART_WIDTH, DEFAULT_CHART_HEIGHT );
		this.yLabel = yLabel;
		this.xLabel = xLabel;
		this.categoryDataset = categoryDataset;
	}
	
	/**
	 * Creates CCM trend graph.
	 * 
	 * @return the JFreeChart graph object.
	 */
	protected JFreeChart createGraph()
	{
		final JFreeChart chart = ChartFactory.createLineChart(null,
				null, yLabel, categoryDataset, PlotOrientation.VERTICAL, true,
				true, false);

		final LegendTitle legend = chart.getLegend();
		legend.setPosition(RectangleEdge.RIGHT);

		chart.setBackgroundPaint(Color.white);

		final CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setForegroundAlpha(0.8f);
		plot.setBackgroundPaint(Color.white);
		plot.setRangeGridlinePaint(Color.darkGray);

		final CategoryAxis domainAxis = new ShiftedCategoryAxis(xLabel);
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		domainAxis.setLowerMargin(0.0);
		domainAxis.setUpperMargin(0.0);
		domainAxis.setCategoryMargin(0.0);
		plot.setDomainAxis(domainAxis);

		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setAutoRange(false);
		rangeAxis.setAutoRangeMinimumSize(5);
		rangeAxis.setLowerBound(0);

		final CategoryItemRenderer renderer = plot.getRenderer();
		
		renderer.setSeriesStroke(0, new BasicStroke(3.0f,
                BasicStroke.CAP_BUTT,
                BasicStroke.CAP_BUTT,
                1.0f,
                new float[] {1.0f, 1.0f},
                0.0f));
		renderer.setSeriesPaint( 0, new Color(0, 145, 0) ); // total complexity
		
		renderer.setSeriesStroke(1, new BasicStroke(3.0f,
                BasicStroke.CAP_BUTT,
                BasicStroke.CAP_BUTT,
                1.0f,
                new float[] {1.0f, 1.0f},
                0.0f));
		renderer.setSeriesPaint( 1, new Color(207, 69, 21) ); // number of methods
		
		renderer.setSeriesStroke(2, new BasicStroke(3.0f,
                BasicStroke.CAP_BUTT,
                BasicStroke.CAP_BUTT,
                1.0f,
                new float[] {1.0f, 1.0f},
                0.0f));
		renderer.setSeriesPaint( 2, new Color(0, 64, 128) ); // average complexity

		plot.setInsets(new RectangleInsets(5.0, 0, 0, 5.0));

		return chart;
	}
	
}
