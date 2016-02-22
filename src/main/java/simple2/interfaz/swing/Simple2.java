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
	public static PanelCentral panelCentral;

	/**
	 * Panel superior que contendra los botones del programa
	 */
	public static JPanel panelSuperior;

	/**
	 * Boton que al pulsar nos muestra el codigo qu hemos escrito y la
	 * codificacion o errores
	 */
	public static Boton botonCodigo;

	/**
	 * Boton que al pulsar nos ejecuta: -Primero codifica el codigo y si no
	 * tiene errores pasa al siguiente punto -Una vez codificado el codigo pasa
	 * a mostrarnos la simulación
	 */
	public static Boton botonEjecutar;

	/**
	 * Boton que al pulsar nos muestra el estado de la memoria
	 */
	public static Boton botonMemoria;

	/**
	 * Boton que al pulsar para la simulacion
	 */
	public static Boton botonParar;

	/**
	 * Boton que nos muestra la ruta
	 */
	public static Boton botonEsquema;

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
		panelCentral = new PanelCentral();
		panelSuperior = new JPanel();

		botonCodigo = new Boton("CODIGO");
		botonCodigo.setIcon(new ImageIcon(getClass().getResource("/images/codigo.gif")));
		botonEjecutar = new Boton("EJECUTAR");
		botonEjecutar.setIcon(new ImageIcon(getClass().getResource("/images/ejecutar.gif")));
		botonMemoria = new Boton("MEMORIA");
		botonMemoria.setIcon(new ImageIcon(getClass().getResource("/images/memoria.gif")));
		botonParar = new Boton("PARAR");
		botonParar.setIcon(new ImageIcon(getClass().getResource("/images/dot_rojo.gif")));
		botonEsquema = new Boton("RUTA");
		botonEsquema.setIcon(new ImageIcon(getClass().getResource("/images/ruta.gif")));

		this.rootPaneContainer.getRootPane().getContentPane().setLayout(new BorderLayout());
		panelSuperior.setLayout(new GridLayout(1, 5));
		panelSuperior.add(botonCodigo);
		panelSuperior.add(botonMemoria);
		panelSuperior.add(botonEjecutar);
		panelSuperior.add(botonEsquema);
		panelSuperior.add(botonParar);
		this.rootPaneContainer.getRootPane().getContentPane().add(panelCentral, BorderLayout.CENTER);
		this.rootPaneContainer.getRootPane().getContentPane().add(panelSuperior, BorderLayout.NORTH);
		panelCentral.verPanel("NUEVO");
		crearMenus();

		botonCodigo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				seleccionar();
				botonCodigo.setSelected(true);
				panelCentral.verPanel("NUEVO");
				panelCentral.noEditable();
			}
		});

		botonEjecutar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (botonParar.isSelected()) {
					botonEjecutar.setSelected(false);
					return;
				}
				if (botonEjecutar.isSelected() == false) {
					botonEjecutar.setText("EJECUTAR");
					panelCentral.fin();
					botonEjecutar.setSelected(false);
					return;
				}
				boolean c = panelCentral.isHiloActivo();
				if (c) {
					return;
				}

				panelCentral.limpiarEjecutar();
				String datos = panelCentral.cogerDatos();
				short[] ensamblado;
				String error = "";

				Ejecutar ejecutar = new Ejecutar();
				Vector<String> codigo_limpio  = ejecutar.comprobar(datos);
				error = ejecutar.ejecutarErrores(datos);

				if (error.length() == 0) {
					seleccionar();
					botonEjecutar.setSelected(true);
					panelCentral.verPanel("ESQUEMA");
					botonEjecutar.setText("FIN");
					ensamblado = ejecutar.ensamblarCodigo(codigo_limpio);
					int instruccionesTotales = codigo_limpio.size();
					panelCentral.escribir(codigo_limpio, ensamblado, instruccionesTotales);
					panelCentral.escribirMemoria(ensamblado, instruccionesTotales, codigo_limpio);
					panelCentral.principal(ensamblado, Simple2.this.tiempo);

					Simple2.this.hiloEspera = new HiloEspera(panelCentral.hilo);
					Simple2.this.hiloEspera.start();

					panelCentral.clean();
				} else {
					botonEjecutar.setSelected(false);
					panelCentral.errores(error);
					panelCentral.editable();
				}
			}
		});

		botonMemoria.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				seleccionar();
				botonMemoria.setSelected(true);
				panelCentral.verPanel("MEMORIA");
			}
		});

		botonEsquema.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				seleccionar();
				botonEsquema.setSelected(true);
				panelCentral.verPanel("ESQUEMA");
			}
		});

		botonParar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String aux = botonEjecutar.getText();
				if (aux.compareTo("EJECUTAR") != 0) {
					Simple2.this.parado = !Simple2.this.parado;
					seleccionar();
					if (Simple2.this.parado) {
						botonParar.setText("SEGUIR");
						botonParar.setIcon(new ImageIcon(getClass().getResource("/images/dot_verde.gif")));
						botonEjecutar.setText("PARADO");
					} else {
						botonParar.setText("PARAR");
						botonParar.setIcon(new ImageIcon(getClass().getResource("/images/dot_rojo.gif")));
						botonEjecutar.setSelected(true);
						botonEjecutar.setText("FIN");
					}
					panelCentral.parar();
				} else {
					botonParar.setSelected(false);
				}
			}
		});

	}

	/**
	 * Nos deselecciona todos los botones
	 *
	 */
	public void seleccionar() {
		botonCodigo.setSelected(false);
		botonEjecutar.setSelected(false);
		botonEsquema.setSelected(false);
		botonMemoria.setSelected(false);
		botonParar.setSelected(false);
		if (this.parado)
			botonParar.setSelected(this.parado);
		boolean c = panelCentral.isHiloActivo();
		if (c) {
			botonEjecutar.setSelected(true);
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
					panelCentral.activar();
					Simple2.this.parado = false;
				}
				panelCentral.fin();
				botonEjecutar.setText("EJECUTAR");
				botonParar.setText("PARAR");
				botonParar.setIcon(new ImageIcon(getClass().getResource("/images/dot_rojo.gif")));
				seleccionar();
				botonParar.setSelected(false);
				panelCentral.verPanel("NUEVO");
				panelCentral.editable();
				panelCentral.limpiar();
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
				panelCentral.verPanel("AYUDA");
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
			botonEjecutar.setSelected(false);
			botonEjecutar.setText("EJECUTAR");
			botonEjecutar.setIcon(new ImageIcon(getClass().getResource("/images/ejecutar.gif")));

		}
	};

}
