package render.quantifyit.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import render.quantifyit.model.operations.Addition;
import render.quantifyit.model.operations.Division;
import render.quantifyit.model.operations.Multiplication;
import render.quantifyit.model.operations.Power;
import render.quantifyit.model.operations.SquareRoot;
import render.quantifyit.model.operations.Subtraction;

/**
 * Immutable, arbitrary-precision signed decimal numbers.
 * Delegates the arithmetic to {@link java.math.BigDecimal}, through a consistent API
 * As a design choice, Decimal trades-off performance for 
 * precision. 
 * 
 * BigDecimal is a complicated class, with many pitfalls. For example,
 * which one of the 15 constructors and 3 static factories should i call? 
 * While Decimal still offers many constructors, they are there for consistency and simplification,
 * not due to support of legacy java releases.
 * 
 * In particular, the ones accepting double suffer the many problems associated with binary 
 * representations of floating points.
 * 
 * Decimal provides simple methods, with short consistent names, favouring reuse. 
 * 
 * Decimal chooses not to extend Number, to avoid having to implement 
 * certain abstract methods. 
 * 
 * @author Fernando Racca
 * @see java.math.BigDecimal
 */
public final class Decimal implements Comparable<Decimal>, Serializable {

	private static final long serialVersionUID = 6840541842364016476L;

	public static final Decimal ZERO 	= new Decimal(BigDecimal.ZERO);
	public static final Decimal ONE 	= new Decimal(BigDecimal.ONE);
	public static final Decimal TWO 	= new Decimal(BigDecimal.valueOf(2));
	public static final Decimal THREE 	= new Decimal(BigDecimal.valueOf(3));
	public static final Decimal TEN	 	= new Decimal(BigDecimal.TEN);

	/**
	 * Defaults to {@link RoundingMode#HALF_EVEN HALF_EVEN}, the IEEE 754R default.
	 * "Note that this is the rounding mode that statistically minimizes cumulative
	 * error when applied repeatedly over a sequence of calculations."
	 * 
	 * This is not the same as MathContext, which defaults to 
	 * {@link RoundingMode#HALF_UP HALF_UP}
	 */
	private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;
	
	/**
	 * Defaults to 10 digits
	 */
	private static final int DEFAULT_SCALE = 10;
	
	private final transient BigDecimal significand;
	
	/*
	 * Constructors. all default to BigDecimal
	 */
	
	private Decimal(final BigDecimal value) {
		this.significand = value;
	}

	public static Decimal $(final int value) {
		return $((long)value);
	}
	
	public static Decimal $(final int value, final MathContext roundingCriteria) {
		return $((long)value, roundingCriteria);
	}
	
	public static Decimal $(final long value) {
		return $(BigDecimal.valueOf(value));
	}
	
	public static Decimal $(final long value, final MathContext roundingCriteria) {
		return $(BigDecimal.valueOf(value).round(roundingCriteria));
	}
	
	public static Decimal $(final double value) {
		return $(Double.toString(value));
	}
	
	public static Decimal $(final double value, final MathContext roundingCriteria) {
		return $(Double.toString(value), roundingCriteria);
	}
	
	public static Decimal $(final String value) {
		if(value == null) {
			throw new IllegalArgumentException("Decimal(String) failed construction due to a null argument.");
		}
		return $(new BigDecimal(value));
	}

	public static Decimal $(final String value, final MathContext roundingCriteria) {
		if(value == null ) {
			throw new IllegalArgumentException("Value can't be null");
		}
		return $(new BigDecimal(value).round(roundingCriteria));
	}

	public static Decimal $(final BigDecimal value) {
		if(value == null) {
			throw new IllegalArgumentException("Value can't be null.");
		}
		return new Decimal(value);
	}
	
	public static Decimal $(final BigDecimal value, final int scale) {
		return $(value, scale, DEFAULT_ROUNDING);
	}
	
	public static Decimal $(final BigDecimal value, final int scale, final RoundingMode roundingMode) {
		if(value == null) {
			throw new IllegalArgumentException("Value can't be null.");
		}
		return $(value.setScale(scale, roundingMode));
	}
	
