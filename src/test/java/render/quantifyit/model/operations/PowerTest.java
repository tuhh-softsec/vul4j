package render.quantifyit.model.operations;

import static render.quantifyit.model.AssertDecimal.assertDecimal;
import static render.quantifyit.model.Decimal.$;

import java.math.MathContext;

import org.junit.Test;

import render.quantifyit.model.Decimal;

public class PowerTest {
	
	@Test
	public void testPowerInt() {
		final Decimal simple = $(2).power(2);
		assertDecimal(4, simple);
		
		final Decimal negative = $(-3).power(3);
		assertDecimal(-27, negative);
		
		final Decimal powerInt = $(125).power(5);
		assertDecimal(30517578125d, powerInt);
		
		final Decimal powerLargeInt = $(Integer.MAX_VALUE).power(2);
		assertDecimal("4611686014132420609", powerLargeInt);
	}

	@Test
	public void testPowerIntMathContext() {
		final Decimal simple = $(2).power(16, new MathContext(2));
		assertDecimal(66000, simple);
		
		final Decimal negative = $(-3).power(3, new MathContext(1));
		assertDecimal(-30, negative);
		
		final Decimal powerInt = $(125).power(5, new MathContext(10));
		assertDecimal(30517578130d, powerInt);
		
		final Decimal powerLargeInt = $(Integer.MAX_VALUE).power(2, new MathContext(15));
		assertDecimal("4.61168601413242e+18", powerLargeInt);	
	}
}
