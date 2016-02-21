/*
 * Created on 07-ago-2003
 *
 */
package simple2.rutadedatos;
/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */

/**
 * Esta clase representa a una microinstruccion del ordenador Simple2.
 */
public class MicroInstruccion
{
	
	/**
	 * La microinstrucción en binario, como un entero largo sin signo
	 */		
	private long instruccion;

	/**
	 * Crea una instancia de la clase a partir de un valor.
	 * @param valor El valor binario de la microinstrucción.
	 */
	public MicroInstruccion (long valor)
	{
		instruccion = valor;
	}
		
	/**
	 * Obtiene la microinstrucción como un entero largo sin signo.
	 * @return La microinstruccion como un entero sin signo
	 */
	public long ToLong ()
	{
		return instruccion;
	}
		
	/**
	 * Obtiene la operación de la ALU
	 * @return La operacion de la ALU
	 */
	public int GetALU ()
	{
		return GetBits (9, 4);
	}
		
	/**
	 * Obtiene el valor del campo FIR
	 * @return El valor del campo FIR
	 */
	public int GetFIR ()
	{
		return GetBits (13, 1);
	}
		
	/**
	 * Obtiene el valor del campo ADDR.
	 * @return El valor del campo ADDR.
	 */
	public int GetADDR ()
	{
		return GetBits (14, 10);
	}
		
	/**
	 * Obtiene el valor del campo A.
	 * @return El valor del campo A.
	 */
	public int GetA ()
	{
		return GetBits (24, 4);
	}
		
	/**
	 * Obtiene el valor del campo B.
	 * @return El valor del campo B.
	 */
	public int GetB ()
	{
		return GetBits (28, 4);
	}
		
	/**
	 * Obtiene el valor del campo C.
	 * @return El valor del campo C.
	 */
	public int GetC ()
	{
		return GetBits (32, 4);
	}
		
	/**
	 * Obtiene el valor del campo ENC.
	 * @return El valor del campo ENC.
	 */
	public int GetENC ()
	{
		return GetBits (36, 1);
	}
		
	/**
	 * Obtiene el valor del campo WR.
	 * @return El valor del campo WR.
	 */
	public int GetWR ()
	{
		return GetBits (37, 1);
	}
		
	/**
	 * Obtiene el valor del campo RD. 
	 * @return El valor del campo RD
	 */
	public int GetRD ()
	{
			return GetBits (38, 1);
	}
		
	/**
	 * Obtiene el valor del campo MAR.
	 * @return El valor del campo MAR
	 */
	public int GetMAR ()
	{
		return GetBits (39, 1);
	}
		
	/**
	 * Obtiene el valor del campo MBR.
	 * @return El valor del campo MBR
	 */
	public int GetMBR ()
	{
		return GetBits (40, 1);
	}
		
	/**
	 * Obtiene el valor del campo SH.
	 * @return El valor del campo SH
	 */
	public int GetSH ()
	{
		return GetBits (41, 3);
	}
		
	/**
	 * Obtiene el valor del campo COND.
	 * @return El valor del campo COND
	 */
	public int GetCOND ()
	{
		return GetBits (44, 3);
	}
		
	/**
	 * Obtiene el valor del campo AMUX.
	 * @return El valor del campo AMUX
	 */
	public int GetAMUX ()
	{
		return GetBits (47, 1);
	}
		
	/**
	 * Obtiene el valor de un campo en una posición y de
	 * un tamaño dados.
	 * @param b Posición del primer bit del campo.
	 * 
	 * @param count Número de bits que componen el campo.
	 */
	private int GetBits (int b, int count)
	{
		long mask = 0xFFFFFFFFFFFFFFFFL >>> (64 - count);
		mask = mask << b;
		long resultado = (instruccion & mask) >> b;
		return (int) resultado;
	}
		
	/**
	 * Conversores a cadenas representando enteros en Hexadecimal
	 * @return Devuelve la cadena que representa en hexadecimal
	 */
	public String toHexString ()
	{
		String tmp = "" + Long.toHexString (instruccion);
		while (tmp.length() < 12)
			tmp = "0" + tmp;
		String ret = tmp.substring(0,4) + " " + tmp.substring(4,8) +" " + tmp.substring(8);
		ret = ret.toUpperCase();
		return ret;
	}
		
}
