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
 * 
 * Interface que deben implementar las clases que respondan a los eventos de
 * cambios en los contenidos de la memoria
 */
public interface MemoryChangeListener {
	/**
	 * Se llama para inicializar el listener, pasandole un array con el
	 * contenido de toda la memoria.
	 * 
	 * @param newMemoryValues
	 *            Los valores almacenados en la memoria
	 */

	void MemoryChanged(short[] newMemoryValues);

	/**
	 * Se llama cuando cambia una posicion de la memoria.
	 * 
	 * @param dir
	 *            La direccion que ha cambiado
	 * @param newValue
	 *            El nuevo valor de la posición de memoria
	 */
	void MemoryChanged(int dir, short newValue);
}
