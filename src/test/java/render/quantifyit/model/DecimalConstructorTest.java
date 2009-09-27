package render.quantifyit.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.math.BigDecimal;
import java.math.MathContext;

import org.junit.Test;

public class DecimalConstructorTest {

	private static final int INT_NEGATIVE = -12345;
	private static final int INT_POSITIVE = 12345;
	private static final double DOUBLE_POSITIVE = 12345d;
	private static final double DOUBLE_NEGATIVE = -12345d;
	private static final String STRING_NEGATIVE = "-12345";
	private static final String STRING_POSITIVE = "12345";
	private static final String STRING_DECIMAL = "12345.0";

	@Test
	public void testThatPositiveIntegerDecimalsWithStringConstructorAreEquals(){
		final BigDecimal bPositiveIntegerString = new BigDecimal(STRING_POSITIVE);
		final BigDecimal bmPositiveIntegerString = new BigDecimal(STRING_POSITIVE, new MathContext(3));
		final Decimal dPositiveIntegerString = new Decimal(STRING_POSITIVE);
		final Decimal dmPositiveIntegerString = new Decimal(STRING_POSITIVE, new MathContext(3));
		
		assertEqualString(bPositiveIntegerString, dPositiveIntegerString);
		assertEqualString(bmPositiveIntegerString, dmPositiveIntegerString);
		
		assertEquals(bPositiveIntegerString, dPositiveIntegerString.asBigDecimal());
		assertEquals(INT_POSITIVE, dPositiveIntegerString.asInteger());
		assertEquals(bPositiveIntegerString.intValue(), dPositiveIntegerString.asInteger());
		assertEquals(DOUBLE_POSITIVE, dPositiveIntegerString.asDouble(), 0);
		assertEquals(bPositiveIntegerString.doubleValue(), dPositiveIntegerString.asDouble(), 0);
		
		assertEquals(bmPositiveIntegerString, dmPositiveIntegerString.asBigDecimal());
		assertEquals(12300, dmPositiveIntegerString.asInteger());
		assertEquals(bmPositiveIntegerString.intValue(), dmPositiveIntegerString.asInteger());
		assertEquals(12300d, dmPositiveIntegerString.asDouble(), 0);
		assertEquals(bmPositiveIntegerString.doubleValue(), dmPositiveIntegerString.asDouble(), 0);
	}
	
	@Test
	public void testThatNegativeIntegerDecimalsWithStringConstructorAreEquals(){
		final BigDecimal bNegativeIntegerString = new BigDecimal(STRING_NEGATIVE);
		final BigDecimal bmNegativeIntegerString = new BigDecimal(STRING_NEGATIVE, new MathContext(3));
		final Decimal dNegativeIntegerString = new Decimal(STRING_NEGATIVE);
		Decimal dmNegativeIntegerString = new Decimal(STRING_NEGATIVE, new MathContext(3));
		
		assertEqualString(bNegativeIntegerString, dNegativeIntegerString);
		assertEqualString(bmNegativeIntegerString, dmNegativeIntegerString);

		assertEquals(bNegativeIntegerString, dNegativeIntegerString.asBigDecimal());
		assertEquals(INT_NEGATIVE, dNegativeIntegerString.asInteger());
		assertEquals(bNegativeIntegerString.intValue(), dNegativeIntegerString.asInteger());
		assertEquals(DOUBLE_NEGATIVE, dNegativeIntegerString.asDouble(), 0);
		assertEquals(bNegativeIntegerString.doubleValue(), dNegativeIntegerString.asDouble(), 0);
		
		assertEquals(bmNegativeIntegerString, dmNegativeIntegerString.asBigDecimal());
		assertEquals(-12300, dmNegativeIntegerString.asInteger());
		assertEquals(bmNegativeIntegerString.intValue(), dmNegativeIntegerString.asInteger());
		assertEquals(-12300d, dmNegativeIntegerString.asDouble(), 0);
		assertEquals(bmNegativeIntegerString.doubleValue(), dmNegativeIntegerString.asDouble(), 0);
	}

	@Test
	public void testThatPositiveIntegerDecimalsWithIntConstructorAreEquals(){
		BigDecimal bPositiveIntegerInt = new BigDecimal(INT_POSITIVE);
		Decimal dPositiveIntegerInt = new Decimal(INT_POSITIVE);
		
		assertEqualString(bPositiveIntegerInt, dPositiveIntegerInt);
		
		assertEquals(bPositiveIntegerInt, dPositiveIntegerInt.asBigDecimal());
		assertEquals(INT_POSITIVE, dPositiveIntegerInt.asInteger());
		assertEquals(bPositiveIntegerInt.intValue(), dPositiveIntegerInt.asInteger());
		assertEquals(DOUBLE_POSITIVE, dPositiveIntegerInt.asDouble(), 0);
		assertEquals(bPositiveIntegerInt.doubleValue(), dPositiveIntegerInt.asDouble(), 0);
	}
	
