package simple2.rutadedatos;

import junit.framework.*;
import simple2.rutadedatos.*;


public class AluTest extends TestCase {

	public void testSuma ()
	{
		ALU alu = new ALU();
		Assert.assertEquals ("ErrorSuma01", (short)5, alu.Operar(0,0, (short)5,(short) 0));
	}

	public void testAnd ()
	{
		ALU alu = new ALU();
		Assert.assertEquals ("ErrorAnd", (short)7, alu.Operar(1,0, (short)7,(short) 7));
		Assert.assertEquals ("ErrorAnd", (short)0, alu.Operar(1,0, (short)7,(short) 0));
	}

	public void testTransparente()
	{
		ALU alu =new ALU();
		Assert.assertEquals ("ErrorTransparente",(short)4, alu.Operar(2,0,(short)4,(short)9));
	}

	public void testOr()
	{
		ALU alu = new ALU();
		Assert.assertEquals ("ErrorOr", (short)7, alu.Operar(4,0, (short)7,(short) 0));
		Assert.assertEquals ("ErrorOr", (short)0, alu.Operar(4,0, (short)0,(short) 0));
		Assert.assertEquals ("ErrorOr", (short)7, alu.Operar(4,0, (short)7,(short) 7));
	}

}
