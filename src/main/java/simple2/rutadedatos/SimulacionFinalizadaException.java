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
 * Excepción que se lanza cuando termina la simulación.
 */
public class SimulacionFinalizadaException extends Exception
{
	/**
	 * Crea una instancia de la clase
	 * @param mensaje El mensaje de la excepcion
	 */
	public SimulacionFinalizadaException(String mensaje)
	{
		super(mensaje);
	}
}
