/*
 * Created on 28-jul-2003
 *
 */
package simple2.ensamblador;
import java.util.Hashtable;
/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */
import java.util.Vector;

/** 
 * Esta clase es la encargada de ensamblar el codigo fuente.
 * Para ello primero hace una pasada para verificar sintaxis y verificar otros tipos de errores.
 * Si todas la verificaciones tienen éxito comienza la traduccion de las instrucciones.
 */
public class Ejecutar extends InstruccionGeneral{
	
	/**
	* Tabla hash que contiene los mnemonicos de las intrucciones y una instancia del tipo de clase 
	* a la que pertenece la instrucion.
	*/
	private Hashtable instrucciones;
	
	
	/**
	 * Instancia de la clase.
	 */
	public Ejecutar(){
		
	}
	
	/**
	 * Se encarga de llamar a funciones para limpiar el código de comentarios y lineas 
	 * en blanco
	 * 
	 * @param datos Codigo que hemos escrito para ensamblar
	 * @return Nos devuelve el código limpiado si no tiene errores
	 */
	public Vector Comprobar(String datos) {
		Vector v= null;
		Vector codigo_limpio = null;
		String errores = "";
		crearHashInstrucciones ();
		String[] salida=null;
		String[] ensamblado=null;
		
		v= SepararEnVector(datos);
		try
			{
			codigo_limpio= PrimeraPasada(v);
			}
		catch (ErrorCodigoException ex)
			{
			errores += ex.getMessage();
			}
		return codigo_limpio;
	}
	
	/**
	 * Se encarga de encontrar errores en el código
	 * 
	 * @param datos Codigo que hemos escrito para ensamblar
	 * @return Nos devuelve los errores producidos al escribir el código
	 */
	public String EjecutarErrores(String datos) {
			Vector v= null;
			Vector codigo_limpio = null;
			String errores = "";
			crearHashInstrucciones ();
			String[] salida=null;
			String[] ensamblado=null;
		
			v= SepararEnVector(datos);
			try
				{
				codigo_limpio= PrimeraPasada(v);
				}
			catch (ErrorCodigoException ex)
				{
				errores += ex.getMessage();
				}
			return errores;
		}
		
	/**
	 * Ensambla el código que ha sido limpiado
	 * 
	 * @param codigo_limpio Código sin comentarios y sin lineas en blanco
	 * @return Devuelve el código ensamblado
	 */
	public short[] EnsamblarCodigo(Vector codigo_limpio) {
		String errores = "";
		crearHashInstrucciones ();
		short[] ensamblado=null;
		ensamblado = ensamblar (codigo_limpio);
		return ensamblado;
	}
	
	/**
	* Crea una tabla hash con todas las intrucciones del repertorio.
	*/ 
	private void crearHashInstrucciones ()
	{
		instrucciones = new Hashtable ();
		
		InstruccionGeneral aritmeticas = new InstruccionAritmetica();
		InstruccionGeneral  saltos= new InstruccionSalto ();
		InstruccionGeneral sinparametros = new InstruccionSinParametros();

		instrucciones.put ("LODD", aritmeticas);
		instrucciones.put ("LODI", aritmeticas);
		instrucciones.put ("STOD", aritmeticas);
		instrucciones.put ("ADDD", aritmeticas);
		instrucciones.put ("ADDI", aritmeticas);
		instrucciones.put ("SUBD", aritmeticas);
		instrucciones.put ("SUBI", aritmeticas);
		instrucciones.put ("PUSH", sinparametros);
		instrucciones.put ("POP", sinparametros);
		instrucciones.put ("RETN", sinparametros);
		instrucciones.put ("HALT", sinparametros);
		instrucciones.put ("JNEG", saltos);
		instrucciones.put ("JZER", saltos);
		instrucciones.put ("JCAR", saltos);
		instrucciones.put ("JUMP", saltos);
		instrucciones.put ("CALL", saltos);
	}
	
	/**
	 * Separa las lineas de código introducido en Vectores
	 * 
	 * @param datos Codigo que hemos escrito para ensamblar
	 * @return Devuelve el código en un Vector
	 */
	public Vector SepararEnVector(String datos){
		Vector v=new Vector();
		int s=0;
		String texto="";
		while(s!=datos.length())//Mientras no termine de leer todo el texto
		{
			if(datos.charAt(s)=='\n'){
				v.addElement(texto);
				texto="";
			}
			else texto=texto+datos.charAt(s);
			s++;
		}
		if(texto.length()>0){
			v.addElement(texto);
		}
		
		s=0;
		return v; 
	}
	
	/** 
	* Actualmente devuelve 0
	* 
	* @param instruccion instrucion a codificar
	* @param etiquetas etiquetas encontradas en el código
	* @param linea linea a codificar
	* @return  0
	*/
	public short codificar (String instruccion, int linea)
		{
			return 0;
		}
	
	
	/** Comprueba si una instruccion es valida y si no lo es lanza una excepcion.
	  *
      * @param instruccion instruccion a validar
      * @param linea linea
      * @throws ErrorCodigoException si instruccion no valida
      * @return  cadena de texto
	  */
	public String validar (String instruccion,int linea) throws ErrorCodigoException
		{
			return "";	
		}		

