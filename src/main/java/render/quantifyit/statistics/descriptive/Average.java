package render.quantifyit.statistics.descriptive;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import render.quantifyit.model.Decimal;
import render.quantifyit.util.DecimalUtils;

public class Average  {
	
	/**
	 * The arithmetic mean (or simply the mean) of a list of numbers is the sum of all 
	 * of the list divided by the number of items in the list. 
	 * Returns NaN if 0 elements.
	 * @param elements
	 * @return
	 */
	public static Decimal mean(final Decimal ... elements) {
		DecimalUtils.notNullOrEmpty(elements);	
		int count = 0;
		Decimal sum = Decimal.ZERO;
		for (Decimal element : elements){
			sum = sum.plus(element);
			count ++;
		}
		return sum.by(new Decimal(count));
	}
	
	
	/**
	 * A median is described as the number separating the higher half of a sample, 
	 * a population, or a probability distribution, from the lower half. 
	 * The median of a finite list of numbers can be found by arranging all the 
	 * observations from lowest value to highest value and picking the middle one. 
	 * If there is an even number of observations, the median is not unique, so one 
	 * often takes the mean of the two middle values. At most half the population have 
	 * values less than the median and at most half have values greater than the median.
	 * @param elements
	 * @see http://en.wikipedia.org/wiki/Median 
	 * @return
	 */
	public static Decimal median(final Decimal... elements) {
		DecimalUtils.notNullOrEmpty(elements);
		Arrays.sort(elements);
		Decimal median = Decimal.ZERO;
		final int medianPosition = elements.length/2;
		
		if (elements.length % 2 == 0){
			median = ( elements[medianPosition - 1].plus(elements[medianPosition]) ).halve(); 
		}else{
			median = elements[medianPosition];
		}
		return median;
	}

	/**
	 * In statistics, the mode is the value that occurs the most frequently in a data 
	 * set or a probability distribution.
	 * @param elements
	 * @see http://en.wikipedia.org/wiki/Mode_(statistics)
	 * @return
	 */
	public static Decimal[] mode(final Decimal... elements) {
		DecimalUtils.notNullOrEmpty(elements);
		if(elements.length == 1){
			return new Decimal[]{elements[0]};
		}
		Arrays.sort(elements);
		
		final Set<Decimal> modes = new HashSet<Decimal>(); 
		Decimal last = elements[0];
		Decimal current = null;
		int counter = 1;
		int maxCount = 1;
		for (int i = 1; i < elements.length; i++) {
			current = elements[i];
			if(last.same(current)) {
				counter++;
			} else {
				if ( counter > maxCount) {
					maxCount = counter;
					modes.clear();
					modes.add(last);
				} else if ( counter == maxCount ) {
					modes.add(last);
				}
				last = current;
				counter = 1;
			}
		}
		if ( counter == maxCount ) {
			modes.add(last);
		}
		
		return modes.toArray(new Decimal[]{});
	}


}
