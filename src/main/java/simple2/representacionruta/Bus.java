/*
 * Created on 07-ago-2003
 *
 */
package simple2.representacionruta;
/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */
/**
 * 
 * Esta clase representa un bus de cables. Sólo se
 * permite que un cable esté activo en un momento dado.
 */
public class Bus
	{
	/**
	 * Array con los cables que componen el bus.
	 */
	private Cable[] cables = null;
	
	/**
	 * Cable activo en el bus (un número negativo es ninguno.
	 * @param cables
	 */
		
	private int cableActivo = -1;
		
	/**
	 * Crea una instancia de la clase.
	 * @param cables Los cables que componen el bus.
	 */	
	public Bus (Cable[] cables)
	{
		this.cables= cables;
	}
	
	/**
	 * Enciende una linea del bus 
	 * @param linea Linea que hay que encender del bus
	 */
	public void Encender (int linea)
	{
		if ((linea >= cables.length) || (linea < 0))
			return;			
		Apagar();
		cables[linea].Encender();
		cableActivo = linea;
	}
	
	/**
	 * Pinta el bus 
	 *
	 */
	public void Repintar ()
	{
		for (int i=0; i < cables.length; i++)
			cables[i].Repintar();
				
		if (cableActivo > -1)
			cables[cableActivo].Repintar();
	}
	
	/**
	 * Apaga el bus
	 *
	 */
	public void Apagar()
	{
		for (int i=0; i < cables.length; i++)
			cables[i].Apagar();
		cableActivo = -1;
	}
}