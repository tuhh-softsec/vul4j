package render.quantifyit.model.operations;

import java.math.MathContext;
import java.math.RoundingMode;

import render.quantifyit.model.Decimal;

public class Division {

	private Division(){}
	
	public static <X extends Decimal, Y extends Decimal> Decimal divide(final X x, final Y y){
		return Decimal.$(x.asBigDecimal().divide(y.asBigDecimal()));
	}
	
	public static <X extends Decimal, Y extends Decimal> Decimal divide(final X x, final Y y, final int scale){
		return Decimal.$(x.asBigDecimal().divide(y.asBigDecimal(), scale));
	}
	
	public static <X extends Decimal, Y extends Decimal> Decimal divide(final X x, final Y y, final RoundingMode roundingMode){
		return Decimal.$(x.asBigDecimal().divide(y.asBigDecimal(), roundingMode));
	}
	
	public static <X extends Decimal, Y extends Decimal> Decimal divide(final X x, final Y y, final int scale, final RoundingMode roundingMode){
		return Decimal.$(x.asBigDecimal().divide(y.asBigDecimal(), scale, roundingMode));
	}
	
	public static <X extends Decimal, Y extends Decimal> Decimal divide(final X x, final Y y, 
			final MathContext roundingCriteria){
		return Decimal.$(x.asBigDecimal().divide(y.asBigDecimal(),roundingCriteria));
	}
}
