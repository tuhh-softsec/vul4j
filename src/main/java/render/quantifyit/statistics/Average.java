package render.quantifyit.statistics;

public interface Average {

	double mean(double... elements);
	
	double median(double... elements);
	
	Double[] mode(double... elements);
	
}
