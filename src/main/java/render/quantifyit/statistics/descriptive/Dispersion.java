package render.quantifyit.statistics.descriptive;

public class Dispersion {

	public static double standardDeviation(double... elements) {
		return Math.sqrt(variance(elements));
	}

	public static double variance(double... elements) {
		double mean = Average.mean(elements);
		double sumOfSquares = 0;
		for (double element : elements) {
			sumOfSquares += Math.pow(element, 2);
		}
		
		return (sumOfSquares/elements.length) - Math.pow(mean, 2);
	}

	public static double sd2Var(double standardDeviation) {
		return Math.pow(standardDeviation,2);
	}

	public static double var2Sd(double variance) {
		if(variance < 0){
			throw new IllegalArgumentException("Please give a non negative number.");
		}
		return Math.sqrt(variance);
	}
	
	public static double min(double... elements){
		if (elements == null || elements.length == 0){
			throw new IllegalArgumentException("Please give at least one value.");
		}
		double min = elements[0];
		for (double element : elements) {
			if(Math.min(min, element) != min){
				min = element;
			}
		}
		return min;
	}

	public static double max(double... elements){
		if (elements == null || elements.length == 0){
			throw new IllegalArgumentException("Please give at least one value.");
		}
		double max = elements[0];
		for (double element : elements) {
			if(Math.max(max, element) != max) {
				max = element;
			}
		}
		return max;
	}
	

}
