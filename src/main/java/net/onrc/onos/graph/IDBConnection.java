package net.onrc.onos.graph;

import com.tinkerpop.frames.FramedGraph;

public interface IDBConnection {
    public FramedGraph getFramedGraph();
    public void addEventListener(final LocalGraphChangedListener listener);
    public Boolean isValid();
    public void commit();
    public void rollback();
    public void close();
}
