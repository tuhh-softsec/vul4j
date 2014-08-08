/* Copyright 2014 Google Inc. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.google.jenkins.flakyTestHandler.plugin;

import com.google.common.collect.Maps;
import com.google.jenkins.flakyTestHandler.plugin.HistoryAggregatedFlakyTestResultAction.SingleTestFlakyStats;

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

import java.awt.Color;
import java.util.Map;

import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.util.ColorPalette;
import hudson.util.DataSetBuilder;
import hudson.util.Graph;
import hudson.util.ShiftedCategoryAxis;
import hudson.util.StackedAreaRenderer2;

/**
 * Generate a table to show for a given test, how many passes/fails at given revision
 *
 * @author Qingzhou Luo
 */
public class TestFlakyStatsOverRevision implements Action {

  public final AbstractProject<?, ?> project;

  public HistoryAggregatedFlakyTestResultAction parentAction;

  public Map<String, SingleTestFlakyStats> flakyStatsRevisionMap = Maps.newLinkedHashMap();

  public TestFlakyStatsOverRevision(AbstractProject<?, ?> project,
      HistoryAggregatedFlakyTestResultAction action) {
    this.project = project;
    this.parentAction = action;
  }

  private CategoryDataset buildDataSet() {
    DataSetBuilder<String, RevisionLabel> dsb = new DataSetBuilder<String, RevisionLabel>();

    int number = 1;
    for (Map.Entry<String, SingleTestFlakyStats> entry : flakyStatsRevisionMap.entrySet()) {
      dsb.add(entry.getValue().fail, "failed", new RevisionLabel(number, entry.getKey()));
      dsb.add(entry.getValue().pass, "passed", new RevisionLabel(number, entry.getKey()));
      number++;
    }

    return dsb.build();
  }

  private JFreeChart createChart(CategoryDataset dataset) {

    final JFreeChart chart = ChartFactory.createStackedAreaChart(
        null,                   // chart title
        null,                   // unused
        "count",                  // range axis label*/master
        dataset,                  // data
        PlotOrientation.VERTICAL, // orientation
        false,                     // include legend
        true,                     // tooltips
        false                     // urls
    );

    chart.setBackgroundPaint(Color.white);

    final CategoryPlot plot = chart.getCategoryPlot();

    plot.setBackgroundPaint(Color.WHITE);
    plot.setOutlinePaint(null);
    plot.setForegroundAlpha(0.8f);
    plot.setRangeGridlinesVisible(true);
    plot.setRangeGridlinePaint(Color.black);

    CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
    plot.setDomainAxis(domainAxis);
    domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
    domainAxis.setLowerMargin(0.0);
    domainAxis.setUpperMargin(0.0);
    domainAxis.setCategoryMargin(0.0);

    final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

    StackedAreaRenderer ar = new StackedAreaRenderer2() {

      @Override
      public String generateToolTip(CategoryDataset dataset, int row, int column) {
        RevisionLabel label = (RevisionLabel) dataset.getColumnKey(column);
        Number value = dataset.getValue(row, column);
        switch (row) {
          case 0:
            return label.revision + ": " + value + " fails";
          case 1:
            return label.revision + ": " + value + " passes";
          default:
            return label.revision;
        }
      }
    };
    plot.setRenderer(ar);
    ar.setSeriesPaint(0, ColorPalette.RED); // Fails.
    ar.setSeriesPaint(1, ColorPalette.BLUE); // Passes.

    // crop extra space around the graph
    plot.setInsets(new RectangleInsets(0, 0, 0, 5.0));

    return chart;
  }

  public Map<String, SingleTestFlakyStats> getFlakyStatsMap(String test) {
    if (test != null) {
      for (String testName : parentAction.getAggregatedTestFlakyStatsWithRevision().keySet()) {
        if (test.equals(getSafeTestName(testName))) {
          flakyStatsRevisionMap = parentAction.getAggregatedTestFlakyStatsWithRevision()
              .get(testName);
          return flakyStatsRevisionMap;
        }
      }
    }
    return Maps.newHashMap();
  }

  public static final class RevisionLabel implements Comparable<RevisionLabel> {

    public final String revision;

    public final int number;

    public RevisionLabel(int number, String revision) {
      this.number = number;
      this.revision = revision;
    }

    @Override
    public int compareTo(RevisionLabel that) {
      return this.number - that.number;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof RevisionLabel)) {
        return false;
      }
      RevisionLabel that = (RevisionLabel) o;
      return revision.equals(that.revision);
    }

    @Override
    public int hashCode() {
      return revision.hashCode();
    }

    @Override
    public String toString() {
      return "Rev #" + number;
    }
  }

  public Graph getStatsGraph() {
    return new Graph(-1, 900, 360) {
      protected JFreeChart createGraph() {
        return createChart(buildDataSet());
      }
    };
  }

  @Override
  public String getIconFileName() {
    return null;
  }

  @Override
  public String getDisplayName() {
    return null;
  }

  @Override
  public String getUrlName() {
    return "flakyStatsRevision";
  }

  /**
   * Get the name of a test that's URL-safe.
   *
   * @param testName input test name
   * @return name of the test with illegal characters replaced with '_'
   */
  public static String getSafeTestName(String testName) {
    StringBuilder buf = new StringBuilder(testName);
    for (int i = 0; i < buf.length(); i++) {
      char ch = buf.charAt(i);
      if (!Character.isJavaIdentifierPart(ch)) {
        buf.setCharAt(i, '_');
      }
    }
    return buf.toString();
  }
}
