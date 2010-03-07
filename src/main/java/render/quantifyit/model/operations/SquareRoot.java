package render.quantifyit.model.operations;

import java.math.MathContext;

import render.quantifyit.model.Decimal;

public final class SquareRoot {

	private SquareRoot() {}
	
	public static Decimal squareRoot(final int squared) {
		return Decimal.$(Math.sqrt(squared));
	}
	
	public static Decimal squareRoot(final double squared) {
		return Decimal.$(Math.sqrt(squared));
	}
	
	public static Decimal squareRoot(final Decimal squared) {
		return Decimal.$(Math.sqrt(squared.asDouble()));
	}
	
	public static Decimal squareRoot(final Decimal squared, final MathContext roundingCriteria) {
		return Decimal.$(Math.sqrt(squared.asDouble()), roundingCriteria);
	}
	
}
