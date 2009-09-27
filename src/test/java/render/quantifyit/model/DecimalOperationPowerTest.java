package render.quantifyit.model;

import static render.quantifyit.model.AssertDecimal.assertDecimal;

import java.math.MathContext;

import org.junit.Test;

public class DecimalOperationPowerTest {
	
	@Test
	public void testPowerInt() {
		Decimal simple = new Decimal(2).power(2);
		assertDecimal(new Decimal(4), simple);
		
		Decimal negative = new Decimal(-3).power(3);
		assertDecimal(new Decimal(-27), negative);
		
		Decimal powerInt = new Decimal(125).power(5);
		assertDecimal(new Decimal(30517578125d), powerInt);
		
		Decimal powerLargeInt = new Decimal(Integer.MAX_VALUE).power(2);
		assertDecimal(new Decimal("4611686014132420609"), powerLargeInt);
	}

	@Test
	public void testPowerIntMathContext() {
		Decimal simple = new Decimal(2).power(16, new MathContext(2));
		assertDecimal(new Decimal(66000), simple);
		
		Decimal negative = new Decimal(-3).power(3, new MathContext(1));
		assertDecimal(new Decimal(-30), negative);
		
		Decimal powerInt = new Decimal(125).power(5, new MathContext(10));
		assertDecimal(new Decimal(30517578130d), powerInt);
		
		Decimal powerLargeInt = new Decimal(Integer.MAX_VALUE).power(2, new MathContext(15));
		assertDecimal(new Decimal("4.61168601413242e+18"), powerLargeInt);	
	}
}
