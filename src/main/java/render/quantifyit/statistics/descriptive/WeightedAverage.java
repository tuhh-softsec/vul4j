package render.quantifyit.statistics.descriptive;

import render.quantifyit.model.Probability;

public class WeightedAverage {
	
	public static double weighted(Probability... probabilities){
		double average = 0;
		for (Probability probability : probabilities) {
			average += probability.getWeight() * probability.getValue() ;
		}
		return average;
	}

}
