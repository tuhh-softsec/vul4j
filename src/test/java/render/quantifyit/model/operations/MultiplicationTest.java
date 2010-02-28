package render.quantifyit.model.operations;

import static org.junit.Assert.assertTrue;
import static render.quantifyit.model.AssertDecimal.assertDecimal;
import static render.quantifyit.model.Decimal.$;

import java.math.MathContext;

import org.junit.Test;

import render.quantifyit.model.Decimal;

public class MultiplicationTest {

	@Test
	public void testTimesInt() {
		Decimal simple = $(100).times(50);
		assertDecimal(5000, simple);	
		
		Decimal negativeMultiplication = $(-1000).times($(256));
		assertDecimal(-256000, negativeMultiplication);
		
		Decimal timesIntContext = $(435, new MathContext(2)).times(2);
		assertTrue($(880).same(timesIntContext));
		
		Decimal largeInt = $(Integer.MAX_VALUE).times(10);
		assertTrue($(new Double(Integer.MAX_VALUE) * 10d).same(largeInt));
	}
	
	@Test
	public void testTimesDouble() {
		Decimal simple = $(100d).times(50d);
		assertDecimal(5000d, simple);	
		
		Decimal negativeMultiplication = $(-1000d).times(256d);
		assertDecimal(-256000d, negativeMultiplication);
		
		Decimal timesDoubleContext = $(435d, new MathContext(2)).times(2d);
		assertDecimal(880d, timesDoubleContext);
		
		Decimal largeDouble = $(100000000000d).times(10);
		assertDecimal(1000000000000d, largeDouble);	
	}


	@Test
	public void testTimesDecimal() {
		Decimal simple = $(100).times($(50));
		assertDecimal(5000, simple);	
		
		Decimal negativeMultiplication = $(-1000).times($(256));
		assertDecimal(-256000, negativeMultiplication);
		
		Decimal timesIntContext = $(435, new MathContext(2)).times(2);
		assertDecimal($("8.8E+2"), timesIntContext);
		
		Decimal largeInt = $(Integer.MAX_VALUE).times(10);
		assertDecimal($(Integer.MAX_VALUE).times(Decimal.TEN), largeInt);	
	}
	
}
