/*
 * Created on 07-ago-2003
 *
 */
package simple2.rutadedatos;
/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */
public class SecuenciadorMicroprograma
{
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
	 * Almacena el contenido de MAR, el registro de
	 * direccionamiento de memoria
	 */
	private short _mar = 0;
		
	/**
	 * Almacena el contenido de MBR, el registro que contiene
	 * los datos leidos a escribir de memoria
	 */
	private short _mbr = 0;
		
	private int _petLecturaMemoria = 0;
	private int _petEscrituraMemoria = 0;
		
	/**
	 * Lleva la cuenta de los subciclos ejecutados
	 */
	private int subciclos = 0;
		
	/**
	 * Almacena el contenido de RMC,es decir, la
	 * microinstruccion a ejecutar
	 */
	private MicroInstruccion rmc = null;
	private MemoriaControl memoriaControl = null;
	private BancoRegistros registros = new BancoRegistros ();
	private ALU alu = new ALU ();
	private MemoriaPrincipal mp = null;
	private IRepresentacionRDD repRdd; 
		
	/**
	 * Crea una una instancia de la clase
	 * 
	 * @param memoriaPrincipal La memoria principal con su contenido inicial
	 * 
	 * @return La memoria de control con su contenido inicial
	 */	
	public SecuenciadorMicroprograma (short[] memoriaPrincipal, long[] memControl)
	{
		this.mp = new MemoriaPrincipal (memoriaPrincipal);
		this.memoriaControl = new MemoriaControl (memControl);
			
	}
		
	/**
	 * Actualiza las peticiones de memoria
	 * 
	 * @param inst Microinstruccion que se quiere actualizar
	 */	
	private void ActualizarPeticionesMemoria (MicroInstruccion  inst)
	{

		if ((inst.GetMAR () == 1) || (inst.GetMBR () == 1))
		{
			this._petLecturaMemoria = 0;
			this._petEscrituraMemoria = 0;
		}

		if ((inst.GetWR () == 1) && (inst.GetRD () == 1))
		{
			this._petEscrituraMemoria = 0;
			this._petLecturaMemoria = 0;
		}
		else if (inst.GetWR () == 1)
		{
			this._petEscrituraMemoria++;
			this._petLecturaMemoria = 0;
		}
		else if (inst.GetRD () == 1)
		{
			this._petLecturaMemoria++;
			this._petEscrituraMemoria = 0;
		}
		else
		{
			this._petLecturaMemoria = 0;
			this._petEscrituraMemoria = 0;
		}
		if (this._petLecturaMemoria > 1) {
			this._mbr = this.mp.LeerDato (this._mar);
		}
		if (this._petEscrituraMemoria > 1) {
			this.mp.EscribirDato (this._mar, this._mbr);
		}
	}


		
	/**
	 * Ejecuta el siguiente subciclo en la ruta de datos
	 */
	public void EjecutarSubciclo () throws SimulacionFinalizadaException
	{

		switch (this.subciclos % 4)
		{
			case 0: EjecutarSubciclo1();
					break;
			case 1: EjecutarSubciclo2();
					break;
			case 2: EjecutarSubciclo3();
					break;
			case 3: EjecutarSubciclo4();
					break;
			default:
				throw new AssertionError("Opcion no contemplada");
		}
		this.subciclos++;
	}
		
	/**
	 * Ejecuta el primer subciclo de la microinstrucción.
	 * En él, lo que hace es cargar en RMC la microinstrucción
	 * situada en la dirección indicada por RDC en la memoria de control.
	 */	
	private void EjecutarSubciclo1() throws SimulacionFinalizadaException
	{
		if ( ( this.registros.LeerRegistro(BancoRegistros.IR) & (short) 0xF800) == (short) 0xF800)
		{			
			throw new SimulacionFinalizadaException ("Fin normal");
		}
			
		//Leemos la siguiente microinstrucción de la memoria de control
		this.rmc = this.memoriaControl.LeerMicroInstruccion (this.RDC);
			
		
		this.repRdd.DibujarCiclo1(this.rmc, this.RDC);
	}
		
	/**
	 * Ejecuta el segundo subciclo de la microinstrucción.
	 * En él, se cargan el bufferA y bufferB los contenidos de los registros
	 * indicados en los campos A y B de la microinstrucción.
	 */	
	private void EjecutarSubciclo2()
	{
		//Subciclo 2. Cargar en bufferA y bufferB los contenidos de
		// los registros correspondientes.
		ActualizarPeticionesMemoria (this.rmc);
		this.regA =this.registros.LeerRegistro (this.rmc.GetA ());
		this.regB =	this.registros.LeerRegistro (this.rmc.GetB ());
		
		this.repRdd.DibujarCiclo2(this.rmc, this.regA, this.regB);

	}
		
