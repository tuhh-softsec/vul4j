package render.quantifyit.statistics.descriptive;

import render.quantifyit.model.Decimal;

public class SampleSummary extends Summary {

	public SampleSummary(final Decimal... dataSet) {
		super(dataSet);
	}
	
	public Decimal getStandardDeviation(){
		return Dispersion.sampleStandardDeviation(getMean(), getDataSet());
	}
}
