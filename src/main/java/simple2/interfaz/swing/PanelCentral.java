/*
 * Created on 05-jul-2003
 *
 */

package simple2.interfaz.swing;
import java.awt.CardLayout;
import java.awt.Color;
import java.util.Vector;

import simple2.representacionruta.RepresentacionRDD;
/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */
import simple2.rutadedatos.SecuenciadorMicroprograma;

/**
* Panel que contiene todos los paneles que se van a utilizar en el 
* programa  
*/
public class PanelCentral extends javax.swing.JPanel{	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4498144025637867213L;

	/**
	* Panel para introducir los patrones.
	*/
	private PanelNuevo panelnuevo;	
	
	/**
	 * Panel donde esta el esquema de la Unidad de Control
	 */
	private PanelEsquema panelesquema;	
	
	/**
	 * Panel que contiene el valor de la memoria
	 */
	private PanelMemoria panelmemoria;
	
	/**
	 * Panel que contiene la ayuda
	 */
	private PanelAyuda panelayuda;
	
	/**
	 * Hilo que se crea para ejecutar la simulacion del simple2
	 */
	HiloEjecucion hilo =null;
	
	/**
	 * Crea una instancia de la clase. Crea todos los paneles que se utilizaran 
	 * en este programa. 
	 */
	public PanelCentral() {
		super();
				
		this.setLayout(new CardLayout());
		
		this.panelnuevo= new PanelNuevo ();
		
		this.panelmemoria=new PanelMemoria();
		
		this.panelesquema=new PanelEsquema();
		
		this.panelayuda=new PanelAyuda();
		
		
		this.panelesquema.pdibujo.setBackground(Color.WHITE);
		this.add (this.panelnuevo, "NUEVO");
		this.add(this.panelmemoria,"MEMORIA");
		this.add (this.panelesquema, "ESQUEMA" );
		this.add (this.panelayuda,"AYUDA");
		this.panelesquema.pdibujo.clean();
		this.panelesquema.pdibujo.refresh();
	}
	
	/**
	 * Limpia el dibujo del panel de esquema
	 *
	 */
	public void clean()
	{
		this.panelesquema.pdibujo.clean();
	}
	
	/**
	* Limpia todas las areas de texto del Panel Nuevo.
	*/
	public void limpiar()
		{
		this.panelnuevo.texto.setText("");
		this.panelnuevo.resultado.setText("");
		this.panelnuevo.errores.setText("");
		}
		
	/**
	 * Limpia las areas de texto donde se almacenaran el codigo codificado
	 * y los errores. 
	 */
	public void limpiarEjecutar()
		{
		this.panelnuevo.resultado.setText("");
		this.panelnuevo.errores.setText("");
		}
		
	/**
	* Muestra el panel seleccionado.
	*
	* @param panel El panel a mostrar.
	*/ 
	public void verPanel (String panel)
		{
		
		CardLayout c1 = (CardLayout) this.getLayout();
		c1.show (this, panel);
		}
		
	/**
	 * Funcion que coge los datos que se quieren codificar(el area de texto
	 * texto del panel nuevo)
	 *  
	 * @return los datos que se encuentren en el area de texto de PanelNuevo
	 */
	String CogerDatos()
		{
		return this.panelnuevo.texto.getText();
		}
	
	/**
	 * Escribe en el panel Nuevo en el area de texto resultado la instruccion y su 
	 * codificación correspondiente
	 * 
	 * @param codigo_limpio El codigo sin comentarios y sin lineas en blanco
	 * @param ensamblado El código limpio ensamblado
	 * @param instruccionesTotales Numero de instrucciones totales
	 */
	public void Escribir (Vector<String> codigo_limpio,short[] ensamblado,int instruccionesTotales){
		this.panelnuevo.Escribir (codigo_limpio,ensamblado,instruccionesTotales);	
		}
		
	/**
	 * LLama a Errores del panel nuevo 
	 * 
	 * @param errores Cadena que contiene los errores producidos en el ensamblado
	 */
	public void Errores (String errores){
		this.panelnuevo.Errores (errores);	
	}

	/**
	 * Limpia el panel esquema para poder dibujar de nuevo en él
	 *
	 */
	public void limpiarEsquema()
		{
		this.panelesquema.pdibujo.clean();
		this.panelesquema.pdibujo.refresh();
		}
	
	/**
	 * Escribir en el area de texto del PanelMemoria los datos de la memoria 
	 * 
	 * @param ensamblado Codigo ensamblado correctamente
	 * @param instruccionesTotales Numero de instrucciones totales 
	 */	
	public void EscribirMemoria(short[] ensamblado,int instruccionesTotales,Vector<String> codigo_limpio){
		this.panelmemoria.EscribirMemoria(ensamblado,instruccionesTotales,codigo_limpio);
	}
	
	/**
	 * En el panel donde se escribe el código a ensamblar no se puede 
	 * editar(modificar)  
	 *
	 */
	public void NoEditable(){
		this.panelnuevo.texto.setEditable(false);
	}
	
	/**
	 * En el panel donde se escribe el código a ensamblar se puede 
	 * editar(modificar)
	 */
	public void Editable(){
			this.panelnuevo.texto.setEditable(true);
	}
	
	/**
	 * Se encarga de simular el Simple2
	 * @param ensamblado Codigo ensamblado(Valores iniciales de la MemoriaPrincipal)
	 */
	public void Principal(short[] ensamblado,int tiempo){
		RepresentacionRDD repRdd = new RepresentacionRDD (this.panelesquema.pdibujo);
		this.panelesquema.pdibujo.clean();
		//Almacenamiento del dibujo cuando cambia de tamaño
		this.panelesquema.pdibujo.setRepresentacionRDD(repRdd);
		//Crear un nuevo "secuenciador" con el short[] creado al ensamblar y micromemoria null
		SecuenciadorMicroprograma mic =	new SecuenciadorMicroprograma (ensamblado,null);

		//Añadimos el listener de memoria (si hay)
		mic.AddMemoryChangeListener (this.panelmemoria);
	
		//Asociamos la representacion de datos.
		mic.SetRepresentacionRDD(repRdd);
		mic.AddRegisterChangeListener(repRdd);
		this.hilo = new HiloEjecucion (mic);
		this.hilo.SetTSubciclo(tiempo);
		this.hilo.start();	
	}
	
	/**
	 * Parar el hilo de ejecucion
	 *
	 */
	public void Parar()
	{
		if(this.hilo!=null)
			this.hilo.CambiarPausado();
	}
	
	/**
	 * Función que nos dice si el hilo esta activo
	 * @return True hilo activo
	 * 		   False hilo inactivo
	 */
	public boolean HiloActivo()
	{
		return ((this.hilo!=null) && this.hilo.isAlive());
	}
	
	/**
	 * Detiene el hilo
	 *
	 */
	public void Fin()
	{
		if(this.hilo!=null)
			this.hilo.detener();
	}
	
	/**
	 * Detiene el hilo
	 *
	 */
	public void Activar()
		{
		if(this.hilo!=null)
			this.hilo.detener();
		}
	
}
