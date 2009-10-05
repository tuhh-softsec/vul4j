package render.quantifyit.decimal.performance.simple;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import render.quantifyit.decimal.MemorySnapshot;
import render.quantifyit.decimal.PerformanceUtils;
import render.quantifyit.model.Decimal;
import render.quantifyit.statistics.descriptive.PopulationSummary;

public class SimplePerformanceStatisticsTest {

	private static final int STARTUP_PAUSE = 2;
	private static final int ELEMENTS = 1000000;
	private static final boolean GC_ON = false;
	private static final boolean STORE_RESULTS = false;
	private static final boolean PRINT_ENV = false;
	
	private static Decimal[] decimalResults;
	private static Decimal[] decimalDataSet;
	
	private static BigDecimal[] bigDecimalResults;
	private static BigDecimal[] bigDecimalDataSet;
	
	private static double[] doubleResults;
	private static double[] doubleDataSet;

	private static List<Decimal> decimalDuration = new ArrayList<Decimal>(); 
	private static List<Decimal> bigDecimalDuration = new ArrayList<Decimal>();
	private static List<Decimal> doubleDuration = new ArrayList<Decimal>();

	private MemorySnapshot testMemoryConsumption;
	private static MemorySnapshot totalMemoryUsed;

	private static long totalExecutionTime;
	
	@BeforeClass
	public static void performanceBenchmarkStarts() throws InterruptedException{
		System.out.format("Performance tests for Quantify: %s%n", new Date());
		if(PRINT_ENV){
			PerformanceUtils.runtime();
		}
		
		System.out.format("Creating dataset with %d random numbers:%n", ELEMENTS);
		
		doubleDataSet = new double[ELEMENTS];
		for (int i = 0; i < doubleDataSet.length; i++) {
			doubleDataSet[i] = 100000 * (Math.random() + .1d);
		}
		
		decimalDataSet = new Decimal[ELEMENTS];
		for (int i = 0; i < decimalDataSet.length; i++) {
			decimalDataSet[i] = new Decimal(100000 * (Math.random() + .1d));
		}
		
		bigDecimalDataSet = new BigDecimal[ELEMENTS];
		for (int i = 0; i < bigDecimalDataSet.length; i++) {
			bigDecimalDataSet[i] = new BigDecimal(100000 * (Math.random() + .1d)).setScale(10, RoundingMode.HALF_EVEN);
		}
		
		if(STORE_RESULTS){
			doubleResults = new double[ELEMENTS];
			decimalResults = new Decimal[ELEMENTS];
			bigDecimalResults = new BigDecimal[ELEMENTS];
		}
		
		System.out.format("Finished creating data, sleeping %s seconds...%n", STARTUP_PAUSE);
		System.gc();
		Thread.sleep(STARTUP_PAUSE * 1000);
		PerformanceUtils.outputSystemLoad();
		System.out.println("Ready to start!");
		totalExecutionTime = PerformanceUtils.start();
		totalMemoryUsed = PerformanceUtils.memorySnapshot();
	}
	
	@AfterClass
	public static void summary(){
		final String bigDecimalClassName = BigDecimal.class.getSimpleName();
		final String doubleClassName = Double.class.getSimpleName();
		
		PopulationSummary decimalSummary = new PopulationSummary(decimalDuration.toArray(new Decimal[]{})); 
		PopulationSummary bigDecimalSummary = new PopulationSummary(bigDecimalDuration.toArray(new Decimal[]{}));
		PopulationSummary doubleSummary = new PopulationSummary(doubleDuration.toArray(new Decimal[]{})); 

		System.out.format("%n%1$-20s%n%n", "Summary:");
		
		System.out.format("|%1$-15s|%2$-11s|%3$-12s|%n", "Class", "Mean", "Std. Deviation");
		outputTimeSpentRow(decimalSummary, Decimal.class.getSimpleName());
		outputTimeSpentRow(bigDecimalSummary, bigDecimalClassName);
		outputTimeSpentRow(doubleSummary, doubleClassName);

		System.out.format("%n%1$-20s%n%n", "Delta from Java types...");
		System.out.format("|%1$-15s|%2$-11s|%3$-12s|%n", "Decimal diff", "Mean", "Std. Deviation");
		outputDeltaRow(decimalSummary, bigDecimalSummary, bigDecimalClassName);
		outputDeltaRow(decimalSummary, doubleSummary, doubleClassName);
		
		
		System.out.format("Decimal takes %s times longer than %s%n", 
				decimalSummary.getMean().by(bigDecimalSummary.getMean()).format("%5.2f"),  bigDecimalClassName);
		System.out.format("Decimal takes %s times longer than %s%n", 
				decimalSummary.getMean().by(doubleSummary.getMean()).format("%5.2f"),  doubleClassName);
		
		PerformanceUtils.memoryConsumed(totalMemoryUsed);
		PerformanceUtils.outputSystemLoad();
		PerformanceUtils.end(totalExecutionTime);
	}
	
