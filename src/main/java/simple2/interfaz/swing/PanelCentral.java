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
		
		panelnuevo= new PanelNuevo ();
		
		panelmemoria=new PanelMemoria();
		
		panelesquema=new PanelEsquema();
		
		panelayuda=new PanelAyuda();
		
		
		panelesquema.pdibujo.setBackground(Color.WHITE);
		this.add (panelnuevo, "NUEVO");
		this.add(panelmemoria,"MEMORIA");
		this.add (panelesquema, "ESQUEMA" );
		this.add (panelayuda,"AYUDA");
		panelesquema.pdibujo.clean();
		panelesquema.pdibujo.refresh();
	}
	
	/**
	 * Limpia el dibujo del panel de esquema
	 *
	 */
	public void clean()
	{
		panelesquema.pdibujo.clean();
	}
	
	/**
	* Limpia todas las areas de texto del Panel Nuevo.
	*/
	public void limpiar()
		{
		panelnuevo.texto.setText("");
		panelnuevo.resultado.setText("");
		panelnuevo.errores.setText("");
		}
		
	/**
	 * Limpia las areas de texto donde se almacenaran el codigo codificado
	 * y los errores. 
	 */
	public void limpiarEjecutar()
		{
		panelnuevo.resultado.setText("");
		panelnuevo.errores.setText("");
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
		return panelnuevo.texto.getText();
		}
	
	/**
	 * Escribe en el panel Nuevo en el area de texto resultado la instruccion y su 
	 * codificación correspondiente
	 * 
	 * @param codigo_limpio El codigo sin comentarios y sin lineas en blanco
	 * @param ensamblado El código limpio ensamblado
	 * @param instruccionesTotales Numero de instrucciones totales
	 */
	public void Escribir (Vector codigo_limpio,short[] ensamblado,int instruccionesTotales){
		panelnuevo.Escribir (codigo_limpio,ensamblado,instruccionesTotales);	
		}
		
	/**
	 * LLama a Errores del panel nuevo 
	 * 
	 * @param errores Cadena que contiene los errores producidos en el ensamblado
	 */
	public void Errores (String errores){
		panelnuevo.Errores (errores);	
	}

	/**
	 * Limpia el panel esquema para poder dibujar de nuevo en él
	 *
	 */
	public void limpiarEsquema()
		{
		panelesquema.pdibujo.clean();
		panelesquema.pdibujo.refresh();
		}
	
	/**
	 * Escribir en el area de texto del PanelMemoria los datos de la memoria 
	 * 
	 * @param ensamblado Codigo ensamblado correctamente
	 * @param instruccionesTotales Numero de instrucciones totales 
	 */	
	public void EscribirMemoria(short[] ensamblado,int instruccionesTotales,Vector codigo_limpio){
		panelmemoria.EscribirMemoria(ensamblado,instruccionesTotales,codigo_limpio);
	}
	
	/**
	 * En el panel donde se escribe el código a ensamblar no se puede 
	 * editar(modificar)  
	 *
	 */
	public void NoEditable(){
		panelnuevo.texto.setEditable(false);
	}
	
	/**
	 * En el panel donde se escribe el código a ensamblar se puede 
	 * editar(modificar)
	 */
	public void Editable(){
			panelnuevo.texto.setEditable(true);
	}
	
	/**
	 * Se encarga de simular el Simple2
	 * @param ensamblado Codigo ensamblado(Valores iniciales de la MemoriaPrincipal)
	 */
	public void Principal(short[] ensamblado,int tiempo){
		RepresentacionRDD repRdd = new RepresentacionRDD (panelesquema.pdibujo);
		panelesquema.pdibujo.clean();
		//Almacenamiento del dibujo cuando cambia de tamaño
		panelesquema.pdibujo.setRepresentacionRDD(repRdd);
		//Crear un nuevo "secuenciador" con el short[] creado al ensamblar y micromemoria null
		SecuenciadorMicroprograma mic =	new SecuenciadorMicroprograma (ensamblado,null);

		//Añadimos el listener de memoria (si hay)
		mic.AddMemoryChangeListener (panelmemoria);
	
		//Asociamos la representacion de datos.
		mic.SetRepresentacionRDD(repRdd);
		mic.AddRegisterChangeListener(repRdd);
		hilo = new HiloEjecucion (mic);
		hilo.SetTSubciclo(tiempo);
		hilo.start();	
	}
	
	/**
	 * Parar el hilo de ejecucion
	 *
	 */
	public void Parar()
	{
		if(hilo!=null)
			hilo.CambiarPausado();
	}
	
	/**
	 * Función que nos dice si el hilo esta activo
	 * @return True hilo activo
	 * 		   False hilo inactivo
	 */
	public boolean HiloActivo()
	{
		return ((hilo!=null) && hilo.isAlive());
	}
	
	/**
	 * Detiene el hilo
	 *
	 */
	public void Fin()
	{
		if(hilo!=null)
			hilo.detener();
	}
	
	/**
	 * Detiene el hilo
	 *
	 */
	public void Activar()
		{
		if(hilo!=null)
			hilo.detener();
		}
	
}
