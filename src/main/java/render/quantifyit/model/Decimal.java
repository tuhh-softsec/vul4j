package render.quantifyit.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Decimal implements Comparable<Decimal>, Serializable{

	private static final long serialVersionUID = 6840541842364016476L;

	public static final Decimal ZERO 	= new Decimal("0");
	public static final Decimal ONE 	= new Decimal("1");
	public static final Decimal TWO 	= new Decimal("2");
	public static final Decimal THREE 	= new Decimal("3");
	public static final Decimal FOUR 	= new Decimal("4");
	public static final Decimal FIVE 	= new Decimal("5");
	public static final Decimal SIX 	= new Decimal("6");
	public static final Decimal SEVEN 	= new Decimal("7");
	public static final Decimal EIGHT 	= new Decimal("8");
	public static final Decimal NINE 	= new Decimal("9");
	public static final Decimal TEN	 	= new Decimal("10");

	private final BigDecimal value;
	
	public Decimal(final int value){
		this(Integer.toString(value));
	}
	
	public Decimal(final double value){
		this(Double.toString(value));
	}
	
	public Decimal(final String value) {
		this.value = new BigDecimal(value);
	}

	public Decimal(final String value, final MathContext roundingCriteria) {
		this.value = new BigDecimal(value, roundingCriteria);
	}

	private Decimal(final BigDecimal value) {
		this.value = value;
	}
	
	public Decimal plus(int augend) {
		return new Decimal(value.add(new BigDecimal(Integer.toString(augend))));
	}
	
	public Decimal minus(int subtrahend) {
		return new Decimal(value.subtract(new BigDecimal(Integer.toString(subtrahend))));
	}
	
	public Decimal times(int multiplicand) {
		return new Decimal(value.multiply(new BigDecimal(Integer.toString(multiplicand))));
	}
	
	public Decimal by(int divisor) {
		return new Decimal(value.divide(new BigDecimal(Integer.toString(divisor))));
	}
	
	public Decimal power(int power){
		return new Decimal(value.pow(power));
	}

	public Decimal power(int power, MathContext roundingCriteria){
		return new Decimal(value.pow(power, roundingCriteria));
	}
	
	
	public Decimal plus(double augend) {
		return new Decimal(value.add(new BigDecimal(Double.toString(augend))));
	}
	
	public Decimal minus(double subtrahend) {
		return new Decimal(value.subtract(new BigDecimal(Double.toString(subtrahend))));
	}
	
	public Decimal times(double multiplicand) {
		return new Decimal(value.multiply(new BigDecimal(Double.toString(multiplicand))));
	}
	
	public Decimal by(double divisor) {
		return new Decimal(value.divide(new BigDecimal(Double.toString(divisor))));
	}
	
	
	public Decimal plus(Decimal augend) {
		return new Decimal(value.add(augend.getBigDecimal()));
	}
	
	public Decimal minus(Decimal subtrahend) {
		return new Decimal(value.subtract(subtrahend.getBigDecimal()));
	}
	
	public Decimal times(Decimal multiplicand) {
		return new Decimal(value.multiply(multiplicand.getBigDecimal()));
	}
	
	public Decimal by(Decimal divisor) {
		return new Decimal(value.divide(divisor.getBigDecimal()));
	}
	
	@Override
	public boolean equals(Object x) {
	    if (!(x instanceof Decimal))
            return false;
        Decimal other = (Decimal) x;
        if (x == this)
            return true;
		
		return this.value.equals(other.getBigDecimal());
	}

	@Override
	public int hashCode() {
		return this.value.hashCode();
	}
	
	@Override
	public String toString() {
		return this.value.toPlainString();
	}
	
	public String toSciString(){
		return value.toString();
	}
	
	public String toEngString(){
		return value.toEngineeringString();
	}	
	
	@Override
	public int compareTo(Decimal x) {
		return this.value.compareTo(x.getBigDecimal());
	}
	
	public BigDecimal getBigDecimal() {
		return value;
	}
	
	public int getScale(){
		return value.scale();
	}
	
	public int getPrecision(){
		return value.precision();
	}
	
	public Decimal scaleTo(int scale){
		return new Decimal(value.setScale(scale));
	}
	
	public Decimal scaleTo(int scale, RoundingMode roundingMode){
		return new Decimal(value.setScale(scale, roundingMode));
	}
	
	public Decimal roundTo(MathContext roundingCriteria){
		return new Decimal(value.round(roundingCriteria));
	}

	public double asDouble(){
		return value.doubleValue();
	}
	
	public int asInteger(){
		return value.intValue();
	}
}
