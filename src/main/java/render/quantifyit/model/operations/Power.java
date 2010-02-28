package render.quantifyit.model.operations;

import java.math.MathContext;

import render.quantifyit.model.Decimal;

public class Power {

	public static <X extends Decimal> Decimal power(final X x, final int power){
		return Decimal.$(x.asBigDecimal().pow(power));
	}

	public static <X extends Decimal> Decimal power(final X x, final int power, final MathContext roundingCriteria){
		return Decimal.$(x.asBigDecimal().pow(power, roundingCriteria));
	}
}
