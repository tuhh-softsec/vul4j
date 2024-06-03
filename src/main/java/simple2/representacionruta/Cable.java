/*
 * Created on 07-ago-2003
 *
 */
package simple2.representacionruta;

import java.awt.Color;

/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */
/**
 * Esta clase representa un cable de datos.
 * 
 */
public class Cable extends ElementoDibujable {

	/**
	 * Lista de las coordenadas de las rectas que componen el cable.
	 */
	protected int[] puntos = null;

	/**
	 * Crea una instancia de la clase.
	 * 
	 * @param dib
	 *            La superficie de dibujo sobre la que se dibujar este objeto.
	 * @param puntos
	 *            Las coordenadas de las rectas que componen el cable
	 */
	public Cable(InterfaceDibujo dib, int[] puntos) {
		super(dib);
		this.puntos = puntos;
	}

	/**
	 * Pinta el cable inactivo
	 */
	@Override
	protected void pintarInactivo() {
		int x = 0;
		while (x < (this.puntos.length - 2)) {
			int x1 = this.puntos[x];
			int y1 = this.puntos[x + 1];
			int x2 = this.puntos[x + 2];
			int y2 = this.puntos[x + 3];

			x += 2;
			this.dibujo.dibujarRecta(Color.BLACK, x1, y1, x2, y2);
		}

	}

	/**
	 * Pinta una punta de flecha al final del cable.
	 */
	protected void pintarFlechaFin() {
		int x = this.puntos.length;

		pintarFlecha(new int[] { this.puntos[x - 4], this.puntos[x - 3], this.puntos[x - 2], this.puntos[x - 1] });
	}

	/**
	 * Dibuja una punta de flecha al principio del cable.
	 */
	protected void pintarFlechaInicio() {
		pintarFlecha(new int[] { this.puntos[2], this.puntos[3], this.puntos[0], this.puntos[1] });
	}

	/**
	 * Pinta una punta de flecha al final de la linea indicada.
	 * 
	 * @param linea
	 *            Coordenadas de la linea
	 */
	private void pintarFlecha(int[] linea) {
		if (linea.length != 4) {
			return;
		}
		int x1 = 0;
		int x2 = 0;
		int y1 = 0;
		int y2 = 0;

		if (linea[0] == linea[2]) // es una lnea vertical
		{
			x1 = linea[0] - 5;
			x2 = linea[0] + 5;

			if (linea[1] < linea[3]) // La linea va hacia abajo
				y1 = linea[3] - 5;
			else
				y1 = linea[3] + 5;
			y2 = y1;
		} else if (linea[1] == linea[3]) // es una linea horizontal
		{
			y1 = linea[1] - 5;
			y2 = linea[1] + 5;

			if (linea[0] < linea[2]) // La linea va hacia la derecha
				x1 = linea[2] - 5;
			else
				x1 = linea[2] + 5;
			x2 = x1;
		} else {
			return;
		}

		this.dibujo.dibujarRecta(Color.RED, linea[2], linea[3], x1, y1);
		this.dibujo.dibujarRecta(Color.RED, linea[2], linea[3], x2, y2);
	}

	/**
	 * Pintar cable activo
	 */
	@Override
	protected void pintarActivo() {
		int x = 0;
		while (x < (this.puntos.length - 2)) {
			int x1 = this.puntos[x];
			int y1 = this.puntos[x + 1];
			int x2 = this.puntos[x + 2];
			int y2 = this.puntos[x + 3];

			x += 2;
			this.dibujo.dibujarRecta(Color.RED, x1, y1, x2, y2);
		}
	}
}
