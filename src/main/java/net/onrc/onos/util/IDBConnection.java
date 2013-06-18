package net.onrc.onos.util;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.frames.FramedGraph;

public interface IDBConnection {
	public FramedGraph<TitanGraph> getFramedGraph();
	public void addEventListener(final LocalGraphChangedListener listener);
	public Boolean isValid();
	public void commit();
	public void rollback();
	public void close();
}
