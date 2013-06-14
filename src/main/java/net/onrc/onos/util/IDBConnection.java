package net.onrc.onos.util;

import net.onrc.onos.util.GraphDBConnection.GenerateEvent;
import net.onrc.onos.util.GraphDBConnection.Transaction;
import net.onrc.onos.util.GraphDBConnection.TransactionHandle;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.frames.FramedGraph;

public interface IDBConnection {
	public FramedGraph<TitanGraph> getFramedGraph();
	public void addEventListener(final LocalGraphChangedListener listener);
	public Boolean isValid();
	public void startTx();
	public void endTx(Transaction tx);
	public void endTx(TransactionHandle tr, Transaction tx);
	public void endTx(Transaction tx, GenerateEvent fire);
	public void close();
}
