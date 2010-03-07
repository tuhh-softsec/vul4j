package render.quantifyit.model.operations;

import java.math.MathContext;

import render.quantifyit.model.Decimal;

public abstract class AbstractArithmeticOperation implements Operation {

	protected final transient Decimal x;
	protected final transient Decimal y;

	protected transient MathContext mathContext;
	
	public AbstractArithmeticOperation(final int x, final int y) {
		this(Decimal.$(x), Decimal.$(y));
	}
	
	public AbstractArithmeticOperation(final long x, final long y) {
		this(Decimal.$(x), Decimal.$(y));
	}
	
	public AbstractArithmeticOperation(final double x, final double y) {
		this(Decimal.$(x), Decimal.$(y));
	}
	
	public <X extends Decimal, Y extends Decimal> AbstractArithmeticOperation(final X x, final Y y) {
		this.x = x;
		this.y = y;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Operation> T precision(final int precision) {
		this.mathContext = new MathContext(precision);
		return (T) this;
	}

}
