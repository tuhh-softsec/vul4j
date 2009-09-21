package render.quantifyit.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.math.BigDecimal;
import java.math.MathContext;

import org.junit.Before;
import org.junit.Test;


public class DecimalTest {
	
	@Before
	public void setUp(){
	}

	@Test
	public void testThatPositiveIntegerDecimalsWithStringConstructorAreEquals(){
		BigDecimal bPositiveIntegerString = new BigDecimal("12345");
		BigDecimal bmPositiveIntegerString = new BigDecimal("12345", new MathContext(3));
		Decimal dPositiveIntegerString = new Decimal("12345");
		Decimal dmPositiveIntegerString = new Decimal("12345", new MathContext(3));
		
		assertEqualString(bPositiveIntegerString, dPositiveIntegerString);
		assertEqualString(bmPositiveIntegerString, dmPositiveIntegerString);
		
		assertEquals(bPositiveIntegerString, dPositiveIntegerString.getBigDecimal());
		assertEquals(12345, dPositiveIntegerString.asInteger());
		assertEquals(bPositiveIntegerString.intValue(), dPositiveIntegerString.asInteger());
		assertEquals(12345d, dPositiveIntegerString.asDouble(), 0);
		assertEquals(bPositiveIntegerString.doubleValue(), dPositiveIntegerString.asDouble(), 0);
		
		assertEquals(bmPositiveIntegerString, dmPositiveIntegerString.getBigDecimal());
		assertEquals(12300, dmPositiveIntegerString.asInteger());
		assertEquals(bmPositiveIntegerString.intValue(), dmPositiveIntegerString.asInteger());
		assertEquals(12300d, dmPositiveIntegerString.asDouble(), 0);
		assertEquals(bmPositiveIntegerString.doubleValue(), dmPositiveIntegerString.asDouble(), 0);
	}
	
	@Test
	public void testThatNegativeIntegerDecimalsWithStringConstructorAreEquals(){
		BigDecimal bNegativeIntegerString = new BigDecimal("-12345");
		BigDecimal bmNegativeIntegerString = new BigDecimal("-12345", new MathContext(3));
		Decimal dNegativeIntegerString = new Decimal("-12345");
		Decimal dmNegativeIntegerString = new Decimal("-12345", new MathContext(3));
		
		assertEqualString(bNegativeIntegerString, dNegativeIntegerString);
		assertEqualString(bmNegativeIntegerString, dmNegativeIntegerString);

		assertEquals(bNegativeIntegerString, dNegativeIntegerString.getBigDecimal());
		assertEquals(-12345, dNegativeIntegerString.asInteger());
		assertEquals(bNegativeIntegerString.intValue(), dNegativeIntegerString.asInteger());
		assertEquals(-12345d, dNegativeIntegerString.asDouble(), 0);
		assertEquals(bNegativeIntegerString.doubleValue(), dNegativeIntegerString.asDouble(), 0);
		
		assertEquals(bmNegativeIntegerString, dmNegativeIntegerString.getBigDecimal());
		assertEquals(-12300, dmNegativeIntegerString.asInteger());
		assertEquals(bmNegativeIntegerString.intValue(), dmNegativeIntegerString.asInteger());
		assertEquals(-12300d, dmNegativeIntegerString.asDouble(), 0);
		assertEquals(bmNegativeIntegerString.doubleValue(), dmNegativeIntegerString.asDouble(), 0);
	}

	@Test
	public void testThatPositiveIntegerDecimalsWithIntConstructorAreEquals(){
		BigDecimal bPositiveIntegerInt = new BigDecimal(12345);
		Decimal dPositiveIntegerInt = new Decimal(12345);
		
		assertEqualString(bPositiveIntegerInt, dPositiveIntegerInt);
		
		assertEquals(bPositiveIntegerInt, dPositiveIntegerInt.getBigDecimal());
		assertEquals(12345, dPositiveIntegerInt.asInteger());
		assertEquals(bPositiveIntegerInt.intValue(), dPositiveIntegerInt.asInteger());
		assertEquals(12345d, dPositiveIntegerInt.asDouble(), 0);
		assertEquals(bPositiveIntegerInt.doubleValue(), dPositiveIntegerInt.asDouble(), 0);
	}
	
