package render.quantifyit.model;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.MathContext;

import org.junit.Before;
import org.junit.Test;

public class DecimalPrecisionAndScaleTest {
	
	@Before
	public void setUp(){
		separator();
	}
	
	@Test
	public void testIntegerConstructorWithoutContext(){
		message("Integer constructor, without context");
		
		output(202);
		output(422);
		output(Integer.MAX_VALUE);
		output(Integer.MIN_VALUE);
	}
	
	@Test
	public void testIntegerConstructorWithContext(){
		message("Integer constructor, with context");
		
		output(202, new MathContext(1));
		output(422, new MathContext(2));
		output(Integer.MAX_VALUE, new MathContext(5));
		output(Integer.MIN_VALUE, new MathContext(10));
	}
	
	/**
	 * BigDecimal String constructor
	 * @see {@link java.math.BigDecimal(String)}
	 */
	@Test
	public void testDoubleConstructorWithoutContext(){
		message("Double constructor, no context. WARNING: precision and scale in Decimal is different than BigDecimal");
		
		output(202.000);
		output(422.0000);
		output(Math.PI);
		output(Math.E);
		output(100000d + Math.PI);
		output(300d + Math.E);
	}
	
	/**
	 * BigDecimal String constructor
	 * @see {@link java.math.BigDecimal(String)}
	 */
	@Test
	public void testDoubleConstructorWithContext(){
		message("Double constructor, with context.");
		
		output(202.000, new MathContext(1));
		output(422.0000, new MathContext(2));
		output(Math.PI, new MathContext(6));
		output(Math.E, new MathContext(7));
		output(100000d + Math.PI, new MathContext(10));
		output(300d + Math.E, new MathContext(10));
	}
	
	@Test
	public void testNoContextConstructor(){
		message("No context, by default, all required decimals up to 15...");

		output("202.000");
		output("422.0000");
		output(Double.toString(Math.PI));
		output(Double.toString(Math.E));
		output(Double.toString(100000d + Math.PI));
		output(Double.toString(300d + Math.E));
	}
	
	@Test
	public void testWithContextConstructor(){
		message("With context (defines the total amount of digits to use)");
		
		output("202.000", new MathContext(3));
		output("422.0000", new MathContext(6));
		output(Double.toString(Math.PI), new MathContext(10));
		output(Double.toString(Math.E), new MathContext(10));
		output(Double.toString(100000d + Math.PI), new MathContext(10));
		output(Double.toString(300d + Math.E), new MathContext(10));
	}
	
	@Test
	public void testWithLossOfIntegerPrecision(){
		message("With loss of integer precision...");
		
		output("202.000", new MathContext(1));
		output("422.0000", new MathContext(1));
		output(Double.toString(Math.PI), new MathContext(1));
		output(Double.toString(Math.E), new MathContext(1));
		output(Double.toString(100000d + Math.PI), new MathContext(1));
		output(Double.toString(300d + Math.E), new MathContext(1));
	}
	
	@Test
	public void testNegativeNumbers(){
		message("Negative numbers...");

		output("-202.000", new MathContext(1));
		output("-422.0000", new MathContext(2));
		output(Double.toString(-Math.PI), new MathContext(3));
		output(Double.toString(-Math.E), new MathContext(4));
	}
	
	@Test
	public void testSmallDecimals(){
		message("Small decimals...");
		
		output("0.0202", new MathContext(1));
		output("0.00422", new MathContext(2));
		output("0.000202", new MathContext(3));
		output("0.000422", new MathContext(4));
	}
	
	@Test
	public void testPeriodicals(){
		message("Periodicals...");
		
		output("0.33333333333", new MathContext(4));
		output("2.33333333333", new MathContext(2));
		output("6.66666666667", new MathContext(9));
		output("9.99999999999", new MathContext(3));	
	}

	private void message(final String message) {
		System.out.format("%s\n", message);
	}

	private void separator() {
		System.out.format("------------------------------------------\n");
	}
	
	private void output(final int number){
		System.out.format("[%s]\n", number);
		assertAndOutputNumber(new Decimal(number), new BigDecimal(number));
	}

	private void output(final int number, final MathContext context){
		System.out.format("[%s]{%d}\n", number, context.getPrecision());
		assertAndOutputNumber(new Decimal(number, context), new BigDecimal(number, context));
	}
	
	private void output(final double number){
		System.out.format("[%s]\n", number);
		output(new Decimal(number));
	}
	
	private void output(final double number, final MathContext context){
		System.out.format("[%s]{%d}\n", number, context.getPrecision());
		assertAndOutputNumber(new Decimal(number, context), new BigDecimal(number, context));
	}
	
	private void output(final String number){
		System.out.format("[%s]\n", number);
		assertAndOutputNumber(new Decimal(number), new BigDecimal(number));
	}
	
	private void output(final String number, final MathContext context){
		System.out.format("[%s]{%d}\n", number, context.getPrecision());
		assertAndOutputNumber(new Decimal(number, context), new BigDecimal(number, context));
	}
	
	private void output(final Decimal number) {
		final String output = number.toString();

		final StringBuilder msg = new StringBuilder();
		msg.append("\tsciString\t:%s").append("\n\tplainString\t:%s").append("\n\tengString\t:%s")
			.append("\n\tprecision\t:%d").append("\n\tscale\t\t:%d\n");
		
		System.out.format(msg.toString(),number.toSciString(), output, number.toEngString(), 
				 number.getPrecision(), number.getScale());
	}

	private void assertNumber(final BigDecimal bigDecimal, final Decimal decimal) {
		assertEquals(bigDecimal.precision(), decimal.getPrecision());
		assertEquals(bigDecimal.scale(), decimal.getScale());
	}

	private void assertAndOutputNumber(final Decimal decimal, final BigDecimal bigDecimal) {
		output(decimal);
		assertNumber(bigDecimal, decimal);
	}
}