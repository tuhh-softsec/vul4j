package render.quantifyit.statistics.descriptive;

import render.quantifyit.model.Decimal;

public class PopulationSummary extends Summary {
	
	public PopulationSummary(final Decimal... dataSet) {
		super(dataSet);
	}

	public Decimal getStandardDeviation(){
		return Dispersion.populationStandardDeviation(getDataSet());
	}

	public Decimal zScoreFor(int index){
		return Dispersion.zScore(dataSet[index], getMean(), getStandardDeviation());
	}
}
