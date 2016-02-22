/*
 * Created on 19-ago-2003
 *
 */
package simple2.interfaz.swing;

import java.awt.BorderLayout;

import javax.swing.JPanel;

/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */
public class PanelAyuda extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2896921229124204024L;

	/**
	 * 
	 */
	public PanelAyuda() {
		super();
		setLayout(new BorderLayout());
		PanelHtml panelHtml = new PanelHtml("/docs/index.html");
		add(panelHtml, BorderLayout.CENTER);

	}

}
