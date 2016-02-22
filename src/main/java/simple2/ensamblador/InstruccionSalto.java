/*
 * Created on 28-jul-2003
 *
 */

package simple2.ensamblador;

import java.util.Hashtable;

/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */

/**
 * Esta clase es la encargada de verificar si las intrucciones de salto tienen
 * el formato correcto y las codifica.
 */
public class InstruccionSalto extends InstruccionGeneral {

	/**
	 * Contiene las instrucciones que maneja la clase con su código de
	 * operación.
	 */
	private Hashtable<String, Integer> tabla;

	/**
	 * Crea una instancia de la clase e inicializa el atributo tabla con las
	 * instrucciones .
	 */
	public InstruccionSalto() {
		this.tabla = new Hashtable<>();

		this.tabla.put("JNEG", Integer.valueOf(0x0A));
		this.tabla.put("JZER", Integer.valueOf(0x0B));
		this.tabla.put("JCAR", Integer.valueOf(0x0C));
		this.tabla.put("JUMP", Integer.valueOf(0x0D));
		this.tabla.put("CALL", Integer.valueOf(0x0E));
	}

	/**
	 * Traduce la instrucción de salto. La instrucion debe estar validada
	 * previamente con el metodo
	 *
	 * @param instruccion
	 *            Instrucción con su nombre y parametros.
	 * @param linea
	 *            Linea en la que aparece la instrucción.
	 *
	 * @return La codificación de la instrucción.
	 */
	@Override
	public short codificar(String instruccion, int linea) {
		int codigo;
		String[] cadena = separarOperandos(instruccion);
		int inmediato = Integer.parseInt(cadena[1]);
		Object c = this.tabla.get(cadena[0]);
		codigo = ((Integer) c).intValue();
		codigo = codigo << 11;

		return ((short) (codigo + inmediato));
	}

	/**
	 * Comprueba que la instrucción de salto que se va a codificar tenga el
	 * formato correcto.
	 *
	 * @return Cadena vacia si no se han producido errores. Cadena con un
	 *         mensaje que indica el motivo del error en la sintaxis.
	 * @param instruccion
	 *            Instrucción con su nombre y parametros.
	 * @param linea
	 *            Linea en la que aparece la instrucción.
	 * @throws ErrorCodigoException
	 *             si ocurre algun error en el código, la excepcion contiene el
	 *             mensaje de error
	 */
	@Override
	public String validar(String instruccion, int linea) throws ErrorCodigoException {
		if (contieneCaracteresNoValidos(instruccion)) {
			return "Linea: " + linea + ". " + CONTIENE_CARACTERES_INVALIDOS;
		}
		String[] cadena = separarOperandos(instruccion);
		Object c = this.tabla.get(cadena[0]);
		if (c == null) {
			return "Linea: " + linea + ". No se reconoce la instruccion " + cadena[0] + "\n";
		}
		if (cadena.length != 2) {
			return "Linea: " + linea + ". Número de parámetros incorrectos.\n";
		}
		try {
			Integer.parseInt(cadena[1]);
		} catch (Exception e) {
			return "Linea: " + linea + ". El segundo parametro tiene que ser un número\n";
		}

		return "";
	}

}