	@Test
	public void testThatNegativeIntegerDecimalsWithIntConstructorAreEquals(){
		BigDecimal bNegativeIntegerInt = new BigDecimal(INT_NEGATIVE);
		Decimal dNegativeIntegerInt = new Decimal(INT_NEGATIVE);
		
		assertEqualString(bNegativeIntegerInt, dNegativeIntegerInt);

		assertEquals(bNegativeIntegerInt, dNegativeIntegerInt.asBigDecimal());
		assertEquals(INT_NEGATIVE, dNegativeIntegerInt.asInteger());
		assertEquals(bNegativeIntegerInt.intValue(), dNegativeIntegerInt.asInteger());
		assertEquals(DOUBLE_NEGATIVE, dNegativeIntegerInt.asDouble(), 0);
		assertEquals(bNegativeIntegerInt.doubleValue(), dNegativeIntegerInt.asDouble(), 0);
	}
	
	@Test
	public void provesThatBigDecimalDoubleConstructorLosePrecisionSoNoComparisonIsPosible(){
		Double original = DOUBLE_POSITIVE;
		assertEquals(STRING_DECIMAL, original.toString());

		BigDecimal stringConstructor = new BigDecimal(original.toString());
		assertEquals(STRING_DECIMAL, stringConstructor.toString());

		BigDecimal doubleConstructor = new BigDecimal(original);
		assertEquals(STRING_POSITIVE, doubleConstructor.toString());
		assertEquals(STRING_POSITIVE, doubleConstructor.toPlainString());
		assertEquals(STRING_POSITIVE, doubleConstructor.toEngineeringString());
		assertEquals(STRING_DECIMAL, Double.toString(doubleConstructor.doubleValue()));
		assertEquals(STRING_DECIMAL, Double.valueOf(doubleConstructor.toString()).toString());
		
		assertFalse(stringConstructor.equals(doubleConstructor));
		
		Decimal decimal = new Decimal(original);
		assertEquals(STRING_DECIMAL, decimal.toString());	
		assertEquals(STRING_DECIMAL, decimal.toSciString());	
		assertEquals(STRING_DECIMAL, decimal.toEngString());	
		assertEquals(STRING_DECIMAL, Double.toString(decimal.asDouble()));
	}
	
	@Test
	public void testThatPositiveIntegerDecimalsWithDoubleConstructorCanBeConvertedToPrimitives(){
		BigDecimal bPositiveIntegerDouble = new BigDecimal(DOUBLE_POSITIVE);
		Decimal dPositiveIntegerDouble = new Decimal(DOUBLE_POSITIVE);

		assertEquals(INT_POSITIVE, dPositiveIntegerDouble.asInteger());
		assertEquals(bPositiveIntegerDouble.intValue(), dPositiveIntegerDouble.asInteger());
		assertEquals(DOUBLE_POSITIVE, dPositiveIntegerDouble.asDouble(), 0);
		assertEquals(bPositiveIntegerDouble.doubleValue(), dPositiveIntegerDouble.asDouble(), 0);
	}
	
	@Test
	public void testThatNegativeIntegerDecimalsWithDoubleConstructorCanBeConvertedToPrimitives(){
		BigDecimal bNegativeIntegerDouble = new BigDecimal(DOUBLE_NEGATIVE);
		Decimal dNegativeIntegerDouble = new Decimal(DOUBLE_NEGATIVE);
		
		assertEquals(INT_NEGATIVE, dNegativeIntegerDouble.asInteger());
		assertEquals(bNegativeIntegerDouble.intValue(), dNegativeIntegerDouble.asInteger());
		assertEquals(DOUBLE_NEGATIVE, dNegativeIntegerDouble.asDouble(), 0);
		assertEquals(bNegativeIntegerDouble.doubleValue(), dNegativeIntegerDouble.asDouble(), 0);
	}

	@Test(expected=NumberFormatException.class)
	public void testShouldFailOnPositiveInfinity(){
		new Decimal(Double.POSITIVE_INFINITY);
	}
	
	@Test(expected=NumberFormatException.class)
	public void testShouldFailOnNegativeInfinity(){
		new Decimal(Double.NEGATIVE_INFINITY);
	}
	
	@Test(expected=NumberFormatException.class)
	public void testShouldFailOnNaN(){
		new Decimal(Double.NaN);
	}
	
	private void assertEqualString(BigDecimal expected, Decimal actual) {
		assertEquals(expected.toString(), actual.toSciString());
		assertEquals(expected.toEngineeringString(), actual.toEngString());
		assertEquals(expected.toPlainString(), actual.toString());
	}
	

}
