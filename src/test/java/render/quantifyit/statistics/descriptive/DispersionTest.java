package render.quantifyit.statistics.descriptive;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DispersionTest {
	
	@Test
	public void constructor(){
		new Dispersion();
	}
	
	/**
	 * Variance 
	 */
	
	@Test
	public void testShouldFindTheVarianceOfAListOfElements(){
		double[] elements = new double[]{1,2,9};
		double variance = Dispersion.variance(elements);
		assertEquals(12.67, variance, 2);
	}
	
	@Test
	public void testShouldFindTheVarianceOfAListOfNegativeElements(){
		double[] elements = new double[]{-10,-130,-9};
		double variance = Dispersion.variance(elements);
		assertEquals(3226.89, variance, 2);
	}
	
	@Test
	public void testShouldFindTheVarianceOfEmptyElements(){
		double[] elements = new double[]{};
		double variance = Dispersion.variance(elements);
		assertEquals(Double.NaN, variance, 0);
	}
	
	@Test
	public void testShouldFindTheVarianceOfOneElement(){
		double[] elements = new double[]{2};
		double variance = Dispersion.variance(elements);
		assertEquals(0, variance, 0);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testShouldFindTheVarianceOfNullElements(){
		double[] elements = null;
		Dispersion.variance(elements);
	}
	

	
	/**
	 * Standard deviation
	 */
	@Test
	public void testShouldFindTheStandardDeviationOfAListOfElements(){
		double[] elements = new double[]{1,2,3,4,20};
		double standardDeviation = Dispersion.standardDeviation(elements);
		assertEquals(7.07, standardDeviation, 2);
	}
	
	@Test
	public void testShouldFindTheStandardDeviationOfAListOfNegativeElements(){
		double[] elements = new double[]{-10,-130,-9};
		double standardDeviation = Dispersion.standardDeviation(elements);
		assertEquals(56.81, standardDeviation, 2);
	}
	
	@Test
	public void testShouldFindTheStandardDeviationOfEmptyElements(){
		double[] elements = new double[]{};
		double standardDeviation = Dispersion.standardDeviation(elements);
		assertEquals(Double.NaN, standardDeviation, 0);
	}
	
	@Test
	public void testShouldFindTheStandardDeviationOfOneElement(){
		double[] elements = new double[]{2};
		double standardDeviation = Dispersion.standardDeviation(elements);
		assertEquals(0, standardDeviation, 0);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testShouldFindTheStandardDeviationOfNullElements(){
		double[] elements = null;
		Dispersion.standardDeviation(elements);
	}
	
	/**
	 * Variance to Standard deviation
	 */
	@Test
	public void testThatGivenAVarianceFindsTheStandardDeviation(){
		double standardDeviation = Dispersion.var2Sd(3226.89d);
		assertEquals(56.81, standardDeviation, 2);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testThatGivenAVarianceThrowsExceptionIfVarianceIsNegative(){
		Dispersion.var2Sd(-3226.89d);
	}
	
	/**
	 *  Standard deviation to Variance
	 */
	@Test
	public void testThatGivenAStandardDeviationFindsTheVariance(){
		double variance = Dispersion.sd2Var(56.81);
		assertEquals(3226.89d, variance, 2);
	}
}
