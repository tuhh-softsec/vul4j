package render.quantifyit.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Immutable, arbitrary-precision signed decimal numbers.
 * Delegates the arithmetic to {@link java.math.BigDecimal}, through a consistent API
 * As a design choice, Decimal trades-off performance for 
 * precision. 
 * 
 * BigDecimal is a complicated class, with many pitfalls. For example,
 * which one of the 15 constructors and 3 static factories should i call? 
 * In particular, the ones accepting double are well-known for being broken.
 * 
 * Decimal provides simple methods, with short consistent names, favouring reuse. 
 * It only supports int, double and String, although much prefers int and String. 
 * 
 * Decimal also chooses not to extend Number, to avoid having to implement 
 * certain abstract methods. 
 * 
 * @author Fernando Racca
 * @see java.math.BigDecimal
 */
public class Decimal implements Comparable<Decimal>, Serializable{

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

	private static final long serialVersionUID = 6840541842364016476L;

	public static final Decimal ZERO 	= new Decimal("0");
	public static final Decimal ONE 	= new Decimal("1");
	public static final Decimal TWO 	= new Decimal("2");
	public static final Decimal THREE 	= new Decimal("3");
	public static final Decimal TEN	 	= new Decimal("10");

	private final BigDecimal significand;
	
	/*
	 * Constructors. all default to BigDecimal(String value)
	 */
	
	
	public Decimal(final int value){
		this(BigDecimal.valueOf((long)value));
	}
	
	public Decimal(final int value, final MathContext roundingCriteria){
		this(new BigDecimal(value, roundingCriteria));
	}
	
	public Decimal(final long value) {
		this(BigDecimal.valueOf(value));
	}
	
	public Decimal(final long value, final MathContext roundingCriteria) {
		this(new BigDecimal(value, roundingCriteria));
	}
	
	public Decimal(final double value){
		this(Double.toString(value));
	}
	
	public Decimal(final double value, final MathContext roundingCriteria){
		this(Double.toString(value), roundingCriteria);
	}
	
	public Decimal(final String value) {
		if(value == null) {
			throw new IllegalArgumentException("Decimal(String) failed construction due to a null argument.");
		}
		this.significand = new BigDecimal(value);
	}

	public Decimal(final String value, final MathContext roundingCriteria) {
		if(value == null ) {
			throw new IllegalArgumentException(
					"Decimal(String, MathContext) failed construction due to a null argument.");
		}
		this.significand = new BigDecimal(value, roundingCriteria);
	}

	private Decimal(final BigDecimal value){
		if(value == null) {
			throw new IllegalArgumentException("Decimal(BigDecimal) failed construction due to a null argument.");
		}
		this.significand = value;
	}
	
	public Decimal(final BigDecimal value, final int scale) {
		this(value, scale, DEFAULT_ROUNDING);
	}
	
	public Decimal(final BigDecimal value, final int scale, final RoundingMode roundingMode) {
		if(value == null) {
			throw new IllegalArgumentException(
				"Decimal(BigDecimal, int scale, RoundingMode) failed construction due to a null argument.");
		}
		this.significand = value.setScale(scale, roundingMode);
	}
	
	public static Decimal valueOf(final BigDecimal value, final MathContext roundingCriteria) {
		if(value == null) {
			throw new IllegalArgumentException(
				"Decimal(BigDecimal, MathContext) failed construction due to a null argument.");
		}
		return new Decimal(value.round(roundingCriteria));
	}

	/*
	 * PLUS = Addition = BigDecimal.add(...)
	 */
	
	public Decimal plus(final int augend) {
		return new Decimal(significand.add(new BigDecimal(Integer.toString(augend))));
	}
	
	public Decimal plus(final double augend) {
		return new Decimal(significand.add(new BigDecimal(Double.toString(augend))));
	}
	
	public Decimal plus(final Decimal augend) {
		return new Decimal(significand.add(augend.asBigDecimal()));
	}
	public Decimal plus(final Decimal augend, final MathContext roundingCriteria) {
		return new Decimal(significand.add(augend.asBigDecimal(), roundingCriteria));
	}
	
	/*
	 * MINUS = Subtraction = BigDecimal.subtract(...)
	 */
	
