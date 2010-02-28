package render.quantifyit.model.operations;

import java.math.MathContext;

import render.quantifyit.model.Decimal;

public class Addition {

	private Addition(){}
	
	public static <X extends Decimal, Y extends Decimal> Decimal add(final X x, final Y y){
		return Decimal.$(x.asBigDecimal().add(y.asBigDecimal()));
	}

	public static <X extends Decimal, Y extends Decimal> Decimal add(final X x, final  Y y, final MathContext roundingCriteria){
		return Decimal.$(x.asBigDecimal().add(y.asBigDecimal(), roundingCriteria));
	}
}
