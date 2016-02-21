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
 * Esta clase es la encargada de verificar si las intrucciones de salto
 * tienen el formato correcto y las codifica.
 */
public class InstruccionSalto extends InstruccionGeneral{
	
	/**
	* Contiene las instrucciones que maneja la clase con su código de
	* operación.
	*/
	private Hashtable tabla;

	/**
	* Crea una instancia de la clase e inicializa el atributo tabla con las
	* instrucciones .
	*/
	public InstruccionSalto() {
		tabla = new Hashtable ();
		
		tabla.put ("JNEG", new Integer (0x0A));
		tabla.put ("JZER", new Integer (0x0B));
		tabla.put ("JCAR", new Integer (0x0C));
		tabla.put ("JUMP", new Integer (0x0D));
		tabla.put ("CALL", new Integer (0x0E));
	}
	
	/**
	 * Traduce la instrucción de salto.
	 * La instrucion debe estar validada previamente con el metodo 
	 *
	 * @param instruccion Instrucción con su nombre y parametros.
	 * @param linea Linea en la que aparece la instrucción.
	 *
	 * @return La codificación de la instrucción.
	 */
	public short codificar (String instruccion,int linea)
		{
			int codigo;
			String[] cadena = separarOperandos (instruccion);
			int inmediato=Integer.parseInt(cadena[1]);
			Object c = tabla.get (cadena[0]);
			codigo=((Integer) c).intValue();
			codigo=codigo<<11;
			
			return ((short) (codigo+inmediato));
		}
	
	/**
     * Comprueba que la instrucción de salto que se
     * va a codificar tenga el formato correcto.
     *
     * @return 	Cadena vacia si no se han producido errores.
     * 			Cadena con un mensaje que indica el motivo del error en la sintaxis.
     * @param instruccion Instrucción con su nombre y parametros.
     * @param linea Linea en la que aparece la instrucción.
     * @throws ErrorCodigoException   si ocurre algun error en el código, la excepcion
     * contiene el mensaje de error
     */	
	public String validar (String instruccion, int linea) throws ErrorCodigoException
			{
				String tipo="";
				if (contieneCaracteresNoValidos (instruccion)){
					return "Linea: " + linea + ". " + CONTIENE_CARACTERES_INVALIDOS;
				}
				String[] cadena = separarOperandos (instruccion);
				Object c = tabla.get (cadena[0]);
				if (c == null){
					return "Linea: " + linea +". No se reconoce la instruccion " + cadena[0] + "\n";
				}
				if (cadena.length != 2){
					return "Linea: " + linea + ". Número de parámetros incorrectos.\n";
				}
				else{
					try{
						int numero=Integer.parseInt(cadena[1]);
						}
					catch(Exception e){
						return "Linea: "+linea+". El segundo parametro tiene que ser un número\n";
						}
					}
			
				return "";
			}

}
