/*
 * Created on 07-ago-2003
 *
 */
package simple2.rutadedatos;

/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */

public class MemoriaControl {
	/**
	 * Tamaño de la memoria de Contol
	 */
	public static int TAMANO = 1024;

	/**
	 * Array donde se guardan los datos de memoria
	 */
	private long[] memoria = null;

	/**
	 * Creamos una Memoria de Control
	 * 
	 * @return Memoria de Control que se le pasa por defecto
	 */
	public static long[] getMemoriaDefecto() {
		long[] mem = new long[TAMANO];

		mem[0] = 0x400000FF4000L; // Salto al microprograma de búsqueda y
									// decodificación
		// ;
		// ; Tabla de saltos
		mem[0x001] = 0x40000008C000L; // Salto al microprograma 1 (LODD n)
		mem[0x002] = 0x4000000DC000L; // Salto al microprograma 2 (LODI n)
		mem[0x003] = 0x40000012C000L; // Salto al microprograma 3 (STOD n)
		mem[0x004] = 0x40000017C000L; // Salto al microprograma 4 (ADDD n)
		mem[0x005] = 0x400000200000L; // Salto al microprograma 5 (ADDI n)
		mem[0x006] = 0x400000240000L; // Salto al microprograma 6 (SUBD n)
		mem[0x007] = 0x400000280000L; // Salto al microprograma 7 (SUBI n)
		mem[0x008] = 0x4000002C0000L; // Salto al microprograma 8 (PUSH)
		mem[0x009] = 0x400000300000L; // Salto al microprograma 9 (POP)
		mem[0x00A] = 0x400000340000L; // Salto al microprograma 10 (JNEG n)
		mem[0x00B] = 0x400000380000L; // Salto al microprograma 11 (JZER n)
		mem[0x00C] = 0x4000003C0000L; // Salto al microprograma 12 (JCAR n)
		mem[0x00D] = 0x400000400000L; // Salto al microprograma 13 (JUMP n)
		mem[0x00E] = 0x400000440000L; // Salto al microprograma 14 (CALL n)
		mem[0x00F] = 0x400000480000L; // Salto al microprograma 15 (RETN)
		// ;
		// ; Microprograma 1 (LODD n)
		mem[0x23] = 0x001A43000200L;
		mem[0x24] = 0x00C0A0000000L;
		mem[0x25] = 0xC05100FF0400L;
		// ;
		// ; Microprograma 2 (LODI n)
		mem[0x37] = 0x401143FF0200L;
		// ;
		// ; Microprograma 3 (STOD n)
		mem[0x4B] = 0x001A43000200L;
		mem[0x4C] = 0x0080A0000000L;
		mem[0x4D] = 0x010001000400L;
		mem[0x4E] = 0x002000000000L;
		mem[0x4F] = 0x402000FF0000L;
		// ;
		// ; Microprograma 4 (ADDD n)
		mem[0x5F] = 0x001A43000200L;
		mem[0x60] = 0x00C0A0000000L;
		mem[0x61] = 0x004000000000L;
		mem[0x62] = 0xC01110FF0000L;
		// ;
		// ; Microprograma 5 (ADDI n)
		mem[0x080] = 0x001A43000200L;
		mem[0x081] = 0x4011A1FF0000L;
		// ;
		// ; Microprograma 6 (SUBD n)
		mem[0x090] = 0x001A43000200L;
		mem[0x091] = 0x00C0A0000000L;
		mem[0x092] = 0x805A00000400L;
		mem[0x093] = 0x001A0A000600L;
		mem[0x094] = 0x001A6A000000L;
		mem[0x095] = 0x4011A1FF0000L;
		// ;
		// ; Microprograma 7 (SUBI n)
		mem[0x0A0] = 0x001A43000200L;
		mem[0x0A1] = 0x001A0A000600L;
		mem[0x0A2] = 0x001A6A000000L;
		mem[0x0A3] = 0x4011A1FF0000L;
		// ;
		// ; Microprograma 8 (PUSH)
		mem[0x0B0] = 0x001272000000L;
		mem[0x0B1] = 0x008020000000L;
		mem[0x0B2] = 0x010001000400L;
		mem[0x0B3] = 0x002000000000L;
		mem[0x0B4] = 0x402000FF0000L;
		// ;
		// ; Microprograma 9 (POP)
		mem[0x0C0] = 0x00C020000000L;
		mem[0x0C1] = 0x805100000400L;
		mem[0x0C2] = 0x401262FF0000L;
		// ;
		// ; Microprograma 10 (JNEG n)
		mem[0x0D0] = 0x100051400000L;
		mem[0x0D1] = 0x400000FF0000L;
		// ;
		// ; Microprograma 11 (JZER n)
		mem[0x0E0] = 0x200051400000L;
		mem[0x0E1] = 0x400000FF0000L;
		// ;
		// ; Microprograma 12 (JCAR n)
		mem[0x0F0] = 0x400000FF0000L;
		// ;
		// ; Microprograma 13 (JUMP n)
		mem[0x100] = 0x401043FF4200L;
		// ;
		// ; Microprograma 14 (CALL n)
		mem[0x110] = 0x001272000000L;
		mem[0x111] = 0x008020000000L;
		mem[0x112] = 0x010000000400L;
		mem[0x113] = 0x003043000200L;
		mem[0x114] = 0x402000FF4000L;
		// ;
		// ; Microprograma 15 (RETN)
		mem[0x120] = 0x00C020000000L;
		mem[0x121] = 0x805000000400L;
		mem[0x122] = 0x401262FF0000L;
		// ;
		// ; Microprograma de búsqueda y decodificación
		mem[0x3FC] = 0x001060000000L;
		mem[0x3FD] = 0x00C000000000L;
		mem[0x3FE] = 0x805300000400L;
		mem[0x3FF] = 0x000000002000L;

		return mem;
	}

