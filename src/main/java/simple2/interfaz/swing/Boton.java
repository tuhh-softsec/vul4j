/*
 * Created on 05-jul-2003
 *
 */

package simple2.interfaz.swing;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JToggleButton;

/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */

/**
 * Esta clase es utilizada para crear objetos "botón" que cambian de color
 * cuando el ratón pasa sobre ellos.
 */
public class Boton extends JToggleButton{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7818066963818582018L;

	/**
	 * Almacena el color del botón en estado normal.
	 */
	private Color colorNormal;
	
	/**
	 * Almacena el color del botón cuando el ratón está sobre él.
	 */
	private Color colorSobre = new Color (192, 192, 210);
	
	/**
	 * Crea una instancia de la clase con el color de fondo por defecto. 
	 */
	public Boton() {
		super();
		this.colorNormal = getBackground ();
		addMouseListener (new ManejadorRaton ());
	}
	
	/**
	* Crea una instancia de la clase con la acción asociada 
	* a El color de fondo es por defecto.
	*
	* @param a Acción que realiza el botón cuando es presionado.
	*/
	public Boton (Action a)
	{
		super (a);
		this.colorNormal = getBackground();
		addMouseListener (new ManejadorRaton ());
	}
	
	/**
	* Crea una instancia de la clase con una etiqueta que se le pasa como argumento.
	* @param etiqueta Cadena de texto.
	*/
	public Boton (String etiqueta)
	{
		super (etiqueta);
		this.colorNormal = getBackground ();
		addMouseListener (new ManejadorRaton ());
	}
	
	/**
	* Crea una instancia de la clase con el icono que se le pasa como argumento.
	* @param icono Imagen que posee el botón.
	*/	
	public Boton (Icon icono)
	{
		super (icono);
		this.colorNormal = getBackground ();
		addMouseListener (new ManejadorRaton ());
	}
	
	/**
	* Crea una instancia de la clase con una etiqueta y un icono que son 
	* pasados como argumentos.
	* @param etiqueta El texto que se mostrará en el botón.
	* @param icono El icono que se mostrará en el botón.
	*/
	public Boton (String etiqueta, Icon icono)
	{
		super (etiqueta, icono);
		this.colorNormal = getBackground ();
		addMouseListener (new ManejadorRaton ());
	}
	
	
    /**
	* Esta clase permite que cuando el cursor del ratón pase por encima
	* del botón cambie el color de fondo del mismo.
	* Cuando el cursor sale se vuelve a colocar el color que tenia inicialmente.
	*/
	class ManejadorRaton extends MouseAdapter
	{
		/**
		 * Constructor de la clase.
		 */
		public ManejadorRaton()
		{
			 super();
		}
		
		/**
		 * Cambia el color de fondo del botón.
		 * @param me Evento del ratón.
		 */
		@Override
		public void mouseEntered (MouseEvent me)
		{
			if (isEnabled ())
				setBackground (Boton.this.colorSobre);
			else 
				setBackground (Boton.this.colorNormal);
		}
		
		/**
		 * Restaura el color del fondo del botón que tenia inicialmente.
		 * @param me Evento del ratón.
		 */
		@Override
		public void mouseExited (MouseEvent me)
		{
			setBackground (Boton.this.colorNormal);
		}
	}

}
