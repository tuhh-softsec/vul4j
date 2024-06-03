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
 * Esta clase representa un bus de cables. Slo se permite que un cable est
 * activo en un momento dado.
 */
public class Bus {
	/**
	 * Array con los cables que componen el bus.
	 */
	private Cable[] cables = null;

	/**
	 * Cable activo en el bus (un nmero negativo es ninguno.
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
	public void encender(int linea) {
		if ((linea >= this.cables.length) || (linea < 0)) {
			return;
		}
		apagar();
		this.cables[linea].encender();
		this.cableActivo = linea;
	}

	/**
	 * Pinta el bus
	 *
	 */
	public void repintar() {
		for (int i = 0; i < this.cables.length; i++) {
			this.cables[i].repintar();
		}

		if (this.cableActivo > -1){
			this.cables[this.cableActivo].repintar();
		}
	}

	/**
	 * Apaga el bus
	 *
	 */
	public void apagar() {
		for (int i = 0; i < this.cables.length; i++){
			this.cables[i].apagar();
		}
		this.cableActivo = -1;
	}
}