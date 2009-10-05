package render.quantifyit.util;

import java.util.Iterator;
import java.util.List;

import render.quantifyit.model.Decimal;

public class DecimalUtils {

	public static Decimal[] packInts(final int... elements){
		final Decimal[] array = new Decimal[elements.length];
		for (int i =0; i < elements.length; i++) {
			array[i] = new Decimal(elements[i]);
		}
		return array;
	}
	
	public static Decimal[] packLongs(final long... elements){
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

	public static boolean contains(final List<Decimal> source, final Decimal target){
		return contains(source.iterator(), target);
	}

	
	public static boolean containsAll(final List<Decimal> source, final List<Decimal> target) {
		final Iterator<Decimal> sourceIterator = source.iterator();
		final Iterator<Decimal> targetIterator = target.iterator();
		while (targetIterator.hasNext()) {
			if (!contains(sourceIterator, targetIterator.next())) {
				return false;
			}
		}
		return true;
	}

	private static boolean contains(final Iterator<Decimal> sourceIterator, final Decimal target){
		if (target == null) {
		    while (sourceIterator.hasNext()){
				if (sourceIterator.next()==null) {
				    return true;
				}
		    }
		} else {
		    while (sourceIterator.hasNext()) {
				if (target.same(sourceIterator.next())){
				    return true;
				}
		    }
		}
		return false;
	}
	
}
