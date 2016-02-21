package simple2.rutadedatos;

import junit.framework.*;


public class RutaDeDatosSuite {

	public static Test suite () {
		TestSuite suite = new TestSuite("Tests de la ruta de datos");

		suite.addTest (new TestSuite(AluTest.class));
		suite.addTest (new TestSuite(MicroInstruccionTest.class));
	    return suite;
	}
}
