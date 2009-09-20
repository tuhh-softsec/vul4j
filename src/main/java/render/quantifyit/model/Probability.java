package render.quantifyit.model;

public class Probability {
	
	private final double weight;
	
	private final double value;

	public Probability(final double weight, final double value){
		this.weight = weight;
		this.value = value;
	}
	
	public double getWeight() {
		return weight;
	}

	public double getValue() {
		return value;
	}

}
