package render.lambda.statistics;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class AverageImplTest {

	private Average average;

	@Before
	public void setUp() throws Exception {
		average = new AverageImpl();
	}
	
	/**
	 * Lean MEAN Machine
	 */
	
	@Test
	public void testShouldCalculateTheMeanOfASetOfNumbers() {
		double[] elements = new double[]{1,2,3,4,5,6,7};
		double mean = average.mean(elements);
		assertEquals(4, mean, 0);
		
		elements = new double[]{100,25,52,26,69,39,1};
		mean = average.mean(elements);
		assertEquals(44.57, mean, 2);
	}
	
	@Test
	public void testShouldReturnAMeanOfZeroIfNoElements(){
		double elements = 0;
		double mean = average.mean(elements);
		assertEquals(0, mean, 0);
	}
	
	@Test
	public void testShouldReturnNegativeMeanElementsAreNegative(){
		double[] elements = new double[]{-1,-2,-3,-4,-5,-6,-7};
		double mean = average.mean(elements);
		assertEquals(-4, mean, 0);
	}
	
	@Test
	public void testShouldReturnAMeanNaNIfEmptyElements(){
		double[] elements =  new double[]{};
		double mean = average.mean(elements);
		assertEquals(Double.NaN, mean, 0);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionOnNullElements(){
		double[] elements =  null;
		average.mean(elements);
	}

	/**
	 * MEDIAN
	 */
	
	@Test
	public void testShouldCalculateTheMedianOfASetOfNumbers() {
		double[] elements = new double[]{1,2,3,4,5,6,7};
		double median = average.median(elements);
		assertEquals(4, median, 0);
		
		elements = new double[]{100,25,52,26,69,39,1};
		median = average.median(elements);
		assertEquals(39, median, 0);
	}
	
	@Test
	public void testShouldCalculateTheMedianWhenEvenAmountOfElements(){
		double[] elements = new double[]{32,28,23,7};
		double median = average.median(elements);
		assertEquals(25.5, median, 1);
	}
	
	@Test
	public void testShouldReturnAMedianOfZeroIfNoElements(){
		double elements = 0;
		double median = average.median(elements);
		assertEquals(0, median, 0);
	}
	
	@Test
	public void testShouldReturnNegativeMedianElementsAreNegative(){
		double[] elements = new double[]{-1,-2,-3,-4,-5,-6,-7};
		double median = average.median(elements);
		assertEquals(-4, median, 0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionIfEmptyElements(){
		double[] elements =  new double[]{};
		average.median(elements);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionIfNullElements(){
		double[] elements = null;
		average.median(elements);
	}
	
	/**
	 * Depeche MODE
	 */
	
	@Test
	public void testShouldCalculateTheModeOfASetOfNumbers() {
		double[] elements = new double[]{100,25,52,26,25,39,1};
		double mode = average.mode(elements)[0];
		assertEquals(25, mode, 0);
	}
	
	@Test
	public void testShoudReturnTheSameElementIfOnlyOnePresent(){
		double elements = 1;
		double mode = average.mode(elements)[0];
		assertEquals(1, mode, 0);
	}
	
	@Test
	public void testShouldReturnMoreThanOneModeIfSetIsBimodal(){
		double[] elements = new double[]{1,2,3,2,4,6,4,5,1,3,2,1,5,1,2,3};
		Double[] modes = average.mode(elements);
		assertEquals(2, modes.length);
		assertTrue(Arrays.asList(modes).containsAll(Arrays.asList(new Double[]{1d,2d})));
	}
	
	@Test
	public void testShouldReturnMoreThanOneModeIfSetIsMultimodal(){
		double[] elements = new double[]{1,2,1,2,3,4,3,4,5,6,5,6,7,8,7,8};
		Double[] modes = average.mode(elements);
		assertEquals(8, modes.length);
		assertTrue(Arrays.asList(modes).containsAll(Arrays.asList(new Double[]{1d,2d,3d,4d,5d,6d,7d,8d})));
	}
	
	@Test
	public void testShouldReturnNegativeModeElementsAreNegative(){
		double[] elements = new double[]{-1,-2,-3,-3,-4,-3,-7};
		double mode = average.mode(elements)[0];
		assertEquals(-3, mode, 0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionIfModeHasEmptyElements(){
		double[] elements =  new double[]{};
		average.mode(elements);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionIfModeHasNullElements(){
		double[] elements = null;
		average.mode(elements);
	}
}
