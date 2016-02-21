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
		Assert.assertEquals ("ErrorALU0", (int)0, mic.GetALU());
		MicroInstruccion mic1 = new MicroInstruccion(10);
		Assert.assertEquals ("ErrorALU1", (int)0, mic1.GetALU());
		MicroInstruccion mic2 = new MicroInstruccion(7680);
		Assert.assertEquals ("ErrorALU2", (int)15, mic2.GetALU());
		MicroInstruccion mic3 = new MicroInstruccion(512);
		Assert.assertEquals ("ErrorALU3", (int)1, mic3.GetALU());
		MicroInstruccion mic4 = new MicroInstruccion(1024);
		Assert.assertEquals ("ErrorALU4", (int)2, mic4.GetALU());
	}
	@Test
	public void testFIR()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorFIR0", (int)0, mic.GetFIR());
		MicroInstruccion mic1 = new MicroInstruccion(8192);
		Assert.assertEquals ("ErrorFIR1", (int)1, mic1.GetFIR());
	}
	@Test
	public void testADDR()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorADDR0", (int)0, mic.GetALU());
		MicroInstruccion mic1 = new MicroInstruccion(1450063481);
		Assert.assertEquals ("ErrorADDR1", (int)11, mic1.GetALU());
		MicroInstruccion mic2 = new MicroInstruccion(1256548);
		Assert.assertEquals ("ErrorADDR2", (int)6, mic2.GetALU());
		MicroInstruccion mic3 = new MicroInstruccion(150364125312L);
		Assert.assertEquals ("ErrorADDR3", (int)12, mic3.GetALU());

	}
	@Test
	public void testA()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorA0", (int)0, mic.GetA());
		MicroInstruccion mic1 = new MicroInstruccion(134217728);
		Assert.assertEquals ("ErrorA1", (int)8, mic1.GetA());
	}
	@Test
	public void testB()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorB0", (int)0, mic.GetB());
		MicroInstruccion mic1 = new MicroInstruccion(2147483648L);
		Assert.assertEquals ("ErrorB1", (int)8, mic1.GetB());
		MicroInstruccion mic2 = new MicroInstruccion(1073741824);
		Assert.assertEquals ("ErrorB2", (int)4, mic2.GetB());
		MicroInstruccion mic3 = new MicroInstruccion(1073749856);
		Assert.assertEquals ("ErrorB3", (int)4, mic3.GetB());
		MicroInstruccion mic4 = new MicroInstruccion(536870912);
		Assert.assertEquals ("ErrorB4", (int)2, mic4.GetB());
		MicroInstruccion mic5 = new MicroInstruccion(536872592);
		Assert.assertEquals ("ErrorB5", (int)2, mic5.GetB());
		MicroInstruccion mic6 = new MicroInstruccion(268435456);
		Assert.assertEquals ("ErrorB6", (int)1, mic6.GetB());
		MicroInstruccion mic7 = new MicroInstruccion(268435455);
		Assert.assertEquals ("ErrorB7", (int)0, mic7.GetB());
		MicroInstruccion mic8 = new MicroInstruccion(1610612736);
		Assert.assertEquals ("ErrorB8", (int)6, mic8.GetB());
	}
	@Test
	public void testC()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorC0", (int)0, mic.GetC());
		MicroInstruccion mic1 = new MicroInstruccion(34359738368L);
		Assert.assertEquals ("ErrorC1", (int)8, mic1.GetC());
		MicroInstruccion mic2 = new MicroInstruccion(17179869184L);
		Assert.assertEquals ("ErrorC2", (int)4, mic2.GetC());
		MicroInstruccion mic3 = new MicroInstruccion(8589934592L);
		Assert.assertEquals ("ErrorC3", (int)2, mic3.GetC());
		MicroInstruccion mic4 = new MicroInstruccion(34294967296L);
		Assert.assertEquals ("ErrorC4", (int)7, mic4.GetC());
	}
	@Test
	public void testENC()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorENC0", (int)0, mic.GetENC());
		MicroInstruccion mic1 = new MicroInstruccion(68719476736L);
		Assert.assertEquals ("ErrorENC1", (int)1, mic1.GetENC());
	}
	@Test
	public void testWR()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorWR0", (int)0, mic.GetWR());
		MicroInstruccion mic1 = new MicroInstruccion(137438953472L);
		Assert.assertEquals ("ErrorWR1", (int)1, mic1.GetWR());
	}
	@Test
	public void testRD()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorRD0", (int)0, mic.GetRD());
		MicroInstruccion mic1 = new MicroInstruccion(274877906944L);
		Assert.assertEquals ("ErrorRD1", (int)1, mic1.GetRD());
	}
	@Test
	public void testMAR()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorMAR0", (int)0, mic.GetMAR());
		MicroInstruccion mic1 = new MicroInstruccion(549755813888L);
		Assert.assertEquals ("ErrorMAR1", (int)1, mic1.GetMAR());
	}
	@Test
	public void testMBR()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorMBR0", (int)0, mic.GetMBR());
		MicroInstruccion mic1 = new MicroInstruccion(1099511627776L);
		Assert.assertEquals ("ErrorMBR1", (int)1, mic1.GetMBR());
	}
	@Test
	public void testSH()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorSH0", (int)0, mic.GetSH());
		MicroInstruccion mic1 = new MicroInstruccion(8796093022208L);
		Assert.assertEquals ("ErrorSH1", (int)4, mic1.GetSH());
		MicroInstruccion mic2 = new MicroInstruccion(4398046511104L);
		Assert.assertEquals ("ErrorSH2", (int)2, mic2.GetSH());
		MicroInstruccion mic3 = new MicroInstruccion(2199023255552L);
		Assert.assertEquals ("ErrorSH3", (int)1, mic3.GetSH());
	}
	@Test
	public void testCOND()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorCOND0", (int)0, mic.GetCOND());
		MicroInstruccion mic1 = new MicroInstruccion(70368744177664L);
		Assert.assertEquals ("ErrorCOND1", (int)4, mic1.GetCOND());
		MicroInstruccion mic2 = new MicroInstruccion(35184372088832L);
		Assert.assertEquals ("ErrorCOND2", (int)2, mic2.GetCOND());
		MicroInstruccion mic3 = new MicroInstruccion(17592186044416L);
		Assert.assertEquals ("ErrorCOND3", (int)1, mic3.GetCOND());
	}
	@Test
	public void testAMUX()
	{
		MicroInstruccion mic = new MicroInstruccion(0);
		Assert.assertEquals ("ErrorAMUX0", (int)0, mic.GetAMUX());
		MicroInstruccion mic1 = new MicroInstruccion(140737488355328L);
		Assert.assertEquals ("ErrorAMUX1", (int)1, mic1.GetAMUX());
		MicroInstruccion mic2 = new MicroInstruccion(144858585821452L);
		Assert.assertEquals ("ErrorAMUX2", (int)1, mic2.GetAMUX());
	}

}
