package render.quantifyit.decimal;

import render.quantifyit.model.Decimal;

public class MemorySnapshot {

	private final Decimal total;
	private final Decimal free;

	public MemorySnapshot(final Decimal memoryCommitted, final Decimal memoryUsed) {
		this.total = memoryCommitted;
		this.free = memoryUsed;
	}
	
	public Decimal getTotalAllocated() {
		return total;
	}

	public Decimal getFree() {
		return free;
	}

	public String getFormattedDelta(){
		return PerformanceUtils.formatMemory(total.minus(free));
	}
	
	public String getFormattedTotalAllocated(){
		return PerformanceUtils.formatMemory(total);
	}
	
	public String getFormattedFree(){
		return PerformanceUtils.formatMemory(free);
	}
	

}
