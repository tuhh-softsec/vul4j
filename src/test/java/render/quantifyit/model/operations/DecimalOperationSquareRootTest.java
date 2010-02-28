package render.quantifyit.model.operations;

import static render.quantifyit.model.AssertDecimal.assertDecimal;

import java.math.MathContext;

import org.junit.Test;

import render.quantifyit.model.Decimal;

public class DecimalOperationSquareRootTest {

	@Test
	public void testSquareRootSimple(){
		assertDecimal(3, Decimal.$(9).squareRoot());
		assertDecimal(Decimal.TEN, Decimal.$(100.0).squareRoot());
	}
	
	@Test
	public void testSquareRootDecimals(){
		assertDecimal(3.953479480154159, Decimal.$(15.63).squareRoot(), 15);
		assertDecimal(379.824, Decimal.$(144266).squareRoot(new MathContext(6)));
	}
	
	@Test
	public void testSquareRootOf2(){
		assertDecimal(1.4142135623730951, Decimal.TWO.squareRoot());
		assertDecimal(1.414214, Decimal.TWO.squareRoot(new MathContext(7)));
	}
}