	/** 
	* Transforma una cadena de texto en un vector en el que
	* cada elemento es una línea del texto.
	* @param instrucciones a meter en el vector
	* @return  vector con todas las instrucciones
	*/
	public Vector stringToVector (String instrucciones)
		{
			Vector v0 =  new Vector();
			int first = 0;
			int last = 0;
		
			last = instrucciones.indexOf ('\n', first);
			while (last != -1)
			{
				String inst = instrucciones.substring(first, last);
				v0.add (inst);
				first = last+1;
				last = instrucciones.indexOf('\n', first);
			}	
			return v0;
		}
	
	/** 
	* Recorre el vector que contiene las instrucciones válidas y las codifica.
	*
	* @return Un array de enteros que corresponden a la traduccion de todas
	* las instrucciones.
	* @param vectorInstrucciones vector con todas las instrucciones a ensamblar
	*/
	public short[] ensamblar(Vector vectorInstrucciones)
		{
		InstruccionGeneral x;
		String inst;
		String in;

		int instruccionesTotales = vectorInstrucciones.size();
		short[] out = new short[instruccionesTotales];
		int i;
		
		for (i = 0; i < instruccionesTotales; i++)
		{
			inst = (String) vectorInstrucciones.get(i);
			
			//coge el mnemonico de la instruccion (hasta el primer espacio o la intruccion entera)
			if (inst.indexOf (' ') != -1)
				in = inst.substring (0, inst.indexOf(' '));
			else
				in = inst;
			
			//extrae de la tabla de instrucciones la instancia correspondiente al mnemonico	
			x = ((InstruccionGeneral)instrucciones.get(in));
			
			//codifica la instrucion 
			out[i] = x.codificar (inst, i);	
		}		
		return out;
	}
		
	/** 
	* Quita los comentarios y los espacios de la instruccion para dejala
	* "limpia" para poderla codificar.
	* 
	* @param instruc instrución.
	* @return  instrucción limpia
	*/
	public static String quitarComentariosYEspacios (String instruc)
		{
		int aux=0;
		// Ponemos a mayúsculas y quitamos los espacios del principio y final
		instruc = instruc.toUpperCase ().trim ();
		// Quitamos lineas en blanco y comentarios.

		aux = instruc.indexOf(";");
		
		if (aux > -1)
			instruc = instruc.substring (0,aux);
		
		instruc = instruc.trim();

		boolean separador = false;
		StringBuffer buffer = new StringBuffer();
		int i = 0;
		 
		// Limpiamos un poco los separadores. Un grupo de ellos pasa a 
		// ser ' '.
		
		for (i = 0; i < instruc.length(); i++)
		{
			if (esSeparador (instruc.charAt(i)))
			{
				if (!separador)
				{
					separador = true;
					buffer.append (' ');
				}
			}
			else
			{
				separador = false;
				buffer.append (instruc.charAt(i));
			}
		}					
		return buffer.toString();
	}
	
	
	/**
	 * Verifica si hay errores en el código.
	 * Realiza el desarrollo de las macros y los includes de los ficheros.
	 * 
	 * @param origen Vector que contiene las fuentes del programa.
	 * @return una cadena vacía -Si durante la verificacion del código no se han producido errores.
	 * Una cadena no vacía que contiene los errores -En caso de que haya algún error.
	 * @throws ErrorCodigoException si se encuentra algun error, la excepcion con tiene un mensaje con el error 
	 * producido.	
	 */
	public Vector PrimeraPasada (Vector origen) throws ErrorCodigoException
		{
		String instruc="";
		String errores = "";
		InstruccionGeneral n;
		String in;
		int aux;
		int l_fichero, l_salida;
		
		l_fichero = 0;
		Vector vectorInstrucciones = new Vector();
		
		int l_fichero2=0;
		int l_base=0;
				
		for (l_fichero = 1; l_fichero <origen.size()+1; l_fichero++)
			{
			l_fichero2 = l_fichero + l_base;
			//lee la siguiente linea del fichero para analizarla
			instruc = (String) origen.get(l_fichero-1);
			
			instruc = quitarComentariosYEspacios (instruc);
					
			//Quita las lineas en blanco
			if (instruc.equals(""))
				continue;
			
			//coge la primera parte de la instruccion. (hasta el primer espacio o paréntesis).
			if (instruc.indexOf(' ') != -1)
				in = instruc.substring (0, instruc.indexOf(' '));
			else
				in = instruc;
							
			n = (InstruccionGeneral) instrucciones.get(in);
			String salida_ = "";
			if (n != null)
				{
				salida_ = n.validar (instruc, l_fichero);
				vectorInstrucciones.add (instruc);
				if(!salida_.equals(""))
					errores=errores+salida_;
				}
			else {
				errores=errores+ "Linea: " + l_fichero + ". No existe la instruccion\n";
				} 
				
		}	
		if (errores.equals("")){
			if(instruc.compareTo("HALT")!=0)
				vectorInstrucciones.add ("HALT");
			return vectorInstrucciones;
		}
		else
			throw new ErrorCodigoException (errores);
					
		}
		
		
	}