	/*
	 * MemoriaControl01.dat 000 4000 00FF 4000; Salto al microprograma de
	 * búsqueda y decodificación ; ; Tabla de saltos 001 4000 0008 C000; Salto
	 * al microprograma 1 002 4000 000D C000; Salto al microprograma 2 003 4000
	 * 0012 C000; Salto al microprograma 3 004 4000 0017 C000; Salto al
	 * microprograma 4 ; ; Microprograma 1 (LODD n) 023 001A 4300 0200 024 00C0
	 * A000 0000 025 0040 0000 0000 026 C011 00FF 0400 ; ; Microprograma 2 (LODI
	 * n) 037 4011 43FF 0200 ; ; Microprograma 3 (STOD n) 04B 001A 4300 0200 04C
	 * 0080 A000 0000 04D 0100 0100 0400 04E 0020 0000 0000 04F 4020 00FF 0000 ;
	 * ; Microprograma 4 (ADDD n) 05F 001A 4300 0200 060 00C0 A000 0000 061 0040
	 * 0000 0000 062 C011 10FF 0000 ; ; Microprograma de búsqueda y
	 * decodificación 3FC 0010 6000 0000 3FD 00C0 0000 0000 3FE 8053 0000 0400
	 * 3FF 0000 0000 2000
	 */

	/**
	 * Creamos una instancia de la clase.Si existe una memoria inicial de
	 * control esa es Memoria de Control y si no existe la Memoria de Control es
	 * la Memoria de Control por Defecto
	 * 
	 * @param memoriaInicial
	 *            Le pasamos la Memoria de Control inicial
	 */
	public MemoriaControl(long[] memoriaInicial) {
		if ((memoriaInicial == null) || (memoriaInicial.length != TAMANO)) {
			this.memoria = MemoriaControl.getMemoriaDefecto();
		} else {
			this.memoria = memoriaInicial;
		}
	}

	/**
	 * Lee un dato de la memoria de Control
	 * 
	 * @param direccion
	 *            La direccion en la que leerá el dato
	 * 
	 * @return El dato solicitado
	 */
	public long leerDato(short direccion) {
		return this.memoria[direccion];
	}

	/**
	 * Escribe un dato en la memoria de Control.
	 * 
	 * @param direccion
	 *            La direccion en la que se escribira el dato
	 * @param dato
	 *            El dato a escribir
	 */
	public void escribirDato(short direccion, long dato) {
		this.memoria[direccion] = dato;
	}

	/**
	 * Lee una microinstruccion
	 * 
	 * @param direccion
	 *            La direccion en la que leerá la microinstruccion
	 * 
	 * @return La microinstruccion solicitada
	 */
	public MicroInstruccion leerMicroInstruccion(short direccion) {
		return new MicroInstruccion(this.memoria[direccion]);
	}

}
