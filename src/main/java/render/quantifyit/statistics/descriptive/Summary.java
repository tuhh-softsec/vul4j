package render.quantifyit.statistics.descriptive;

import render.quantifyit.model.Decimal;
import render.quantifyit.util.DecimalUtils;

public abstract class Summary {

	protected final Decimal[] dataSet;

	public Summary(final Decimal... dataSet){
		DecimalUtils.notNullOrEmpty(dataSet);
		this.dataSet = dataSet;
	}
	
	public Decimal getMean(){
		return Average.mean(getDataSet());
	}
	
	public abstract Decimal getStandardDeviation();

	public Decimal[] getDataSet() {
		return dataSet;
	}
}
