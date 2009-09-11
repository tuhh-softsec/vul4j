package render.lambda.statistics;

public class VariabilityImpl implements Variability {

	private final Average average;

	public VariabilityImpl(Average average) {
		this.average = average;
	}

	@Override
	public double standardDeviation(double... elements) {
		return Math.sqrt(variance(elements));
	}

	@Override
	public double variance(double... elements) {
		double mean = average.mean(elements);
		double sumOfSquares = 0;
		for (double element : elements) {
			sumOfSquares += Math.pow(element, 2);
		}
		
		return (sumOfSquares/elements.length) - Math.pow(mean, 2);
	}

	@Override
	public double sd2Var(double standardDeviation) {
		return Math.pow(standardDeviation,2);
	}

	@Override
	public double var2Sd(double variance) {
		if(variance < 0){
			throw new IllegalArgumentException("Please give a non negative number.");
		}
		return Math.sqrt(variance);
	}

}
