package render.quantifyit.statistics;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import render.quantifyit.statistics.AverageImpl;
import render.quantifyit.statistics.Variability;
import render.quantifyit.statistics.VariabilityImpl;

public class VariabilityImplTest {

	private Variability variability;

	@Before
	public void setUp() {
		variability = new VariabilityImpl(new AverageImpl()); 
	}
	
	/**
	 * Variance 
	 */
	
	@Test
	public void testShouldFindTheVarianceOfAListOfElements(){
		double[] elements = new double[]{1,2,9};
		double variance = variability.variance(elements);
		assertEquals(12.67, variance, 2);
	}
	
	@Test
	public void testShouldFindTheVarianceOfAListOfNegativeElements(){
		double[] elements = new double[]{-10,-130,-9};
		double variance = variability.variance(elements);
		assertEquals(3226.89, variance, 2);
	}
	
	@Test
	public void testShouldFindTheVarianceOfEmptyElements(){
		double[] elements = new double[]{};
		double variance = variability.variance(elements);
		assertEquals(Double.NaN, variance, 0);
	}
	
	@Test
	public void testShouldFindTheVarianceOfOneElement(){
		double[] elements = new double[]{2};
		double variance = variability.variance(elements);
		assertEquals(0, variance, 0);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testShouldFindTheVarianceOfNullElements(){
		double[] elements = null;
		variability.variance(elements);
	}
	

	
	/**
	 * Standard deviation
	 */
	@Test
	public void testShouldFindTheStandardDeviationOfAListOfElements(){
		double[] elements = new double[]{1,2,3,4,20};
		double standardDeviation = variability.standardDeviation(elements);
		assertEquals(7.07, standardDeviation, 2);
	}
	
	@Test
	public void testShouldFindTheStandardDeviationOfAListOfNegativeElements(){
		double[] elements = new double[]{-10,-130,-9};
		double standardDeviation = variability.standardDeviation(elements);
		assertEquals(56.81, standardDeviation, 2);
	}
	
	@Test
	public void testShouldFindTheStandardDeviationOfEmptyElements(){
		double[] elements = new double[]{};
		double standardDeviation = variability.standardDeviation(elements);
		assertEquals(Double.NaN, standardDeviation, 0);
	}
	
	@Test
	public void testShouldFindTheStandardDeviationOfOneElement(){
		double[] elements = new double[]{2};
		double standardDeviation = variability.standardDeviation(elements);
		assertEquals(0, standardDeviation, 0);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testShouldFindTheStandardDeviationOfNullElements(){
		double[] elements = null;
		variability.standardDeviation(elements);
	}
	
	/**
	 * Variance to Standard deviation
	 */
	@Test
	public void testThatGivenAVarianceFindsTheStandardDeviation(){
		double standardDeviation = variability.var2Sd(3226.89d);
		assertEquals(56.81, standardDeviation, 2);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testThatGivenAVarianceThrowsExceptionIfVarianceIsNegative(){
		variability.var2Sd(-3226.89d);
	}
	
	/**
	 * Variance to Standard deviation
	 */
	@Test
	public void testThatGivenAStandardDeviationFindsTheVariance(){
		double standardDeviation = variability.sd2Var(56.81);
		assertEquals(3226.89d, standardDeviation, 2);
	}
}
