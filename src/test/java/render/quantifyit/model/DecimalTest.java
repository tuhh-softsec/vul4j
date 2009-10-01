package render.quantifyit.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.MathContext;

import org.junit.Test;

public class DecimalTest {

	@Test
	public void testThatDecimalsWithIdenticRepresentationAreTheSame(){
		Decimal identicInt = new Decimal(1234, new MathContext(2)).plus(new Decimal(34.56789), new MathContext(3));
		final Decimal expected = new Decimal("1230");
		assertFalse(expected.equals(identicInt));
		assertTrue(expected.compareTo(identicInt) == 0);
		assertTrue(expected.same(identicInt));
		
		Decimal identicDecimals = new Decimal(654).plus(new Decimal(46.321456789), new MathContext(6));
		assertTrue(new Decimal(700.321).same(identicDecimals));
	}
	
	@Test
	public void testPositiveAndNegativeNumbers(){
		assertTrue(new Decimal(0.0001).isPositive());
		assertTrue(new Decimal(1).isPositive());
		assertFalse(new Decimal(-0.0001).isPositive());
		assertTrue(new Decimal(-1).isNegative());
		assertTrue(new Decimal(-0.0001).isNegative());
		assertFalse(new Decimal(0.0001).isNegative());
		assertFalse(Decimal.ZERO.isPositive());
		assertFalse(Decimal.ZERO.isNegative());
	}
}
