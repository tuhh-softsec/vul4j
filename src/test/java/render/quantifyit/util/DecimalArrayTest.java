package render.quantifyit.util;

import org.junit.Test;

import render.quantifyit.model.Decimal;

public class DecimalArrayTest {

	@Test(expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionIfModeHasEmptyElements(){
		DecimalArray.notNullOrEmpty(new Decimal[]{});
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionIfModeHasNullElements(){
		final Decimal[] nullArray = null;
		DecimalArray.notNullOrEmpty(nullArray);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testShouldThrowExceptionOnModeWithNullVararg(){
		final Decimal nullObject = null;
		DecimalArray.notNullOrEmpty(nullObject);
	}

}
