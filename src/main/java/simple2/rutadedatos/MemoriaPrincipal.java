/*
 * Created on 07-ago-2003
 *
 */
package simple2.rutadedatos;
import java.util.*;
/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */

/**
 * 
 * Esta clase representa la memoria principal del ordenador
 * Simple2.
 */
public class MemoriaPrincipal
{

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
	private ArrayList listeners = new ArrayList ();

		
	private short[] memoria = null;
		

	private int mask = 0;
		
	/**
	 * Crea una instancia de la clase con todos los datos inicializados a 0
	 */	
	public MemoriaPrincipal ()
	{
		memoria = new short[TAMANO];
		mask = (0xFFFF >> (16 - BITSDIRECCION));
	}
	
	/**
	 * Crea una instancia de la clase con los datos iniciales 
	 * que se le indican como parametros
	 * 
	 * @param codigoInicial Los datos iniciales de la memoria, se ponen en 
	 * la memoria a partir de la posicion 0
	 */
	public MemoriaPrincipal (short[] codigoInicial)
	{
		memoria = new short[TAMANO];
		mask = (0xFFFF >> (16 - BITSDIRECCION));
			
		for (int i = 0; (i < memoria.length) && (i < codigoInicial.length); i++)
		{
			memoria[i] = codigoInicial[i];
		}
	}
	
	/**
	 * Escribe un dato en la memoria
	 * @param direccion La direccion en la que se escribira el dato
	 * @param dato El dato a escribir
	 */	
	public void EscribirDato (short direccion, short dato)
	{
		int dir = (direccion & mask);
		memoria[dir] = dato;

		ActualizarListeners (dir, dato);
	}
		
	/**
	 * Lee un dato de la memoria
	 * 
	 * @param direccion La direccion donde leeremos el dato
	 * @return El dato solicitado
	 */	
	public short LeerDato (short direccion)
		{
		int dir = (direccion & mask);
		return memoria[dir];
		}

	/**
	 * Añade un listener para los cambios en la memoria.
	 * @param l El listener.
	 */

	public void AddMemoryChangeListener (MemoryChangeListener l)
		{
		listeners.add (l);
		l.MemoryChanged (memoria);
		}
		
	/**
	 * Notifica a los listeners que se ha producido una 
	 * una modificación en la memoria.
	 * @param dir La dirección de la memoria modificada.
	 * @param newValue El nuevo valor almacenado en dir
	 */
	private void ActualizarListeners (int dir, short newValue)
		{
		for (int i = 0; i < listeners.size(); i++)
		{
			((MemoryChangeListener) listeners.get(i)).
				MemoryChanged (dir, newValue);
		}
	}
}
