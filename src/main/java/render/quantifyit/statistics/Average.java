package render.quantifyit.statistics;

public interface Average {

	double mean(double... elements);
	
	
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
	double median(double... elements);
	
	Double[] mode(double... elements);
	
}
