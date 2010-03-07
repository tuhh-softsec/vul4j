package render.quantifyit.model.operations;

import java.math.MathContext;

import render.quantifyit.model.Decimal;

public final class Multiplication {

	private Multiplication() {}
	
	public static <X extends Decimal, Y extends Decimal> Decimal multiply(final X x, final Y y) {
		return Decimal.$(x.asBigDecimal().multiply(y.asBigDecimal()));
	}

	public static <X extends Decimal, Y extends Decimal> Decimal multiply(final X x, final  Y y, final MathContext roundingCriteria) {
		return Decimal.$(x.asBigDecimal().multiply(y.asBigDecimal(), roundingCriteria));
	}
	
}
