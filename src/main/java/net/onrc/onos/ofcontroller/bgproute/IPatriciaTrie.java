package net.onrc.onos.ofcontroller.bgproute;

import java.util.Iterator;

public interface IPatriciaTrie<V> {
	public V put(Prefix prefix, V value);
	
	public V lookup(Prefix prefix);
	
	public V match(Prefix prefix);
	
	public boolean remove(Prefix prefix, V value);
	
	public Iterator<Entry<V>> iterator();
	
	interface Entry<V> {
		public Prefix getPrefix();
		public V getValue();
	}
}
