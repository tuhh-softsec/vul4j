package render.quantifyit.model;

import static org.junit.Assert.assertTrue;
import static render.quantifyit.model.AssertDecimal.assertDecimal;

import java.math.MathContext;

import org.junit.Test;

public class DecimalOperationTimesTest {

	@Test
	public void testTimesInt() {
		Decimal simple = new Decimal(100).times(50);
		assertDecimal(new Decimal(5000), simple);	
		
		Decimal negativeMultiplication = new Decimal(-1000).times(new Decimal(256));
		assertDecimal(new Decimal(-256000), negativeMultiplication);
		
		Decimal timesIntContext = new Decimal(435, new MathContext(2)).times(2);
		assertTrue(new Decimal(880).same(timesIntContext));
		
		Decimal largeInt = new Decimal(Integer.MAX_VALUE).times(10);
		assertTrue(new Decimal(new Double(Integer.MAX_VALUE) * 10d).same(largeInt));
	}
	
	@Test
	public void testTimesDouble() {
		Decimal simple = new Decimal(100d).times(50d);
		assertDecimal(new Decimal(5000d), simple);	
		
		Decimal negativeMultiplication = new Decimal(-1000d).times(256d);
		assertDecimal(new Decimal(-256000d), negativeMultiplication);
		
		Decimal timesDoubleContext = new Decimal(435d, new MathContext(2)).times(2d);
		assertDecimal(new Decimal(880d), timesDoubleContext);
		
		Decimal largeDouble = new Decimal(100000000000d).times(10);
		assertDecimal(new Decimal(1000000000000d), largeDouble);	
	}


	@Test
	public void testTimesDecimal() {
		Decimal simple = new Decimal(100).times(new Decimal(50));
		assertDecimal(new Decimal(5000), simple);	
		
		Decimal negativeMultiplication = new Decimal(-1000).times(new Decimal(256));
		assertDecimal(new Decimal(-256000), negativeMultiplication);
		
		Decimal timesIntContext = new Decimal(435, new MathContext(2)).times(2);
		assertDecimal(new Decimal("8.8E+2"), timesIntContext);
		
		Decimal largeInt = new Decimal(Integer.MAX_VALUE).times(10);
		assertDecimal(new Decimal(Integer.MAX_VALUE).times(Decimal.TEN), largeInt);	
	}
	
}
