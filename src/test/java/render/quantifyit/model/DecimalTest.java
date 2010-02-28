package render.quantifyit.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static render.quantifyit.model.AssertDecimal.assertDecimal;

import java.math.MathContext;

import org.junit.Test;

public class DecimalTest {

	@Test
	public void testThatDecimalsWithIdenticRepresentationAreTheSame(){
		final Decimal firstElement = Decimal.$(1234, new MathContext(2));
		assertDecimal(Decimal.$(1200), firstElement);
		Decimal identicInt = firstElement.plus(Decimal.$(34.56789), new MathContext(5));
		final Decimal expected = Decimal.$(1234.6);
		assertEquals(0, expected.compareTo(identicInt));
		assertTrue(expected.same(identicInt));
		
		Decimal identicDecimals = Decimal.$(654).plus(Decimal.$(46.321456789), new MathContext(6));
		assertTrue(Decimal.$(700.321).same(identicDecimals));
	}
	
	@Test
	public void testPositiveAndNegativeNumbers(){
		assertTrue(Decimal.$(0.0001).isPositive());
		assertTrue(Decimal.$(1).isPositive());
		assertFalse(Decimal.$(-0.0001).isPositive());
		assertTrue(Decimal.$(-1).isNegative());
		assertTrue(Decimal.$(-0.0001).isNegative());
		assertFalse(Decimal.$(0.0001).isNegative());
		assertFalse(Decimal.ZERO.isPositive());
		assertFalse(Decimal.ZERO.isNegative());
	}
	
	@Test
	public void testDecimalFormatting(){
		assertEquals("  45.000", String.format("%8.3f", Decimal.$(45).asBigDecimal()));
	}
	
}
