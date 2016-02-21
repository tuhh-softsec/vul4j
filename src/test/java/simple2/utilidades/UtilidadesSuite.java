package simple2.utilidades;
import junit.framework.*;

public class UtilidadesSuite {

	public static Test suite () {
		TestSuite suite = new TestSuite("Tests del ensamblador");

		//suite.addTest(new TestSuite (EnsambladorTest.class));;
		
	    return suite;
	}
}