	public static Decimal $(final BigDecimal value, final MathContext roundingCriteria) {
		if(value == null ) {
			throw new IllegalArgumentException("Value can't be null");
		}
		return $(value.round(roundingCriteria));
	}

	/*
	 * PLUS = Addition = BigDecimal.add(...)
	 */
	
	public Decimal plus(final int augend) {
		return Addition.add(this, $(augend));
	}
	
	public Decimal plus(final long augend) {
		return Addition.add(this, $(augend));
	}
	
	public Decimal plus(final double augend) {
		return Addition.add(this, $(augend));		
	}
	
	public Decimal plus(final Decimal augend) {
		return Addition.add(this, augend);
	}
	
	public Decimal plus(final Decimal augend, final MathContext roundingCriteria) {
		return Addition.add(this, augend, roundingCriteria);
	}
	
	/*
	 * MINUS = Subtraction = BigDecimal.subtract(...)
	 */
	
	public Decimal minus(final int subtrahend) {
		return Subtraction.subtraction(this, $(subtrahend));
	}

	public Decimal minus(final long subtrahend) {
		return Subtraction.subtraction(this, $(subtrahend));
	}
	
	public Decimal minus(final double subtrahend) {
		return Subtraction.subtraction(this, $(subtrahend));
	}

	public Decimal minus(final Decimal subtrahend) {
		return Subtraction.subtraction(this, subtrahend);
	}
	
	public Decimal minus(final Decimal subtrahend, final MathContext roundingCriteria) {
		return Subtraction.subtraction(this, subtrahend, roundingCriteria);
	}
	
	/*
	 * TIMES = Multiplication = BigDecimal.multiply(...)
	 */	
	
	public Decimal times(final int multiplicand) {
		return Multiplication.multiply(this, $(multiplicand));
	}
	
	public Decimal times(final long multiplicand) {
		return Multiplication.multiply(this, $(multiplicand));
	}
	
	public Decimal times(final double multiplicand) {
		return Multiplication.multiply(this, $(multiplicand));
	}

	public Decimal times(final Decimal multiplicand) {
		return Multiplication.multiply(this, multiplicand);
	}
	
	public Decimal times(final Decimal multiplicand, final MathContext roundingCriteria) {
		return Multiplication.multiply(this, multiplicand, roundingCriteria);
	}
	
	/*
	 * divideBy = Division = BigDecimal.divide(...)
	 * For maximum control use Divide or subclasses.
	 */
	
	public Decimal divideBy(final int divisor) {
		return divideBy($(divisor));
	}
	
	public Decimal divideBy(final long divisor) {
		return divideBy($(divisor));
	}
	
	public Decimal divideBy(final double divisor) {
		return divideBy($(divisor));
	}
	
	public Decimal divideBy(final Decimal divisor) {
		return Division.divide(this, divisor, DEFAULT_SCALE, DEFAULT_ROUNDING);
	}
	
	public Decimal halve() {
		return this.divideBy(2);
	}
	
	/*
	 * POWER = Power = BigDecimal.pow(...)
	 */
	
	public Decimal power(final int power) {
		return Power.power(this, power);
	}

	public Decimal power(final int power, final MathContext roundingCriteria) {
		return Power.power(this, power, roundingCriteria);
	}
	
	public Decimal square() {
		return Power.power(this, 2);
	}
	
	public Decimal cube() {
		return Power.power(this, 3);
	}
	
	/*
	 * Square root
	 */
	
	public Decimal squareRoot() {
		return SquareRoot.squareRoot(this);
	}
	
	public Decimal squareRoot(final MathContext roundingCriteria) {
		return SquareRoot.squareRoot(this, roundingCriteria);
	}

