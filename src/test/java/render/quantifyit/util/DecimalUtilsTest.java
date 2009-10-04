package render.quantifyit.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Test;

import render.quantifyit.model.Decimal;

public class DecimalUtilsTest {

	@Test
	public void testPackLongs(){
		final Decimal[] longs = DecimalUtils.packLongs(23142341234123412L, 345235234523532453L, new Date().getTime());
		assertEquals(3, longs.length);
	}
	
	
	/**
	 * fails with a HashSet, since it relies on equals(), which is different for ints and doubles
	 */
	@Test
	public void testShouldAddUniqueElementsIntoASet(){ 
		Set<Decimal> decimals = new TreeSet<Decimal>(); 
		decimals.add(new Decimal(1));
		decimals.add(Decimal.ONE);
		decimals.add(new Decimal(1.0d));
		decimals.add(new Decimal(2));
		decimals.add(Decimal.TWO);
		decimals.add(new Decimal(2.0d));
		decimals.add(new Decimal(3));
		decimals.add(Decimal.THREE);
		decimals.add(new Decimal(3.0d));
		
		assertEquals(3, decimals.size());
		assertTrue(decimals.contains(Decimal.ONE));
		assertTrue(decimals.contains(Decimal.TWO));
		assertTrue(decimals.contains(Decimal.THREE));
	}
	
	@Test
	public void testShouldAddDecimalsAsKeysForAMap(){
		Map<Decimal, String> decimals = new TreeMap<Decimal, String>();
		decimals.put(new Decimal(1), "asdf");
		decimals.put(Decimal.ONE, "asdf");
		decimals.put(new Decimal(1d), "ASDF");
		decimals.put(new Decimal(2), "qwer");
		decimals.put(Decimal.TWO, "qwer");
		decimals.put(new Decimal(2d), "QWER");
		decimals.put(new Decimal(3), "zxcv");
		decimals.put(Decimal.THREE, "zxcv");
		decimals.put(new Decimal(3d), "ZXCV");
		
		assertEquals(3, decimals.size());
		assertTrue(decimals.containsKey(Decimal.ONE));
		assertTrue(decimals.containsKey(Decimal.TWO));
		assertTrue(decimals.containsKey(Decimal.THREE));
		assertTrue(decimals.containsValue("ASDF"));
		assertTrue(decimals.containsValue("QWER"));
		assertTrue(decimals.containsValue("ZXCV"));
		
//		assertTrue(DecimalUtils.containsAll(decimals, Arrays.asList(DecimalUtils.pack(1,2,3))));
	}
	
	// an array of ints, is not equal to the array of doubles, because 
	// new BigDecimal(1d).equals(new BigDecimal(1) is false  since the double value is 1.0d

	
	@Test
	public void testThatAListOfDecimalCanContainAllTheElementsOfAnotherListIfTheyAreTheSame(){
		final Decimal[] doubles = DecimalUtils.pack(1,2,1,2,3,4,3,4,5,6,5,6,7,8,7,8);
		final List<Decimal> doublesList = Arrays.asList(doubles);
		final List<Decimal> intsList = Arrays.asList(DecimalUtils.packInts(1,2,3,4,5,6,7,8));
		
		assertFalse(doublesList.containsAll(intsList));
		assertFalse(intsList.containsAll(doublesList));
		
		assertTrue(DecimalUtils.containsAll(doublesList, intsList));
	}
	
	@Test
	public void testASetOfDecimals(){
		final List<Decimal> doublesList = Arrays.asList(DecimalUtils.pack(1,2,1,2,3,4,3,4,5,6,5,6,7,8,7,8));
		final Set<Decimal> doublesSet = new TreeSet<Decimal>(doublesList);
		final Set<Decimal> intsSet = new TreeSet<Decimal>(Arrays.asList(DecimalUtils.packInts(1,2,3,4,5,6,7,8)));
		
		assertEquals(8, intsSet.size());
		assertEquals(8, doublesSet.size());
		
//		assertTrue(DecimalUtils.containsAll(doublesSet, intsSet));
//		assertTrue(DecimalUtils.containsAll(intsSet, doublesSet));
	}
	

	@Test
	public void testPackIntsAndDoubles(){
		Decimal[] longs = DecimalUtils.packLongs(23142341234123412L, 345235234523532453L, new Date().getTime());
		assertEquals(3, longs.length);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionIfModeHasEmptyElements(){
		DecimalUtils.notNullOrEmpty(new Decimal[]{});
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionIfModeHasNullElements(){
		final Decimal[] nullArray = null;
		DecimalUtils.notNullOrEmpty(nullArray);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionOnModeWithNullVararg(){
		final Decimal nullObject = null;
		DecimalUtils.notNullOrEmpty(nullObject);
	}

}
