package render.quantifyit.model.operations;

import java.math.RoundingMode;

import render.quantifyit.model.Decimal;

public class Divide extends AbstractArithmeticOperation {
	
	protected transient Integer scaleField;
	private transient RoundingMode roundingMode;
	
	public Divide(final int x, final int y) {
		super(x,y);
	}
	
	public Divide(final long x, final long y) {
		super(x,y);
	}

	public Divide(final double x, final double y) {
		super(x,y);
	}
	
	public Divide(final Decimal dividend, final Decimal divisor) {
		super(dividend, divisor);
		if(divisor.isZero()) {
			if(dividend.isZero()) {
				throw new ArithmeticException("Division undefined: 0 / 0 is Not a Number");
			}
			throw new ArithmeticException(String.format("Divide by zero exception: %s / 0", dividend));
		}
	}

	public Divide scale(final Integer scale) {
		this.scaleField = scale;
		return this;
	}

	public Divide round(final RoundingMode roundingMode) {
		this.roundingMode = roundingMode;
		return this;
	}
	
	public Divide halfEven() {
		this.roundingMode = RoundingMode.HALF_EVEN;
		return this;
	}
	
	public Divide halfUp() {
		this.roundingMode = RoundingMode.HALF_UP;
		return this;
	}
	
	@Override
	public Decimal eval() {
		if(mathContext != null ) {
			return Division.divide(x, y, mathContext);
		}
		
		final boolean hasScale = scaleField != null;
		final boolean hasRoundingMode = roundingMode != null;
		if (hasScale && hasRoundingMode ) {
			return Division.divide(x, y, scaleField, roundingMode);
		}
		if ( hasRoundingMode ) {
			return Division.divide(x, y, roundingMode);
		} 

		return Division.divide(x, y);
	}

}
