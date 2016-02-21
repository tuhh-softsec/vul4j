/*
 * Created on 28-jul-2003
 *
 */
package simple2.ensamblador;
import java.util.*;
/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */

/**
 * Esta clase es una Clase abstracta de la que heredan varias clases específicas
 * para cada tipo de instrucción de la arquitectura.
 * Esta clase define el "formato" de todas las clase hijas que heredan de ella
 * todos sus metodos.
 */
public abstract class InstruccionGeneral {

	/**
	* Array de caracteres que contiene los 'simbolos especiales' validos
	* en el código fuente. Se incluyen : ' ', '#', '(', ')', ',', '_', 
	* '-', '$', '%'
	*/
	protected static final char[] caracteresValidos =
		{ ' ', '#', '(', ')', ',', '_', '-','$','%' };
	
	/**
	* Array de caracteres que contiene los caracteres que se pueden usar como
	* separadores validos de los argumentos...
	* Se incluyen : el espacio, la coma y el tabulador
	*/
	protected static final char[] caracteresSeparador =
		{ ' ', ',', '\t' };
	
	/**
	* Error que se produce cuando en alguna instruccion apareces caracteres
	* no validos (no son letras, ni numeros , si separadores validos, 
	* ni símbolos especiales válidos.
	*/
	protected static final String CONTIENE_CARACTERES_INVALIDOS =
		"La instrucción contiene caráteres no válidos\n";
	
	/**
	 * Metodo abstracto de la clase que implementan las clases hijas.
	 * Su funcion es comprobar la sintaxis de las instrucciones.
	 * @param instruccion Instrucción con su nombre y parametros.
	 * @param linea Linea en la que aparece la instrucción.
	 * @return cadena vacia si no se han producido errores.
	 * @throws ErrorCodigoException si se alguana linea analizada tiene errores.
	 */	
	public abstract String validar (String instruccion, int linea) throws ErrorCodigoException;
		
	/**
	* Comprueba si letra es un carácter separador.
	*
	* @param letra El carácter a comprobar.
	*
	* @return 
	*		true - si validación cierta.
	*		false - si validación falsa.
	**/
	protected static boolean esSeparador (char letra)
	{
		for (int i = 0; i < caracteresSeparador.length; i++)
			if (caracteresSeparador[i] == letra)
				return true;
		return false;

	}
	
	/**
	 * Metodo abstracto de la clase que implementan las clases hijas.
	 * Su función es codificar la instruccion que se le pasa como parametro 
	 * (que previamente se ha validado).
	 * @param instruccion Instrucción con su nombre y parametros.
	 * @param linea Linea en la que aparece la instrucción.
	 * @return La codificación de la instrucción.
	 */
	public short codificar (String instruccion, int linea)
		{
			return 0;
		}
	
	/**
	* Comprueba si una instruccion tiene caracteres no válidos.
	* (los caracteres validos son letras (A..Z), numeros (0..9)
	*
	* @param instrucción- cadena.
	*
	* @return 
	* 		true - si validación cierta.
	* 		false - si validación falsa.
	**/
	protected boolean contieneCaracteresNoValidos (String instruccion)
	{

		for (int z = 0; z < instruccion.length (); z++)
		{
			if (!esValido (instruccion.charAt (z)))
				return true;
		}
		return false;
	}
	
	/**
	* Comprueba si letra es un caracter especial válido.
	*
	* @param letra
	*
	* @return 
	*		true - si validación cierta.
	*		false - si validación falsa.
	*/
	protected boolean esValido (char letra)
	{
		if ((letra <= 'Z') && (letra >= 'A'))
			return true;
		if ((letra <= '9') && (letra >= '0'))
			return true;
		for (int i = 0; i < caracteresValidos.length; i++)
		{
			if (letra == caracteresValidos[i])
				return true;
		}
		return false;
	}
		
	/**
	* Separa los distintos operandos de una instrucció y mete cada uno 
	* como un elemento de una array de cadenas.
	*
	* @param instrucción Exprexion a disgregar.
	*
	* @return Array con los operandos de la instruccion, incluido el nombre
	* de la función.
	*/
	public String[] separarOperandos (String instruccion)
	{
		int i = 0;
		int a = 0;
		boolean continuar = true;
		Vector v = new Vector ();
		while (continuar)
		{
			while ((i < instruccion.length ())&& !esSeparador (instruccion.charAt (i)))
				i++;

			if (i == instruccion.length ())
			{
				continuar = false;
				if (i > a)
					v.add (instruccion.substring (a, i));
			}
			else if (i > a)
			{
				v.add (instruccion.substring (a, i));
				a = i + 1;
			}
			else
				a++;
			i++;
		}
		String[]d = new String[v.size ()];
		for (int z = 0; z < v.size (); z++)
		{
			d[z] = (String) v.get (z);
		}
		return d;
	}
	
	/**
	* Devuelve la representación en número binario de 32 bits del entero que recibe como parámetro.
	*
	* @param valor - entero que le corresponde a la traduccion de una instruccion.
	*
	* @return la codificacion de una intrucción en binario.
	*/
	public static String intToBinaryString (int valor)
		{
		String cadena = Integer.toBinaryString (valor);
		for (int z= cadena.length(); z < 11; z++)
			cadena = "0" + cadena;
		return cadena;
		}
		
}
