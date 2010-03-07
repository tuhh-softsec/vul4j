package render.quantifyit.model.operations;

import java.math.MathContext;

import render.quantifyit.model.Decimal;

public final class Subtraction {

	private Subtraction() {}
	
	public static <X extends Decimal, Y extends Decimal> Decimal subtraction(final X x, final Y y) {
		return Decimal.$(x.asBigDecimal().subtract(y.asBigDecimal()));
	}

	public static <X extends Decimal, Y extends Decimal> Decimal subtraction(final X x, final Y y,
			final MathContext roundingCriteria) {
		return Decimal.$(x.asBigDecimal().subtract(y.asBigDecimal(), roundingCriteria));
	}
}
