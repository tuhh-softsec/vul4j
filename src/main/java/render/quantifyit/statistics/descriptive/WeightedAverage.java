package render.quantifyit.statistics.descriptive;

import render.quantifyit.model.Decimal;
import render.quantifyit.model.Probability;

public class WeightedAverage {
	
	public static Decimal weighted(final Probability... probabilities){
		Decimal average = Decimal.ZERO;
		for (Probability probability : probabilities) {
			average = average.plus(	probability.getWeight().times(probability.getValue()) );
		}
		return average;
	}

}
