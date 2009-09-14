package render.quantifyit.statistics.descriptive;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Average  {
	
	/**
	 * The arithmetic mean (or simply the mean) of a list of numbers is the sum of all 
	 * of the list divided by the number of items in the list. 
	 * Returns NaN if 0 elements.
	 * @param elements
	 * @return
	 */
	public static double mean(double... elements) {
		if (elements == null){
			throw new IllegalArgumentException("Please give at least one value.");
		}		
		int count = 0;
		double sum = 0;
		for (double element : elements){
			sum += element;
			count ++;
		}
		return sum/count;
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
	public static double median(double... elements) {
		if (elements == null || elements.length == 0){
			throw new IllegalArgumentException("Please give at least one value.");
		}
		Arrays.sort(elements);
		double median = 0;
		int medianPosition = elements.length/2;
		
		if (elements.length%2 == 0){
			median = (elements[medianPosition - 1] + elements[medianPosition])/2;
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
	public static Double[] mode(double... elements) {
		if (elements == null || elements.length == 0){
			throw new IllegalArgumentException("Please give at least one value.");
		}
		Set<Double> modes = new HashSet<Double>(); 
		int previousFrequency = 0;
		for (int i = 0; i < elements.length; i++) {
			int frequency = 0;
			for (int j = 0; j < elements.length; j++){
				if(elements[i] == elements[j]){
					frequency++;
				}
			}
			if(previousFrequency > frequency){
				modes.add(elements[i - 1]);
			} else if(previousFrequency < frequency) {
				modes.clear();
				modes.add(elements[i]);
			} else {
				modes.add(elements[i]);
				if(i != 0){
					modes.add(elements[i-1]);					
				}
			}
			previousFrequency = frequency;
		}
		
		return modes.toArray(new Double[]{});
	}


}
