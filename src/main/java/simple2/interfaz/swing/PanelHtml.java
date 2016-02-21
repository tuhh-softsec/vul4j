/*
 * Created on 05-sep-2003
 *
 */
package simple2.interfaz.swing;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */
/**
 * Panel que renderiza el contenido de un archivo html.
 * Además permite el seguimiento de los links.
 *
 */
public class PanelHtml extends JScrollPane implements HyperlinkListener{

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
	 * @param fichero El fichero a visualizar.
	 */
	public PanelHtml (String fichero)
	{
		URL url = null;
		try
		{
			try
			{
				url = getClass().getResource (fichero);
				
			}
			catch (Exception ex)
			{
				url = null;
			}
			if (url != null)
			{
				html = new JEditorPane (url);
				html.setEditable (false);
				html.addHyperlinkListener (this);
				this.getViewport ().add (html);
			}
			else
			{
				html = new JEditorPane();
				html.setEditable(false);
				html.setText("No pude encontrar el archivo");
				this.getViewport ().add (html);
			}
		}
		catch (MalformedURLException e)
		{
			System.err.println ("Malformed URL: " + e);
		}
		catch (IOException e)
		{
			System.err.println ("IOException: " + e);
		}
	}
	/**
	 * Cambia el fichero a visualizar
	 *
	 * @param fichero El nuevo fichero a visualizar.
	 */
	public void setPage (String fichero)
	{
		URL url = null;
		try
		{
			url = getClass().getResource (fichero);
			
		}
		catch (Exception ex)
		{
			System.err.println ("No pude abrir " + fichero);
			url = null;
		}
		if (url != null)
		{
			try
			{			
				html.setPage(url);
			}
			catch (IOException ioe)
			{
				System.err.println ("IOE: " + ioe);
			}
		}
		
	}
	/** 
	 * Se ejecuta al pulsar sobre un link.
	 * Su efecto es cambiar la página a visualizar a la que apunta el enlace.
	 * @param e evento que se lanza al pulsar sobre un link.
	 */
	public void hyperlinkUpdate (HyperlinkEvent e)
	{
		if (e.getEventType () == HyperlinkEvent.EventType.ACTIVATED)
		{
			if (e instanceof HTMLFrameHyperlinkEvent)
			{
				((HTMLDocument) html.getDocument ()).
					processHTMLFrameHyperlinkEvent ((HTMLFrameHyperlinkEvent) e);
			}
			else
			{
				try
				{
					html.setPage (e.getURL ());
				}
				catch (IOException ioe)
				{
					System.err.println ("IOE: " + ioe);
				}
			}
		}
	}
}