	@Before
	public void setUp()  {
		if(STORE_RESULTS) {
			testMemoryConsumption = PerformanceUtils.memorySnapshot();
		}
	}
	
	@After
	public void measureMemoryAndTearDown() throws InterruptedException{
		if(STORE_RESULTS) {
			PerformanceUtils.memoryConsumed(testMemoryConsumption);
		}
		if(GC_ON){
			System.gc();
			Thread.sleep(2000);
		}			
	}
	

	@Test
	public void testPerformanceOFBigDecimalPlus(){
		System.out.println("BigDecimal operation: Add:");
		final long executionTime = PerformanceUtils.start();
		BigDecimal result = null;
		for (int i = 0; i < bigDecimalDataSet.length; i++) {
			result = bigDecimalDataSet[i].add(new BigDecimal(i));
			if(STORE_RESULTS){
				bigDecimalResults[i] = result;
			}
		}
		bigDecimalDuration.add(PerformanceUtils.end(ELEMENTS, executionTime));
	}
	
	@Test
	public void testPerformanceOFBigDecimalMinus(){
		System.out.println("BigDecimal operation: Subtract:");
		final long executionTime = PerformanceUtils.start();
		BigDecimal result = null;
		for (int i = 0; i < bigDecimalDataSet.length; i++) {
			result = bigDecimalDataSet[i].subtract(new BigDecimal(i));
			if(STORE_RESULTS){
				bigDecimalResults[i] = result;
			}
		}
		bigDecimalDuration.add(PerformanceUtils.end(ELEMENTS, executionTime));
	}
	
	@Test
	public void testPerformanceOFBigDecimalMultiply(){
		System.out.println("BigDecimal operation: Multiply:");
		final long executionTime = PerformanceUtils.start();
		BigDecimal result = null;
		for (int i = 0; i < bigDecimalDataSet.length; i++) {
			result = bigDecimalDataSet[i].multiply(new BigDecimal(i));
			if(STORE_RESULTS){
				bigDecimalResults[i] = result;
			}
		}
		bigDecimalDuration.add(PerformanceUtils.end(ELEMENTS, executionTime));
	}
	
	@Test
	public void testPerformanceOFBigDecimalDivide(){
		System.out.println("BigDecimal operation: Divide:");
		final long executionTime = PerformanceUtils.start();
		BigDecimal result = null;
		for (int i = 1; i < bigDecimalDataSet.length +1; i++) {
			result = bigDecimalDataSet[i - 1].divide(new BigDecimal(i), 10, RoundingMode.HALF_EVEN);
			if(STORE_RESULTS){
				bigDecimalResults[i - 1] = result;
			}
		}
		bigDecimalDuration.add(PerformanceUtils.end(ELEMENTS, executionTime));
	}
	
	@Test
	public void testPerformanceOFDecimalPlus(){
		System.out.println("Decimal operation: Plus:");
		final long executionTime = PerformanceUtils.start();
		Decimal result = null;
		for (int i = 0; i < decimalDataSet.length; i++) {
			result = decimalDataSet[i].plus(new Decimal(i));
			if(STORE_RESULTS){
				decimalResults[i] = result;
			}
		}
		decimalDuration.add(PerformanceUtils.end(ELEMENTS, executionTime));
	}
	
