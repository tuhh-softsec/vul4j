package render.quantifyit.statistics.descriptive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class AverageTest {

	@Test
	public void constructor(){
		new Average();
	}
	
	/**
	 * Lean MEAN Machine
	 */
	
	@Test
	public void testShouldCalculateTheMeanOfASetOfNumbers() {
		double[] elements = new double[]{1,2,3,4,5,6,7};
		double mean = Average.mean(elements);
		assertEquals(4, mean, 0);
		
		elements = new double[]{100,25,52,26,69,39,1};
		mean = Average.mean(elements);
		assertEquals(44.57, mean, 2);
	}
	
	@Test
	public void testShouldReturnAMeanOfZeroIfNoElements(){
		double mean = Average.mean(0);
		assertEquals(0, mean, 0);
	}
	
	@Test
	public void testShouldReturnNegativeMeanElementsAreNegative(){
		double[] elements = new double[]{-1,-2,-3,-4,-5,-6,-7};
		double mean = Average.mean(elements);
		assertEquals(-4, mean, 0);
	}
	
	@Test
	public void testShouldReturnAMeanNaNIfEmptyElements(){
		double[] elements =  new double[]{};
		double mean = Average.mean(elements);
		assertEquals(Double.NaN, mean, 0);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionOnNullElements(){
		Average.mean(null);
	}

	/**
	 * MEDIAN
	 */
	
	@Test
	public void testShouldCalculateTheMedianOfASetOfNumbers() {
		double[] elements = new double[]{1,2,3,4,5,6,7};
		double median = Average.median(elements);
		assertEquals(4, median, 0);
		
		elements = new double[]{100,25,52,26,69,39,1};
		median = Average.median(elements);
		assertEquals(39, median, 0);
	}
	
	@Test
	public void testShouldCalculateTheMedianWhenEvenAmountOfElements(){
		double[] elements = new double[]{32,28,23,7};
		double median = Average.median(elements);
		assertEquals(25.5, median, 1);
	}
	
	@Test
	public void testShouldReturnAMedianOfZeroIfNoElements(){
		double elements = 0;
		double median = Average.median(elements);
		assertEquals(0, median, 0);
	}
	
	@Test
	public void testShouldReturnNegativeMedianElementsAreNegative(){
		double[] elements = new double[]{-1,-2,-3,-4,-5,-6,-7};
		double median = Average.median(elements);
		assertEquals(-4, median, 0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionIfEmptyElements(){
		Average.median(new double[]{});
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionIfNullElements(){
		Average.median(null);
	}
	
	/**
	 * Depeche MODE
	 */
	
	@Test
	public void testShouldCalculateTheModeOfASetOfNumbers() {
		double[] elements = new double[]{100,25,52,26,25,39,1};
		double mode = Average.mode(elements)[0];
		assertEquals(25, mode, 0);
	}
	
	@Test
	public void testShoudReturnTheSameElementIfOnlyOnePresent(){
		double elements = 1;
		double mode = Average.mode(elements)[0];
		assertEquals(1, mode, 0);
	}
	
	@Test
	public void testShouldReturnMoreThanOneModeIfSetIsBimodal(){
		double[] elements = new double[]{1,2,3,2,4,6,4,5,1,3,2,1,5,1,2,3};
		Double[] modes = Average.mode(elements);
		assertEquals(2, modes.length);
		assertTrue(Arrays.asList(modes).containsAll(Arrays.asList(new Double[]{1d,2d})));
	}
	
	@Test
	public void testShouldReturnMoreThanOneModeIfSetIsMultimodal(){
		double[] elements = new double[]{1,2,1,2,3,4,3,4,5,6,5,6,7,8,7,8};
		Double[] modes = Average.mode(elements);
		assertEquals(8, modes.length);
		assertTrue(Arrays.asList(modes).containsAll(Arrays.asList(new Double[]{1d,2d,3d,4d,5d,6d,7d,8d})));
	}
	
	@Test
	public void testShouldReturnNegativeModeElementsAreNegative(){
		double[] elements = new double[]{-1,-2,-3,-3,-4,-3,-7};
		double mode = Average.mode(elements)[0];
		assertEquals(-3, mode, 0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionIfModeHasEmptyElements(){
		double[] elements =  new double[]{};
		Average.mode(elements);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionIfModeHasNullElements(){
		double[] elements = null;
		Average.mode(elements);
	}
}
