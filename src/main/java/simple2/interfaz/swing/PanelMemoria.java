/*
 * Created on 02-ago-2003
 *
 */

package simple2.interfaz.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

import simple2.rutadedatos.MemoryChangeListener;

/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */

/**
 * Creamos una clase que crea el Panel de Memoria
 * 
 */
public class PanelMemoria extends JPanel implements MemoryChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1293539651735935280L;
	/**
	 * Area de texto donde se almacenan los datos de memoria
	 */
	public static final int TAMANO = 2048;
	Object[][] data = new Object[][] { {} };
	String[] headers = new String[] {};
	DefaultTableModel model = new DefaultTableModel(this.data, this.headers);
	JTable tabla;
	JScrollPane a1;

	/**
	 * Vector donde se guardan los nombres de las posiciones de la memoria
	 */
	Vector<String> memoria = new Vector<>();

	/**
	 * Creamos una instancia de la clase
	 *
	 */
	public PanelMemoria() {

		super();
		setLayout(new BorderLayout());
		this.tabla = new JTable(this.model);
		this.tabla.setPreferredSize(new Dimension(714, 230));
		this.tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.tabla.setEnabled(false);

		this.a1 = new JScrollPane(this.tabla);
		this.a1.setPreferredSize(new Dimension(400, 250));

		add(this.a1, BorderLayout.CENTER);

		this.a1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		for (int i = 0; i < 2048; i++) {
			this.memoria.addElement(toHexString(i));
		}
	}

	/**
	 * Escribimos en el area de texto el valor de cada posicion de memoria
	 * 
	 * @param ensamblado
	 *            Los valores que tiene la Memoria Principal
	 * @param instruccionesTotales
	 *            Numero de elementos de la memoria
	 */
	public void escribirMemoria(short[] ensamblado, int instruccionesTotales, Vector<String> codigo_limpio) {
		String[] titulo = new String[] { "POSICION EN MEMORIA", "VALOR HEXADECIMAL", "VALOR BINARIO", "INSTRUCCION" };
		this.model.setColumnIdentifiers(titulo);

		this.model.setRowCount(TAMANO);
		this.model.setColumnCount(4);
		int c = 0;
		while (c != TAMANO) {
			this.model.setValueAt(String.valueOf(this.memoria.elementAt(c)), c, 0);
			if (c < instruccionesTotales) {
				String cadena = (Integer.toHexString(ensamblado[c])).toUpperCase();
				if (ensamblado[c] < 0) {
					cadena = cadena.substring(cadena.length() - 4);
				}
				if (cadena.length() == 1) {
					cadena = "000" + cadena;
				}
				if (cadena.length() == 2) {
					cadena = "00" + cadena;
				}
				if (cadena.length() == 3) {
					cadena = "0" + cadena;
				}
				this.model.setValueAt(String.valueOf(cadena), c, 1);
				this.model.setValueAt(toBinString(ensamblado[c]), c, 2);
				this.model.setValueAt(String.valueOf(codigo_limpio.elementAt(c)), c, 3);
			} else {
				this.model.setValueAt("0000", c, 1);
				this.model.setValueAt("0000000000000000", c, 2);
			}
			c++;
		}

		int alto_fila = this.tabla.getRowHeight();
		Dimension size = this.tabla.getSize();

		size.height = alto_fila * (TAMANO);
		this.tabla.setPreferredSize(size);
		this.tabla.revalidate();
		this.tabla.scrollRectToVisible(new Rectangle(0, size.height - alto_fila, 100, size.height));
	}

	/**
	 * Cambiamos un valor del panel de memoria
	 * 
	 * @param dir
	 *            Posicion que ocupa en la memoria
	 * @param newValue
	 *            Nuevo valor que tiene esa posicion de memoria
	 */
	public void memoryChanged(int dir, short newValue) {
		this.model.setValueAt(String.valueOf(this.memoria.elementAt(dir)), dir, 0);
		String cadena = (Integer.toHexString(newValue)).toUpperCase();
		if (newValue < 0)
			cadena = cadena.substring(cadena.length() - 4);
		if (cadena.length() == 1)
			cadena = "000" + cadena;
		if (cadena.length() == 2)
			cadena = "00" + cadena;
		if (cadena.length() == 3)
			cadena = "0" + cadena;
		this.model.setValueAt(String.valueOf(cadena), dir, 1);
		this.model.setValueAt(toBinString(newValue), dir, 2);

		int alto_fila = this.tabla.getRowHeight();
		Dimension size = this.tabla.getSize();

		size.height = alto_fila * (TAMANO);
		this.tabla.setPreferredSize(size);
		this.tabla.revalidate();
		this.tabla.scrollRectToVisible(new Rectangle(0, size.height - alto_fila, 100, size.height));

	}

	/**
	 * Cambiamos todos los valores del panel de memoria
	 * 
	 * @param ensamblado
	 *            Los nuevos valores de la Memoria Principal
	 */
	public void memoryChanged(short[] ensamblado) {
		int instruccionesTotales = ensamblado.length;
		int c = 0;
		while (c != TAMANO) {
			this.model.setValueAt(String.valueOf(this.memoria.elementAt(c)), c, 0);
			if (c < instruccionesTotales) {
				String cadena = (Integer.toHexString(ensamblado[c])).toUpperCase();
				if (ensamblado[c] < 0) {
					cadena = cadena.substring(cadena.length() - 4);
				}
				if (cadena.length() == 1) {
					cadena = "000" + cadena;
				}
				if (cadena.length() == 2) {
					cadena = "00" + cadena;
				}
				if (cadena.length() == 3) {
					cadena = "0" + cadena;
				}
				this.model.setValueAt(String.valueOf(cadena), c, 1);
				this.model.setValueAt(toBinString(ensamblado[c]), c, 2);
			} else {
				this.model.setValueAt("0000", c, 1);
				this.model.setValueAt("0000000000000000", c, 2);
			}
			c++;
		}
	}

	/**
	 * Pasa un numero entero a Hexadecimal
	 * 
	 * @param valor
	 *            Numero que queremos pasar a hexadecimal
	 * @return El resultado en hexadecimal
	 */
	public static String toHexString(int valor) {
		String ret = Integer.toHexString(valor);
		while (ret.length() < 4) {
			ret = "0" + ret;
		}
		return ret.toUpperCase();
	}

	/**
	 * Pasa un numero entero a Binario
	 * 
	 * @param valor
	 *            Numero que queremos pasar a binario
	 * @return El resultado en binario
	 */
	public static String toBinString(int valor) {
		String ret = Integer.toBinaryString(valor);
		if (valor < 0) {
			ret = ret.substring(ret.length() - 16);
		}
		while (ret.length() < 16) {
			ret = "0" + ret;
		}

		return ret.toUpperCase();
	}

}
