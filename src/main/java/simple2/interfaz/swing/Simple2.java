/*
 * Created on 05-jul-2003
 *
 */

package simple2.interfaz.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.RootPaneContainer;

import simple2.ensamblador.Ejecutar;

/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */

/**
 * Clase principal del programa
 */
public class Simple2 {

	/**
	 * Panel central que contendrá todos los paneles que se utilizarán en el
	 * programa
	 */
	private PanelCentral panelCentral;

	/**
	 * Panel superior que contendra los botones del programa
	 */
	private JPanel panelSuperior;

	/**
	 * Boton que al pulsar nos muestra el codigo qu hemos escrito y la
	 * codificacion o errores
	 */
	private Boton botonCodigo;

	/**
	 * Boton que al pulsar nos ejecuta: -Primero codifica el codigo y si no
	 * tiene errores pasa al siguiente punto -Una vez codificado el codigo pasa
	 * a mostrarnos la simulación
	 */
	private Boton botonEjecutar;

	/**
	 * Boton que al pulsar nos muestra el estado de la memoria
	 */
	private Boton botonMemoria;

	/**
	 * Boton que al pulsar para la simulacion
	 */
	private Boton botonParar;

	/**
	 * Boton que nos muestra la ruta
	 */
	private Boton botonEsquema;

	/**
	 * Variable que nos indica si el botón parado ha sido pulsado
	 */
	boolean parado = false;

	/**
	 * Variable que guarda el tiempo de cada subciclo
	 */
	int tiempo = 1000;

	/**
	 * Texto donde escribimos el tiempo
	 */
	JTextField t;

	private RootPaneContainer rootPaneContainer;

	/**
	 * Hilo que nos controla cuando termina el hilo de la simulación
	 */
	private Thread hiloEspera = null;

	/**
	 * Crea una instancia de la clase.Con el tamaño, nombre y mostrando al
	 * arrancar el programa el panel de inicio.
	 *
	 */
	public Simple2(RootPaneContainer rootPaneContainer) {
		super();
		this.rootPaneContainer = rootPaneContainer;
	}

