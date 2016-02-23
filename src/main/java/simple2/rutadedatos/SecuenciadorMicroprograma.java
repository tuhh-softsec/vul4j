/*
 * Created on 07-ago-2003
 *
 */
package simple2.rutadedatos;

/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */
public class SecuenciadorMicroprograma {
	/**
	 * Almacena el contenido de bufferA
	 */
	short regA = 0;

	/**
	 * Almacena el contenido de bufferB
	 */
	short regB = 0;

	/**
	 * Almacena el contenido del registro de RDC
	 */
	private short RDC = 0;

	/**
	 * Almacena el contenido de MAR, el registro de direccionamiento de memoria
	 */
	private short _mar = 0;

	/**
	 * Almacena el contenido de MBR, el registro que contiene los datos leidos a
	 * escribir de memoria
	 */
	private short _mbr = 0;

	private int _petLecturaMemoria = 0;
	private int _petEscrituraMemoria = 0;

	/**
	 * Lleva la cuenta de los subciclos ejecutados
	 */
	private int subciclos = 0;

	/**
	 * Almacena el contenido de RMC,es decir, la microinstruccion a ejecutar
	 */
	private MicroInstruccion rmc = null;
	private MemoriaControl memoriaControl = null;
	private BancoRegistros registros = new BancoRegistros();
	private ALU alu = new ALU();
	private MemoriaPrincipal mp = null;
	private IRepresentacionRDD repRdd;

	/**
	 * Crea una una instancia de la clase
	 * 
	 * @param memoriaPrincipal
	 *            La memoria principal con su contenido inicial
	 * 
	 */
	public SecuenciadorMicroprograma(short[] memoriaPrincipal) {
		this.mp = new MemoriaPrincipal(memoriaPrincipal);
		this.memoriaControl = new MemoriaControl();

	}

	/**
	 * Actualiza las peticiones de memoria
	 * 
	 * @param inst
	 *            Microinstruccion que se quiere actualizar
	 */
	private void actualizarPeticionesMemoria(MicroInstruccion inst) {

		if ((inst.getMAR() == 1) || (inst.getMBR() == 1)) {
			this._petLecturaMemoria = 0;
			this._petEscrituraMemoria = 0;
		}

		if ((inst.getWR() == 1) && (inst.getRD() == 1)) {
			this._petEscrituraMemoria = 0;
			this._petLecturaMemoria = 0;
		} else if (inst.getWR() == 1) {
			this._petEscrituraMemoria++;
			this._petLecturaMemoria = 0;
		} else if (inst.getRD() == 1) {
			this._petLecturaMemoria++;
			this._petEscrituraMemoria = 0;
		} else {
			this._petLecturaMemoria = 0;
			this._petEscrituraMemoria = 0;
		}
		if (this._petLecturaMemoria > 1) {
			this._mbr = this.mp.leerDato(this._mar);
		}
		if (this._petEscrituraMemoria > 1) {
			this.mp.escribirDato(this._mar, this._mbr);
		}
	}

	/**
	 * Ejecuta el siguiente subciclo en la ruta de datos
	 */
	public void ejecutarSubciclo() throws SimulacionFinalizadaException {

		switch (this.subciclos % 4) {
		case 0:
			ejecutarSubciclo1();
			break;
		case 1:
			ejecutarSubciclo2();
			break;
		case 2:
			ejecutarSubciclo3();
			break;
		case 3:
			ejecutarSubciclo4();
			break;
		default:
			throw new AssertionError("Opcion no contemplada");
		}
		this.subciclos++;
	}

	/**
	 * Ejecuta el primer subciclo de la microinstrucción. En él, lo que hace es
	 * cargar en RMC la microinstrucción situada en la dirección indicada por
	 * RDC en la memoria de control.
	 */
	private void ejecutarSubciclo1() throws SimulacionFinalizadaException {
		if ((this.registros.getValorRegistro(BancoRegistros.IR) & (short) 0xF800) == (short) 0xF800) {
			throw new SimulacionFinalizadaException("Fin normal");
		}

		// Leemos la siguiente microinstrucción de la memoria de control
		this.rmc = this.memoriaControl.leerMicroInstruccion(this.RDC);

		this.repRdd.dibujarCiclo1(this.rmc, this.RDC);
	}

