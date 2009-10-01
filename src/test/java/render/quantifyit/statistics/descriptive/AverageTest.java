package render.quantifyit.statistics.descriptive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static render.quantifyit.model.AssertDecimal.assertDecimal;
import static render.quantifyit.util.DecimalUtils.pack;
import static render.quantifyit.util.DecimalUtils.packInts;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import render.quantifyit.model.Decimal;

public class AverageTest {
	
	/**
	 * Lean MEAN Machine
	 */
	
	@Test
	public void testShouldCalculateTheMeanOfASetOfNumbers() {
		Decimal[] elements = pack(1,2,3,4,5,6,7);
		Decimal mean = Average.mean(elements);
		assertDecimal(new Decimal(4), mean);
		
		elements = pack(100,25,52,26,69,39,1);
		mean = Average.mean(elements).scaleTo(2);
		assertDecimal(new Decimal(44.57), mean);
	}
	
	@Test
	public void testShouldReturnAMeanOfZeroIfNoElements(){
		assertDecimal(Decimal.ZERO, Average.mean(Decimal.ZERO));
	}
	
	@Test
	public void testShouldReturnNegativeMeanElementsAreNegative(){
		final Decimal[] elements = pack(-1,-2,-3,-4,-5,-6,-7);
		final Decimal mean = Average.mean(elements);
		assertDecimal(new Decimal(-4), mean);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionOnMeanWithNullArray(){
		final Decimal[] nullArray = null;
		Average.mean(nullArray);
	}
	
	@Test
	public void testShouldReturnDecimalAverages(){
		final double[] dataset = new double[]{1.3,2.5,3.1,4.25,5.97,6.41,7.132};
		final Decimal[] elements = pack(dataset);
		final Decimal mean = Average.mean(elements).scaleTo(2);
		assertDecimal(4.38, mean);
	}
	
	@Test
	public void testShouldReturnWeightedAverageWithDoubles(){
		final Decimal[] elements = new Decimal[10];
		for (int i = 0; i < 10; i++) {
			 elements[i] = new Decimal(.1);
		}
		assertDecimal(.1, Average.mean(elements), 1);
	}
	
	/**
	 * MEDIAN
	 */
	
	@Test
	public void testShouldCalculateTheMedianOfASetOfNumbers() {
		Decimal[] elements = pack(1,2,3,4,5,6,7);
		Decimal median = Average.median(elements);
		assertDecimal(4, median);
		
		elements = pack(100,25,52,26,69,39,1);
		median = Average.median(elements);
		assertDecimal(39, median);
	}
	
	@Test
	public void testShouldCalculateTheMedianWhenEvenAmountOfElements(){
		final Decimal[] elements = pack(32,28,23,7);
		final Decimal median = Average.median(elements);
		assertDecimal(25.5, median);
	}
	
	@Test
	public void testShouldReturnAMedianOfZeroIfNoElements(){
		final Decimal elements = Decimal.ZERO;
		final Decimal median = Average.median(elements);
		assertDecimal(0, median);
	}
	
	@Test
	public void testShouldReturnNegativeMedianElementsAreNegative(){
		final Decimal[] elements = pack(-1,-2,-3,-4,-5,-6,-7);
		final Decimal median = Average.median(elements);
		assertDecimal(-4, median);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionOnMedianWithNullArray(){
		final Decimal[] nullArray = null;
		Average.median(nullArray);
	}
	
	/**
	 * Depeche MODE
	 */
	
	@Test
	public void testShouldCalculateTheModeOfASetOfNumbers() {
		final Decimal[] elements = pack(100,25,52,26,25,39,1);
		final Decimal mode = Average.mode(elements)[0];
		assertDecimal(25, mode);
	}
	
	@Test
	public void testShoudReturnTheSameElementIfOnlyOnePresent(){
		final Decimal elements = Decimal.ONE;
		final Decimal mode = Average.mode(elements)[0];
		assertDecimal(1, mode);
	}
	
	@Test
	public void testShouldReturnMoreThanOneModeIfSetIsBimodal(){
		final double[] dataset = new double[]{1,2,3,2,4,6,4,5,1,3,2,1,5,1,2,3};
		final Decimal[] elements = pack(dataset);
		final Decimal[] modes = Average.mode(elements);
		assertEquals(2, modes.length);
		final List<Decimal> results = Arrays.asList(pack(1,2)); //4x1, 4x2
		
		assertTrue(Arrays.asList(modes).containsAll(results));
	}
	
	@Test
	public void testShouldReturnMoreThanOneModeIfSetIsMultimodal(){
		final Decimal[] elements = pack(1,2,1,2,3,4,3,4,5,6,5,6,7,8,7,8);
		final Decimal[] modes = Average.mode(elements);
		assertEquals(8, modes.length);
		final List<Decimal> results = Arrays.asList(pack(1,2,3,4,5,6,7,8));
		assertTrue(Arrays.asList(modes).containsAll(results));
	}
	
	@Test
	public void testShouldReturnNegativeModeElementsAreNegative(){
		final Decimal[] elements = pack(-1,-2,-3,-3,-4,-3,-7);
		final Decimal mode = Average.mode(elements)[0];
		assertDecimal(-3, mode);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionIfModeHasNullElements(){
		final Decimal[] nullArray = null;
		Average.mode(nullArray);
	}
}
