package render.quantifyit.statistics.descriptive;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import render.quantifyit.model.Probability;


public class WeightedAverageTest {
	
	@Test
	public void testShouldReturnWeightedAverage(){
		double weightedAverage = WeightedAverage.weighted(
							new Probability(.8, 20), 
							new Probability(.2, 30));
		
		assertEquals(22, weightedAverage, 0);
	}

}
