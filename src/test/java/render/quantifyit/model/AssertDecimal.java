package render.quantifyit.model;

import static org.junit.Assert.assertTrue;
import static render.quantifyit.model.Decimal.$;


import java.math.RoundingMode;

public final class AssertDecimal {
	
	private AssertDecimal() {}
	
	public static void assertDecimal(final Decimal expected, final Decimal actual) {
		assertTrue(String.format("Decimal comparison: expected:%n%s but got: [%n%s%nPrecision: %s%nScale: %s]", 
				expected, actual, actual.getPrecision(), actual.getScale()), 
				expected.same(actual));
	}
	
	public static void assertDecimal(final int expected, final Decimal actual) {
		assertDecimal($(expected), actual);
	}
	
	public static void assertDecimal(final long expected, final Decimal actual) {
		assertDecimal($(expected), actual);
	}
	
	public static void assertDecimal(final double expected, final Decimal actual) {
		assertDecimal($(expected), actual);
	}
	
	public static void assertDecimal(final String expected, final Decimal actual) {
		assertDecimal($(expected), actual);
	}
	
	public static void assertDecimal(final Decimal expected, final Decimal actual, final int scale, final RoundingMode roundingMode) {
		if(expected.getScale() != scale) {
			throw new AssertionError(String.format("The expected scale (%s) is not the same as the desired: (%s).", expected.getScale(), scale));
		}
		final Decimal scaledActual = actual.scaleTo(scale, roundingMode);
		final String message = String.format(
				"Decimal comparison with scale (%s) and RoundingMode (%s), expected:%n%s but got:%n%s%n", 
				scale, roundingMode, expected, scaledActual);
		assertTrue(message, expected.same(scaledActual));
	}
	
	public static void assertDecimal(final Decimal expected, final Decimal actual, final int scale) {
		assertDecimal(expected, actual, scale, RoundingMode.HALF_EVEN);
	}
	
	public static void assertDecimal(final int expected, final Decimal actual, final int scale, final RoundingMode roundingMode) {
		assertDecimal($(expected), actual, scale, roundingMode);
	}
	
	public static void assertDecimal(final int expected, final Decimal actual, final int scale) {
		assertDecimal($(expected), actual, scale, RoundingMode.HALF_EVEN);
	}
	
	public static void assertDecimal(final double expected, final Decimal actual, final int scale, final RoundingMode roundingMode) {
		assertDecimal($(expected), actual, scale, roundingMode);
	}
	
	public static void assertDecimal(final double expected, final Decimal actual, final int scale) {
		assertDecimal($(expected), actual, scale, RoundingMode.HALF_EVEN);
	}

}
