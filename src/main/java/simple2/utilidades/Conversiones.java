/*
 * Created on 13-ago-2003
 *
 */
package simple2.utilidades;
/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */
/**
 * Contiene todas las conversiones
 * Constructor privado, no se permite crear instancias de
 * esta clase.
 */
public class Conversiones {
	
	/**
	 * Instancia de la clase
	 *
	 */	
	private Conversiones(){}
	
	/**
	 * Obtiene la cadena con la representación en hexadecimal de valor.
	 * @param valor El valor
	 * @return La cadena con la representación en hexadecimal de valor.
	 */
	public static String ToHexString (int valor)
	{
		String ret = Integer.toHexString(valor);
		while (ret.length() < 8)
			  ret = "0" + ret;
		  return ret.toUpperCase();
	}
		
	/**
	 * Obtiene la cadena con la representación en hexadecimal de valor.
	 * @param valor El numero
	 * @return La cadena con representacion en hexadecimal de valor
	 */	
	public static String ToHexString (short valor)
	{
		String ret = Integer.toHexString(valor);
		while (ret.length() < 4)
			  ret = "0" + ret;
		while (ret.length() > 4)
			ret = ret.substring (1);
		return ret.toUpperCase();

	}
		
	/**
	 * Obtiene la cadena con la representación en binario de valor.
	 * @param valor El numero.
	 * @param digitos el numero de digitos que tendrá el resultado.
	 * @return Una cadena con la representación binaria de valor.
	 */	
	public static String ToBinaryString (int valor, int digitos)
	{
		int aux;
		aux = valor;
		String ret = "";
		for (int i=0; (i < digitos) && (i < 32); i++)
		{
			ret = (aux & 1) + ret;
			aux = aux >> 1;
		}
		return ret;
	}
}

