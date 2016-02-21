/*
 * Created on 07-ago-2003
 *
 */
package simple2.representacionruta;
import java.awt.*;
/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */
/**
 * Esta clase representa un cable de datos.
 * 
 */
public class Cable extends ElementoDibujable
	{

	/**	Lista de las coordenadas de las rectas que componen
	 *el cable.
	 */
	protected int[] puntos = null;
		
	/**
	 * Crea una instancia de la clase.
	 * @param dib La superficie de dibujo sobre la que se dibujará este objeto.
	 * @param puntos Las coordenadas de las rectas que componen el cable
	 */	
	public Cable (InterfaceDibujo dib, int[] puntos)
	{
		super (dib);
		this.puntos = puntos;
	}
	
	/**
	 * Pinta el cable inactivo
	 */	
	protected void PintarInactivo ()
	{
		int x = 0;
		while (x < (puntos.length -2) )
		{
			int x1 = puntos[x];
			int y1 = puntos[x+1];
			int x2 = puntos[x+2];
			int y2 = puntos[x+3];
				
			x+=2;
			dibujo.dibujarRecta (Color.BLACK, x1, y1, x2, y2);
		}

	}
	
	/**
	 * Pinta una punta de flecha al final del cable.
	 */
	protected void PintarFlechaFin ()
	{
		int x = puntos.length;
			
		PintarFlecha ( new int[] { puntos[x-4], puntos[x-3], puntos[x-2], puntos[x-1]});
	}
	
	/**
	 * Dibuja una punta de flecha al principio del cable.
	 */
	protected void PintarFlechaInicio ()
	{
		PintarFlecha (new int[] { puntos[2], puntos[3], puntos[0], puntos[1]});
	}
	
	/**
	* Pinta una punta de flecha al final de la linea indicada.
	* @param linea Coordenadas de la linea
	*/
	private void PintarFlecha (int[] linea)
	{
		if (linea.length != 4)
			return;	
		int x1 = 0;
		int x2 = 0;
		int y1 = 0;
		int y2 = 0;
			
		if (linea[0] == linea[2]) // es una línea vertical
		{
			x1 = linea[0] -5;
			x2 = linea[0] +5;
				
			if (linea[1] < linea[3]) //La linea va hacia abajo
				y1 = linea[3] - 5;
			else
				y1 = linea[3] + 5;
			y2 = y1;
		}
		else if (linea[1]==linea[3]) // es una linea horizontal
		{
			y1 = linea[1] -5;
			y2 = linea[1] +5;
				
			if (linea[0] < linea[2]) // La linea va hacia la derecha
				x1 = linea[2] - 5;
			else
				x1 = linea[2] + 5;
			x2 = x1;
		}
		else
		{
			return;
		}			
			
		dibujo.dibujarRecta (Color.RED,linea[2], linea[3], x1, y1);
		dibujo.dibujarRecta (Color.RED,linea[2], linea[3], x2, y2);
	}
	
	/**
	 * Pintar cable activo
	 */	
	protected void PintarActivo ()
	{
		int x = 0;
		while (x < (puntos.length -2) )
		{
			int x1 = puntos[x];
			int y1 = puntos[x+1];
			int x2 = puntos[x+2];
			int y2 = puntos[x+3];
				
			x+=2;
			dibujo.dibujarRecta (Color.RED, x1, y1, x2, y2);
		}
	}
}

