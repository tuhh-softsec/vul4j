/*
 * Created on 06-ago-2003
 *
 */
package simple2.representacionruta;
/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */
/**
 * 
 * Esta clase representa a un cable unidireccional, con
 * punta de flecha en uno de sus extremos.
 */
public class CableUnidireccional extends Cable{
	
	/**
	 * Crea una instancia de la clase.
	 * @param dib La superficie sobre la que se dibujará este objeto.
	 * @param puntos Las coordenadas de las rectas que componen este cable.
	 */
	public CableUnidireccional (InterfaceDibujo dib, int[] puntos)
		{
		super(dib,puntos);
		}
	
	/**
	 * Pintar el cable inactivo
	 */	
	@Override
	protected void PintarInactivo ()
		{
		super.PintarInactivo();	
		PintarFlechaFin();
		}
	
	/**
	 * Pintar el cable activo
	 */
	@Override
	protected void PintarActivo ()
		{
		super.PintarActivo();
		PintarFlechaFin();
		}
		

}