	@Test
	public void testPerformanceOFDecimalMinus(){
		System.out.println("Decimal operation: Minus:");
		final long executionTime = PerformanceUtils.start();
		Decimal result = null;
		for (int i = 0; i < decimalDataSet.length; i++) {
			result = decimalDataSet[i].minus(new Decimal(i));
			if(STORE_RESULTS){
				decimalResults[i] = result;
			}
		}
		decimalDuration.add(PerformanceUtils.end(ELEMENTS, executionTime));
	}
	
	@Test
	public void testPerformanceOFDecimalTimes(){
		System.out.println("Decimal operation: Times:");
		final long executionTime = PerformanceUtils.start();
		Decimal result = null;
		for (int i = 0; i < decimalDataSet.length; i++) {
			result = decimalDataSet[i].times(new Decimal(i));
			if(STORE_RESULTS){
				decimalResults[i] = result;
			}
		}
		decimalDuration.add(PerformanceUtils.end(ELEMENTS, executionTime));
	}
	
	@Test
	public void testPerformanceOFDecimalBy(){
		System.out.println("Decimal operation: By:");
		final long executionTime = PerformanceUtils.start();
		Decimal result = null;
		for (int i = 1; i < decimalDataSet.length +1; i++) {
			result = decimalDataSet[i - 1].by(i);	
			if(STORE_RESULTS){
				decimalResults[i - 1] = result;
			}
		}
		decimalDuration.add(PerformanceUtils.end(ELEMENTS, executionTime));
	}

	
	@Test
	public void testPerformanceOFDoublePlus(){
		System.out.println("Double operation: Plus:");
		final long executionTime = PerformanceUtils.start();
		double result = 0.0;
		for (int i = 0; i < doubleDataSet.length; i++) {
			result = doubleDataSet[i] + i;
			if(STORE_RESULTS){
				doubleResults[i] = result;
			}
		}
		doubleDuration.add(PerformanceUtils.end(ELEMENTS, executionTime));
	}
	
	@Test
	public void testPerformanceOFDoubleMinus(){
		System.out.println("Double operation: Minus:");
		final long executionTime = PerformanceUtils.start();
		double result = 0.0;
		for (int i = 0; i < doubleDataSet.length; i++) {
			result = doubleDataSet[i] - i;
			if(STORE_RESULTS){
				doubleResults[i] = result;
			}
		}
		doubleDuration.add(PerformanceUtils.end(ELEMENTS, executionTime));
	}
	
	@Test
	public void testPerformanceOFDoubleTimes(){
		System.out.println("Double operation: Times:");
		final long executionTime = PerformanceUtils.start();
		double result = 0.0;
		for (int i = 0; i < doubleDataSet.length; i++) {
			result = doubleDataSet[i] * i;
			if(STORE_RESULTS){
				doubleResults[i] = result;
			}
		}
		doubleDuration.add(PerformanceUtils.end(ELEMENTS, executionTime));
	}
	
	@Test
	public void testPerformanceOFDoubleBy(){
		System.out.println("Double operation: Divide:");
		final long executionTime = PerformanceUtils.start();
		double result = 0.0;
		for (int i = 1; i < doubleDataSet.length + 1; i++) {
			result = doubleDataSet[i -1] / i;
			if(STORE_RESULTS){
				doubleResults[i - 1] = result;
			}
		}
		doubleDuration.add(PerformanceUtils.end(ELEMENTS, executionTime));
	}
	
	private static void outputDeltaRow(final PopulationSummary x, final PopulationSummary y, final String className){
		final Decimal meanDelta = x.getMean().minus(y.getMean()); 
		final Decimal stdDelta = x.getStandardDeviation().minus(y.getStandardDeviation());
		output(className, meanDelta, stdDelta);
	}
	
	private static void outputTimeSpentRow(PopulationSummary summary, String label){
		output(label, summary.getMean(), summary.getStandardDeviation());
	}
	
	private static void output(final String label, final Decimal mean, final Decimal stDev){
		System.out.format("|%1$-15s|%2$-10s|%3$-12s|%n", label, 
				PerformanceUtils.formatDuration(mean), PerformanceUtils.formatDuration(stDev));
	}
	
}
