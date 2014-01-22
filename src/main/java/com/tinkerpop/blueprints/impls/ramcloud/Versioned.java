package com.tinkerpop.blueprints.impls.ramcloud;

public class Versioned<T> {
	private T value;
	private long version;

	public Versioned(T value) {
		this(value, 0L);
	}

	public Versioned(T value, long version) {
		this.value = value;
		this.version = version;
	}

	public T getValue() {
		return value;
	}
	public long getVersion() {
		return version;
	}
	public void setValue(T value, long version) {
		this.value = value;
		this.version = version;
	}
}
