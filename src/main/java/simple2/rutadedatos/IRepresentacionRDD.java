/*
 * Created on 18-ago-2003
 *
 */
package simple2.rutadedatos;

/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */
public interface IRepresentacionRDD {

	/**
	 * Vuelve a dibujar todos los elementos en la superficie de dibujo
	 */
	void ActualizarTodo();

	/**
	 * Activa los elementos activos durante el subciclo 1.
	 * 
	 * @param mic
	 *            La microinstrucción que se acaba de cargar.
	 */
	void DibujarCiclo1(MicroInstruccion mic, short rdc);

	/**
	 * Activa los elementos activos durante el subciclo 2.
	 * 
	 * @param mic
	 *            La microinstrucción actualmente en ejecución. Nos indica los
	 *            registros de origen.
	 * @param regA
	 *            El contenido de BufferA.
	 * @param regB
	 */
	void DibujarCiclo2(MicroInstruccion mic, short regA, short regB);

	/**
	 * Activa los elementos activos durante el subciclo 3.
	 * 
	 * @param mic
	 *            La microinstrucción en ejecución.
	 * @param vSH
	 *            El valor del registro SH.
	 * @param vMAR
	 *            El valor del registro MAR.
	 * @param vMBR
	 *            El valor del registro MBR.
	 * @param valorC
	 *            El valor de la salida C de la ALU.
	 * @param valorN
	 *            El valor de la salida N de la ALU.
	 * @param valorZ
	 *            El valor de la salida Z de la ALU.
	 */
	void DibujarCiclo3(MicroInstruccion mic, short vSH, short vMAR, short vMBR, int valorC, int valorN, int valorZ);

	/**
	 * Activa los elementos activos durante el subciclo 4.
	 * 
	 * @param mic
	 *            La microinstrucción en ejecución.
	 * @param vMBR
	 *            El valor del registro MBR.
	 */
	void DibujarCiclo4(MicroInstruccion mic, short vMBR);

	/**
	 * Apaga todos los elementos.
	 */
	void Clean();

	/**
	 * Apaga todos los elementos.
	 */
	void Detener();

}
