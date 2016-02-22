/*
 * Created on 07-ago-2003
 *
 */
package simple2.rutadedatos;

import java.util.ArrayList;
/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */
import java.util.List;

/**
 * 
 * Esta clase representa la memoria principal del ordenador Simple2.
 */
public class MemoriaPrincipal {

	/**
	 * Numero de palabras de 16 bits que amacena la memoria
	 */
	public int TAMANO = 2048;

	/**
	 * Numero de bits utilizados para las direcciones
	 */
	public int BITSDIRECCION = 11;

	/**
	 * Array donde se guardan los datos que hay en la memoria principal
	 */
	private List<MemoryChangeListener> listeners = new ArrayList<>();

	private short[] memoria = null;

	private int mask = 0;

	/**
	 * Crea una instancia de la clase con todos los datos inicializados a 0
	 */
	public MemoriaPrincipal() {
		this.memoria = new short[this.TAMANO];
		this.mask = (0xFFFF >> (16 - this.BITSDIRECCION));
	}

	/**
	 * Crea una instancia de la clase con los datos iniciales que se le indican
	 * como parametros
	 * 
	 * @param codigoInicial
	 *            Los datos iniciales de la memoria, se ponen en la memoria a
	 *            partir de la posicion 0
	 */
	public MemoriaPrincipal(short[] codigoInicial) {
		this.memoria = new short[this.TAMANO];
		this.mask = (0xFFFF >> (16 - this.BITSDIRECCION));

		for (int i = 0; (i < this.memoria.length) && (i < codigoInicial.length); i++) {
			this.memoria[i] = codigoInicial[i];
		}
	}

	/**
	 * Escribe un dato en la memoria
	 * 
	 * @param direccion
	 *            La direccion en la que se escribira el dato
	 * @param dato
	 *            El dato a escribir
	 */
	public void escribirDato(short direccion, short dato) {
		int dir = (direccion & this.mask);
		this.memoria[dir] = dato;

		notificarListeners(dir, dato);
	}

	/**
	 * Lee un dato de la memoria
	 * 
	 * @param direccion
	 *            La direccion donde leeremos el dato
	 * @return El dato solicitado
	 */
	public short leerDato(short direccion) {
		int dir = (direccion & this.mask);
		return this.memoria[dir];
	}

	/**
	 * Añade un listener para los cambios en la memoria.
	 * 
	 * @param l
	 *            El listener.
	 */

	public void addMemoryChangeListener(MemoryChangeListener l) {
		this.listeners.add(l);
		l.memoryChanged(this.memoria);
	}

	/**
	 * Notifica a los listeners que se ha producido una una modificación en la
	 * memoria.
	 * 
	 * @param dir
	 *            La dirección de la memoria modificada.
	 * @param newValue
	 *            El nuevo valor almacenado en dir
	 */
	private void notificarListeners(int dir, short newValue) {
		for (MemoryChangeListener listener : this.listeners) {
			listener.memoryChanged(dir, newValue);
		}
	}
}
