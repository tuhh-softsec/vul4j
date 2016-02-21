package simple2.ensamblador;

import junit.framework.*;

public class EnsambladorSuite {

	public static Test suite () {
		TestSuite suite = new TestSuite("Tests del ensamblador");

		suite.addTest(new TestSuite (EnsambladorTest.class));;
		
	    return suite;
	}
}
