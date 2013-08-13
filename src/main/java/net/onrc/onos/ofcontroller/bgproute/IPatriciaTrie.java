package net.onrc.onos.ofcontroller.bgproute;

import java.util.Iterator;

public interface IPatriciaTrie {
	public RibEntry put(Prefix p, RibEntry r);
	
	public RibEntry lookup(Prefix p);
	
	public RibEntry match(Prefix p);
	
	public boolean remove(Prefix p, RibEntry r);
	
	public Iterator<Entry> iterator();
	
	interface Entry {
		public Prefix getPrefix();
		public RibEntry getRib();
	}
}
