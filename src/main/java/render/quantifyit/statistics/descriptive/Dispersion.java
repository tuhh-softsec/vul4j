package render.quantifyit.statistics.descriptive;

import java.util.Arrays;

import render.quantifyit.model.Decimal;
import render.quantifyit.util.DecimalUtils;

public class Dispersion {

	public static Decimal sampleVariance(final Decimal mean, final Decimal[] elements) {
		if(elements.length == 1) {
			return Decimal.ZERO;
		}
		return sumOfSquaredDeltas(mean, elements).by(elements.length - 1);
	}
	
	public static Decimal populationVariance(final Decimal mean, final Decimal[] elements) {
		if(elements.length == 1){
			return Decimal.ZERO;
		}
		return sumOfSquaredDeltas(mean, elements).by(elements.length);
	}
	
	public static Decimal sampleVariance(final Decimal... elements) {
		return sampleVariance(Average.mean(elements), elements);
	}
	
	public static Decimal populationVariance(final Decimal... elements) {
		return populationVariance(Average.mean(elements), elements);
	}

	public static Decimal sampleStandardDeviation(final Decimal mean, final Decimal... elements) {
		return sampleVariance(mean, elements).squareRoot();
	}
	
	public static Decimal sampleStandardDeviation(final Decimal... elements) {
		return sampleVariance(elements).squareRoot();
	}
	
	public static Decimal populationStandardDeviation(final Decimal mean, final Decimal... elements) {
		return populationVariance(mean, elements).squareRoot();
	}
	
	public static Decimal populationStandardDeviation(final Decimal... elements) {
		return populationVariance(elements).squareRoot();
	}

	public static Decimal sd2Var(final Decimal standardDeviation) {
		return standardDeviation.square();
	}

	public static Decimal var2Sd(final Decimal variance) {
		if(variance.isNegative()){
			throw new IllegalArgumentException("Please give a non negative number.");
		}
		return variance.squareRoot();
	}
	
	public static Decimal min(final Decimal... elements){
		DecimalUtils.notNullOrEmpty(elements);
		
		Decimal min = elements[0];
		for (Decimal element : elements) {
			if(element.lt(min)){
				min = element;
			}
		}
		return min;
	}

	public static Decimal max(final Decimal... elements){
		DecimalUtils.notNullOrEmpty(elements);
		
		Decimal max = elements[0];
		for (Decimal element : elements) {
			if(element.gt(max)) {
				max = element;
			}
		}
		return max;
	}
		
	public static Decimal range(final Decimal... elements){
		DecimalUtils.notNullOrEmpty(elements);

		Arrays.sort(elements);
		
		return elements[elements.length-1].minus(elements[0]);
	}
	
	/**
	 * z = (x - μ)/ σ
	 */
	public static Decimal zScore(final Decimal element, final Decimal populationMean, final Decimal populationStandardDeviation){
		return element.minus(populationMean).by(populationStandardDeviation);
	}
	
	/**
	 * (x - μ)^2
	 */
	private static Decimal sumOfSquaredDeltas(final Decimal mean, final Decimal... elements) {
		Decimal sumOfSquaredDeltas = Decimal.ZERO;
		for (Decimal element : elements) {  
			sumOfSquaredDeltas = sumOfSquaredDeltas.plus(element.minus(mean).square());
		}
		return sumOfSquaredDeltas;
	}	
	
}
