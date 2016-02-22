package simple2.rutadedatos;


import org.junit.Assert;
import org.junit.Test;


public class MicroInstruccionTest {
	public MicroInstruccionTest(){
		super();
	}
	@Test
	public void testAlu ()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorALU0", 0, mic.getALU());
		MicroInstruccion mic1 = new MicroInstruccion(10);
		Assert.assertEquals ("ErrorALU1", 0, mic1.getALU());
		MicroInstruccion mic2 = new MicroInstruccion(7680);
		Assert.assertEquals ("ErrorALU2", 15, mic2.getALU());
		MicroInstruccion mic3 = new MicroInstruccion(512);
		Assert.assertEquals ("ErrorALU3", 1, mic3.getALU());
		MicroInstruccion mic4 = new MicroInstruccion(1024);
		Assert.assertEquals ("ErrorALU4", 2, mic4.getALU());
	}
	@Test
	public void testFIR()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorFIR0", 0, mic.getFIR());
		MicroInstruccion mic1 = new MicroInstruccion(8192);
		Assert.assertEquals ("ErrorFIR1", 1, mic1.getFIR());
	}
	@Test
	public void testADDR()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorADDR0", 0, mic.getALU());
		MicroInstruccion mic1 = new MicroInstruccion(1450063481);
		Assert.assertEquals ("ErrorADDR1", 11, mic1.getALU());
		MicroInstruccion mic2 = new MicroInstruccion(1256548);
		Assert.assertEquals ("ErrorADDR2", 6, mic2.getALU());
		MicroInstruccion mic3 = new MicroInstruccion(150364125312L);
		Assert.assertEquals ("ErrorADDR3", 12, mic3.getALU());

	}
	@Test
	public void testA()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorA0", 0, mic.getA());
		MicroInstruccion mic1 = new MicroInstruccion(134217728);
		Assert.assertEquals ("ErrorA1", 8, mic1.getA());
	}
	@Test
	public void testB()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorB0", 0, mic.getB());
		MicroInstruccion mic1 = new MicroInstruccion(2147483648L);
		Assert.assertEquals ("ErrorB1", 8, mic1.getB());
		MicroInstruccion mic2 = new MicroInstruccion(1073741824);
		Assert.assertEquals ("ErrorB2", 4, mic2.getB());
		MicroInstruccion mic3 = new MicroInstruccion(1073749856);
		Assert.assertEquals ("ErrorB3", 4, mic3.getB());
		MicroInstruccion mic4 = new MicroInstruccion(536870912);
		Assert.assertEquals ("ErrorB4", 2, mic4.getB());
		MicroInstruccion mic5 = new MicroInstruccion(536872592);
		Assert.assertEquals ("ErrorB5", 2, mic5.getB());
		MicroInstruccion mic6 = new MicroInstruccion(268435456);
		Assert.assertEquals ("ErrorB6", 1, mic6.getB());
		MicroInstruccion mic7 = new MicroInstruccion(268435455);
		Assert.assertEquals ("ErrorB7", 0, mic7.getB());
		MicroInstruccion mic8 = new MicroInstruccion(1610612736);
		Assert.assertEquals ("ErrorB8", 6, mic8.getB());
	}
	@Test
	public void testC()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorC0", 0, mic.getC());
		MicroInstruccion mic1 = new MicroInstruccion(34359738368L);
		Assert.assertEquals ("ErrorC1", 8, mic1.getC());
		MicroInstruccion mic2 = new MicroInstruccion(17179869184L);
		Assert.assertEquals ("ErrorC2", 4, mic2.getC());
		MicroInstruccion mic3 = new MicroInstruccion(8589934592L);
		Assert.assertEquals ("ErrorC3", 2, mic3.getC());
		MicroInstruccion mic4 = new MicroInstruccion(34294967296L);
		Assert.assertEquals ("ErrorC4", 7, mic4.getC());
	}
	@Test
	public void testENC()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorENC0", 0, mic.getENC());
		MicroInstruccion mic1 = new MicroInstruccion(68719476736L);
		Assert.assertEquals ("ErrorENC1", 1, mic1.getENC());
	}
	@Test
	public void testWR()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorWR0", 0, mic.getWR());
		MicroInstruccion mic1 = new MicroInstruccion(137438953472L);
		Assert.assertEquals ("ErrorWR1", 1, mic1.getWR());
	}
	@Test
	public void testRD()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorRD0", 0, mic.getRD());
		MicroInstruccion mic1 = new MicroInstruccion(274877906944L);
		Assert.assertEquals ("ErrorRD1", 1, mic1.getRD());
	}
	@Test
	public void testMAR()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorMAR0", 0, mic.getMAR());
		MicroInstruccion mic1 = new MicroInstruccion(549755813888L);
		Assert.assertEquals ("ErrorMAR1", 1, mic1.getMAR());
	}
	@Test
	public void testMBR()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorMBR0", 0, mic.getMBR());
		MicroInstruccion mic1 = new MicroInstruccion(1099511627776L);
		Assert.assertEquals ("ErrorMBR1", 1, mic1.getMBR());
	}
	@Test
	public void testSH()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorSH0", 0, mic.getSH());
		MicroInstruccion mic1 = new MicroInstruccion(8796093022208L);
		Assert.assertEquals ("ErrorSH1", 4, mic1.getSH());
		MicroInstruccion mic2 = new MicroInstruccion(4398046511104L);
		Assert.assertEquals ("ErrorSH2", 2, mic2.getSH());
		MicroInstruccion mic3 = new MicroInstruccion(2199023255552L);
		Assert.assertEquals ("ErrorSH3", 1, mic3.getSH());
	}
	@Test
	public void testCOND()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorCOND0", 0, mic.getCOND());
		MicroInstruccion mic1 = new MicroInstruccion(70368744177664L);
		Assert.assertEquals ("ErrorCOND1", 4, mic1.getCOND());
		MicroInstruccion mic2 = new MicroInstruccion(35184372088832L);
		Assert.assertEquals ("ErrorCOND2", 2, mic2.getCOND());
		MicroInstruccion mic3 = new MicroInstruccion(17592186044416L);
		Assert.assertEquals ("ErrorCOND3", 1, mic3.getCOND());
	}
	@Test
	public void testAMUX()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorAMUX0", 0, mic.getAMUX());
		MicroInstruccion mic1 = new MicroInstruccion(140737488355328L);
		Assert.assertEquals ("ErrorAMUX1", 1, mic1.getAMUX());
		MicroInstruccion mic2 = new MicroInstruccion(144858585821452L);
		Assert.assertEquals ("ErrorAMUX2", 1, mic2.getAMUX());
	}

}
