package render.quantifyit.model.operations;

import java.math.MathContext;

import render.quantifyit.model.Decimal;

public abstract class AbstractArithmeticOperation implements Operation {

	protected final Decimal x;
	protected final Decimal y;

	protected MathContext mathContext;
	
	public AbstractArithmeticOperation(final int x, final int y){
		this(Decimal.$(x), Decimal.$(y));
	}
	
	public AbstractArithmeticOperation(final long x, final long y){
		this(Decimal.$(x), Decimal.$(y));
	}
	
	public AbstractArithmeticOperation(final double x, final double y){
		this(Decimal.$(x), Decimal.$(y));
	}
	
	public <X extends Decimal, Y extends Decimal> AbstractArithmeticOperation(final X x, final Y y){
		this.x = x;
		this.y = y;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Operation> T precision(int precision) {
		this.mathContext = new MathContext(precision);
		return (T) this;
	}

}
