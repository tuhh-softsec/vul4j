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
	 * 
	 */
	private static final long serialVersionUID = -3536272960457696642L;

	/**
	 * Crea una instancia de la clase
	 * @param mensaje El mensaje de la excepcion
	 */
	public SimulacionFinalizadaException(String mensaje)
	{
		super(mensaje);
	}
}
