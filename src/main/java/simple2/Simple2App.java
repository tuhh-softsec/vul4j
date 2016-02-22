package simple2;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import simple2.interfaz.swing.Simple2;

public class Simple2App {

	public static void main(String[] args) {

		JFrame window = new JFrame("Simple2");
		window.setSize(720, 550);
		// Añadimos este "listener" para cerrar la aplicación cuando cerremos la
		// ventana principal
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		Simple2 simple = new Simple2(window);
		simple.init();
		window.setVisible(true);

	}

}
