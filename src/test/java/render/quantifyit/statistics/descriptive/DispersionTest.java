package render.quantifyit.statistics.descriptive;

import static render.quantifyit.model.AssertDecimal.assertDecimal;
import static render.quantifyit.util.DecimalUtils.pack;

import org.junit.Test;

import render.quantifyit.model.Decimal;


public class DispersionTest {
	
	@Test
	public void testShouldFindTheSampleVarianceList1WithMean(){
		final Decimal[] elements = pack(1,2,9);
		final Decimal varianceWithMean = Dispersion.sampleVariance(new Decimal(4), elements);
		assertDecimal(19, varianceWithMean);
	}
	
	@Test
	public void testShouldFindTheSampleVarianceList1(){
		final Decimal[] elements = pack(1,2,9);
		final Decimal variance = Dispersion.sampleVariance(elements);
		assertDecimal(19, variance);
	}
	
	@Test
	public void testShouldFindThePopulationVarianceList1WithMean(){
		final Decimal[] elements = pack(1,2,9);
		final Decimal variance = Dispersion.populationVariance(new Decimal(4), elements);
		assertDecimal(12.67, variance, 2);
	}
	
	@Test
	public void testShouldFindThePopulationVarianceList1(){
		final Decimal[] elements = pack(1,2,9);
		final Decimal variance = Dispersion.populationVariance(elements);
		assertDecimal(12.67, variance, 2);
	}
	
	@Test
	public void testShouldFindTheSampleVarianceList2(){
		final Decimal[] elements = pack(-10,-130,-9);
		final Decimal variance = Dispersion.sampleVariance(elements);
		assertDecimal(4840.33, variance, 2);
	}

	@Test
	public void testShouldFindThePopulationVarianceList2(){
		final Decimal[] elements = pack(-10,-130,-9);
		final Decimal variance = Dispersion.populationVariance(elements);
		assertDecimal(3226.89, variance, 2);
	}	
	
	@Test
	public void testShouldReturnZeroOnSampleVarianceOfOneElement(){
		final Decimal[] elements = pack(2);
		final Decimal variance = Dispersion.sampleVariance(elements);
		assertDecimal(0, variance);
	}

	@Test
	public void testShouldReturnZeroOnPopulationVarianceOfOneElement(){
		final Decimal[] elements = pack(2);
		final Decimal variance = Dispersion.populationVariance(elements);
		assertDecimal(0, variance);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionOnSampleVarianceWithNullArray(){
		final Decimal[] nullArray = null;
		Dispersion.sampleVariance(nullArray);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionOnPopulationVarianceWithNullArray(){
		final Decimal[] nullArray = null;
		Dispersion.sampleVariance(nullArray);
	}
	
	@Test
	public void testShouldFindTheSampleVarianceList3(){
		final Decimal[] elements = pack(4, 7, 13, 16);
		assertDecimal(Decimal.TEN, Average.mean(elements));
		final Decimal variance = Dispersion.sampleVariance(elements);
		assertDecimal(30, variance);
	}
	
	@Test
	public void testShouldFindThePopulationVarianceList3(){
		final Decimal[] elements = pack(4, 7, 13, 16);
		assertDecimal(Decimal.TEN, Average.mean(elements));
		final Decimal variance = Dispersion.populationVariance(elements);
		assertDecimal(new Decimal("22.50"), variance, 2);
	}
	
	@Test
	public void testShouldFindTheSampleVarianceList4(){
		final Decimal oneHundredMillions = Decimal.TEN.power(8);
		final Decimal[] elements = new Decimal[]{
						oneHundredMillions.plus(4),
						oneHundredMillions.plus(7),
						oneHundredMillions.plus(13),
						oneHundredMillions.plus(16),
						};
		final Decimal variance = Dispersion.sampleVariance(elements);
		assertDecimal(30, variance);
	}
	
	@Test
	public void testShouldFindThePopulationVarianceList4(){
		final Decimal oneHundredMillions = Decimal.TEN.power(8);
		final Decimal[] elements = new Decimal[]{
				oneHundredMillions.plus(4),
				oneHundredMillions.plus(7),
				oneHundredMillions.plus(13),
				oneHundredMillions.plus(16),
		};
		final Decimal variance = Dispersion.populationVariance(elements);
		assertDecimal(new Decimal("22.50"), variance, 2);
	}

	
	
	/**
	 * Standard deviation
	 */
	
	@Test
	public void testShouldFindTheSampleStandardDeviationList1WithMean(){
		final Decimal[] elements = pack(1,2,3,4,20);
		final Decimal sampleSD = Dispersion.sampleStandardDeviation(new Decimal(6), elements);
		assertDecimal(7.91, sampleSD, 2);
	}
	
	@Test
	public void testShouldFindTheSampleStandardDeviationList1(){
		final Decimal[] elements = pack(1,2,3,4,20);
		final Decimal sampleSD = Dispersion.sampleStandardDeviation(elements);
		assertDecimal(7.91, sampleSD, 2);
	}
	
	@Test
	public void testShouldFindThePopulationStandardDeviationList1WithMean(){
		final Decimal[] elements = pack(1,2,3,4,20);
		final Decimal populationSD = Dispersion.populationStandardDeviation(new Decimal(6), elements);
		assertDecimal(7.07, populationSD, 2);
	}
	
	@Test
	public void testShouldFindThePopulationStandardDeviationList1(){
		final Decimal[] elements = pack(1,2,3,4,20);
		final Decimal populationSD = Dispersion.populationStandardDeviation(elements);
		assertDecimal(7.07, populationSD, 2);
	}
	
	@Test
	public void testShouldFindTheSampleStandardDeviationList2(){
		final Decimal[] elements = pack(-10,-130,-9);
		final Decimal sampleSD = Dispersion.sampleStandardDeviation(elements);
		assertDecimal(69.57, sampleSD, 2);
	}
	
	@Test
	public void testShouldFindThePopulationStandardDeviationList2(){
		final Decimal[] elements = pack(-10,-130,-9);
		final Decimal populationSD = Dispersion.populationStandardDeviation(elements);
		assertDecimal(56.81, populationSD, 2);
	}
	
	@Test
	public void testShouldFindTheSampleStandardDeviationList3(){
		final double[] dataSet = new double[]{2,4,4,4,5,5,7,9};
		final Decimal[] elements = pack(dataSet);
		final Decimal sampleSD = Dispersion.sampleStandardDeviation(elements);
		assertDecimal(2.14, sampleSD, 2);
	}
	@Test
	public void testShouldFindThePopulationStandardDeviationList3(){
		final double[] dataSet = new double[]{2,4,4,4,5,5,7,9};
		final Decimal[] elements = pack(dataSet);
		final Decimal populationSD = Dispersion.populationStandardDeviation(elements);
		assertDecimal(2, populationSD);
	}
	
	@Test
	public void testShouldFindTheStandardDeviationOfOneElement(){
		final Decimal[] elements = pack(2);
		final Decimal standardDeviation = Dispersion.sampleStandardDeviation(elements);
		assertDecimal(0, standardDeviation);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionOnStandardDeviationWithNullArray(){
		final Decimal[] nullArray = null;
		Dispersion.sampleStandardDeviation(nullArray);
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
