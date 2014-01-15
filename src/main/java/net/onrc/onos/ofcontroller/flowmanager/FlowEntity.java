/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.onrc.onos.ofcontroller.flowmanager;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Edge;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.onrc.onos.graph.DBOperation;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IBaseObject;
import com.tinkerpop.blueprints.impls.ramcloud.RamCloudGraph;
import com.tinkerpop.blueprints.impls.ramcloud.RamCloudVertex;
import java.util.List;
import java.util.Set;

/**
 *
 * @author nickkaranatsios
 */
public class FlowEntity implements FlowEntityManager {
    private String primaryKey;
    private Class<?> hasMany;
    private Collection<?> many = new ArrayList<>();
    private Map<String, Object> properties;
    private Map<String, Map<String, Object>> operations = new HashMap<>();
    private ArrayList<Object> children = new ArrayList<>();
    private ArrayList<Object> edges = new ArrayList<>();
    private int opCount;
    public Direction dir;

    public FlowEntity() {
        opCount = 0;
    }
    
    private class EntityEdge {
        private Object src;
        private Object dst;
        private Direction dir;
        private String label;
        private DBOperationType op;
        
        public EntityEdge(Object src, Object dst, DBOperationType op, Direction dir, String label) {
            this.src = src;
            this.dst = dst;
            this.dir = dir;
            this.label = label;
            this.op = op;
        }
        
        public EntityEdge(Object src, Object dst, String label) {
            this.src = src;
            this.dst = dst;
            this.label = label;
        }
        
        @Override
        public String toString() {
            return "EntityEdge: " + src + " " + dst + " " + label;
        }
    }

    private class RamCloudEdgeEntity implements Edge {
        private Vertex src;
        private Vertex dst;
        private Direction direction;
        private String label;

        public RamCloudEdgeEntity(Vertex src, Vertex dst, Direction direction, String label) {
            this.src = src;
            this.dst = dst;
            this.direction = direction;
            this.label = label;
        }
        
        @Override
        public Vertex getVertex(com.tinkerpop.blueprints.Direction dir) throws IllegalArgumentException {
            if (dir == Direction.IN) {
                System.out.println("returning in vertex " + this.dst.getId());
                return dst;
            } else if (dir == Direction.OUT) {
                System.out.println("returning out vertex " + this.src.getId());
                return src;
            }
            return null;
        }

        @Override
        public String getLabel() {
            return this.label;
        }

        @Override
        public <T> T getProperty(String key) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Set<String> getPropertyKeys() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setProperty(String key, Object value) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public <T> T removeProperty(String key) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Object getId() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    
    @Override
    public void setPrimaryKey(String key) {
        primaryKey = key;
    }
    
    @Override
    public String getPrimaryKey() {
        return primaryKey;
    }

    @Override
    public void hasMany(Class<?> cClass) {
        hasMany = cClass;
    }
    
    @Override
    public void operationBegin(String opName) {
        properties = new HashMap<>();
        operations.put(getOpKey(opName), properties);
        opCount++;
    }
    
    @Override
    public void operationEnd(String opName) {
        String opKey = getOpKey(opName);
        if (operations.containsKey(opKey)) {
            System.out.println(operations);
        }
        
    }
    
    
    private String getOpKey(String opName) {
        return opName + new Integer(opCount).toString();
        
    }

    @Override
    public void setProperty(String propertyName, Object value) {
        properties.put(propertyName, value);
    }

    @Override
    public FlowEntityManager append(Object entity) {
        children.add(entity);
        return this;
    }
    
    @Override
    public Object getProperty(String propertyName) {
        if (properties.containsKey(propertyName)) {
            return properties.get(propertyName);
        }
        return null;
    }
    
    @Override
    public void persist(DBOperation dbHandler) {
        System.out.println("total operations: " );
        System.out.println(operations);
        // get a hold of all the flow entries for the current flowpath.
        if (children.size() > 0) {
            int noOfChildren = children.size();
            if (noOfChildren > 0) {
                // construct a list of null ids for creating vertices for all
                // flow entries.
                ArrayList<Object> ids = new ArrayList<>(noOfChildren);
                // set properties
                Map<RamCloudVertex, Map<String, Object>> propertiesToSet = new HashMap<>();
                
                RamCloudGraph graph = (RamCloudGraph)dbHandler.getDBConnection().getFramedGraph().getBaseGraph();
                for (int i = 0; i < noOfChildren; i++) {
                    ids.add(null);
                    //addedVertices.add((RamCloudVertex) graph.addVertex(null));
                }
                List<RamCloudVertex> addedVertices = graph.addVertices(ids);
                System.out.println("Added vertices " + addedVertices);
                // call setVertices()
                //Iterable<Vertex> vertices = dbHandler.setVertices(ids);
                //Iterator vi = vertices.iterator();
                // get source and destination edge match vertex v construct list
                // of edges

                ArrayList<Edge> edgesToSet = new ArrayList<>();
                for (int i = 0; i < noOfChildren; i++) {
                    FlowEntity childEntity = (FlowEntity)children.get(i);
                    Vertex srcVertex = addedVertices.get(i);                    
                    propertiesToSet.put((RamCloudVertex)srcVertex, childEntity.properties);
                    //Vertex srcVertex = getVertexEdge(vi, i);

                    if (srcVertex == null) continue;
                    for (int j = 0; j < childEntity.edges.size(); j++) {
                        EntityEdge edge = (EntityEdge) childEntity.edges.get(j);
                        edgesToSet.add(new RamCloudEdgeEntity(srcVertex, ((IBaseObject) edge.dst).asVertex(), edge.dir, edge.label));
                    }
                }
                graph.addEdges(edgesToSet);
                graph.setProperties(propertiesToSet);
            }
        }
        for (int i = 0; i < children.size(); i++) {
            FlowEntityManager entity = (FlowEntityManager)children.get(i);
            System.out.println(entity.getProperties());
        }
    }

    private Vertex getVertexEdge(Iterator vi, int idx) {
        int i = 0;
        while (vi.hasNext()) {
            Vertex v = (Vertex)vi.next();
            if (i == idx) {
                return v;
            }
            i++;
        }
        return null;
    }
    
    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    @Override
    public void addEdge(Object dst, Direction dir, String label) {
        edges.add(new EntityEdge(this, dst, DBOperationType.ADD, dir, label));
    }
    
    @Override
    public void removeEdge(Object src, Object dst, Direction dir, String label) {
        edges.add(new EntityEdge(src, dst, DBOperationType.REMOVE, dir, label));
    }
}
