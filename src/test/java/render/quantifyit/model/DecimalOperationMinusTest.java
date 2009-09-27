package render.quantifyit.model;

import static render.quantifyit.model.AssertDecimal.assertDecimal;

import java.math.MathContext;

import org.junit.Test;

public class DecimalOperationMinusTest {
	
	@Test
	public void testMinusInt() {
		Decimal simple = new Decimal(100).minus(50);
		assertDecimal(new Decimal(50), simple);
		
		Decimal minusInt = new Decimal(1000).minus(new Decimal(256.56789));
		assertDecimal(new Decimal("743.43211"), minusInt);
		
		Decimal minusIntContext = new Decimal(1034, new MathContext(2)).minus(750);
		assertDecimal(new Decimal(250), minusIntContext);
		
		Decimal largeInt = new Decimal(Integer.MIN_VALUE).minus(100);
		assertDecimal(new Decimal(Integer.MIN_VALUE - 100L), largeInt);
	}
	
	@Test
	public void testMinusDouble() {
		Decimal simple = new Decimal(100d).minus(50d);
		assertDecimal(new Decimal(50d), simple);
		
		Decimal minusDouble = new Decimal(1000d).minus(256.56789d);
		assertDecimal(new Decimal(743.43211d), minusDouble);
		
		Decimal minusDoubleContext = new Decimal(1034d, new MathContext(2)).minus(750d);
		assertDecimal(new Decimal(250d), minusDoubleContext);
		
		Decimal largeDouble = new Decimal(100000000000d).minus(10);
		assertDecimal(new Decimal(99999999990d), largeDouble);
	}

	@Test
	public void testMinusDecimal() {
		Decimal simple = new Decimal(100).minus(new Decimal(50));
		assertDecimal(new Decimal(50), simple);
		
		Decimal minusInt = new Decimal(1000).minus(new Decimal(256.56789));
		assertDecimal(new Decimal("743.43211"), minusInt);
		
		Decimal minusIntContext = new Decimal(1034, new MathContext(2)).minus(750);
		assertDecimal(new Decimal(250), minusIntContext);
		
		Decimal largeInt = new Decimal(Integer.MIN_VALUE).minus(100);
		assertDecimal(new Decimal(Integer.MIN_VALUE - 100L), largeInt);	
	}

	@Test
	public void testMinusDecimalContext() {
		Decimal simple = new Decimal(100).minus(new Decimal(50), new MathContext(2));
		assertDecimal(new Decimal(50), simple);
		
		Decimal minusInt = new Decimal(1000).minus(new Decimal(256.56789));
		assertDecimal(new Decimal("743.43211"), minusInt);
		
		Decimal minusIntContext = new Decimal(1034, new MathContext(2)).minus(750);
		assertDecimal(new Decimal(250), minusIntContext);
		
		Decimal largeInt = new Decimal(Integer.MIN_VALUE).minus(100);
		assertDecimal(new Decimal(Integer.MIN_VALUE - 100L), largeInt);	
	}


}
