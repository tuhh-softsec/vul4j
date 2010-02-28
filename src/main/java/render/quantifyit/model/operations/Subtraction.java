package render.quantifyit.model.operations;

import java.math.MathContext;

import render.quantifyit.model.Decimal;

public class Subtraction {

	public static <X extends Decimal, Y extends Decimal> Decimal subtraction(X x, Y y){
		return Decimal.$(x.asBigDecimal().subtract(y.asBigDecimal()));
	}

	public static <X extends Decimal, Y extends Decimal> Decimal subtraction(X x, Y y,
			MathContext roundingCriteria) {
		return Decimal.$(x.asBigDecimal().subtract(y.asBigDecimal(), roundingCriteria));
	}
}
