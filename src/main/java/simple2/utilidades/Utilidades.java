/*
 * Created on 05-jul-2003
 *
 */

package simple2.utilidades;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */
/**
 * Esta clase provee al usuario de una forma fácil de centrar una ventana en la pantalla del ordenador.
 */
public class Utilidades extends Object{

	/**
	 * Constructor de la clase.
	 * Como todos son métodos estáticos pensados para ser llamados sin crear objetos de esta
	 * clase, esta función no debería ser utilizada.
	 */
	public Utilidades() {
	}
	
	/**
	 * Centra (en el eje X y en el eje Y) la ventana que se le pasa como parametro en la pantalla del
	 * monitor.
	 * Si el la altura de la ventana es mayor de el del monitor se hace que la altura
	 * sea la misma que la de la pantalla. Lo mismo pasa con la anchura.
	 * @param ventana Ventana que se quiere centrar.
	 */
	public static void centrarVentana (Window ventana)
		{
		Dimension TamPantalla = Toolkit.getDefaultToolkit ().getScreenSize ();
			
		Dimension TamVentana = ventana.getSize ();
		
		if (TamVentana.height > TamPantalla.height)
			TamVentana.height = TamPantalla.height;
		if (TamVentana.width > TamPantalla.width)
			TamVentana.width = TamPantalla.width;
		ventana.setSize (TamVentana);
		ventana.setLocation ((TamPantalla.width - TamVentana.width) / 2,
					   (TamPantalla.height - TamVentana.height) / 2);
		}

	/**
	 * Centra (en el eje X y en el eje Y) el cuadro de diálogo que se le pasa como parametro 
	 * en la pantalla del monitor.
	 * @param ventana El cuadro de dialogo a centrar.
	 */	
	public static void centrarJDialog (javax.swing.JDialog ventana)
		{
		Dimension TamPantalla = Toolkit.getDefaultToolkit().getScreenSize ();
			
		Dimension TamVentana = ventana.getSize ();
		
		if (TamVentana.height > TamPantalla.height)
			TamVentana.height = TamPantalla.height;
		if (TamVentana.width > TamPantalla.width)
			TamVentana.width = TamPantalla.width;
			ventana.setSize (TamVentana);
			
		
		int x = (TamPantalla.width - TamVentana.width)/2;
		int y = (TamPantalla.height - TamVentana.height)/2;
		ventana.setLocation (x,y);
		}

}
