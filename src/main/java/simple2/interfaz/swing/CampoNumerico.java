/*
 * Created on 03-sep-2003
 *
 */
package simple2.interfaz.swing;

import java.awt.Toolkit;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */
/**
 * Esta clase es un campo de edición de texto que sólo acepta numeros. Cualquier
 * carácter introducido que no sea numérico será rechazado. También limita el
 * tamaño del campo de edición, no permitiendo introducir un número de mayor
 * número de digitos que los especificados en el constructor del objeto.
 */
public class CampoNumerico extends JTextField {

	/**
	 * 
	 */
	private static final long serialVersionUID = -607499991012895863L;
	/**
	 * Valor en el que se almacena el valor del ancho del campo de edición. La
	 * clase almacena internamente el ancho del campo en carácteres. Además, es
	 * el máximo y no permite introducir un número con más dígitos que <font
	 * size=-1> cols </font>.
	 */
	private int cols;

	/**
	 * Constructor de la clase
	 * 
	 * @param columnas
	 *            Numero máximo de dígitos que admite
	 */
	public CampoNumerico(int columnas) {
		super();
		this.cols = columnas;
	}

	public CampoNumerico(int valor_inicial, int columnas) {
		super(columnas);
		this.cols = columnas;
		setValue(valor_inicial);
	}

	/**
	 * Devuelve el valor almacenado en el campo de edición como un entero.
	 *
	 * @return El valor almacenado actualmente en el campo.
	 */

	public int getValue() {
		int retVal = 0;
		if (getText().compareTo("") != 0)
			retVal = Integer.parseInt(getText());
		return retVal;
	}

	/**
	 * Establece el valor del campo en un entero.
	 *
	 * @param valor
	 *            El valor que tomará el campo.
	 */
	public void setValue(int valor) {
		setText(String.valueOf(valor));
	}

	/**
	 * Función que crea un nuevo modelo de documento. Determina de que forma
	 * serán tratados los datos que se introduzcan en el campo de edición.
	 *
	 * @return El nuevo modelo de documento.
	 */
	@Override
	protected Document createDefaultModel() {
		return new CampoNumericoDoc();
	}

	/**
	 * Clase del tipo de documento del campo de edición. Es la que nos permite
	 * rechazar aquellos carácteres que no estén en los rangos 0-9 así como
	 * aquellos que hagan que el contenido exceda de la longitud máxima.
	 */
	protected class CampoNumericoDoc extends PlainDocument {

		/**
		 * 
		 */
		private static final long serialVersionUID = 803699531167972655L;

		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			if (str == null) {
				return;
			}
			char[] source = str.toCharArray();

			char[] result = new char[source.length];
			int j = 0;
			for (int i = 0; i < result.length; i++) {
				if (Character.isDigit(source[i]))
					result[j++] = source[i];
				else
					Toolkit.getDefaultToolkit().beep();
			}

			if ((getLength() + source.length) <= CampoNumerico.this.cols)
				super.insertString(offs, new String(result, 0, j), a);
			else
				Toolkit.getDefaultToolkit().beep();
		}
	}
}
