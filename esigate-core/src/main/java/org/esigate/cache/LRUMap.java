package org.esigate.cache;

import java.util.LinkedHashMap;

public class LRUMap<K, V> extends LinkedHashMap<K, V>{
	private static final long serialVersionUID = 7760625028168125797L;
	private int maximumSize = 0;
	
	

	public LRUMap(int maximumSize) {
		super(maximumSize, 0.75f, true);
		this.maximumSize = maximumSize;
	}
	
	public LRUMap() {
		this(1000);
	}

	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		return size() > maximumSize;
	}
}
