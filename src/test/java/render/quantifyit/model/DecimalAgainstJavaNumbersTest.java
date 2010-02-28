package render.quantifyit.model;

import static render.quantifyit.model.Decimal.$;
import java.math.BigDecimal;
import java.math.MathContext;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static render.quantifyit.model.AssertDecimal.assertDecimal;

import static java.math.BigDecimal.TEN;

public class DecimalAgainstJavaNumbersTest {
	
	@Test
	public void oneBigDecimal(){
		final BigDecimal pointOneB = new BigDecimal(.1);
		BigDecimal sumB = BigDecimal.ZERO;
		for (int i = 0; i < 10; i++) {
			sumB = sumB.add(pointOneB);
		}
		assertFalse(BigDecimal.ONE.equals(sumB));
	}
	
	@Test
	public void oneDouble(){
		final double pointOneD = .1;
		double sumD = 0;
		for (int i = 0; i < 10; i++) {
			sumD = sumD + pointOneD;
		}
		assertFalse(Double.valueOf(1.0).equals(sumD));
	}
	
	@Test 
	public void oneDecimal(){
		final Decimal pointOne = Decimal.$(.1);

		Decimal sumD = Decimal.ZERO;
		for (int i = 0; i < 10; i++) {
			sumD = sumD.plus(pointOne);
		}
		assertEquals("1.0", sumD.toString());
		assertFalse(Decimal.$(1).equals(sumD.toString()));
		assertTrue(Decimal.$(1).compareTo(sumD) == 0);
		assertTrue(Decimal.$(1).same(sumD));
		assertTrue(Decimal.ONE.same(sumD));
	}
	
	@Test
	public void conciseness(){
		assertEquals(.25, .5*.5, 0);
		assertDecimal($(.25), $(.5).times(.5));
	}
	
	@Test
	public void testShowASimpleChainingVersusBigDecimal(){
		BigDecimal result = new BigDecimal(125.75).subtract(new BigDecimal(100)).add(new BigDecimal(4.25)).divide(new BigDecimal(3), new MathContext(2));
		assertEquals(TEN, result);
	
		Decimal actual = $(125.75).minus(100).plus(4.25).divideBy(3);
		assertDecimal(10, actual);
	}
	
}
