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
 * Esta clase representa un bus de cables. Sólo se permite que un cable esté
 * activo en un momento dado.
 */
public class Bus {
	/**
	 * Array con los cables que componen el bus.
	 */
	private Cable[] cables = null;

	/**
	 * Cable activo en el bus (un número negativo es ninguno.
	 * 
	 * @param cables
	 */

	private int cableActivo = -1;

	/**
	 * Crea una instancia de la clase.
	 * 
	 * @param cables
	 *            Los cables que componen el bus.
	 */
	public Bus(Cable[] cables) {
		this.cables = cables;
	}

	/**
	 * Enciende una linea del bus
	 * 
	 * @param linea
	 *            Linea que hay que encender del bus
	 */
	public void Encender(int linea) {
		if ((linea >= this.cables.length) || (linea < 0))
			return;
		Apagar();
		this.cables[linea].Encender();
		this.cableActivo = linea;
	}

	/**
	 * Pinta el bus
	 *
	 */
	public void Repintar() {
		for (int i = 0; i < this.cables.length; i++)
			this.cables[i].Repintar();

		if (this.cableActivo > -1)
			this.cables[this.cableActivo].Repintar();
	}

	/**
	 * Apaga el bus
	 *
	 */
	public void Apagar() {
		for (int i = 0; i < this.cables.length; i++)
			this.cables[i].Apagar();
		this.cableActivo = -1;
	}
}