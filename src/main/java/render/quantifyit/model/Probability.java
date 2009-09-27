package render.quantifyit.model;

/**
 * The probability of an event and the associated value
 */
public class Probability {
	
	private final Decimal weight;
	
	private final Decimal value;

	public Probability(final Decimal weight, final Decimal value){
		this.weight = weight;
		this.value = value;
	}

	public Decimal getWeight() {
		return weight;
	}

	public Decimal getValue() {
		return value;
	}

}
