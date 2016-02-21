package simple2.ensamblador;
/*
 * Created on 28-jul-2003
 *
 */

/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */

/**Excepcion que se produce si hay algun error en el codigo ensamblador
 * del programa.
 */
public class ErrorCodigoException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5886778339065866481L;

	/**
	 * Crea una instancia de la clase.
	 */
	public ErrorCodigoException() {
		super();
	}
	
	/**
	 * Crea una instancia de la clase
     * @param linea Texto que almacena la excepcion.
	 */
	public ErrorCodigoException(String linea){
		super (linea);
	}


}
