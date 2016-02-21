/*
 * Created on 01-ago-2003
 *
 */

package simple2.interfaz.swing;
import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */ 

 /**
  * Clase que se va a utilizar para dibujar en el panel  
  */
public class PanelEsquema extends JPanel{

	/**
	 * Panel donde dibujamos
	 */
	PanelDibujo pdibujo;
	
	/**
	 *Panel superior 
	 */
	JPanel panel;
	
	/**
	 * Etiqueta que usamos como titulo
	 */
	JLabel l1;
	
	/**
	 * Anchura del panel de dibujo
	 */
	private static final int ANCHO = 740;
	
	/**
	 *Altura del panel de dibujo 
	 */
	private static final int ALTO = 560;
	
	/**
	 * Instancia de la clase. Dibuja la ruta de datos.
	 *
	 */
	public PanelEsquema() {
		super();
		setLayout(new BorderLayout());
		pdibujo = new PanelDibujo ();
		panel =new JPanel();
		l1 = new JLabel ("RUTA DE DATOS DE SIMPLE2");
			
		panel.add (l1);
		add (panel, BorderLayout.NORTH);
		add (pdibujo, BorderLayout.CENTER);
	}


}
