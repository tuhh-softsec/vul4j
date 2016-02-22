package simple2.rutadedatos;

import org.junit.Assert;
import org.junit.Test;


public class AluTest {

	public AluTest() {
		super();
	}
	@Test
	public void testSuma ()
	{
		ALU alu = new ALU();
		Assert.assertEquals ("ErrorSuma01", (short)5, alu.operar(0,0, (short)5,(short) 0));
	}
	
	@Test
	public void testAnd ()
	{
		ALU alu = new ALU();
		Assert.assertEquals ("ErrorAnd", (short)7, alu.operar(1,0, (short)7,(short) 7));
		Assert.assertEquals ("ErrorAnd", (short)0, alu.operar(1,0, (short)7,(short) 0));
	}
	@Test
	public void testTransparente()
	{
		ALU alu =new ALU();
		Assert.assertEquals ("ErrorTransparente",(short)4, alu.operar(2,0,(short)4,(short)9));
	}
	@Test
	public void testOr()
	{
		ALU alu = new ALU();
		Assert.assertEquals ("ErrorOr", (short)7, alu.operar(4,0, (short)7,(short) 0));
		Assert.assertEquals ("ErrorOr", (short)0, alu.operar(4,0, (short)0,(short) 0));
		Assert.assertEquals ("ErrorOr", (short)7, alu.operar(4,0, (short)7,(short) 7));
	}

}
