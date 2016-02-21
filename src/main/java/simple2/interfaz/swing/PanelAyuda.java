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
public class PanelAyuda extends JPanel{

	/**
	 * 
	 */
	public PanelAyuda() {
		super();
		setLayout(new BorderLayout());
		PanelHtml panelHtml = new PanelHtml ("/docs/index.html");
		add (panelHtml, BorderLayout.CENTER);
		
	}

}
