/*
 * Created on 07-ago-2003
 *
 */
package simple2.representacionruta;

/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */

public abstract class ElementoDibujable implements IElementoDibujable {

	protected InterfaceDibujo dibujo = null;

	private boolean activo = false;

	/**
	 * Constructor.
	 * 
	 * @param d
	 *            La superficie de dibujo sobre la que se dibujará este objeto.
	 */
	protected ElementoDibujable(InterfaceDibujo d) {
		this.dibujo = d;
	}

	/**
	 * Dibuja el objeto en su estado inactivo.
	 */
	public void apagar() {
		pintarInactivo();
		this.activo = false;
	}

	/**
	 * Dibuja el objeto en su estado activo.
	 */
	public void encender() {
		pintarActivo();
		this.activo = true;
	}

	/**
	 * Redibuja el objeto en su estado actual.
	 */
	public void repintar() {
		if (this.activo)
			pintarActivo();
		else
			pintarInactivo();

	}

	/**
	 * Escribir texto en el elemento dibujable
	 */
	public void setText(String texto) {
	}

	/**
	 * Devuelve el texto el elemento
	 * 
	 * @return El texto que contiene el elemento
	 */
	public String getText() {
		return "";
	}

	/**
	 * Pinta el elemento dibujable como estado inactivo
	 *
	 */
	protected abstract void pintarInactivo();

	/**
	 * Pinta el elemento dibujable como estado activo
	 *
	 */
	protected abstract void pintarActivo();

}
