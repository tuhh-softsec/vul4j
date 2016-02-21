package simple2.interfaz.swing;
/*
 * Created on 11-ago-2003
 *
 */

import simple2.rutadedatos.SecuenciadorMicroprograma;
import simple2.rutadedatos.SimulacionFinalizadaException;
/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */
/**
 * 
 * Clase utilzada para encapsular los hilos
 */
public class HiloEjecucion extends Thread {

	/**
	 * Simulador de la ruta de datos.
	 */	
	private SecuenciadorMicroprograma mic = null;
		
	/**
	 * Indica si se debe terminar la simulacion. 
	 */	
	private boolean terminar = false;
		
	/**
	 * Indica si la simulación está pausada.
	 */	
	private boolean pausado = false;
		
	/**
	 * Indica el tiempo que esperará entre subciclos.
	 */	
	private int tSubciclo = 1000;
		
	/**
	 * Crea una instancia de la clase.
	 * @param mic El simulador de la ruta de datos a utilizar.
	 */	
	public HiloEjecucion (SecuenciadorMicroprograma mic)
	{
		super();
		this.mic = mic;
	}
		
	/**
	 * Método de ejecución del hilo.
	 */	
	@Override
	public void run ()
	{
		int acumulado = 0;
		int paso = 100;
		try
		{
			this.mic.EjecutarSubciclo();
			while (!this.terminar)
			{
				try{
					Thread.sleep (paso);
					}
				catch(InterruptedException ie){
				}
				if (!this.pausado)
				{
					acumulado += paso;
					if (acumulado >= this.tSubciclo)
					{
						acumulado=0;
						this.mic.EjecutarSubciclo();
					}
				}
			}
			this.mic.Detener();
		}
		catch (SimulacionFinalizadaException e)
		{
			this.mic.Detener();
			return;
		}
	}
		
	/**
	 * Detiene la ejecucion del hilo.
	 */	
	public void detener ()
	{
		this.terminar = true;
	}
		
	/**
	 * Nos indica si el hilo está pausado.
	 * @return 	True: si el hilo está pausado
	 * 			False:en otro caso
	 */
	public boolean GetPausado()
	{
		return this.pausado;
	}
		
	/**
	 * Detiene y reanuda la ejecucion del hilo.
	 */	
	public void CambiarPausado ()
	{
		this.pausado = !this.pausado;
	}
		
	/**
	 * Establece el tiempo que durará cada subciclo.
	 * @param valor El tiempo en ms que durará cada subciclo. 
	 */	
	public void SetTSubciclo (int valor)
	{
		this.tSubciclo = valor;
	}
}