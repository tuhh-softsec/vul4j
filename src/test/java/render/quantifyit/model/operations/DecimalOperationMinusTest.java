package render.quantifyit.model.operations;

import static render.quantifyit.model.AssertDecimal.assertDecimal;

import java.math.MathContext;

import org.junit.Test;

import render.quantifyit.model.Decimal;

public class DecimalOperationMinusTest {
	
	@Test
	public void testMinusInt() {
		Decimal simple = Decimal.$(100).minus(50);
		assertDecimal(Decimal.$(50), simple);
		
		Decimal minusInt = Decimal.$(1000).minus(Decimal.$(256.56789));
		assertDecimal(Decimal.$("743.43211"), minusInt);
		
		Decimal minusIntContext = Decimal.$(1034, new MathContext(2)).minus(750);
		assertDecimal(Decimal.$(250), minusIntContext);
		
		Decimal largeInt = Decimal.$(Integer.MIN_VALUE).minus(100);
		assertDecimal(Decimal.$(Integer.MIN_VALUE - 100L), largeInt);
	}
	
	@Test
	public void testMinusDouble() {
		Decimal simple = Decimal.$(100d).minus(50d);
		assertDecimal(Decimal.$(50d), simple);
		
		Decimal minusDouble = Decimal.$(1000d).minus(256.56789d);
		assertDecimal(Decimal.$(743.43211d), minusDouble);
		
		Decimal minusDoubleContext = Decimal.$(1034d, new MathContext(2)).minus(750d);
		assertDecimal(Decimal.$(250d), minusDoubleContext);
		
		Decimal largeDouble = Decimal.$(100000000000d).minus(10);
		assertDecimal(Decimal.$(99999999990d), largeDouble);
	}

	@Test
	public void testMinusDecimal() {
		Decimal simple = Decimal.$(100).minus(Decimal.$(50));
		assertDecimal(Decimal.$(50), simple);
		
		Decimal minusInt = Decimal.$(1000).minus(Decimal.$(256.56789));
		assertDecimal(Decimal.$("743.43211"), minusInt);
		
		Decimal minusIntContext = Decimal.$(1034, new MathContext(2)).minus(750);
		assertDecimal(Decimal.$(250), minusIntContext);
		
		Decimal largeInt = Decimal.$(Integer.MIN_VALUE).minus(100);
		assertDecimal(Decimal.$(Integer.MIN_VALUE - 100L), largeInt);	
	}

	@Test
	public void testMinusDecimalContext() {
		Decimal simple = Decimal.$(100).minus(Decimal.$(50), new MathContext(2));
		assertDecimal(Decimal.$(50), simple);
		
		Decimal minusInt = Decimal.$(1000).minus(Decimal.$(256.56789));
		assertDecimal(Decimal.$("743.43211"), minusInt);
		
		Decimal minusIntContext = Decimal.$(1034, new MathContext(2)).minus(750);
		assertDecimal(Decimal.$(250), minusIntContext);
		
		Decimal largeInt = Decimal.$(Integer.MIN_VALUE).minus(100);
		assertDecimal(Decimal.$(Integer.MIN_VALUE - 100L), largeInt);	
	}


}
