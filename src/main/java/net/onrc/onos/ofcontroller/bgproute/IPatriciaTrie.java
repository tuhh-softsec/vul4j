package net.onrc.onos.ofcontroller.bgproute;

import java.util.Iterator;

public interface IPatriciaTrie {
	public Rib put(Prefix p, Rib r);
	
	public Rib lookup(Prefix p);
	
	public Rib match(Prefix p);
	
	public boolean remove(Prefix p, Rib r);
	
	public Iterator<Entry> iterator();
	
	interface Entry {
		public Prefix getPrefix();
		public Rib getRib();
	}
}
