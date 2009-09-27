package render.quantifyit.util;

import render.quantifyit.model.Decimal;

public class DecimalArray {

	public static Decimal[] pack(final int... elements){
		final Decimal[] array = new Decimal[elements.length];
		for (int i =0; i < elements.length; i++) {
			array[i] = new Decimal(elements[i]);
		}
		return array;
	}
	
	public static Decimal[] pack(final double... elements){
		final Decimal[] array = new Decimal[elements.length];
		for (int i =0; i < elements.length; i++) {
			array[i] = new Decimal(elements[i]);
		}
		return array;
	}
	
	public static void notNullOrEmpty(final Decimal... elements) {
		if (elements == null || elements.length == 0 || elements[0] == null){
			throw new IllegalArgumentException("Please provide at least one value.");
		}
	}
}
