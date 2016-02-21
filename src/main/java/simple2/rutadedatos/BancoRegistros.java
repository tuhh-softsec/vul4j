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
 * Clase de los Bancos de Registros
 */
public class BancoRegistros
	{
	public static int PC = 0x00;
	public static int AC = 0x01;
	public static int SP = 0x02;
	public static int IR = 0x03;
	public static int OMASK = 0x04;
	public static int RCON1 = 0x05;
	public static int RCON2 = 0x06;
	public static int RCON3 = 0x07;
	public static int AMASK = 0x08;
	public static int SMASK = 0x09;
	public static int A = 0x0A;
	public static int B = 0x0B;
	public static int C = 0x0C;
	public static int D = 0x0D;
	public static int E = 0x0E;
	public static int F = 0x0F;
	private static String[] nombres = new String[] {
		"PC",
		"AC", 
		"SP", 
		"IR", 
		"OMASK", 
		"RCON1", 
		"RCON2", 
		"RCON3", 
		"AMASK", 
		"SMASK", 
		"A",
		"B",
		"C", 
		"D", 
		"E", 
		"F"};
	
	/**
	 * Array en el que se almacena internamente el contenido
	 * de los registros
	 */
	private ArrayList listeners = new ArrayList();

	/**
	 * Contenido de los registros
	 */
	private short[] registros;

	/**
	 * Nos devuelve el nombre del registro
	 * @param reg Posicion del registro
	 * @return Devuelve el nombre del registro si la posicion existe 
	 */
	public static String GetNombreRegistro(int reg)
	{
		if ((reg>=0) && (reg < nombres.length))
			return nombres[reg];
		else
			return "";
	}
		
	/**
	 * Crea una instancia de la clase
	 */
	public BancoRegistros ()
	{
		registros = new short[16];
		Reset ();

	}
		
	/**
	 * Inicializa el contenido de todos los registros a los
	 * que son constantes les da el valor
	 */	
	public void Reset ()
	{
		for (int i = 0; i < registros.length; i++)
			registros[i] = 0;

		registros[4] = (short) 0x07FF;//OMASK
		registros[5] = (short) 0x0000;//RCON1
		registros[6] = (short) 0x0001;//RCON2
		registros[7] = (short) -1;//RCON3
		registros[8] = (short) 0x0FFF;//AMASK
		registros[9] = (short) 0x00FF;//SMASK
	}

	/**
	 * Escribe el valor de un registro(si no es de los constantes)
	 * 
	 * @param registro El registro en el que se escribirá
	 * @param valor El valor que se quiere escribir
	 */	
	public void EscribirRegistro (int registro, short valor)
	{
		if ((registro > 15) || (registro < 0))
			return;
		if ((registro < 4) || (registro > 9))
		{
			registros[registro] = valor;
			ActualizarListeners(registro, valor);
		}
	}

	/**
	 * Obtiene el valor almacenado en el registro
	 * 
	 * @param registro El registro a leer
	 * 
	 * @return El dato almacenado en el registro(si existe la posicion)
	 */	
	public short LeerRegistro (int registro)
		{
		if ((registro < 0) || (registro > 15))
			return 0;
		return registros[registro];
	}
		
	/**
	 * Añade a la lista de escuchadores(listeners)
	 * @param l 
	 */	
	public void AddRegisterChangeListener (RegisterChangeListener l)
	{
		l.RegisterChanged(registros);
		listeners.add(l);
	}
	
	/**
	 * Actualiza el valor del registro
	 * @param dir El registro que se desea actualizar
	 * @param newValue El nuevo valor de ese registro
	 */	
	private void ActualizarListeners (int dir, short newValue)
	{
		for (int i = 0; i < listeners.size(); i++)
		{
			((RegisterChangeListener) listeners.get(i)).RegisterChanged (dir, newValue);
		}
	}
		

}
