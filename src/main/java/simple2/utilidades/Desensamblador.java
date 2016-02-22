/*
 * Created on 02-sep-2003
 *
 */
package simple2.utilidades;

/**
 * 
 * @author Montserrat Sotomayor Gonzalez
 *
 */
/**
 * 
 * Clase que pasandole una instrucción codificada devuelve la instrucción
 * original
 */
public final class Desensamblador {
	/**
	 * @param inst
	 *            Instrucción codificada
	 * @return Devuelve la instrucción original
	 */
	public static String desensamblar(short inst) {
		String cadena = "";
		short opcode;
		short parametro;
		opcode = (short) ((inst >>> 11) & 0x1F);
		parametro = (short) (inst & 0x07FF);
		switch (opcode) {
		case 1:
			cadena = "LODD " + parametro;
			break;
		case 2:
			cadena = "LODI " + parametro;
			break;
		case 3:
			cadena = "STOD " + parametro;
			break;
		case 4:
			cadena = "ADDD " + parametro;
			break;
		case 5:
			cadena = "ADDI " + parametro;
			break;
		case 6:
			cadena = "SUBD " + parametro;
			break;
		case 7:
			cadena = "SUBI " + parametro;
			break;
		case 8:
			cadena = "PUSH";
			break;
		case 9:
			cadena = "POP";
			break;
		case 10:
			cadena = "JNEG " + parametro;
			break;
		case 11:
			cadena = "JZER " + parametro;
			break;
		case 12:
			cadena = "JCAR " + parametro;
			break;
		case 13:
			cadena = "JUMP " + parametro;
			break;
		case 14:
			cadena = "CALL " + parametro;
			break;
		case 15:
			cadena = "RENT";
			break;
		case 31:
			cadena = "HALT";
			break;
		default:
			cadena = "no valido " + opcode + " " + parametro;

		}
		return cadena;
	}
};