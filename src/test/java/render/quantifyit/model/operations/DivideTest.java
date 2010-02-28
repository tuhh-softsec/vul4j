package render.quantifyit.model.operations;

import static render.quantifyit.model.Decimal.$;
import static org.junit.Assert.assertEquals;
import static render.quantifyit.model.AssertDecimal.assertDecimal;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.junit.Test;

import render.quantifyit.model.Decimal;

public class DivideTest {
	
	@Test
	public void testDivideRoundsHalfEvenWithNoPrecision(){
		Decimal result = new Divide(312, 7).halfEven().eval();
		assertDecimal(45, result);
	}
	
	@Test
	public void testDivideRoundsHalfEvenWithPrecision(){
		Decimal result = new Divide(312, 7).halfEven().precision(4).eval();
		assertDecimal(44.57, result);
	}
	
	@Test
	public void testDivideRoundsHalfUpWithPrecision(){
		Decimal result = new Divide(15, 2).halfUp().eval();
		assertDecimal(8, result);
	}
	
	@Test(expected=ArithmeticException.class)
	public void testDivideWithZeroThrowException(){
		new Divide(10, 0).eval();
	}

	
	@Test(expected=ArithmeticException.class)
	public void testNaN(){
		new Divide(0, 0).eval();
	}
	
	@Test
	public void testDivideInts() {
		Decimal simple = new Divide(100, 20).eval();
		assertDecimal(5, simple);
		
		Decimal negativeDivision = new Divide(-1000, 25).eval();
		assertDecimal(-40, negativeDivision);
		
		Decimal byIntContext = new Divide(700, 3).precision(2).eval();
		assertDecimal(230, byIntContext);
		
		Decimal largeInt = new Divide(Integer.MAX_VALUE, 10).eval();
		assertDecimal(Division.divide(Decimal.$(Integer.MAX_VALUE), Decimal.TEN), largeInt);
	}
	
	@Test
	public void testByLong() {
		Decimal simple = new Divide(100L, 20L).eval();
		assertDecimal(5, simple);	
		
		Decimal negativeDivision = new Divide(-1000L, 25L).eval();
		assertDecimal(-40, negativeDivision);
		
		Decimal byDoubleContext = new Divide(700L, 3L).precision(2).eval();
		assertDecimal(230, byDoubleContext);
		
		Decimal largeLong = new Divide(Long.MAX_VALUE, 10L).eval();
		assertDecimal(Division.divide(Decimal.$(Long.MAX_VALUE), Decimal.TEN), largeLong);
	}
	
	@Test
	public void testByDouble() {
		Decimal simple = new Divide(100d, 20d).eval();
		assertDecimal(5, simple);	
		
		Decimal negativeDivision = new Divide(-1000d, 25d).eval();
		assertDecimal(-40d, negativeDivision);
		
		Decimal byDoubleContext = new Divide(700d, 3d).precision(2).eval();
		assertDecimal(230, byDoubleContext);
		
		Decimal largeDouble = new Divide(Double.MAX_VALUE, 10d).eval();
		assertDecimal(Division.divide(Decimal.$(Double.MAX_VALUE), Decimal.TEN), largeDouble);
	}
	
	@Test
	public void testDivisionWithPeriodicResult(){
		double periodic = 312d/7d;
		assertEquals("long period", 44.57142857142857, periodic, 0);

		Decimal withPrecision = new Divide(312, 7).precision(16).eval();
		assertDecimal(44.57142857142857, withPrecision);	
		
		Decimal defaulted = $(312).divide(7);
		assertDecimal(44.5714285714, defaulted);	
	}
	
	@Test
	public void testDivisionRoundedToInt(){
		BigDecimal expected = new BigDecimal(312d).divide(new BigDecimal(7d), RoundingMode.HALF_EVEN);
		assertEquals(new BigDecimal(45), expected);
		
		Decimal result = new Divide(312, 7).halfEven().eval();
		assertDecimal(45, result);	
	}
	
	@Test
	public void testDivisionWithScaling(){
		BigDecimal expected = new BigDecimal(312d).divide(new BigDecimal(7d), 2, RoundingMode.HALF_UP);
		assertEquals(new BigDecimal("44.57"), expected);
		
		Decimal actual = new Divide(312, 7).scale(2).halfUp().eval();
		assertDecimal(44.57, actual);	
	}
	
	@Test
	public void testDivisionWithPrecision(){
		BigDecimal expected = new BigDecimal(312d).divide(new BigDecimal(7d), new MathContext(4));
		assertEquals(new BigDecimal("44.57"), expected);
		
		Decimal actual = new Divide(312, 7).precision(4).eval();
		assertDecimal(44.57, actual);
	}
	
	@Test
	public void testShouldRoughlyApproximateToPi(){
		double doubleApproximation = 223d/71d;
		assertEquals(3.1408450704, doubleApproximation, 9);

		BigDecimal bigDecimalApproximation = new BigDecimal(223d).divide(new BigDecimal(71d), RoundingMode.HALF_EVEN);
		assertEquals(new BigDecimal(3), bigDecimalApproximation);
		
		bigDecimalApproximation = new BigDecimal(223d).divide(new BigDecimal(71d), 5, RoundingMode.HALF_EVEN);
		assertEquals(new BigDecimal("3.14085"), bigDecimalApproximation);
		
		Decimal decimalApproximation = $(223).divide(71);
		assertDecimal(3.1408450704, decimalApproximation);	
		decimalApproximation = new Divide(223, 71).precision(6).eval();
		assertDecimal(3.14085, decimalApproximation);	
		decimalApproximation = $(223d).divide(71d);
		assertDecimal(3.1408450704, decimalApproximation);	
		decimalApproximation = new Divide(223d, 71d).precision(6).eval();
		assertDecimal(3.14085, decimalApproximation);	
	}
	
	@Test
	public void testShouldCloselyApproximateToPi(){
		double doubleApproximation = 355d/113d;
		assertEquals(3.1415929204, doubleApproximation, 9);
		
		BigDecimal bigDecimalApproximation = new BigDecimal(355d).divide(new BigDecimal(113d), RoundingMode.HALF_EVEN);
		assertEquals(new BigDecimal(3), bigDecimalApproximation);
		
		bigDecimalApproximation = new BigDecimal(355d).divide(new BigDecimal(113d), 7, RoundingMode.HALF_EVEN);
		assertEquals(new BigDecimal("3.1415929"), bigDecimalApproximation);
		
		Decimal decimalApproximation = $(355).divide(113);
		assertDecimal(3.1415929204, decimalApproximation);	
		decimalApproximation = new Divide(355, 113).precision(6).eval();
		assertDecimal(3.14159, decimalApproximation);	
		decimalApproximation = $(355d).divide(113d);
		assertDecimal(3.1415929204, decimalApproximation);	
		decimalApproximation = new Divide(355d, 113d).precision(6).eval();
		assertDecimal(3.14159, decimalApproximation);	
	}
}
