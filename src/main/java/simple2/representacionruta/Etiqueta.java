/*
 * Created on 13-ago-2003
 *
 */

package simple2.representacionruta;
import java.awt.Color;
/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */

/**
 * Clase para dibujar etiquetas  
 */
public class Etiqueta extends ElementoDibujable {

	/**
	 * Posición x de la esquina superior izquierda de la caja.
	 */
	private int x;

	/**
	 * Posición y de la esquina superior izquierda de la caja.
	 */
	private int y;

	/**
	 * Texto que se muestra dentro de la caja.
	 */
	private String texto;

	
	/**
	 * Crea una instancia de la clase.
	 * @param dib La superficie sobre la que se debe dibujar este objeto.
	 * @param x Posición x de la esquina superior izquierda de la caja.
	 * @param y Posición y de la esquina superior izquierda de la caja.
	 * @param texto Texto a mostrar dentro de la caja.
	 */
	public Etiqueta(InterfaceDibujo dib, int x, int y, String texto) {
		super(dib);
		this.x = x;
		this.y = y;
		this.texto = texto;
	}

	/**
	 * Pintar el objeto en estado activo
	 */
	protected void PintarActivo() {
		dibujo.dibujarTexto(Color.RED, x, y, texto);
	}

	/**
	 * Pintar el objeto en estado inactivo
	 */
	protected void PintarInactivo() {
		dibujo.dibujarTexto(Color.BLACK, x, y, texto);
	}

	/**
	 * Escribir el texto en la etiqueta
	 */
	public void setText(String texto) {
		this.texto = texto;
	}

	/**
	 * Obtener el texto de la etiqueta
	 * @return Devuelve el texto de la etiqueta
	 */
	public String getText() {
		return texto;
	}

}


