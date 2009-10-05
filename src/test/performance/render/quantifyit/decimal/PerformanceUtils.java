package render.quantifyit.decimal;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import render.quantifyit.model.Decimal;

public class PerformanceUtils {
	
	private static final int THOUSAND = 1000;

	private static final Decimal SECOND = Decimal.TEN.power(12);
	private static final Decimal MILLISECOND = Decimal.TEN.power(9);
	private static final Decimal MICROSECOND = Decimal.TEN.power(6);

	private static final int KILOBYTES = 1024;
	private static final Decimal MEGABYTES = new Decimal(KILOBYTES).power(2);
	private static final Decimal GIGABYTES = new Decimal(KILOBYTES).power(3);
	
	public static long start() {
        return System.nanoTime();
    }
	
	public static Decimal end(final long executionTime){
        Decimal picoDuration = new Decimal(endTime(executionTime));
        System.out.format("Execution finished in %s%n", formatDuration(picoDuration));
        return picoDuration;
	}
    
    public static Decimal end(final int iterations, final long executionTime) {
        Decimal picoDuration = new Decimal(endTime(executionTime) / iterations);
        System.out.format("Execution finished in %s%n", formatDuration(picoDuration));
        return picoDuration;
    }
    
    private static long endTime(final long executionTime){
    	long nanoSeconds = System.nanoTime() - executionTime;
    	long picoDuration = nanoSeconds * THOUSAND ;
    	return picoDuration;
    }

	public static void outputProperties() {
		System.out.format("%n%nObtained on: %n");
		String[] properties = { "java.runtime.name", 
				"java.specification.version", "java.version","java.runtime.version",
				"java.vm.vendor", "java.vm.name", "java.vm.version",
				"java.vm.specification.vendor", "java.vm.info", "java.class.version",
				"sun.arch.data.model", "sun.management.compiler" };

		for (String key : properties) {
			System.out.format("%s : [%s]%n", key, System.getProperty(key));
		}
	}
    
	public static MemorySnapshot memorySnapshot(){
		Runtime rt = Runtime.getRuntime();
		final Decimal totalMemory = new Decimal(rt.totalMemory());
		final Decimal freeMemory = new Decimal(rt.freeMemory());
		return new MemorySnapshot(totalMemory, freeMemory);
	}
	
	public static void memoryConsumed(final MemorySnapshot atStart){
		MemorySnapshot now = memorySnapshot();
		
		System.out.format("%nMemory allocated:%s%nMemory free:\t %s%nDelta:\t\t %s%n", 
				now.getFormattedTotalAllocated(), now.getFormattedFree(), now.getFormattedDelta());
	}
	
	public static void runtime(){
		OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
		System.out.format("Operating system: [%s %s][%s]%n", os.getName(), os.getArch(), os.getVersion());
		
		System.out.format("Available processors: %s%n", os.getAvailableProcessors());
	}

	public static void outputSystemLoad(){
		System.out.format("%nSystem load average: %s%n", systemLoad());
	}
	
	public static String systemLoad(){
		OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
		return systemLoad(os);
	}
	
	public static String formatDuration(Decimal picoDuration) {
		Decimal output = null;
        String unit = null;
        if ( picoDuration.gt(SECOND) ) {
        	output = picoDuration.movePointToLeft(12);
        	unit = "s";
        } else if (picoDuration.gt(MILLISECOND)) {
        	output = picoDuration.movePointToLeft(9);
        	unit = "ms";
        }else if (picoDuration.gt(MICROSECOND)) {
        	output = picoDuration.movePointToLeft(6);
        	unit = "μs";
        } else {
        	output = picoDuration.movePointToLeft(3);
        	unit = "ns";
        }
        return String.format("%s %s", output.scaleTo(3).format("%8.3f"), unit);
	}
	
	public static String formatMemory(final Decimal memory){
		Decimal output = null;
		String unit = null;
		if(memory.gt(GIGABYTES)){
			output = memory.movePointToLeft(9);
			unit = "gb";
		} else if (memory.gt(MEGABYTES)){
			output = memory.movePointToLeft(6);
			unit = "mb";
		} else if ( memory.gt(new Decimal(KILOBYTES))){
			output = memory.movePointToLeft(3);
			unit = "kb";
		} 
		return String.format("%s %s", output.scaleTo(3).format("%8.3f"), unit);
	}
	
	private static String systemLoad(final OperatingSystemMXBean os) {
		return String.format("%3.2f", os.getSystemLoadAverage() * 100) + "%";	
	}
}
