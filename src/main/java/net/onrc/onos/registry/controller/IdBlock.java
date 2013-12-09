package net.onrc.onos.registry.controller;

public class IdBlock {
	private long start;
	private long end;
	private long size;
	
	public IdBlock(long start, long end, long size) {
		this.start = start;
		this.end = end;
		this.size = size;
	}
	
	public long getStart() {
		return start;
	}

	public long getEnd() {
		return end;
	}

	public long getSize() {
		return size;
	}
	
	@Override
	public String toString() {
		return "IdBlock [start=" + start + ", end=" + end + ", size=" + size
				+ "]";
	}
}
