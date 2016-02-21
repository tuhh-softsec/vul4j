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
		mp = new MemoriaPrincipal (memoriaPrincipal);
		memoriaControl = new MemoriaControl (memControl);
			
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
			_petLecturaMemoria = 0;
			_petEscrituraMemoria = 0;
		}

		if ((inst.GetWR () == 1) && (inst.GetRD () == 1))
		{
			_petEscrituraMemoria = 0;
			_petLecturaMemoria = 0;
		}
		else if (inst.GetWR () == 1)
		{
			_petEscrituraMemoria++;
			_petLecturaMemoria = 0;
		}
		else if (inst.GetRD () == 1)
		{
			_petLecturaMemoria++;
			_petEscrituraMemoria = 0;
		}
		else
		{
			_petLecturaMemoria = 0;
			_petEscrituraMemoria = 0;
		}
		if (_petLecturaMemoria > 1)
			_mbr = mp.LeerDato (_mar);
		if (_petEscrituraMemoria > 1)
			mp.EscribirDato (_mar, _mbr);
	}

	/**
	 * Leer MBR
	 * @return Devuelve _mbr
	 */
	private short LeerMBR ()
	{
		if (_petLecturaMemoria > 1)
			_mbr = mp.LeerDato (_mar);
		return _mbr;
	}
		
	/**
	 * Ejecuta el siguiente subciclo en la ruta de datos
	 */
	public void EjecutarSubciclo () throws SimulacionFinalizadaException
	{

		switch (subciclos % 4)
		{
			case 0: EjecutarSubciclo1();
					break;
			case 1: EjecutarSubciclo2();
					break;
			case 2: EjecutarSubciclo3();
					break;
			case 3: EjecutarSubciclo4();
					break;
		}
		subciclos++;
	}
		
	/**
	 * Ejecuta el primer subciclo de la microinstrucción.
	 * En él, lo que hace es cargar en RMC la microinstrucción
	 * situada en la dirección indicada por RDC en la memoria de control.
	 */	
	private void EjecutarSubciclo1() throws SimulacionFinalizadaException
	{
		if ( ( registros.LeerRegistro(BancoRegistros.IR) & (short) 0xF800) == (short) 0xF800)
		{			
			throw new SimulacionFinalizadaException ("Fin normal");
		}
			
		//Leemos la siguiente microinstrucción de la memoria de control
		rmc = memoriaControl.LeerMicroInstruccion (RDC);
			
		
		repRdd.DibujarCiclo1(rmc, RDC);
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
		ActualizarPeticionesMemoria (rmc);
		regA =registros.LeerRegistro (rmc.GetA ());
		regB =	registros.LeerRegistro (rmc.GetB ());
		
		repRdd.DibujarCiclo2(rmc, regA, regB);

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

		if (rmc.GetAMUX () == 1)
		{
			alu.Operar (rmc.GetALU (),  rmc.GetSH (), _mbr, regB);
		}
		else
		{
			alu.Operar (rmc.GetALU (),  rmc.GetSH (), regA, regB);
		}


		if (rmc.GetMAR () == 1)
			_mar = regB;
			
		repRdd.DibujarCiclo3(rmc, alu.LeerResultado(), _mar, _mbr, 
						alu.LeerC(), alu.LeerN(), alu.LeerZ());
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
		if (rmc.GetMBR () == 1)
		{
			_mbr = alu.LeerResultado ();
		}
		if (rmc.GetENC () == 1)
		{
			registros.EscribirRegistro (rmc.GetC (),  alu.LeerResultado());
		}
		//Opciones de salto.
		if (rmc.GetFIR () == 1)
		{
			RDC = (short) ((registros.LeerRegistro
				(BancoRegistros.IR) >> 11) & (0x1F));
		}
		else if (Bifurca (rmc.GetCOND()))
		{
			RDC = (short) rmc.GetADDR ();
		}
		else
		{
			RDC++;
		}
		repRdd.DibujarCiclo4(rmc, _mbr);
	}
		
	/**
	 * Detiene la simulación de la ruta de datos
	 */	
	public void Detener ()
	{
		repRdd.Detener();
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
			return (alu.LeerN () == 1);
		case 2:
			return (alu.LeerZ () == 1);
		case 3:
			return (alu.LeerC () == 1);
		case 4:
			return true;
		}
		return true;
	}
		
	/**
	 * Añade un listener para los cabios de memoria
	 * @param l El listener
	 */
	public void AddMemoryChangeListener (MemoryChangeListener l)
	{
		mp.AddMemoryChangeListener (l);
	}
	
	/**
	 * Añade un listener para los cambios de los registros.
	 * @l El listener
	 */	
	public void AddRegisterChangeListener (RegisterChangeListener l)
	{
		registros.AddRegisterChangeListener(l);
	}
	
	/**
	 * Establece la representación de ruta de datos a utilizar para dibujar.
	 * @param r La representacion
	 */
	public void SetRepresentacionRDD(IRepresentacionRDD r)
	{
		repRdd = r;
		repRdd.Clean();
	}

}
