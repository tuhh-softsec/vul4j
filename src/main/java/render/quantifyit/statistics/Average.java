package render.quantifyit.statistics;

public interface Average {
	
	/**
	 * The arithmetic mean (or simply the mean) of a list of numbers is the sum of all 
	 * of the list divided by the number of items in the list. 
	 * Returns NaN if 0 elements.
	 * @param elements
	 * @return
	 */
	double mean(double... elements);
	
	double median(double... elements);
	
	Double[] mode(double... elements);
	
}