	public void init() {
		this.panelCentral = new PanelCentral();
		this.panelSuperior = new JPanel();

		this.botonCodigo = new Boton("CODIGO");
		this.botonCodigo.setIcon(new ImageIcon(getClass().getResource("/images/codigo.gif")));
		this.botonEjecutar = new Boton("EJECUTAR");
		this.botonEjecutar.setIcon(new ImageIcon(getClass().getResource("/images/ejecutar.gif")));
		this.botonMemoria = new Boton("MEMORIA");
		this.botonMemoria.setIcon(new ImageIcon(getClass().getResource("/images/memoria.gif")));
		this.botonParar = new Boton("PARAR");
		this.botonParar.setIcon(new ImageIcon(getClass().getResource("/images/dot_rojo.gif")));
		this.botonEsquema = new Boton("RUTA");
		this.botonEsquema.setIcon(new ImageIcon(getClass().getResource("/images/ruta.gif")));

		this.rootPaneContainer.getRootPane().getContentPane().setLayout(new BorderLayout());
		this.panelSuperior.setLayout(new GridLayout(1, 5));
		this.panelSuperior.add(this.botonCodigo);
		this.panelSuperior.add(this.botonMemoria);
		this.panelSuperior.add(this.botonEjecutar);
		this.panelSuperior.add(this.botonEsquema);
		this.panelSuperior.add(this.botonParar);
		this.rootPaneContainer.getRootPane().getContentPane().add(this.panelCentral, BorderLayout.CENTER);
		this.rootPaneContainer.getRootPane().getContentPane().add(this.panelSuperior, BorderLayout.NORTH);
		this.panelCentral.verPanel("NUEVO");
		crearMenus();

		this.botonCodigo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				seleccionar();
				Simple2.this.botonCodigo.setSelected(true);
				Simple2.this.panelCentral.verPanel("NUEVO");
				Simple2.this.panelCentral.noEditable();
			}
		});

		this.botonEjecutar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (Simple2.this.botonParar.isSelected()) {
					Simple2.this.botonEjecutar.setSelected(false);
					return;
				}
				if (Simple2.this.botonEjecutar.isSelected() == false) {
					Simple2.this.botonEjecutar.setText("EJECUTAR");
					Simple2.this.panelCentral.fin();
					Simple2.this.botonEjecutar.setSelected(false);
					return;
				}
				boolean c = Simple2.this.panelCentral.isHiloActivo();
				if (c) {
					return;
				}

				Simple2.this.panelCentral.limpiarEjecutar();
				String datos = Simple2.this.panelCentral.cogerDatos();
				short[] ensamblado;
				String error = "";

				Ejecutar ejecutar = new Ejecutar();
				Vector<String> codigo_limpio  = ejecutar.comprobar(datos);
				error = ejecutar.ejecutarErrores(datos);

				if (error.length() == 0) {
					seleccionar();
					Simple2.this.botonEjecutar.setSelected(true);
					Simple2.this.panelCentral.verPanel("ESQUEMA");
					Simple2.this.botonEjecutar.setText("FIN");
					ensamblado = ejecutar.ensamblarCodigo(codigo_limpio);
					int instruccionesTotales = codigo_limpio.size();
					Simple2.this.panelCentral.escribir(codigo_limpio, ensamblado, instruccionesTotales);
					Simple2.this.panelCentral.escribirMemoria(ensamblado, instruccionesTotales, codigo_limpio);
					Simple2.this.panelCentral.principal(ensamblado, Simple2.this.tiempo);

					Simple2.this.hiloEspera = new HiloEspera(Simple2.this.panelCentral.hilo);
					Simple2.this.hiloEspera.start();

					Simple2.this.panelCentral.clean();
				} else {
					Simple2.this.botonEjecutar.setSelected(false);
					Simple2.this.panelCentral.errores(error);
					Simple2.this.panelCentral.editable();
				}
			}
		});

		this.botonMemoria.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				seleccionar();
				Simple2.this.botonMemoria.setSelected(true);
				Simple2.this.panelCentral.verPanel("MEMORIA");
			}
		});

		this.botonEsquema.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				seleccionar();
				Simple2.this.botonEsquema.setSelected(true);
				Simple2.this.panelCentral.verPanel("ESQUEMA");
			}
		});

		this.botonParar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String aux = Simple2.this.botonEjecutar.getText();
				if (aux.compareTo("EJECUTAR") != 0) {
					Simple2.this.parado = !Simple2.this.parado;
					seleccionar();
					if (Simple2.this.parado) {
						Simple2.this.botonParar.setText("SEGUIR");
						Simple2.this.botonParar.setIcon(new ImageIcon(getClass().getResource("/images/dot_verde.gif")));
						Simple2.this.botonEjecutar.setText("PARADO");
					} else {
						Simple2.this.botonParar.setText("PARAR");
						Simple2.this.botonParar.setIcon(new ImageIcon(getClass().getResource("/images/dot_rojo.gif")));
						Simple2.this.botonEjecutar.setSelected(true);
						Simple2.this.botonEjecutar.setText("FIN");
					}
					Simple2.this.panelCentral.parar();
				} else {
					Simple2.this.botonParar.setSelected(false);
				}
			}
		});

	}

	/**
	 * Nos deselecciona todos los botones
	 *
	 */
	public void seleccionar() {
		this.botonCodigo.setSelected(false);
		this.botonEjecutar.setSelected(false);
		this.botonEsquema.setSelected(false);
		this.botonMemoria.setSelected(false);
		this.botonParar.setSelected(false);
		if (this.parado)
			this.botonParar.setSelected(this.parado);
		boolean c = this.panelCentral.isHiloActivo();
		if (c) {
			this.botonEjecutar.setSelected(true);
		}
	}

	/**
	 * Menú que te permite elegir varias opciones
	 *
	 */
	private void crearMenus() {
		this.t = new CampoNumerico(this.tiempo, 4);
		JMenuBar menubar = new JMenuBar();
		this.rootPaneContainer.getRootPane().setJMenuBar(menubar);

		JMenu menu;
		JMenuItem menuItem;

		menu = new JMenu("Nuevo");
		menu.setIcon(new ImageIcon(getClass().getResource("/images/new.gif")));
		menuItem = new JMenuItem("Nuevo");
		menuItem.setIcon(new ImageIcon(getClass().getResource("/images/new.gif")));
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Simple2.this.parado) {
					Simple2.this.panelCentral.activar();
					Simple2.this.parado = false;
				}
				Simple2.this.panelCentral.fin();
				Simple2.this.botonEjecutar.setText("EJECUTAR");
				Simple2.this.botonParar.setText("PARAR");
				Simple2.this.botonParar.setIcon(new ImageIcon(getClass().getResource("/images/dot_rojo.gif")));
				seleccionar();
				Simple2.this.botonParar.setSelected(false);
				Simple2.this.panelCentral.verPanel("NUEVO");
				Simple2.this.panelCentral.editable();
				Simple2.this.panelCentral.limpiar();
			}
		});
		menu.add(menuItem);
		menubar.add(menu);

		menu = new JMenu("Opciones");
		menu.setIcon(new ImageIcon(getClass().getResource("/images/reloj.gif")));
		menuItem = new JMenuItem("Tiempo");
		menuItem.setIcon(new ImageIcon(getClass().getResource("/images/reloj.gif")));
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				seleccionar();
				String cad = Simple2.this.t.getText();
				if (cad.length() != 0) {
					try {
						Simple2.this.tiempo = Integer.parseInt(cad);
					} catch (Exception ex) {
					}
				}
			}
		});

		menu.add(menuItem);
		menu.add(this.t);
		menubar.add(menu);

		menu = new JMenu("Ayuda");
		menu.setIcon(new ImageIcon(getClass().getResource("/images/ayuda.gif")));
		menuItem = new JMenuItem("Ayuda");
		menuItem.setIcon(new ImageIcon(getClass().getResource("/images/ayuda.gif")));
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				seleccionar();
				Simple2.this.panelCentral.verPanel("AYUDA");
			}
		});

		menu.add(menuItem);
		menubar.add(menu);

	}

	/**
	 * 
	 * @author Montserrat Sotomayor Gonzalez
	 *
	 */
	/**
	 * 
	 * Hilo que controla cuando el hilo de ejecucion termina
	 */
	public class HiloEspera extends Thread {
		private Thread thread = null;

		/**
		 * Instancia de la clase
		 * 
		 * @param t
		 *            hilo que controla
		 */
		public HiloEspera(Thread t) {
			this.thread = t;
		}

		/**
		 * Método de ejecución del hilo.
		 */
		@Override
		public void run() {
			try {
				this.thread.join();
			} catch (InterruptedException ex) {

			}
			Simple2.this.botonEjecutar.setSelected(false);
			Simple2.this.botonEjecutar.setText("EJECUTAR");
			Simple2.this.botonEjecutar.setIcon(new ImageIcon(getClass().getResource("/images/ejecutar.gif")));

		}
	};

}