	public Decimal minus(final int subtrahend) {
		return new Decimal(significand.subtract(new BigDecimal(Integer.toString(subtrahend))));
	}

	public Decimal minus(final double subtrahend) {
		return new Decimal(significand.subtract(new BigDecimal(Double.toString(subtrahend))));
	}

	public Decimal minus(final Decimal subtrahend) {
		return new Decimal(significand.subtract(subtrahend.asBigDecimal()));
	}
	
	public Decimal minus(final Decimal subtrahend, final MathContext roundingCriteria) {
		return new Decimal(significand.subtract(subtrahend.asBigDecimal(), roundingCriteria));
	}
	
	/*
	 * TIMES = Multiplication = BigDecimal.multiply(...)
	 */
	
	
	public Decimal times(final int multiplicand) {
		return new Decimal(significand.multiply(new BigDecimal(Integer.toString(multiplicand))));
	}
	
	public Decimal times(final double multiplicand) {
		return new Decimal(significand.multiply(new BigDecimal(Double.toString(multiplicand))));
	}

	public Decimal times(final Decimal multiplicand) {
		return new Decimal(significand.multiply(multiplicand.asBigDecimal()));
	}
	
	public Decimal times(final Decimal multiplicand, final MathContext roundingCriteria) {
		return new Decimal(significand.multiply(multiplicand.asBigDecimal(), roundingCriteria));
	}
	
	/*
	 * BY = Division = BigDecimal.divide(...)
	 */
	
	public Decimal by(final int divisor) {
		return by(divisor, DEFAULT_SCALE, DEFAULT_ROUNDING);
	}
	
	public Decimal by(final int divisor, final int scale) {
		return by(divisor, scale, DEFAULT_ROUNDING);
	}
	
	public Decimal by(final int divisor, final int scale, final RoundingMode roundingMode) {
		return by(new Decimal(Integer.toString(divisor)), scale, roundingMode);
	}
	
	public Decimal by(final double divisor) {
		return by(divisor, DEFAULT_SCALE, DEFAULT_ROUNDING);
	}
	
	public Decimal by(final double divisor, final int scale) {
		return by(divisor, scale, DEFAULT_ROUNDING);
	}
	
	public Decimal by(final double divisor, final int scale, final RoundingMode roundingMode) {
		return by(new Decimal(Double.toString(divisor)), scale, roundingMode);
	}
		
	public Decimal by(final Decimal divisor) {
		return by(divisor, DEFAULT_SCALE, DEFAULT_ROUNDING);
	}
	
	public Decimal by(final Decimal divisor, final int scale) {
		return by(divisor, scale, DEFAULT_ROUNDING);
	}
	
	public Decimal by(final Decimal divisor, final int scale, final RoundingMode roundingMode) {
		return new Decimal(significand.divide(divisor.asBigDecimal(), scale, roundingMode));
	}
	
	public Decimal by(final Decimal divisor, final MathContext roundingCriteria) {
		return new Decimal(significand.divide(divisor.asBigDecimal(), roundingCriteria));
	}
	
	public Decimal halve() {
		return this.by(Decimal.TWO);
	}
	
	/*
	 * POWER = Power = BigDecimal.pow(...)
	 */
	
	public Decimal power(final int power){
		return new Decimal(significand.pow(power));
	}

	public Decimal power(final int power, final MathContext roundingCriteria){
		return new Decimal(significand.pow(power, roundingCriteria));
	}
	
	public Decimal square() {
		return this.power(2);
	}
	
	/*
	 * Square root
	 */
	
	public static Decimal squareRoot(final int squared){
		return new Decimal(Math.sqrt(squared));
	}
	
	public static Decimal squareRoot(final double squared){
		return new Decimal(Math.sqrt(squared));
	}
	
	public static Decimal squareRoot(final Decimal squared){
		return new Decimal(Math.sqrt(squared.asDouble()));
	}
	
	public static Decimal squareRoot(final Decimal squared, final MathContext roundingCriteria){
		return new Decimal(Math.sqrt(squared.asDouble()), roundingCriteria);
	}
	
	public Decimal squareRoot(){
		return new Decimal(Math.sqrt(significand.doubleValue()));
	}
	
	public Decimal squareRoot(final MathContext roundingCriteria){
		return new Decimal(Math.sqrt(significand.doubleValue()), roundingCriteria);
	}

