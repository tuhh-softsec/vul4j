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

import java.util.logging.Level;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.furnace.generators.CommunityGenerator;
import com.tinkerpop.furnace.generators.DistributionGenerator;
import com.tinkerpop.furnace.generators.NormalDistribution;

public class FurnaceExamples {

  public FurnaceExamples() {
    // TODO Auto-generated constructor stub
  }

  public static int generateCommunityGraph(Graph graph, String label) {
    CommunityGenerator cg = new CommunityGenerator(label);
    int numEdges;
    
    for(int i = 0; i<30; i++) {
      graph.addVertex(null);
    }
    
    cg.setCommunityDistribution(new NormalDistribution(2.0));
    cg.setDegreeDistribution(new NormalDistribution(2.0));
    numEdges = cg.generate(graph, 3, 60);
    
    return numEdges;
  }
  
  public static int generateDistributionGraph(Graph graph, String label) {
    DistributionGenerator dg = new DistributionGenerator(label);
    int numEdges;
    
    for(int i = 0; i<10; i++) {
      graph.addVertex(null);
    }
    
    dg.setAllowLoops(true);
    dg.setInDistribution(new NormalDistribution(2.0));
    dg.setOutDistribution(new NormalDistribution(2.0));
    numEdges = dg.generate(graph, 20);
    
    return numEdges;
  }
  
  public static void main(String[] args) {
    Graph graph = new RamCloudGraph(Level.FINER);
    
    //generateCommunityGraph(graph, "HippieCommune");
    
    //generateDistributionGraph(graph, "HippieRefuge");
    
    graph.shutdown();
  }

}