	/**
	 * Ejecuta el segundo subciclo de la microinstrucción. En él, se cargan el
	 * bufferA y bufferB los contenidos de los registros indicados en los campos
	 * A y B de la microinstrucción.
	 */
	private void ejecutarSubciclo2() {
		// Subciclo 2. Cargar en bufferA y bufferB los contenidos de
		// los registros correspondientes.
		actualizarPeticionesMemoria(this.rmc);
		this.regA = this.registros.getValorRegistro(this.rmc.getA());
		this.regB = this.registros.getValorRegistro(this.rmc.getB());

		this.repRdd.dibujarCiclo2(this.rmc, this.regA, this.regB);

	}

	/**
	 * Ejecuta el tercer subciclo de la microinstrucción. En él: se ejecuta la
	 * operación de la ALU (indicada en el campo ALU de la microinstrucción)
	 * Desplaza el resultado obtenido en la ALU en el registro SH. Si es
	 * necesario se carga el registro MAR con el contenido de bufferB.
	 */
	private void ejecutarSubciclo3() {
		/*
		 * Subciclo 3.Operación de la ALU. Esperamos a que la ALU y el registro
		 * de desplazamiento produzcan una salida estable, y si es necesario
		 * carga el registro MAR (directamente desde bufferB)
		 */

		if (this.rmc.getAMUX() == 1) {
			this.alu.operar(this.rmc.getALU(), this.rmc.getSH(), this._mbr, this.regB);
		} else {
			this.alu.operar(this.rmc.getALU(), this.rmc.getSH(), this.regA, this.regB);
		}

		if (this.rmc.getMAR() == 1)
			this._mar = this.regB;

		this.repRdd.dibujarCiclo3(this.rmc, this.alu.getResultado(), this._mar, this._mbr, this.alu.getC(),
				this.alu.getN(), this.alu.getZ());
	}

	/**
	 * Ejecuta el cuarto subciclo de la microinstrucción. En él: Almacenamos el
	 * contenido de SH en el banco de registros. Si es necesario copiamos SH a
	 * MBR. Obtenemos en RDC la dirección de la siguiente microinstrucción a
	 * ejecutar.
	 */
	private void ejecutarSubciclo4() {
		/*
		 * Subciclo 4. Almacenamos el contenido de SH en el banco de registros y
		 * si es necesario en MBR. Vemos la lógica de bifurcación
		 */
		if (this.rmc.getMBR() == 1) {
			this._mbr = this.alu.getResultado();
		}
		if (this.rmc.getENC() == 1) {
			this.registros.setValorRegistro(this.rmc.getC(), this.alu.getResultado());
		}
		// Opciones de salto.
		if (this.rmc.getFIR() == 1) {
			this.RDC = (short) ((this.registros.getValorRegistro(BancoRegistros.IR) >> 11) & (0x1F));
		} else if (bifurca(this.rmc.getCOND())) {
			this.RDC = (short) this.rmc.getADDR();
		} else {
			this.RDC++;
		}
		this.repRdd.dibujarCiclo4(this.rmc, this._mbr);
	}

	/**
	 * Detiene la simulación de la ruta de datos
	 */
	public void detener() {
		this.repRdd.detener();
	}

	/**
	 * Indica si se produce un salto (se bifurca) o no.
	 * 
	 * @param condicion
	 *            Numero de la condicion
	 * @return true si se bifurca, false en otro caso
	 */
	private boolean bifurca(int condicion) {
		switch (condicion) {
		case 0:
			return false;
		case 1:
			return (this.alu.getN() == 1);
		case 2:
			return (this.alu.getZ() == 1);
		case 3:
			return (this.alu.getC() == 1);
		case 4:
			return true;
		default:
			return true;
		}
	}

	/**
	 * Añade un listener para los cabios de memoria
	 * 
	 * @param l
	 *            El listener
	 */
	public void addMemoryChangeListener(MemoryChangeListener l) {
		this.mp.addMemoryChangeListener(l);
	}

	/**
	 * Añade un listener para los cambios de los registros.
	 * 
	 * @param l
	 *            El listener
	 */
	public void addRegisterChangeListener(RegisterChangeListener l) {
		this.registros.addRegisterChangeListener(l);
	}

	/**
	 * Establece la representación de ruta de datos a utilizar para dibujar.
	 * 
	 * @param r
	 *            La representacion
	 */
	public void setRepresentacionRDD(IRepresentacionRDD r) {
		this.repRdd = r;
		this.repRdd.clean();
	}

}
