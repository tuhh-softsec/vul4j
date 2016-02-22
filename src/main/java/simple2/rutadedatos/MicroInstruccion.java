/*
 * Created on 07-ago-2003
 *
 */
package simple2.rutadedatos;

/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */

/**
 * Esta clase representa a una microinstruccion del ordenador Simple2.
 */
public class MicroInstruccion {

	/**
	 * La microinstrucción en binario, como un entero largo sin signo
	 */
	private long instruccion;

	/**
	 * Crea una instancia de la clase a partir de un valor.
	 * 
	 * @param valor
	 *            El valor binario de la microinstrucción.
	 */
	public MicroInstruccion(long valor) {
		this.instruccion = valor;
	}

	/**
	 * Obtiene la microinstrucción como un entero largo sin signo.
	 * 
	 * @return La microinstruccion como un entero sin signo
	 */
	public long toLong() {
		return this.instruccion;
	}

	/**
	 * Obtiene la operación de la ALU
	 * 
	 * @return La operacion de la ALU
	 */
	public int getALU() {
		return getBits(9, 4);
	}

	/**
	 * Obtiene el valor del campo FIR
	 * 
	 * @return El valor del campo FIR
	 */
	public int getFIR() {
		return getBits(13, 1);
	}

	/**
	 * Obtiene el valor del campo ADDR.
	 * 
	 * @return El valor del campo ADDR.
	 */
	public int getADDR() {
		return getBits(14, 10);
	}

	/**
	 * Obtiene el valor del campo A.
	 * 
	 * @return El valor del campo A.
	 */
	public int getA() {
		return getBits(24, 4);
	}

	/**
	 * Obtiene el valor del campo B.
	 * 
	 * @return El valor del campo B.
	 */
	public int getB() {
		return getBits(28, 4);
	}

	/**
	 * Obtiene el valor del campo C.
	 * 
	 * @return El valor del campo C.
	 */
	public int getC() {
		return getBits(32, 4);
	}

	/**
	 * Obtiene el valor del campo ENC.
	 * 
	 * @return El valor del campo ENC.
	 */
	public int getENC() {
		return getBits(36, 1);
	}

	/**
	 * Obtiene el valor del campo WR.
	 * 
	 * @return El valor del campo WR.
	 */
	public int getWR() {
		return getBits(37, 1);
	}

	/**
	 * Obtiene el valor del campo RD.
	 * 
	 * @return El valor del campo RD
	 */
	public int getRD() {
		return getBits(38, 1);
	}

	/**
	 * Obtiene el valor del campo MAR.
	 * 
	 * @return El valor del campo MAR
	 */
	public int getMAR() {
		return getBits(39, 1);
	}

	/**
	 * Obtiene el valor del campo MBR.
	 * 
	 * @return El valor del campo MBR
	 */
	public int getMBR() {
		return getBits(40, 1);
	}

	/**
	 * Obtiene el valor del campo SH.
	 * 
	 * @return El valor del campo SH
	 */
	public int getSH() {
		return getBits(41, 3);
	}

	/**
	 * Obtiene el valor del campo COND.
	 * 
	 * @return El valor del campo COND
	 */
	public int getCOND() {
		return getBits(44, 3);
	}

	/**
	 * Obtiene el valor del campo AMUX.
	 * 
	 * @return El valor del campo AMUX
	 */
	public int getAMUX() {
		return getBits(47, 1);
	}

	/**
	 * Obtiene el valor de un campo en una posición y de un tamaño dados.
	 * 
	 * @param b
	 *            Posición del primer bit del campo.
	 * 
	 * @param count
	 *            Número de bits que componen el campo.
	 */
	private int getBits(int b, int count) {
		long mask = 0xFFFFFFFFFFFFFFFFL >>> (64 - count);
		mask = mask << b;
		long resultado = (this.instruccion & mask) >> b;
		return (int) resultado;
	}

	/**
	 * Conversores a cadenas representando enteros en Hexadecimal
	 * 
	 * @return Devuelve la cadena que representa en hexadecimal
	 */
	public String toHexString() {
		String tmp = "" + Long.toHexString(this.instruccion);
		while (tmp.length() < 12){
			tmp = "0" + tmp;
		}
		String ret = tmp.substring(0, 4) + " " + tmp.substring(4, 8) + " " + tmp.substring(8);
		ret = ret.toUpperCase();
		return ret;
	}

}
