/*
 * Created on 02-ago-2003
 *
 */

package simple2.interfaz.swing;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

import simple2.rutadedatos.*;
/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */

/**
 * Creamos una clase que crea el Panel de Memoria
 * 
 */
public class PanelMemoria extends JPanel implements MemoryChangeListener
{

	/**
	 * Area de texto donde se almacenan los datos de memoria
	 */
	public static int tamanno=2048;
	Object[][] data = new Object[][]{ {} };
	String[] headers = new String[]{ } ;
	DefaultTableModel model = new DefaultTableModel(data,  headers); 
	JTable tabla;
	JScrollPane a1;
	
	/**
	 * Vector donde se guardan los nombres de las posiciones de la memoria
	 */
	Vector memoria=new Vector();
	
	/**
	 * Creamos una instancia de la clase
	 *
	 */
	public PanelMemoria() {

		super();
		setLayout (new BorderLayout());
		tabla=new JTable(model);
		tabla.setPreferredSize (new Dimension (714,230));
		tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tabla.setEnabled(false);
			
		a1=new JScrollPane (tabla);
		a1.setPreferredSize(new Dimension(400,250));
		
		add(a1, BorderLayout.CENTER);

		a1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		for (int i=0;i<2048;i++)
			memoria.addElement(ToHexString(i));
	}
	
	/**
	 * Escribimos en el area de texto el valor de cada posicion de memoria
	 * @param ensamblado Los valores que tiene la Memoria Principal
	 * @param instruccionesTotales Numero de elementos de la memoria
	 */
	public void EscribirMemoria(short[] ensamblado,int instruccionesTotales,Vector codigo_limpio){
		String titulo[];
		titulo=new String[4];
		titulo[0]="POSICION EN MEMORIA";
		titulo[1]="VALOR HEXADECIMAL";
		titulo[2]="VALOR BINARIO";
		titulo[3]="INSTRUCCION";
		model.setColumnIdentifiers(titulo);

		model.setRowCount(tamanno);
		model.setColumnCount(4);
		int c=0;
		while(c!=tamanno){
			model.setValueAt(String.valueOf(memoria.elementAt(c)),c,0);
			if(c<instruccionesTotales){
				String cadena=(Integer.toHexString((int) ensamblado[c])).toUpperCase();
				if ((int) ensamblado[c] < 0)
					cadena=cadena.substring(cadena.length()-4);
				if(cadena.length()==1) cadena="000"+cadena;
				if(cadena.length()==2) cadena="00"+cadena;
				if(cadena.length()==3) cadena="0"+cadena;
				model.setValueAt(String.valueOf(cadena),c,1);
				model.setValueAt(ToBinString((int) ensamblado[c]),c,2);
				model.setValueAt(String.valueOf(codigo_limpio.elementAt(c)),c,3);
			}
			else{
				model.setValueAt("0000",c,1);
				model.setValueAt("0000000000000000",c,2);
			}
			c++;
		}

		int alto_fila = tabla.getRowHeight() ;
		Dimension size = tabla.getSize();
		
		size.height = alto_fila  * (tamanno );
		tabla.setPreferredSize(size);
		tabla.revalidate();
		tabla.scrollRectToVisible(new Rectangle (0, size.height - alto_fila ,100, size.height));
	}
	
	/**
	 * Cambiamos un valor del panel de memoria
	 * @param dir Posicion que ocupa en la memoria
	 * @param newValue Nuevo valor que tiene esa posicion de memoria
	 */
	public void MemoryChanged (int dir, short newValue)
	{
		model.setValueAt(String.valueOf(memoria.elementAt(dir)),dir,0);
		String cadena=(Integer.toHexString((int) newValue)).toUpperCase();
		if ((int) newValue < 0)
			cadena=cadena.substring(cadena.length()-4);
		if(cadena.length()==1) cadena="000"+cadena;
		if(cadena.length()==2) cadena="00"+cadena;
		if(cadena.length()==3) cadena="0"+cadena;
		model.setValueAt(String.valueOf(cadena),dir,1);
		model.setValueAt(ToBinString(newValue),dir,2);

		int alto_fila = tabla.getRowHeight() ;
		Dimension size = tabla.getSize();
		
		size.height = alto_fila  * (tamanno );
		tabla.setPreferredSize(size);
		tabla.revalidate();
		tabla.scrollRectToVisible(new Rectangle (0, size.height - alto_fila ,100, size.height));
			
	}
	
	/**
	 * Cambiamos todos los valores del panel de memoria
	 * @param newMemoryValues Los nuevos valores de la Memoria Principal
	 */
	public void MemoryChanged (short[]ensamblado)
		{
		int instruccionesTotales=ensamblado.length;
		int c=0;
		while(c!=tamanno){
			model.setValueAt(String.valueOf(memoria.elementAt(c)),c,0);
			if(c<instruccionesTotales){
				String cadena=(Integer.toHexString((int) ensamblado[c])).toUpperCase();
				if ((int) ensamblado[c] < 0)
					cadena=cadena.substring(cadena.length()-4);
				if(cadena.length()==1) cadena="000"+cadena;
				if(cadena.length()==2) cadena="00"+cadena;
				if(cadena.length()==3) cadena="0"+cadena;
					model.setValueAt(String.valueOf(cadena),c,1);
					model.setValueAt(ToBinString((int) ensamblado[c]),c,2);
				}
				else{
					model.setValueAt("0000",c,1);
					model.setValueAt("0000000000000000",c,2);
				}
			c++;
		}
	}
	
	/**
	 * Pasa un numero entero a Hexadecimal
	 * @param valor Numero que queremos pasar a hexadecimal
	 * @return El resultado en hexadecimal
	 */
	public static String ToHexString (int valor)
	{
		String ret = Integer.toHexString(valor);
		while (ret.length() < 4)
			ret = "0" + ret;
		return ret.toUpperCase();
	}
	
	/**
	 * Pasa un numero entero a Binario
	 * @param valor Numero que queremos pasar a binario
	 * @return El resultado en binario
	 */
	public static String ToBinString (int valor)
	{
		String ret=Integer.toBinaryString(valor);
		if ((int) valor < 0)
			ret=ret.substring(ret.length()-16);
		while (ret.length() < 16)
			ret = "0" + ret;
			
		return ret.toUpperCase();
	}
		

}
