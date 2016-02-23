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
 * Esta clase provee al usuario de una forma fácil de centrar una ventana en la
 * pantalla del ordenador.
 */
public final class Utilidades {

	/**
	 * Constructor de la clase. Como todos son métodos estáticos pensados para
	 * ser llamados sin crear objetos de esta clase, esta función no debería ser
	 * utilizada.
	 */
	private Utilidades() {
		throw new AssertionError("No se permiten instancias de esta clase");
	}

	/**
	 * Centra (en el eje X y en el eje Y) la ventana que se le pasa como
	 * parametro en la pantalla del monitor. Si el la altura de la ventana es
	 * mayor de el del monitor se hace que la altura sea la misma que la de la
	 * pantalla. Lo mismo pasa con la anchura.
	 * 
	 * @param ventana
	 *            Ventana que se quiere centrar.
	 */
	public static void centrarVentana(Window ventana) {
		Dimension tamPantalla = Toolkit.getDefaultToolkit().getScreenSize();

		Dimension tamVentana = ventana.getSize();

		if (tamVentana.height > tamPantalla.height) {
			tamVentana.height = tamPantalla.height;
		}
		if (tamVentana.width > tamPantalla.width) {
			tamVentana.width = tamPantalla.width;
		}
		ventana.setSize(tamVentana);
		ventana.setLocation((tamPantalla.width - tamVentana.width) / 2, (tamPantalla.height - tamVentana.height) / 2);
	}

}
