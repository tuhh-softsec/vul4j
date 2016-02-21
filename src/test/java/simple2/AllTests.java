package simple2;
import simple2.ensamblador.EnsambladorSuite;
import simple2.rutadedatos.RutaDeDatosSuite;
import junit.framework.*;

public class AllTests {

	public static void main (String[] args) {
		junit.textui.TestRunner.run (suite());
	}
	public static Test suite () {
		TestSuite suite = new TestSuite("Todos los Tests de Simple2");
		suite.addTest(RutaDeDatosSuite.suite());
		suite.addTest(EnsambladorSuite.suite());
	    return suite;
	}
}
