package render.quantifyit.model;

import static org.junit.Assert.assertTrue;

import java.math.RoundingMode;

public final class AssertDecimal {
	
	private AssertDecimal(){}
	
	public static void assertDecimal(final Decimal expected, final Decimal actual){
		assertTrue(String.format("Decimal comparison: expected:\n%s but got:\n%s", expected, actual), 
				expected.same(actual));
	}
	
	public static void assertDecimal(final int expected, final Decimal actual){
		assertDecimal(new Decimal(expected), actual);
	}
	
	public static void assertDecimal(final double expected, final Decimal actual){
		assertDecimal(new Decimal(expected), actual);
	}
	
	public static void assertDecimal(final Decimal expected, final Decimal actual, final int scale, final RoundingMode roundingMode){
		if(expected.getScale() != scale) {
			throw new AssertionError("The expected scale is not the same as the desired: " + scale);
		}
		final Decimal scaledActual = actual.scaleTo(scale, roundingMode);
		final String message = String.format(
				"Decimal comparison with scale (%s) and RoundingMode (%s), expected:\n%s but got:\n%s", 
				scale, roundingMode, expected, scaledActual);
		assertTrue(message, expected.same(scaledActual));
	}
	
	public static void assertDecimal(final Decimal expected, final Decimal actual, final int scale){
		assertDecimal(expected, actual, scale, RoundingMode.HALF_EVEN);
	}
	
	public static void assertDecimal(final int expected, final Decimal actual, final int scale, final RoundingMode roundingMode){
		assertDecimal(new Decimal(expected), actual, scale, roundingMode);
	}
	
	public static void assertDecimal(final int expected, final Decimal actual, final int scale){
		assertDecimal(new Decimal(expected), actual, scale, RoundingMode.HALF_EVEN);
	}
	
	public static void assertDecimal(final double expected, final Decimal actual, final int scale, final RoundingMode roundingMode){
		assertDecimal(new Decimal(expected), actual, scale, roundingMode);
	}
	
	public static void assertDecimal(final double expected, final Decimal actual, final int scale){
		assertDecimal(new Decimal(expected), actual, scale, RoundingMode.HALF_EVEN);
	}

}
