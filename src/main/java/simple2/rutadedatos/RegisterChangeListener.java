/*
 * Created on 11-ago-2003
 *
 */
package simple2.rutadedatos;
/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */

/**
 * Interface que deben implementar las clases que respondan 
 * a los eventos de cambios en los contenidos de los registros. 
 */
public interface RegisterChangeListener
{
	/**
	 * Se llama cuando cambia el contenido de un registro.
	 * @param registro El registro que ha cambiado.
	 * @param newValue El nuevo valor almacenado en registro
	 */
		
	void RegisterChanged (int registro, short newValue);

	/**
	 * Se llama para inicializar el listener, pasandole un array con el
	 * contenido de todos los registros.
	 * @param newValues Los valores almacenados en los registros 
	 */
	void RegisterChanged (short[] newValues);
}
