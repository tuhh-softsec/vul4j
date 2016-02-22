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
 * Esta clase se encarga de simular el funcionamiento de la ALU del ordenador
 * SIMPLE 2
 */
public class ALU {

	/**
	 * Campo para almacenar internamente el resultado de la ALU C (activo cuando
	 * hay acarreo)
	 */
	private int c;

	/**
	 * Campo para almacenar internamente el resultado de la ALU N (activo cuando
	 * el resultado es negativo)
	 */
	private int n;

	/**
	 * Campo para almacenar internamente el resultado de la ALU Z (activo cuando
	 * el resultado es cero)
	 */
	private int z;

	/**
	 * Campo para almacenar internamente el resultado de la ALU.
	 */
	private short resultado;

	/**
	 * Crea una instancia de la clase.
	 */
	public ALU() {
		super();
	}

	/**
	 * Comprueba si se produce arrastre
	 * 
	 * @param operandoA
	 *            Primer operando
	 * @param operandoB
	 *            Segundo operando
	 * @return Devuelve si hay arrastre o no
	 */
	private static boolean arrastre(short operandoA, short operandoB) {
		return (((operandoA + operandoB) > 0x7FFF) || ((operandoA + operandoB) < -32767));
	}

	/**
	 * Realiza una operación de la ALU. También actualiza las salidas N, Z, C.
	 * 
	 * @param operacion
	 *            El entero con la operación a realizar
	 * @param operacionSH
	 *            El entero indicando el desplazamiento a realizar en SH
	 * @param operandoA
	 *            El operandoA
	 * @param operandoB
	 *            El operando B
	 * @return El resultado tras pasar por SH de la operación.
	 */
	public short operar(int operacion, int operacionSH, short operandoA, short operandoB) {
		short retval = 0;
		this.c = 0;
		switch (operacion) {
		case 0:
			retval = (short) (operandoA + operandoB);
			if (arrastre(operandoA, operandoB))
				this.c = 1;
			break;
		case 1:
			retval = (short) (operandoA & operandoB);
			break;
		case 2:
			retval = operandoA;
			break;
		case 3:
			retval = (short) (~operandoA);
			break;
		case 4:
			retval = (short) (operandoA | operandoB);
			break;
		case 5:
			retval = (short) (operandoA ^ operandoB);
			break;
		default:
			throw new IllegalArgumentException("codigo de operación no válido" + operacion);
		}

		this.n = retval < 0 ? 1 : 0;
		this.z = retval == 0 ? 1 : 0;

		this.resultado = operarSH(operacionSH, retval);
		return this.resultado;
	}

	/**
	 * Realiza el desplazamiento de SH
	 * 
	 * @param op
	 *            El desplazamiento a realizar
	 * @param valor
	 *            El valor a desplazar
	 * @return valor desplazado segun op.
	 */
	private short operarSH(int op, short valor) {
		switch (op) {
		case 1:
			return (short) (valor >> 1);
		case 2:
			return (short) (valor << 1);
		default:
			return valor; // no hacemos operacion
		}
	}

	/**
	 * Obtiene el resultado de la última operación de la ALU.
	 * 
	 * @return El resultado de la última operación de la ALU.
	 */
	public short getResultado() {
		return this.resultado;
	}

	/**
	 * Obtiene el valor de C tras la última operación de la ALU.
	 * 
	 * @return El valor de C tras la última operación de la ALU.
	 */
	public int getC() {
		return this.c;
	}

	/**
	 * Obtiene el valor de N tras la última operación de la ALU.
	 * 
	 * @return El valor de N tras la última operación de la ALU.
	 */
	public int getN() {
		return this.n;
	}

	/**
	 * Obtiene el valor de Z tras la última operación de la ALU.
	 * 
	 * @return El valor de Z tras la última operación de la ALU.
	 */
	public int getZ() {
		return this.z;
	}

}
