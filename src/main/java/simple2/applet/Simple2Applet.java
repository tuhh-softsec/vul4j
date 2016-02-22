package simple2.applet;

import javax.swing.JApplet;

import simple2.interfaz.swing.Simple2;

public class Simple2Applet extends JApplet {

	private static final long serialVersionUID = 1163101909682637893L;

	public Simple2Applet() {
		super();
	}

	@Override
	public void init() {
		setSize(720, 500);
		Simple2 simple = new Simple2(this);
		simple.init();
		super.init();
	}

}
