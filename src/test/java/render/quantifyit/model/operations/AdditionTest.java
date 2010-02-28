package render.quantifyit.model.operations;

import static render.quantifyit.model.Decimal.$;

import static org.junit.Assert.assertEquals;
import static render.quantifyit.model.AssertDecimal.assertDecimal;

import java.math.MathContext;

import org.junit.Test;

import render.quantifyit.model.Decimal;

public class AdditionTest {

	@Test
	public void testPlusInt() {
		Decimal simple = $(100).plus(100);
		assertDecimal(200, simple);
		
		Decimal largeInt = $(Integer.MAX_VALUE).plus(100);
		assertDecimal(Integer.MAX_VALUE + 100L, largeInt);
		
		Decimal negativeAddition = $(10).plus(-100);
		assertDecimal(-90, negativeAddition);
		
		Decimal plusInt = $(1000).plus(234.56789);
		assertDecimal(1234.56789, plusInt);
		
		Decimal plusIntContext = $(1234, new MathContext(2)).plus(34.56789);
		assertDecimal(1234.56789, plusIntContext);
	}
	

	@Test
	public void testPlusDouble() {
		Decimal simple = $(100d).plus(100d);
		assertDecimal(200,simple);
		
		Decimal largeDouble = $(Integer.MAX_VALUE).plus(100d);
		assertDecimal(Integer.MAX_VALUE + 100d, largeDouble);
		
		Decimal negativeAddition = $(10d).plus(-100d);
		assertDecimal(-90d, negativeAddition);
		
		Decimal plusDouble = $(1000d).plus(234.56789);
		assertDecimal(1234.56789d, plusDouble);
		
		Decimal plusDoubleContext = $(1234d, new MathContext(2)).plus(34.56789d);
		assertDecimal(1234.56789, plusDoubleContext);	
	}

	@Test
	public void testPlusDecimal() {
		Decimal simple = $(100).plus(100);
		assertDecimal(200, simple);
		
		Decimal largeInt = $(Integer.MAX_VALUE).plus(100);
		assertDecimal(Integer.MAX_VALUE + 100L, largeInt);
		
		Decimal negativeAddition = $(10).plus(-100);
		assertDecimal(-90, negativeAddition);
		
		Decimal plusInt = $(1000).plus(234.56789);
		assertDecimal(1234.56789, plusInt);
		
		Decimal plusIntContext = $(1234, new MathContext(2)).plus(34.56789);
		assertDecimal(1234.56789, plusIntContext);	
	}

	@Test
	public void testPlusDecimalContext() {
		Decimal simple = $(100).plus($(149), new MathContext(1));
		assertDecimal("2E+2", simple);
		
		Decimal largeInt = $(Integer.MAX_VALUE).plus($(100), new MathContext(5));
		final Decimal expected = $(Integer.MAX_VALUE + 100L, new MathContext(5));
		assertDecimal(expected, largeInt);
		
		Decimal negativeAddition = $(10).plus(-100);
		assertDecimal(-90, negativeAddition);
		
		Decimal plusInt = $(1000).plus($(234.56789), new MathContext(6));
		assertDecimal(1234.57, plusInt);
		
		Decimal plusIntContext = $(1234, new MathContext(2)).plus($(34.56789), new MathContext(3));
		assertEquals("1230", plusIntContext.toString());	
		assertDecimal("1.23E+3", plusIntContext);	
	}
	
}
