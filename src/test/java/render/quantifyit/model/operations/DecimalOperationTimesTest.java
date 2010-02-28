package render.quantifyit.model.operations;

import static org.junit.Assert.assertTrue;
import static render.quantifyit.model.AssertDecimal.assertDecimal;

import java.math.MathContext;

import org.junit.Test;

import render.quantifyit.model.Decimal;

public class DecimalOperationTimesTest {

	@Test
	public void testTimesInt() {
		Decimal simple = Decimal.$(100).times(50);
		assertDecimal(Decimal.$(5000), simple);	
		
		Decimal negativeMultiplication = Decimal.$(-1000).times(Decimal.$(256));
		assertDecimal(Decimal.$(-256000), negativeMultiplication);
		
		Decimal timesIntContext = Decimal.$(435, new MathContext(2)).times(2);
		assertTrue(Decimal.$(880).same(timesIntContext));
		
		Decimal largeInt = Decimal.$(Integer.MAX_VALUE).times(10);
		assertTrue(Decimal.$(new Double(Integer.MAX_VALUE) * 10d).same(largeInt));
	}
	
	@Test
	public void testTimesDouble() {
		Decimal simple = Decimal.$(100d).times(50d);
		assertDecimal(Decimal.$(5000d), simple);	
		
		Decimal negativeMultiplication = Decimal.$(-1000d).times(256d);
		assertDecimal(Decimal.$(-256000d), negativeMultiplication);
		
		Decimal timesDoubleContext = Decimal.$(435d, new MathContext(2)).times(2d);
		assertDecimal(Decimal.$(880d), timesDoubleContext);
		
		Decimal largeDouble = Decimal.$(100000000000d).times(10);
		assertDecimal(Decimal.$(1000000000000d), largeDouble);	
	}


	@Test
	public void testTimesDecimal() {
		Decimal simple = Decimal.$(100).times(Decimal.$(50));
		assertDecimal(Decimal.$(5000), simple);	
		
		Decimal negativeMultiplication = Decimal.$(-1000).times(Decimal.$(256));
		assertDecimal(Decimal.$(-256000), negativeMultiplication);
		
		Decimal timesIntContext = Decimal.$(435, new MathContext(2)).times(2);
		assertDecimal(Decimal.$("8.8E+2"), timesIntContext);
		
		Decimal largeInt = Decimal.$(Integer.MAX_VALUE).times(10);
		assertDecimal(Decimal.$(Integer.MAX_VALUE).times(Decimal.TEN), largeInt);	
	}
	
}
