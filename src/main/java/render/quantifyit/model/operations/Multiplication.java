package render.quantifyit.model.operations;

import java.math.MathContext;

import render.quantifyit.model.Decimal;

public class Multiplication {

	public static <X extends Decimal, Y extends Decimal> Decimal multiply(final X x, final Y y){
		return Decimal.$(x.asBigDecimal().multiply(y.asBigDecimal()));
	}

	public static <X extends Decimal, Y extends Decimal> Decimal multiply(final X x, final  Y y, MathContext roundingCriteria) {
		return Decimal.$(x.asBigDecimal().multiply(y.asBigDecimal(), roundingCriteria));
	}
	
}