	//TODO: implement modulo
	public Decimal modulo(final Decimal other){
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	/*
	 * Comparison
	 */
	
	
	/**
	 * Honours the equals(Object) implementation of BigDecimal, which is prone to errors, since
	 * new Decimal(1).equals(new Decimal(1d)) evaluates to false, because 1d = 1.0 (10 with a scale of 1)
	 * Use {@link #compareTo(Decimal) instead. 
	 * 
	 * Warning, some collections based on equals ( and its evil twin hashCode), such as HashSet and HashMap
	 * will fail the equality comparison.
	 * Suggestions:
	 * <ul>
	 * 		<li>Do not use the double constructor/methods to compare against ints, 
	 * 		use more strict versions such as Decimal(String), altough this still won't guarantee equality 
	 * 		if you do operations such as division</li>
	 * 		<li>Avoid hash based collections, prefer instead those that use Comparable/Comparator</li>
	 * 		<li>To compare lists, use 
	 * 			{@link DecimalUtils#containsAll(Collection<Decimal> source, Collection<Decimal> target)}</li>
	 * 
	 * {@link BigDecimal#equals(Object) equals(Object)} for 
	 * @see 
	 * @see #compareTo(java.math.BigDecimal)
	 */
	@Override
	public boolean equals(final Object otherObject) {
	    if (!(otherObject instanceof Decimal)){
            return false;
	    }
        final Decimal other = (Decimal) otherObject;
        if (other == this){
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
	public boolean same(final Decimal other){
		return this.compareTo(other) == 0;
	}
	
	public Decimal min(final Decimal other) {
		return (compareTo(other) <= 0 ? this : other);
	}
	
	public Decimal max(final Decimal other) {
		return (compareTo(other) >= 0 ? this : other);
	}
	
	public boolean gt(final Decimal other){
		return compareTo(other) > 0;
	}
	
	public boolean lt(final Decimal other){
		return compareTo(other) < 0;
	}
	
	public boolean gte(final Decimal other){
		return compareTo(other) >= 0;
	}
	
	public boolean lte(final Decimal other){
		return compareTo(other) <= 0;
	}
	
	public boolean isInfinite(){
		return Double.isInfinite(significand.doubleValue());
	}
	
	/**
	 * If the number is GREATER THAN Decimal.ZERO
	 * Doesn't account for positive or negative zero, infinity or NaN
	 * @return true if code>x > 0</code> 
	 */
	public boolean isPositive(){
		return gt(Decimal.ZERO);
	}
	
	/**
	 * If the number is LOWER THAN Decimal.ZERO
	 * Doesn't account for positive or negative zero, infinity or NaN
	 * @return true if code>x < 0</code> 
	 */
	public boolean isNegative(){
		return lt(Decimal.ZERO);
	}
	
	/*
	 * Conversion
	 */
	
	@Override
	public String toString() {
		return this.significand.toPlainString();
	}
	
	public String toSciString(){
		return significand.toString();
	}
	
	public String toEngString(){
		return significand.toEngineeringString();
	}
	
	public String format(String pattern){
		return String.format(pattern, this.significand);
	}
	
	public Decimal abs(){
		return new Decimal(significand.abs());
	}
	
	public Decimal abs(final MathContext roundingCriteria){
		return new Decimal(significand.abs(roundingCriteria));
	}

	public double asDouble(){
		return significand.doubleValue();
	}
	
	public int asInteger(){
		return significand.intValue();
	}

	public BigDecimal asBigDecimal() {
		return significand;
	}
	
	/*
	 * Precision and scale
	 */
		
	public Decimal scaleTo(final int scale){
		return scaleTo(scale, DEFAULT_ROUNDING);
	}
	
	public Decimal scaleTo(final int scale, final RoundingMode roundingMode){
		return new Decimal(significand.setScale(scale, roundingMode));
	}
	
	public Decimal roundTo(final MathContext roundingCriteria){
		return new Decimal(significand.round(roundingCriteria));
	}
	
	public Decimal movePointToLeft(int n){
		return new Decimal(significand.movePointLeft(n));
	}
	
	public int getScale(){
		return significand.scale();
	}
	
	public int getPrecision(){
		return significand.precision();
	}
}
