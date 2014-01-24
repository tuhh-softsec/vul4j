/* Copyright (c) 2013 Stanford University
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR(S) DISCLAIM ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL AUTHORS BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.tinkerpop.blueprints.impls.ramcloud;

import java.awt.Dimension;
import java.util.logging.Level;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.oupls.jung.GraphJung;

import edu.uci.ics.jung.algorithms.layout.BalloonLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.DAGLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

public class JUNGExamples {

  public JUNGExamples() {
    // TODO Auto-generated constructor stub
  }
  
  public static void calculatePageRank(Graph graph) {
    PageRank<Vertex,Edge> pageRank = new PageRank<Vertex, Edge>(new GraphJung(graph), 0.15d);
    pageRank.evaluate();
    
    for (Vertex vertex : graph.getVertices()) {
      System.out.println("The PageRank score of " + vertex + " is: " + pageRank.getVertexScore(vertex));
    }
  }

  public static void displayGraph(Graph graph) {
    GraphJung gj = new GraphJung(graph);
    
    Layout<Vertex, Edge> layout = new CircleLayout<Vertex, Edge>(gj);
    layout.setSize(new Dimension(600, 600));
    BasicVisualizationServer<Vertex, Edge> viz = new BasicVisualizationServer<Vertex, Edge>(layout);
    viz.setPreferredSize(new Dimension(650, 650));

    Transformer<Vertex, String> vertexLabelTransformer = new Transformer<Vertex, String>() {
      public String transform(Vertex vertex) {
        return (String) vertex.getProperty("name");
      }
    };

    Transformer<Edge, String> edgeLabelTransformer = new Transformer<Edge, String>() {
      public String transform(Edge edge) {
        return edge.getLabel();
      }
    };

    viz.getRenderContext().setEdgeLabelTransformer(edgeLabelTransformer);
    viz.getRenderContext().setVertexLabelTransformer(vertexLabelTransformer);

    JFrame frame = new JFrame("TinkerPop");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(viz);
    frame.pack();
    frame.setVisible(true);
    
    try {
      System.in.read();
    } catch(Exception e) {
      // poop
    }
  }
  
  public static void main(String[] args) {
    Graph graph = new RamCloudGraph(Level.FINER);
    
    FurnaceExamples.generateDistributionGraph(graph, "ex");
    
    //calculatePageRank(graph);
    
    displayGraph(graph);
    
    graph.shutdown();
  }

}