	/**
	 * Ejecuta el tercer subciclo de la microinstrucción.
	 * En él:
	 * 		se ejecuta la operación de la ALU (indicada en el campo ALU de la microinstrucción)
	 * 		Desplaza el resultado obtenido en la ALU en el registro SH.
	 * 		Si es necesario se carga el registro MAR con el contenido de bufferB.
	 */	
	private void EjecutarSubciclo3()
	{
		/*
		* Subciclo 3.Operación de la ALU.
		* Esperamos a que la ALU y el registro de desplazamiento
		* produzcan una salida estable, y si es necesario carga 
		* el registro MAR (directamente desde bufferB)
		*/

		if (this.rmc.GetAMUX () == 1)
		{
			this.alu.Operar (this.rmc.GetALU (),  this.rmc.GetSH (), this._mbr, this.regB);
		}
		else
		{
			this.alu.Operar (this.rmc.GetALU (),  this.rmc.GetSH (), this.regA, this.regB);
		}


		if (this.rmc.GetMAR () == 1)
			this._mar = this.regB;
			
		this.repRdd.DibujarCiclo3(this.rmc, this.alu.LeerResultado(), this._mar, this._mbr, 
						this.alu.LeerC(), this.alu.LeerN(), this.alu.LeerZ());
	}
		
	/**
	 * Ejecuta el cuarto subciclo de la microinstrucción.
	 * En él:
	 * 		Almacenamos el contenido de SH en el banco de registros.
	 * 		Si es necesario copiamos SH a MBR.
	 * 		Obtenemos en RDC la dirección de la siguiente microinstrucción
	 *	 	a ejecutar.
	 */	
	private void EjecutarSubciclo4()
	{
	/*
	 * Subciclo 4.
	 * Almacenamos el contenido de SH en el banco de registros y si es
	 * necesario en MBR.
	 * Vemos la lógica de bifurcación
	 */               
		if (this.rmc.GetMBR () == 1)
		{
			this._mbr = this.alu.LeerResultado ();
		}
		if (this.rmc.GetENC () == 1)
		{
			this.registros.EscribirRegistro (this.rmc.GetC (),  this.alu.LeerResultado());
		}
		//Opciones de salto.
		if (this.rmc.GetFIR () == 1)
		{
			this.RDC = (short) ((this.registros.LeerRegistro
				(BancoRegistros.IR) >> 11) & (0x1F));
		}
		else if (Bifurca (this.rmc.GetCOND()))
		{
			this.RDC = (short) this.rmc.GetADDR ();
		}
		else
		{
			this.RDC++;
		}
		this.repRdd.DibujarCiclo4(this.rmc, this._mbr);
	}
		
	/**
	 * Detiene la simulación de la ruta de datos
	 */	
	public void Detener ()
	{
		this.repRdd.Detener();
	}

	/**
	 * Indica si se produce un salto (se bifurca) o no.
	 * @param condicion Numero de la condicion
	 * @return true si se bifurca, false en otro caso
	 */	
	private boolean Bifurca (int condicion)
	{
		switch (condicion)
		{
		case 0:
			return false;
		case 1:
			return (this.alu.LeerN () == 1);
		case 2:
			return (this.alu.LeerZ () == 1);
		case 3:
			return (this.alu.LeerC () == 1);
		case 4:
			return true;
		default:
			return true;
		}
	}
		
	/**
	 * Añade un listener para los cabios de memoria
	 * @param l El listener
	 */
	public void AddMemoryChangeListener (MemoryChangeListener l)
	{
		this.mp.AddMemoryChangeListener (l);
	}
	
	/**
	 * Añade un listener para los cambios de los registros.
	 * @l El listener
	 */	
	public void AddRegisterChangeListener (RegisterChangeListener l)
	{
		this.registros.AddRegisterChangeListener(l);
	}
	
	/**
	 * Establece la representación de ruta de datos a utilizar para dibujar.
	 * @param r La representacion
	 */
	public void SetRepresentacionRDD(IRepresentacionRDD r)
	{
		this.repRdd = r;
		this.repRdd.Clean();
	}

}
