package render.quantifyit.decimal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import render.quantifyit.model.Decimal;

public class PerformanceUtilsTest {

	@Test
	public void testFormatDuration() {
		String duration = PerformanceUtils.formatDuration(new Decimal(16000));
		assertEquals("16.000 ns", duration);
		duration = PerformanceUtils.formatDuration(new Decimal(28435000));
		assertEquals("28.435 μs", duration);
	}

}
