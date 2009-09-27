package render.quantifyit.model;

import static org.junit.Assert.assertEquals;
import static render.quantifyit.model.AssertDecimal.assertDecimal;

import java.math.MathContext;

import org.junit.Test;

public class DecimalOperationPlusTest {

	@Test
	public void testPlusInt() {
		Decimal simple = new Decimal(100).plus(100);
		assertDecimal(new Decimal(200), simple);
		
		Decimal largeInt = new Decimal(Integer.MAX_VALUE).plus(100);
		assertDecimal(new Decimal(Integer.MAX_VALUE + 100L), largeInt);
		
		Decimal negativeAddition = new Decimal(10).plus(-100);
		assertDecimal(new Decimal(-90), negativeAddition);
		
		Decimal plusInt = new Decimal(1000).plus(new Decimal("234.56789"));
		assertDecimal(new Decimal("1234.56789"), plusInt);
		
		Decimal plusIntContext = new Decimal(1234, new MathContext(2)).plus(34.56789);
		assertDecimal(new Decimal("1234.56789"), plusIntContext);
	}
	

	@Test
	public void testPlusDouble() {
		Decimal simple = new Decimal(100d).plus(100d);
		assertDecimal(new Decimal(200),simple);
		
		Decimal largeDouble = new Decimal(Integer.MAX_VALUE).plus(100d);
		assertDecimal(new Decimal(Integer.MAX_VALUE + 100d), largeDouble);
		
		Decimal negativeAddition = new Decimal(10d).plus(-100d);
		assertDecimal(new Decimal(-90d), negativeAddition);
		
		Decimal plusDouble = new Decimal(1000d).plus(new Decimal("234.56789"));
		assertDecimal(new Decimal(1234.56789d), plusDouble);
		
		Decimal plusDoubleContext = new Decimal(1234d, new MathContext(2)).plus(34.56789d);
		assertDecimal(new Decimal(1234.56789), plusDoubleContext);	
	}

	@Test
	public void testPlusDecimal() {
		Decimal simple = new Decimal(100).plus(new Decimal("100"));
		assertDecimal(new Decimal(200), simple);
		
		Decimal largeInt = new Decimal(Integer.MAX_VALUE).plus(new Decimal(100));
		assertDecimal(new Decimal(Integer.MAX_VALUE + 100L), largeInt);
		
		Decimal negativeAddition = new Decimal(10).plus(new Decimal(-100));
		assertDecimal(new Decimal(-90), negativeAddition);
		
		Decimal plusInt = new Decimal(1000).plus(new Decimal("234.56789"));
		assertDecimal(new Decimal("1234.56789"), plusInt);
		
		Decimal plusIntContext = new Decimal(1234, new MathContext(2)).plus(new Decimal(34.56789));
		assertDecimal(new Decimal("1234.56789"), plusIntContext);	
	}

	@Test
	public void testPlusDecimalContext() {
		Decimal simple = new Decimal(100).plus(new Decimal("149"), new MathContext(1));
		assertDecimal(new Decimal("2E+2"), simple);
		
		Decimal largeInt = new Decimal(Integer.MAX_VALUE).plus(new Decimal(100), new MathContext(5));
		assertDecimal(new Decimal(Integer.MAX_VALUE + 100L, new MathContext(5)), largeInt);
		
		Decimal negativeAddition = new Decimal(10).plus(new Decimal(-100));
		assertDecimal(new Decimal(-90), negativeAddition);
		
		Decimal plusInt = new Decimal(1000).plus(new Decimal("234.56789"), new MathContext(6));
		assertDecimal(new Decimal("1234.57"), plusInt);
		
		Decimal plusIntContext = new Decimal(1234, new MathContext(2)).plus(new Decimal(34.56789), new MathContext(3));
		assertEquals("1230", plusIntContext.toString());	
		assertDecimal(new Decimal("1.23E+3"), plusIntContext);	
	}
	
}
