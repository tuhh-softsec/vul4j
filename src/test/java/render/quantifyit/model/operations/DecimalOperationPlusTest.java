package render.quantifyit.model.operations;

import static org.junit.Assert.assertEquals;
import static render.quantifyit.model.AssertDecimal.assertDecimal;

import java.math.MathContext;

import org.junit.Test;

import render.quantifyit.model.Decimal;

public class DecimalOperationPlusTest {

	@Test
	public void testPlusInt() {
		Decimal simple = Decimal.$(100).plus(100);
		assertDecimal(Decimal.$(200), simple);
		
		Decimal largeInt = Decimal.$(Integer.MAX_VALUE).plus(100);
		assertDecimal(Decimal.$(Integer.MAX_VALUE + 100L), largeInt);
		
		Decimal negativeAddition = Decimal.$(10).plus(-100);
		assertDecimal(Decimal.$(-90), negativeAddition);
		
		Decimal plusInt = Decimal.$(1000).plus(Decimal.$("234.56789"));
		assertDecimal(Decimal.$("1234.56789"), plusInt);
		
		Decimal plusIntContext = Decimal.$(1234, new MathContext(2)).plus(34.56789);
		assertDecimal(Decimal.$("1234.56789"), plusIntContext);
	}
	

	@Test
	public void testPlusDouble() {
		Decimal simple = Decimal.$(100d).plus(100d);
		assertDecimal(Decimal.$(200),simple);
		
		Decimal largeDouble = Decimal.$(Integer.MAX_VALUE).plus(100d);
		assertDecimal(Decimal.$(Integer.MAX_VALUE + 100d), largeDouble);
		
		Decimal negativeAddition = Decimal.$(10d).plus(-100d);
		assertDecimal(Decimal.$(-90d), negativeAddition);
		
		Decimal plusDouble = Decimal.$(1000d).plus(Decimal.$("234.56789"));
		assertDecimal(Decimal.$(1234.56789d), plusDouble);
		
		Decimal plusDoubleContext = Decimal.$(1234d, new MathContext(2)).plus(34.56789d);
		assertDecimal(Decimal.$(1234.56789), plusDoubleContext);	
	}

	@Test
	public void testPlusDecimal() {
		Decimal simple = Decimal.$(100).plus(Decimal.$("100"));
		assertDecimal(Decimal.$(200), simple);
		
		Decimal largeInt = Decimal.$(Integer.MAX_VALUE).plus(Decimal.$(100));
		assertDecimal(Decimal.$(Integer.MAX_VALUE + 100L), largeInt);
		
		Decimal negativeAddition = Decimal.$(10).plus(Decimal.$(-100));
		assertDecimal(Decimal.$(-90), negativeAddition);
		
		Decimal plusInt = Decimal.$(1000).plus(Decimal.$("234.56789"));
		assertDecimal(Decimal.$("1234.56789"), plusInt);
		
		Decimal plusIntContext = Decimal.$(1234, new MathContext(2)).plus(Decimal.$(34.56789));
		assertDecimal(Decimal.$("1234.56789"), plusIntContext);	
	}

	@Test
	public void testPlusDecimalContext() {
		Decimal simple = Decimal.$(100).plus(Decimal.$("149"), new MathContext(1));
		assertDecimal(Decimal.$("2E+2"), simple);
		
		Decimal largeInt = Decimal.$(Integer.MAX_VALUE).plus(Decimal.$(100), new MathContext(5));
		final Decimal expected = Decimal.$(Integer.MAX_VALUE + 100L, new MathContext(5));
		assertDecimal(expected, largeInt);
		
		Decimal negativeAddition = Decimal.$(10).plus(Decimal.$(-100));
		assertDecimal(Decimal.$(-90), negativeAddition);
		
		Decimal plusInt = Decimal.$(1000).plus(Decimal.$("234.56789"), new MathContext(6));
		assertDecimal(Decimal.$("1234.57"), plusInt);
		
		Decimal plusIntContext = Decimal.$(1234, new MathContext(2)).plus(Decimal.$(34.56789), new MathContext(3));
		assertEquals("1230", plusIntContext.toString());	
		assertDecimal(Decimal.$("1.23E+3"), plusIntContext);	
	}
	
}