	@Test
	public void testThatNegativeIntegerDecimalsWithIntConstructorAreEquals(){
		BigDecimal bNegativeIntegerInt = new BigDecimal(-12345);
		Decimal dNegativeIntegerInt = new Decimal(-12345);
		
		assertEqualString(bNegativeIntegerInt, dNegativeIntegerInt);

		assertEquals(bNegativeIntegerInt, dNegativeIntegerInt.getBigDecimal());
		assertEquals(-12345, dNegativeIntegerInt.asInteger());
		assertEquals(bNegativeIntegerInt.intValue(), dNegativeIntegerInt.asInteger());
		assertEquals(-12345d, dNegativeIntegerInt.asDouble(), 0);
		assertEquals(bNegativeIntegerInt.doubleValue(), dNegativeIntegerInt.asDouble(), 0);
	}
	
	@Test
	public void provesThatBigDecimalDoubleConstructorLosePrecisionSoNoComparisonIsPosible(){
		Double original = 12345d;
		assertEquals("12345.0", original.toString());

		BigDecimal stringConstructor = new BigDecimal(original.toString());
		assertEquals("12345.0", stringConstructor.toString());

		BigDecimal doubleConstructor = new BigDecimal(original);
		assertEquals("12345", doubleConstructor.toString());
		assertEquals("12345", doubleConstructor.toPlainString());
		assertEquals("12345", doubleConstructor.toEngineeringString());
		assertEquals("12345.0", Double.toString(doubleConstructor.doubleValue()));
		assertEquals("12345.0", Double.valueOf(doubleConstructor.toString()).toString());
		
		assertFalse(stringConstructor.equals(doubleConstructor));
		
		Decimal decimal = new Decimal(original);
		assertEquals("12345.0", decimal.toString());	
		assertEquals("12345.0", decimal.toSciString());	
		assertEquals("12345.0", decimal.toEngString());	
		assertEquals("12345.0", Double.toString(decimal.asDouble()));
	}
	
	@Test
	public void testThatPositiveIntegerDecimalsWithDoubleConstructorCanBeConvertedToPrimitives(){
		BigDecimal bPositiveIntegerDouble = new BigDecimal(12345d);
		Decimal dPositiveIntegerDouble = new Decimal(12345d);

		assertEquals(12345, dPositiveIntegerDouble.asInteger());
		assertEquals(bPositiveIntegerDouble.intValue(), dPositiveIntegerDouble.asInteger());
		assertEquals(12345d, dPositiveIntegerDouble.asDouble(), 0);
		assertEquals(bPositiveIntegerDouble.doubleValue(), dPositiveIntegerDouble.asDouble(), 0);
	}
	
	@Test
	public void testThatNegativeIntegerDecimalsWithDoubleConstructorCanBeConvertedToPrimitives(){
		BigDecimal bNegativeIntegerDouble = new BigDecimal(-12345d);
		Decimal dNegativeIntegerDouble = new Decimal(-12345d);
		
		assertEquals(-12345, dNegativeIntegerDouble.asInteger());
		assertEquals(bNegativeIntegerDouble.intValue(), dNegativeIntegerDouble.asInteger());
		assertEquals(-12345d, dNegativeIntegerDouble.asDouble(), 0);
		assertEquals(bNegativeIntegerDouble.doubleValue(), dNegativeIntegerDouble.asDouble(), 0);
	}
	
	private void assertEqualString(BigDecimal expected, Decimal actual) {
		assertEquals(expected.toPlainString(), actual.toString());
		assertEquals(expected.toEngineeringString(), actual.toEngString());
		assertEquals(expected.toString(), actual.toSciString());
	}
}
