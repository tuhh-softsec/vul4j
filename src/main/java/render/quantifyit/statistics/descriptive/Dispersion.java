package render.quantifyit.statistics.descriptive;

import java.util.Arrays;

import render.quantifyit.model.Decimal;
import render.quantifyit.util.DecimalArray;

public class Dispersion {

	public static Decimal standardDeviation(final Decimal... elements) {
		return variance(elements).squareRoot();
	}

	public static Decimal variance(final Decimal... elements) {
		final Decimal mean = Average.mean(elements);
		
		Decimal sumOfSquares = Decimal.ZERO;
		for (Decimal element : elements) {
			sumOfSquares = sumOfSquares.plus(element.power(2));
		}
		
		return (sumOfSquares.by(elements.length)).minus(mean.power(2));
	}

	public static Decimal sd2Var(final Decimal standardDeviation) {
		return standardDeviation.power(2);
	}

	public static Decimal var2Sd(final Decimal variance) {
		if(variance.isNegative()){
			throw new IllegalArgumentException("Please give a non negative number.");
		}
		return variance.squareRoot();
	}
	
	public static Decimal min(final Decimal... elements){
		DecimalArray.notNullOrEmpty(elements);
		
		Decimal min = elements[0];
		for (Decimal element : elements) {
			if(element.lt(min)){
				min = element;
			}
		}
		return min;
	}

	public static Decimal max(final Decimal... elements){
		DecimalArray.notNullOrEmpty(elements);
		
		Decimal max = elements[0];
		for (Decimal element : elements) {
			if(element.gt(max)) {
				max = element;
			}
		}
		return max;
	}
	
	//TODO: Do something better than just sort them...
	
	public static Decimal range(final Decimal... elements){
		DecimalArray.notNullOrEmpty(elements);

		Arrays.sort(elements);
		
		return elements[elements.length-1].minus(elements[0]);
	}

}
