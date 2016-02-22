/*
 * Created on 05-sep-2003
 *
 */
package simple2.interfaz.swing;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */
/**
 * Panel que renderiza el contenido de un archivo html. Además permite el
 * seguimiento de los links.
 *
 */
public class PanelHtml extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4855422234681163552L;
	/**
	 * Campo de edición donde se mostrará la página html.
	 */
	private JEditorPane html;

	/**
	 * Constructor de la clase.
	 *
	 * @param fichero
	 *            El fichero a visualizar.
	 */
	public PanelHtml(String fichero) {
		Reader reader = null;
		try {
			reader = new InputStreamReader(getClass().getResourceAsStream(fichero), StandardCharsets.ISO_8859_1);
			this.html = new JEditorPane();
			HTMLEditorKit editorKit = new HTMLEditorKit();
			this.html.setEditorKit(editorKit);
			this.html.setEditable(false);
			try {
				HTMLDocument document = new HTMLDocument();
				document.getDocumentProperties().put("IgnoreCharsetDirective", Boolean.TRUE);
				this.html.read(reader, document);
			} catch (IOException ioe) {
				ioe.printStackTrace();
				this.html.setText("No pude encontrar el archivo");
			}
			this.getViewport().add(this.html);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ioe) {
					//
				}
			}
		}
	}

}
