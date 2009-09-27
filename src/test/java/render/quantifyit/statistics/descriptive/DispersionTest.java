package render.quantifyit.statistics.descriptive;

import static render.quantifyit.model.AssertDecimal.assertDecimal;
import static render.quantifyit.util.DecimalArray.pack;

import org.junit.Test;

import render.quantifyit.model.Decimal;


public class DispersionTest {
	
	/**
	 * Variance 
	 */
	
	@Test
	public void testShouldFindTheVarianceOfAListOfElements(){
		final Decimal[] elements = pack(new int[]{1,2,9});
		final Decimal variance = Dispersion.variance(elements);
		assertDecimal(12.67, variance, 2);
	}
	
	@Test
	public void testShouldFindTheVarianceOfAListOfNegativeElements(){
		final Decimal[] elements = pack(new int[]{-10,-130,-9});
		final Decimal variance = Dispersion.variance(elements);
		assertDecimal(3226.89, variance, 2);
	}
	
	@Test
	public void testShouldFindTheVarianceOfOneElement(){
		final Decimal[] elements = pack(new int[]{2});
		final Decimal variance = Dispersion.variance(elements);
		assertDecimal(0, variance);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionOnVarianceWithNullArray(){
		final Decimal[] nullArray = null;
		Dispersion.variance(nullArray);
	}
	
	/**
	 * Standard deviation
	 */
	
	@Test
	public void testShouldFindTheStandardDeviationOfAListOfElements(){
		final Decimal[] elements = pack(new int[]{1,2,3,4,20});
		final Decimal standardDeviation = Dispersion.standardDeviation(elements);
		assertDecimal(7.07, standardDeviation, 2);
	}
	
	@Test
	public void testShouldFindTheStandardDeviationOfAListOfNegativeElements(){
		final Decimal[] elements = pack(new int[]{-10,-130,-9});
		final Decimal standardDeviation = Dispersion.standardDeviation(elements);
		assertDecimal(56.81, standardDeviation, 2);
	}
	
	@Test
	public void testShouldFindTheStandardDeviationOfOneElement(){
		final Decimal[] elements = pack(new int[]{2});
		final Decimal standardDeviation = Dispersion.standardDeviation(elements);
		assertDecimal(0, standardDeviation);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionOnStandardDeviationWithNullArray(){
		final Decimal[] nullArray = null;
		Dispersion.standardDeviation(nullArray);
	}

	
	/**
	 * Variance to Standard deviation
	 */
	
	@Test
	public void testThatGivenAVarianceFindsTheStandardDeviation(){
		final Decimal standardDeviation = Dispersion.var2Sd(new Decimal(3226.89));
		assertDecimal(56.81, standardDeviation, 2);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testThatGivenAVarianceThrowsExceptionIfVarianceIsNegative(){
		Dispersion.var2Sd(new Decimal(-3226.89d));
	}
	
	/**
	 *  Standard deviation to Variance
	 */
	
	@Test
	public void testThatGivenAStandardDeviationFindsTheVariance(){
		final Decimal variance = Dispersion.sd2Var(new Decimal(56.81));
		assertDecimal(3227.3761, variance);
	}
	
	/**
	 * Minimalistic
	 */
	
	@Test
	public void testShouldReturnTheSmallestElementInADataSet(){
		final Decimal[] elements = pack(new double[]{10.23,130,9.178});
		assertDecimal(9.178, Dispersion.min(elements));
	}
	
	@Test
	public void testShouldReturnTheSmallestElementInADataSetWithNegatives(){
		final Decimal[] elements = pack(new double[]{-18,-18.7,-7.8, 3});
		assertDecimal(-18.7, Dispersion.min(elements));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionOnMinWithNullArray(){
		final Decimal[] nullArray = null;
		Dispersion.min(nullArray);
	}
	
	/**
	 * Maximus
	 */
	
	@Test
	public void testShouldReturnTheBigestElementInADataSet(){
		final Decimal[] elements = pack(new double[]{10.23,130,9.178});
		assertDecimal(130, Dispersion.max(elements));
	}
	
	@Test
	public void testShouldReturnTheBigestElementInADataSetWithNegatives(){
		final Decimal[] elements = pack(new double[]{-18,-18.7,-7.8, -3.14});
		assertDecimal(-3.14, Dispersion.max(elements));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionOnMaxWithNullArray(){
		final Decimal[] nullArray = null;
		Dispersion.min(nullArray);
	}
	
	/**
	 * RANGEr
	 */
	
	@Test
	public void testShouldReturnTheRangeBetweenElementInADataSet(){
		final Decimal[] elements = pack(new double[]{10.23,130,9.178});
		assertDecimal(120.822, Dispersion.range(elements));
	}
	
	@Test
	public void testShouldReturnTheRangeBetweenInADataSetWithNegatives(){
		final Decimal[] elements = pack(new double[]{-18,-18.7,-7.8, -3.14});
		final Decimal range = Dispersion.range(elements);
		assertDecimal(15.56, range);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionOnRangeWithNullArray(){
		final Decimal[] nullArray = null;
		Dispersion.min(nullArray);
	}
}
