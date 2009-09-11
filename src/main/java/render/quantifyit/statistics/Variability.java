package render.quantifyit.statistics;

public interface Variability {

	double variance(double... elements);
	
	double standardDeviation(double... elements);
	
	double var2Sd(double variance);
	
	double sd2Var(double standardDeviation);

}
