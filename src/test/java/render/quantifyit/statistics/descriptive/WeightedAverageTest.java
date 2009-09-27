package render.quantifyit.statistics.descriptive;

import static render.quantifyit.model.AssertDecimal.assertDecimal;

import org.junit.Test;

import render.quantifyit.model.Decimal;
import render.quantifyit.model.Probability;


public class WeightedAverageTest {
	
	@Test
	public void testShouldReturnWeightedAverage(){
		Decimal weightedAverage = WeightedAverage.weighted(
							new Probability(new Decimal(.8), new Decimal(20)), 
							new Probability(new Decimal(.2), new Decimal(30)));
		
		assertDecimal(new Decimal(22), weightedAverage);
	}

}
