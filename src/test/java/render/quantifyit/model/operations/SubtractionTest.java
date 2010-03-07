package render.quantifyit.model.operations;

import static render.quantifyit.model.AssertDecimal.assertDecimal;
import static render.quantifyit.model.Decimal.$;

import java.math.MathContext;

import org.junit.Test;

import render.quantifyit.model.Decimal;

public class SubtractionTest {
	
	@Test
	public void testMinusInt() {
		final Decimal simple = $(100).minus(50);
		assertDecimal(50, simple);
		
		final Decimal minusInt = $(1000).minus(256.56789);
		assertDecimal(743.43211, minusInt);
		
		final Decimal minusIntContext = $(1034, new MathContext(2)).minus(750);
		assertDecimal(250, minusIntContext);
		
		final Decimal largeInt = $(Integer.MIN_VALUE).minus(100);
		assertDecimal(Integer.MIN_VALUE - 100L, largeInt);
	}
	
	@Test
	public void testMinusDouble() {
		final Decimal simple = $(100d).minus(50d);
		assertDecimal(50d, simple);
		
		final Decimal minusDouble = $(1000d).minus(256.56789d);
		assertDecimal(743.43211d, minusDouble);
		
		final Decimal minusDoubleContext = $(1034d, new MathContext(2)).minus(750d);
		assertDecimal(250d, minusDoubleContext);
		
		final Decimal largeDouble = $(100000000000d).minus(10);
		assertDecimal(99999999990d, largeDouble);
	}

	@Test
	public void testMinusDecimal() {
		final Decimal simple = $(100).minus(50);
		assertDecimal(50, simple);
		
		final Decimal minusInt = $(1000).minus(256.56789);
		assertDecimal(743.43211, minusInt);
		
		final Decimal minusIntContext = $(1034, new MathContext(2)).minus(750);
		assertDecimal(250, minusIntContext);
		
		final Decimal largeInt = $(Integer.MIN_VALUE).minus(100);
		assertDecimal(Integer.MIN_VALUE - 100L, largeInt);	
	}

	@Test
	public void testMinusDecimalContext() {
		final Decimal simple = $(100).minus($(50), new MathContext(2));
		assertDecimal(50, simple);
		
		final Decimal minusInt = $(1000).minus(256.56789);
		assertDecimal(743.43211, minusInt);
		
		final Decimal minusIntContext = $(1034, new MathContext(2)).minus(750);
		assertDecimal(250, minusIntContext);
		
		final Decimal largeInt = $(Integer.MIN_VALUE).minus(100);
		assertDecimal(Integer.MIN_VALUE - 100L, largeInt);	
	}


}
