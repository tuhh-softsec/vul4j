/*
 * Created on 01-ago-2003
 *
 */

package simple2.interfaz.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;

import simple2.representacionruta.InterfaceDibujo;
import simple2.representacionruta.RepresentacionRDD;

/**
 * @author Montserrat Sotomayor Gonzalez
 *
 */

/**
 * Clase que se encarga de poder dibujar en el panel
 */
public class PanelDibujo extends JPanel implements InterfaceDibujo, ComponentListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3173464326719078682L;

	/**
	 * Objeto que pintará sobre la imagen.
	 */
	private RepresentacionRDD rdd = null;

	/**
	 * Imagen en la que dibujamos.
	 */
	private static Image buffer = null;

	/**
	 * Graphics que usamos para dibujar en buffer.
	 */
	private static Graphics gr = null;

	/**
	 * Color de fondo del dibujo
	 */
	private static final Color BACKGROUND = Color.WHITE;

	/**
	 * Ancho del dibujo
	 */
	private int _ancho;

	/**
	 * Alto del dibujo
	 */
	private int _alto;

	private int coordX(int x) {
		return (this._ancho * x / 720);
	}

	private int coordY(int y) {
		return (this._alto * y / 470);
	}

	/**
	 * Instancia de la clase
	 *
	 */
	public PanelDibujo() {
		// No simula pero pinta el estado apagado
		this.rdd = new RepresentacionRDD(this);
		addComponentListener(this);
	}

	/**
	 * Actualiza el dibujo
	 */
	@Override
	public void refresh() {
		this.repaint();
	}

	/**
	 * Limpia el contenido del dibujo (no lo vuelca en pantalla).
	 */
	@Override
	public void clean() {
		if (gr != null) {
			gr.setColor(BACKGROUND);
			gr.fillRect(0, 0, getSize().width, getSize().height);
		}
	}

	/**
	 * Limpia un área rectangular determinada
	 * 
	 * @param x
	 *            Coordenada horizontal donde empieza el rectángulo
	 * @param y
	 *            Coordenada vertical donde empieza el rectángulo
	 * @param ancho
	 *            Ancho del rectangulo que queremos borrar
	 * @param alto
	 *            Alto del rectangulo que queremos borrar
	 */
	@Override
	public void clean(int x, int y, int ancho, int alto) {
		if (gr != null) {
			gr.setColor(BACKGROUND);
			gr.fillRect(coordX(x), coordY(y), coordX(ancho), coordY(alto));
		}

	}

	/**
	 * Dibuja una linea determinada
	 * 
	 * @param c
	 *            Color que tiene la recta
	 * @param x1
	 *            Coordenada horizontal donde comieza la linea
	 * @param y1
	 *            Coordenada vertical donde comieza la linea
	 * @param x2
	 *            Coordenada horizontal donde termina la linea
	 * @param y2
	 *            Coordenada vertical donde termina la linea
	 */
	@Override
	public void dibujarRecta(Color c, int x1, int y1, int x2, int y2) {
		if (gr != null) {
			gr.setColor(c);
			gr.drawLine(coordX(x1), coordY(y1), coordX(x2), coordY(y2));
		}
	}

	/**
	 * Dibuja una linea determinada
	 * 
	 * @param c
	 *            Color que tienen las letras
	 * @param x
	 *            Coordenada horizontal donde comiezan las letras
	 * @param y
	 *            Coordenada vertical donde comienzan las letras
	 * @param texto
	 *            Texto que queremos escribir
	 */
	@Override
	public void dibujarTexto(Color c, int x, int y, String texto) {
		if (gr != null) {
			gr.setColor(c);
			gr.drawString(texto, coordX(x), coordY(y));
		}
	}

	/**
	 * Dibuja un área rectangular determinada
	 * 
	 * @param c
	 *            Color que tendrá el rectangulo
	 * @param x1
	 *            Coordenada horizontal donde empieza el rectángulo
	 * @param y1
	 *            Coordenada vertical donde empieza el rectángulo
	 * @param ancho
	 *            Ancho del rectangulo
	 * @param alto
	 *            Alto del rectangulo
	 */
	@Override
	public void dibujarRectangulo(Color c, int x1, int y1, int ancho, int alto) {
		if (gr != null) {
			gr.setColor(c);
			gr.drawRect(coordX(x1), coordY(y1), coordX(ancho), coordY(alto));
		}
	}

	/**
	 * 
	 * Métodos de dibujo del panel. Vuelcan el contenido de la imagen en la
	 * pantalla.
	 * 
	 * @param g
	 */
	@Override
	public void paint(Graphics g) {
		if (buffer == null) {
			buffer = createImage(getSize().width, getSize().height);
			gr = buffer.getGraphics();
			this._ancho = getSize().width;
			this._alto = getSize().height;
		}

		// PRINCIPAL.
		// Vuelca el contenido de buffer en la pantalla.
		g.drawImage(buffer, 0, 0, this);
	}

	/**
	 * Re-dibuja la ventana con el color de fondo y luego llama al método
	 * paint().
	 * 
	 * @param g
	 */
	@Override
	public void update(Graphics g) {
		if (buffer == null) {
			buffer = createImage(getSize().width, getSize().height);
			gr = buffer.getGraphics();
			this._ancho = getSize().width;
			this._alto = getSize().height;
		}
		g.drawImage(buffer, 0, 0, this);
	}

	/**
	 * Establece el objeto RepresentacionRDD que dibujará. Es utilizado para
	 * notificarle de los cambios de tamaño, de modo que se redibuje entero de
	 * nuevo.
	 */
	@Override
	public void setRepresentacionRDD(RepresentacionRDD r) {
		this.rdd = r;
	}

	/**
	 * Redimensiona el dibujo
	 * 
	 * @param e
	 *            Es un componente del evento
	 */
	@Override
	public void componentResized(ComponentEvent e) {
		if ((getSize().width <= 0) || (getSize().height <= 0)) {
			return;
		}
		buffer = createImage(getSize().width, getSize().height);
		gr = buffer.getGraphics();
		this._ancho = getSize().width;
		this._alto = getSize().height;
		this.clean();
		if (this.rdd != null){
			this.rdd.ActualizarTodo();
		}

	}

	/**
	 * Mover un componente
	 * 
	 * @param e
	 *            Es un componente del evento
	 */
	@Override
	public void componentMoved(ComponentEvent e) {
	}

	/**
	 * Mostrar un componente
	 * 
	 * @param e
	 *            Es un componente del evento
	 */
	@Override
	public void componentShown(ComponentEvent e) {
	}

	/**
	 * Ocultar un componente
	 * 
	 * @param e
	 *            Es un componente del evento
	 */
	@Override
	public void componentHidden(ComponentEvent e) {
	}
}