	//TODO: implement modulo
	public Decimal modulo(final Decimal other) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	/*
	 * Comparison
	 */
	
	
	/**
	 * Honours the equals(Object) implementation of BigDecimal, which is prone to errors, since
	 * Decimal.$(1).equals(Decimal.$(1d)) evaluates to false, because 1d = 1.0 (10 with a scale of 1)
	 * Use {@link #compareTo(Decimal) instead. 
	 * 
	 * Warning, collections that rely on equals ( and its evil twin hashCode), such as HashSet and HashMap
	 * will fail the equality comparison.
	 * Suggestions:
	 * <ul>
	 * 		<li>Do not use the double constructor/methods to compare against ints, 
	 * 		use more strict versions such as Decimal(String), although this still won't guarantee equality 
	 * 		if you do operations such as division</li>
	 * 		<li>Avoid hash based collections, prefer instead those that use Comparable/Comparator</li>
	 * 		<li>To compare lists, use 
	 * 			 {@link DecimalUtils#containsAll(Collection<Decimal> source, Collection<Decimal> target)}</li>
	 * </ul>
	 * {@link BigDecimal#equals(Object) equals(Object)} for 
	 * @see 
	 * @see #compareTo(java.math.BigDecimal)
	 */
	@Override
	public boolean equals(final Object otherObject) {
	    if (!(otherObject instanceof Decimal)) {
            return false;
	    }
        final Decimal other = (Decimal) otherObject;
        if (other == this) {
            return true;
        }
		return this.significand.equals(other.asBigDecimal());
	}

	@Override
	public int hashCode() {
		return this.significand.hashCode();
	}
	
	public int compareTo(final Decimal other) {
		return this.significand.compareTo(other.asBigDecimal());
	}
	
	/**
	 * Compares values for identical representation. Delegates to compareTo,
	 * but is simpler to use, since it returns a boolean
	 * @param other the value to compare
	 * @return true if both values have exact same representation
	 */
	public boolean same(final Decimal other) {
		return this.compareTo(other) == 0;
	}
	
	public Decimal min(final Decimal other) {
		return (compareTo(other) <= 0 ? this : other);
	}
	
	public Decimal max(final Decimal other) {
		return (compareTo(other) >= 0 ? this : other);
	}
	
	public boolean gt(final Decimal other) {
		return compareTo(other) > 0;
	}
	
	public boolean lt(final Decimal other) {
		return compareTo(other) < 0;
	}
	
	public boolean gte(final Decimal other) {
		return compareTo(other) >= 0;
	}
	
	public boolean lte(final Decimal other) {
		return compareTo(other) <= 0;
	}
	
	public boolean isInfinite() {
		return Double.isInfinite(significand.doubleValue());
	}
	
	/**
	 * If the number is GREATER THAN Decimal.ZERO
	 * Doesn't account for positive or negative zero, infinity or NaN
	 * @return true if code>x > 0</code> 
	 */
	public boolean isPositive() {
		return gt(Decimal.ZERO);
	}
	
	/**
	 * If the number is LOWER THAN Decimal.ZERO
	 * Doesn't account for positive or negative zero, infinity or NaN
	 * @return true if code>x < 0</code> 
	 */
	public boolean isNegative() {
		return lt(Decimal.ZERO);
	}
	
	public boolean isZero() {
		return this.same(ZERO);
	}
	
	/*
	 * Conversion
	 */
	
	@Override
	public String toString() {
		return this.significand.toPlainString();
	}
	
	public String toSciString() {
		return significand.toString();
	}
	
	public String toEngString() {
		return significand.toEngineeringString();
	}
	
	public String format(final String pattern) {
		return String.format(pattern, this.significand);
	}
	
	public Decimal abs() {
		return $(significand.abs());
	}
	
	public Decimal abs(final MathContext roundingCriteria) {
		return $(significand.abs(roundingCriteria));
	}

	public double asDouble() {
		return significand.doubleValue();
	}
	
	public int asInteger() {
		return significand.intValue();
	}
	
	public long asLong() {
		return significand.longValue();
	}

	public BigDecimal asBigDecimal() {
		return significand;
	}
	
	/*
	 * Precision and scale
	 */
		
	public Decimal scaleTo(final int scale) {
		return scaleTo(scale, DEFAULT_ROUNDING);
	}
	
	public Decimal scaleTo(final int scale, final RoundingMode roundingMode) {
		return $(significand.setScale(scale, roundingMode));
	}
	
	public Decimal roundTo(final MathContext roundingCriteria) {
		return $(significand.round(roundingCriteria));
	}
	
	public Decimal movePointToLeft(final int n) {
		return $(significand.movePointLeft(n));
	}
	
	public int getScale() {
		return significand.scale();
	}
	
	public int getPrecision() {
		return significand.precision();
	}
}
